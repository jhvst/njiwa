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

package io.njiwa.sr.ws.interfaces;

import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.sr.ws.types.Eis;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Action;
import javax.xml.ws.Holder;

/**
 * @brief SM-SR ES2 interface for notification of MNOs
 */
@WebService(name = "ES2", targetNamespace = "http://namespaces.gsma.org/esim-messaging/1")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface ES2 {
    @WebMethod(operationName = "HandleProfileDisabledNotification")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-HandleProfileDisabledNotification")
    String handleProfileDisabledNotification(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
            ".org/2007/05/addressing/metadata")
                                                     WsaEndPointReference senderEntity,

                                             @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                             final String receiverEntity,

                                             @WebParam(name = "relatesTo", header = true,
                                                     targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                             final String messageId,
                                             // WSA: Action
                                             @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                                     targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                             final Holder<String> messageType,


                                             @WebParam(name = "Eid", targetNamespace = "http://namespaces.gsma" +
                                                     ".org/esim-messaging/1")
                                             String eid,

                                             @WebParam(name = "Iccid", targetNamespace = "http://namespaces.gsma" +
                                                     ".org/esim-messaging/1")
                                             String iccid,
                                             @WebParam(name = "CompletionTimeStamp")
                                             XMLGregorianCalendar processingEnd
    );

    @WebMethod(operationName = "HandleProfileEnabledNotification")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-HandleProfileEnabledNotification")
    String handleProfileEnabledNotification(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
            ".org/2007/05/addressing/metadata")
                                            WsaEndPointReference senderEntity,

                                            @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                            final String receiverEntity,

                                            @WebParam(name = "relatesTo", header = true,
                                                    targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                            final String messageId,
                                            // WSA: Action
                                            @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                                    targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                            final Holder<String> messageType,


                                            @WebParam(name = "Eid", targetNamespace = "http://namespaces.gsma" +
                                                    ".org/esim-messaging/1")
                                            String eid,

                                            @WebParam(name = "Iccid", targetNamespace = "http://namespaces.gsma" +
                                                    ".org/esim-messaging/1")
                                            String iccid,
                                            @WebParam(name = "CompletionTimeStamp")
                                            XMLGregorianCalendar processingEnd
    );

    @WebMethod(operationName = "HandleProfileDeletedNotification")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-HandleProfileDeletedNotification")
    String handleProfileDeletedNotification(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
            ".org/2007/05/addressing/metadata")
                                            WsaEndPointReference senderEntity,

                                            @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                            final String receiverEntity,

                                            @WebParam(name = "relatesTo", header = true,
                                                    targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                            final String messageId,
                                            // WSA: Action
                                            @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                                    targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                            final Holder<String> messageType,


                                            @WebParam(name = "Eid", targetNamespace = "http://namespaces.gsma" +
                                                    ".org/esim-messaging/1")
                                            String eid,

                                            @WebParam(name = "Iccid", targetNamespace = "http://namespaces.gsma" +
                                                    ".org/esim-messaging/1")
                                            String iccid,
                                            @WebParam(name = "CompletionTimeStamp")
                                            XMLGregorianCalendar processingEnd
    );


    @WebMethod(operationName = "HandleSMSRChangeNotification")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-HandleSMSRChangeNotification")
    String handleSMSRChangeNotification(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
            ".org/2007/05/addressing/metadata")
                                        WsaEndPointReference senderEntity,

                                        @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                        final String receiverEntity,

                                        @WebParam(name = "relatesTo", header = true,
                                                targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                        final String messageId,
                                        // WSA: Action
                                        @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                                targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                        final Holder<String> messageType,

                                        @WebParam(name = "Eis", targetNamespace = "http://namespaces.gsma" +
                                                ".org/esim-messaging/1")
                                                Eis eis,
                                        @WebParam(name = "CompletionTimeStamp")
                                        XMLGregorianCalendar processingEnd
    );


}
