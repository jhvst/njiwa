/*
 * Kasuku - Open Source eUICC Remote Subscription Management Server
 * 
 * 
 * Copyright (C) 2019 - , Digital Solutions Ltd. - http://www.dsmagic.com
 *
 * Paul Bagyenda <bagyenda@dsmagic.com>
 * 
 * This program is free software, distributed under the terms of
 * the GNU General Public License.
 */ 

package io.njiwa.common.ws.handlers;

import io.njiwa.common.Utils;
import io.njiwa.common.model.RpaEntity;
import io.njiwa.sr.model.AsyncWebServiceResponses;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;

/**
 * Created by bagyenda on 29/11/2016.
 */
public class AsyncRequestResponse implements SOAPHandler<SOAPMessageContext> {
    private final static String REPLY_TO_URL_KEY = AsyncRequestResponse.class.getCanonicalName() + ".REPLY_TO_KEY";

    public final static String ANONYMOUS_URL = "http://www.w3.org/2005/08/addressing/anonymous";
    public final static String ANONYMOUS_URL_TEMPLATE = "http://docs.oasis-open.org/ws-rx/wsmc/200702/anonymous?id=";

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;


    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        SOAPMessage message = context.getMessage();

        boolean isOutgoing = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (!isOutgoing) {
            // Incoming message, get wsa:ReplyTo
            try {
                Node n = message.getSOAPHeader().getElementsByTagNameNS("http://www.w3.org/2007/05/addressing/metadata",
                        "ReplyTo").item(0);
                // Find address
                Node address = Utils.XML.findNode(n.getChildNodes(), (Node obj) -> {
                        if (obj.getNodeType() != Node.ELEMENT_NODE)
                            return false;
                        try {
                            String ns = obj.getNamespaceURI();
                            String name = obj.getNodeName();
                            String localName = obj.getLocalName();
                            if ((ns == null && name.equals("Address")) ||
                                    (ns.equalsIgnoreCase("http://www.w3.org/2007/05/addressing/metadata") &&
                                            localName.equals("Address")))
                                return true;
                        } catch (Exception ex) {
                        }
                        return false;
                    });
                String url = Utils.XML.getNodeValue(address);
                if (url.equalsIgnoreCase(ANONYMOUS_URL) ||
                        url.toLowerCase().indexOf(ANONYMOUS_URL_TEMPLATE) == 0) {
                    // Got it!
                    context.put(REPLY_TO_URL_KEY, url);
                    context.setScope(REPLY_TO_URL_KEY, MessageContext.Scope.APPLICATION);
                }
                // Check if anonymous URL
            } catch (Exception ex) {

            }
            return true;
        }

        // Else outgoing: Check for the URL. If set, do the thing
        String url = (String) context.get(REPLY_TO_URL_KEY);
        if (url == null)
            return true; // Nothing to do
        HttpServletResponse resp = (HttpServletResponse) context.get(MessageContext.SERVLET_RESPONSE);
        if (resp.getStatus() != Response.Status.OK.getStatusCode())
            return true; // If code is not 200, nothing to do...

        final RpaEntity rpa = Authenticator.getUser(context);
        HttpServletRequest req = (HttpServletRequest) context.get(MessageContext.SERVLET_REQUEST);
        final String wsAction = Authenticator.getWSAction(context);
        // Get the reply from the message body and save it
        try {
            final Node rNode = Utils.XML.findNode(message.getSOAPBody().getChildNodes(),
                    (Node obj) ->  (obj.getNodeType() == Node.ELEMENT_NODE) // First element node
            );
            final AsyncWebServiceResponses r = new AsyncWebServiceResponses(rpa, wsAction, rNode, url.toLowerCase());
            em.persist(r);
        } catch (Exception ex) {
            Utils.lg.error(String.format("Error re-writing async response/saving to DB for [%s]: %s", wsAction, ex));
            return false;
        }

        // Change response code
        context.put(MessageContext.HTTP_RESPONSE_CODE, Response.Status.ACCEPTED.getStatusCode());

        try {
          // Empty the header and body
            Node body = context.getMessage().getSOAPBody();
            Node firstChild = Utils.XML.findNode(body.getChildNodes(),
                    (Node obj) -> (obj.getNodeType() == Node.ELEMENT_NODE)
            );
            body.removeChild(firstChild);
            // Clear all header nodes
            Node header = context.getMessage().getSOAPHeader();
            NodeList nl;
            while ((nl = header.getChildNodes()).getLength() > 0) {
                Node child = nl.item(0);
                header.removeChild(child);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {

    }
}
