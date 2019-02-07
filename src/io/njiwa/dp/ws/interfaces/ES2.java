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

package io.njiwa.dp.ws.interfaces;

import io.njiwa.common.ws.types.BaseResponseType;
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
 * Created by bagyenda on 05/04/2017.
 */
@WebService(name = "ES2", targetNamespace = "http://namespaces.gsma.org/esim-messaging/1")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface ES2 {
    @WebMethod(operationName = "GetEISResponse")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-GetEIS")
    String getEISPResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                              @WebParam(name = "ProcessingStart", targetNamespace = "http://namespaces.gsma" +
                                      ".org/esim-messaging/1")
                              XMLGregorianCalendar processingStart,
                              @WebParam(name = "ProcessingEnd")
                              XMLGregorianCalendar processingEnd,
                              @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                      ".org/esim-messaging/1")
                              long acceptablevalidity,
                              @WebParam(name = "FunctionExecutionStatus", targetNamespace = "http://namespaces.gsma" +
                                      ".org/esim-messaging/1")
                              BaseResponseType.ExecutionStatus executionStatus,


                              @WebParam(name = "Eis", targetNamespace = "http://namespaces.gsma" +
                                      ".org/esim-messaging/1")
                           Eis eis
    );

    @WebMethod(operationName = "DownloadProfileResponse")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-DownloadProfile")
    String downloadProfileResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                           @WebParam(name = "ProcessingStart", targetNamespace = "http://namespaces.gsma" +
                                   ".org/esim-messaging/1")
                           XMLGregorianCalendar processingStart,
                           @WebParam(name = "ProcessingEnd")
                           XMLGregorianCalendar processingEnd,
                           @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                   ".org/esim-messaging/1")
                           long acceptablevalidity,
                           @WebParam(name = "FunctionExecutionStatus", targetNamespace = "http://namespaces.gsma" +
                                   ".org/esim-messaging/1")
                           BaseResponseType.ExecutionStatus executionStatus,
                           @WebParam(name = "Iccid", targetNamespace = "http://namespaces.gsma" +
                                   ".org/esim-messaging/1")
                           String iccid,
                                   @WebParam(name = "EuiccResponseData", targetNamespace = "http://namespaces.gsma" +
                                           ".org/esim-messaging/1")
                                   String euiccResponsedata
    );

    @WebMethod(operationName = "UpdateProfileRulesResponse")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-UpdateProfileRules")
    String updateProfileRulesResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                                   @WebParam(name = "ProcessingStart", targetNamespace = "http://namespaces.gsma" +
                                           ".org/esim-messaging/1")
                                   XMLGregorianCalendar processingStart,
                                   @WebParam(name = "ProcessingEnd")
                                   XMLGregorianCalendar processingEnd,
                                   @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                           ".org/esim-messaging/1")
                                   long acceptablevalidity,
                                   @WebParam(name = "FunctionExecutionStatus", targetNamespace = "http://namespaces.gsma" +
                                           ".org/esim-messaging/1")
                                   BaseResponseType.ExecutionStatus executionStatus
    );

    @WebMethod(operationName = "UpdateSubscriptionAddressResponse")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-UpdateSubscriptionAddress")
    String updateSubscriptionAddressResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                                      @WebParam(name = "ProcessingStart", targetNamespace = "http://namespaces.gsma" +
                                              ".org/esim-messaging/1")
                                      XMLGregorianCalendar processingStart,
                                      @WebParam(name = "ProcessingEnd")
                                      XMLGregorianCalendar processingEnd,
                                      @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                              ".org/esim-messaging/1")
                                      long acceptablevalidity,
                                      @WebParam(name = "FunctionExecutionStatus", targetNamespace = "http://namespaces.gsma" +
                                              ".org/esim-messaging/1")
                                      BaseResponseType.ExecutionStatus executionStatus
    );

    @WebMethod(operationName = "EnableProfileResponse")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-EnableProfile")
    String enableProfileResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                                 @WebParam(name = "ProcessingStart", targetNamespace = "http://namespaces.gsma" +
                                         ".org/esim-messaging/1")
                                 XMLGregorianCalendar processingStart,
                                 @WebParam(name = "ProcessingEnd")
                                 XMLGregorianCalendar processingEnd,
                                 @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                         ".org/esim-messaging/1")
                                 long acceptablevalidity,
                                 @WebParam(name = "FunctionExecutionStatus", targetNamespace = "http://namespaces.gsma" +
                                         ".org/esim-messaging/1")
                                 BaseResponseType.ExecutionStatus executionStatus

    );

    @WebMethod(operationName = "DisableProfileResponse")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-DisableProfile")
     String disableProfileResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                                         @WebParam(name = "ProcessingStart", targetNamespace = "http://namespaces.gsma" +
                                                 ".org/esim-messaging/1")
                                         XMLGregorianCalendar processingStart,
                                         @WebParam(name = "ProcessingEnd")
                                         XMLGregorianCalendar processingEnd,
                                         @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                                 ".org/esim-messaging/1")
                                         long acceptablevalidity,
                                         @WebParam(name = "FunctionExecutionStatus", targetNamespace = "http://namespaces.gsma" +
                                                 ".org/esim-messaging/1")
                                         BaseResponseType.ExecutionStatus executionStatus
    );

    @WebMethod(operationName = "DeleteProfilePResponse")
    @Action(input = "http://gsma.com/ES2/ProfileManagentCallBack/ES2-DeleteProfile")
    String deleteProfilePResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                                  @WebParam(name = "ProcessingStart", targetNamespace = "http://namespaces.gsma" +
                                          ".org/esim-messaging/1")
                                  XMLGregorianCalendar processingStart,
                                  @WebParam(name = "ProcessingEnd")
                                  XMLGregorianCalendar processingEnd,
                                  @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                          ".org/esim-messaging/1")
                                  long acceptablevalidity,
                                  @WebParam(name = "FunctionExecutionStatus", targetNamespace = "http://namespaces.gsma" +
                                          ".org/esim-messaging/1")
                                  BaseResponseType.ExecutionStatus executionStatus

    );
}
