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

import io.njiwa.common.model.RpaEntity;
import io.njiwa.common.Utils;
import io.njiwa.sr.model.AsyncWebServiceResponses;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Set;

/**
 * Created by bagyenda on 29/11/2016.
 *
 * @brief handles a makeConnectionResponse
 */
public class MakeConnectionResponse implements SOAPHandler<SOAPMessageContext> {
    public final static String ADDRESS_KEY = MakeConnectionResponse.class.getCanonicalName() + ".KEY";

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        boolean isOutgoing = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (!isOutgoing)
            return true; // Ignore incoming
        final RpaEntity rpa = Authenticator.getUser(context);
        // Get the ID URL it was requesting...
        final String address = (String) context.get(ADDRESS_KEY);
        if (rpa == null)
            return false; // Ignore
        List<AsyncWebServiceResponses> l =AsyncWebServiceResponses.fetchAFewPending(em, rpa, address);

        // Clear header and body

        SOAPMessage message = context.getMessage();
        Node body, header;
        try {
            // Empty the header and body
            body = message.getSOAPBody();
            header = message.getSOAPHeader();
            Node firstChild = Utils.XML.findNode(body.getChildNodes(),
                    (Node obj) -> (obj.getNodeType() == Node.ELEMENT_NODE)
            );
            body.removeChild(firstChild);
            // Clear all header nodes

            NodeList nl;
            while ((nl = header.getChildNodes()).getLength() > 0) {
                Node child = nl.item(0);
                header.removeChild(child);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            final AsyncWebServiceResponses ar = l.get(0); // Get the first one
            boolean hasMore = l.size() > 1;

            HttpServletRequest req = (HttpServletRequest) context.get(MessageContext.SERVLET_REQUEST);
            final String remoteAddr = req.getRemoteAddr();
            // Mark it as fetched
            AsyncWebServiceResponses.markAsFetched(em, ar.getId(), remoteAddr);
            em.flush();

            Document doc = header.getOwnerDocument();
            Element mNode = doc.createElementNS("http://docs.oasis-open.org/ws-rx/wsmc/200702", "MessagePending");
            mNode.setAttribute("pending", hasMore ? "true" : "false");
            Element aNode = doc.createElementNS("http://www.w3.org/2005/08/addressing", "Action");
            aNode.setTextContent(ar.getWsAction());
            Element tNode = doc.createElementNS("http://www.w3.org/2005/08/addressing", "To");
            tNode.setTextContent(ar.getAnonURL());

            header.appendChild(aNode);
            header.appendChild(tNode);
            header.appendChild(mNode);

            // Make the responses

            String resp = ar.getMessageXML();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d2 = db.parse(new ByteArrayInputStream(resp.getBytes("UTF-8")));

            Node rNode = d2.getDocumentElement().cloneNode(true);

            rNode = body.getOwnerDocument().importNode(rNode, true);
            body.appendChild(rNode);

        } catch (Exception ex) {
            // Change response code
            context.put(MessageContext.HTTP_RESPONSE_CODE, Response.Status.ACCEPTED.getStatusCode());

            return false;
        }

        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {

    }
}
