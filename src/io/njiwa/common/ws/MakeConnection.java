/*
 * Njiwa Open Source Embedded M2M UICC Remote Subscription Manager
 * 
 * 
 * Copyright (C) 2019 - , Digital Solutions Ltd. - http://www.dsmagic.com
 *
 * Njiwa Dev <dev@njiwa.io>
 * 
 * This program is free software, distributed under the terms of
 * the GNU General Public License.
 */ 

package io.njiwa.common.ws;

import io.njiwa.common.ws.handlers.MakeConnectionResponse;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jws.*;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Action;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

/**
 * Created by bagyenda on 29/11/2016.
 * @brief WS-MakeConnection handler: No Reliable Messaging for now
 */
@WebService(name = "WSMC", targetNamespace = "http://docs.oasis-open.org/ws-rx/wsmc/200702")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
// @BindingType(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING) // XXX Really?
@Stateless
@HandlerChain(file = "handlers/ws-makeconnection-handler-chain.xml")
public class MakeConnection {
    @Resource
    private WebServiceContext context;

    @WebMethod(operationName = "MakeConnection")
    @Action(input = "http://docs.oasis-open.org/ws-rx/wsmc/200702/MakeConnection")
    @WebResult(name = "MakeResponse")
    public String makeConnection(
                                    @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2005/08/addressing")
                                    String receiver,
                                    @WebParam(name = "Address", header = false,
                                            targetNamespace = "http://docs.oasis-open.org/ws-rx/wsmc/200702")
                                    String address

    ) {
        // Store the address in the context
        context.getMessageContext().put(MakeConnectionResponse.ADDRESS_KEY,address);
        context.getMessageContext().setScope(MakeConnectionResponse.ADDRESS_KEY, MessageContext.Scope.APPLICATION);
        // Everything else is done in the handler chain
        return "";
    }
}
