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
import io.njiwa.common.ws.types.BaseResponseType;

import io.njiwa.sr.ws.types.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Action;
import javax.xml.ws.Holder;

/**
 * Created by bagyenda on 01/12/2016.
 */
@WebService(name = "ES3", targetNamespace = "http://namespaces.gsma.org/esim-messaging/1")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface ES3 {
    @WebMethod(operationName = "SendDataResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-SendData")
     String sendDataResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
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

                                   @WebParam(name = "EuiccResponseData", targetNamespace = "http://namespaces.gsma" +
                                           ".org/esim-messaging/1")
                                   String data
    );

    @WebMethod(operationName = "CreateISDPResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-CreateISDP")
     String createISDPResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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

                                     @WebParam(name = "Isd-p-aid", targetNamespace = "http://namespaces.gsma" +
                                             ".org/esim-messaging/1")
                                     String aid,
                                     @WebParam(name = "EuiccResponseData", targetNamespace = "http://namespaces.gsma" +
                                             ".org/esim-messaging/1")
                                     String data

    );

    @WebMethod(operationName = "AuditEISPResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-AuditEISResponse")
    String auditEISResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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

    @WebMethod(operationName = "EnableProfileResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-EnableProfile")
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
                                        BaseResponseType.ExecutionStatus executionStatus,

                                        @WebParam(name = "EuiccResponseData", targetNamespace = "http://namespaces.gsma" +
                                                ".org/esim-messaging/1")
                                        String data

    );

    @WebMethod(operationName = "DisableProfileResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-DisableProfile")
    public String disableProfileResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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

                                         @WebParam(name = "EuiccResponseData", targetNamespace = "http://namespaces.gsma" +
                                                 ".org/esim-messaging/1")
                                         String data

    );

    @WebMethod(operationName = "DeleteISDPResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-DeleteISDP")
     String deleteISDPResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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

                                     @WebParam(name = "EuiccResponseData", targetNamespace = "http://namespaces.gsma" +
                                             ".org/esim-messaging/1")
                                     String data

    );

    @WebMethod(operationName = "UpdateConnectivityParametersPResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-UpdateConnectivityParameters")
    String updateConnectivityParametersResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www" +
            ".w3" +
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

                                                       @WebParam(name = "EuiccResponseData", targetNamespace = "http://namespaces.gsma" +
                                                               ".org/esim-messaging/1")
                                                       String data

    );

    @WebMethod(operationName = "GetEIS")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-GetEISRequest",
            output = "http://gsma.com/ES3/ProfileManagent/ES3-GetEISResponse")
    @WebResult(name = "FunctionExecutionStatus")
    GetEISResponse getEIS(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                    WsaEndPointReference senderEntity,

                          @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                    String receiverEntity,
                          @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                            ".org/2007/05/addressing/metadata")
                                    WsaEndPointReference replyTo,
                          @WebParam(name = "MessageID", header = true,
                                            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                    String messageId,
                          // WSA: Action
                          @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                    Holder<String> messageType,

                          // These are in the body
                          @WebParam(name = "FunctionCallIdentifier")
                                    String functionCallId,

                          @WebParam(name = "ValidityPeriod")
                                    long validityPeriod,

                          @WebParam(name = "Eid")
                                    final String eid,
                          @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                    Holder<String> relatesTo
    );

    @WebMethod(operationName = "AuditEIS")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-AuditEIS",
            output = "http://gsma.com/ES3/ProfileManagentCallback/ES3-AuditEIS")
    @WebResult(name = "FunctionExecutionStatus")
    AuditEISResponse auditEIS(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
            ".org/2007/05/addressing/metadata")
                          WsaEndPointReference senderEntity,

                              @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                          String receiverEntity,
                              @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                  ".org/2007/05/addressing/metadata")
                          WsaEndPointReference replyTo,
                              @WebParam(name = "MessageID", header = true,
                                  targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                          String messageId,
                              // WSA: Action
                              @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                  targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                          Holder<String> messageType,

                              // These are in the body
                              @WebParam(name = "FunctionCallIdentifier")
                          String functionCallId,

                              @WebParam(name = "ValidityPeriod")
                          long validityPeriod,

                              @WebParam(name = "Eid")
                          final String eid,
                              @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                  targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                          Holder<String> relatesTo
    );

    @WebMethod(operationName = "SendData")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-SendData")
    SendDataResponse sendData(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
            ".org/2007/05/addressing/metadata")
                           WsaEndPointReference senderEntity,

                              @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                           final String receiverEntity,
                              @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                   ".org/2007/05/addressing/metadata")
                           WsaEndPointReference replyTo,
                              @WebParam(name = "MessageID", header = true,
                                   targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                           final String messageId,
                              // WSA: Action
                              @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                   targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                           final Holder<String> messageType,

                              // These are in the body
                              @WebParam(name = "FunctionCallIdentifier")
                           String functionCallId,

                              @WebParam(name = "ValidityPeriod")
                           final long validityPeriod,

                              @WebParam(name = "Eid")
                           final String eid,
                              @WebParam(name = "sd-aid")
                           final String aid,
                              @WebParam(name = "Data")
                           final String data,

                              @WebParam(name = "moreToDo")
                           final boolean more,
                              @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                   targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                           Holder<String> relatesTo
    );

    @WebMethod(operationName = "ProfileDownloadCompleted")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-ProfileDownloadCompleted")
     BaseResponseType profileDownloadCompleted(@WebParam(name = "From", header = true,
            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                                     WsaEndPointReference senderEntity,

                                                     @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                                     final String receiverEntity,
                                                     @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                                             ".org/2007/05/addressing/metadata")
                                                     WsaEndPointReference replyTo,
                                                     @WebParam(name = "MessageID", header = true,
                                                             targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                                     final String messageId,
                                                     // WSA: Action
                                                     @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                                             targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                                     final Holder<String> messageType,

                                                     // These are in the body
                                                     @WebParam(name = "FunctionCallIdentifier")
                                                     String functionCallId,

                                                     @WebParam(name = "ValidityPeriod")
                                                     final long validityPeriod,

                                                     @WebParam(name = "Eid")
                                                     final String eid,
                                                     @WebParam(name = "Iccid")
                                                     final String iccid,
                                                     @WebParam(name = "ProfileType")
                                                     final String profileType,
                                                     @WebParam(name = "SubscriptionAddress")
                                                     final SubscriptionAddress subscriptionAddress,
                                                     @WebParam(name = "pol2")
                                                     final Pol2Type pol2);

    @WebMethod(operationName = "UpdatePolicyRules")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-UpdatePolicyRules")
     BaseResponseType updatePolicyRules(@WebParam(name = "From", header = true,
            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                              WsaEndPointReference senderEntity,

                                              @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                              final String receiverEntity,
                                              @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                                      ".org/2007/05/addressing/metadata")
                                              WsaEndPointReference replyTo,
                                              @WebParam(name = "MessageID", header = true,
                                                      targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                              final String messageId,
                                              // WSA: Action
                                              @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                                      targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                              final Holder<String> messageType,

                                              // These are in the body
                                              @WebParam(name = "FunctionCallIdentifier")
                                              String functionCallId,

                                              @WebParam(name = "ValidityPeriod")
                                              final long validityPeriod,

                                              @WebParam(name = "Eid")
                                              final String eid,
                                              @WebParam(name = "Iccid")
                                              final String iccid,
                                              @WebParam(name = "pol2")
                                              final Pol2Type pol2);

    @WebMethod(operationName = "CreateISDP")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-CreateISDP")
    CreateISDPResponse createISDP(@WebParam(name = "From", header = true,
            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             final WsaEndPointReference senderEntity,

                             @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             final String receiverEntity,
                             @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                     ".org/2007/05/addressing/metadata")
                             final WsaEndPointReference replyTo,
                             @WebParam(name = "MessageID", header = true,
                                     targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             final String messageId,
                             // WSA: Action
                             @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                     targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             final Holder<String> messageType,

                             // These are in the body
                             @WebParam(name = "FunctionCallIdentifier")
                             String functionCallId,

                             @WebParam(name = "ValidityPeriod")
                             final long validityPeriod,

                             @WebParam(name = "Eid")
                             final String eid,
                             @WebParam(name = "Iccid")
                             final String iccid,
                             @WebParam(name = "Mno-id")
                             final String mnoId,

                             @WebParam(name = "RequiredMemory")
                             final int requiredMem,

                             @WebParam(name = "moreToDo")
                             final boolean more,
                             @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                     targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             Holder<String> relatesTo
    );

    @WebMethod(operationName = "EnableProfile")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-EnableProfile")
    EnableProfileResponse enableProfile(@WebParam(name = "From", header = true,
            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                final WsaEndPointReference senderEntity,

                                @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                final String receiverEntity,
                                @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                        ".org/2007/05/addressing/metadata") final
                                WsaEndPointReference replyTo,
                                @WebParam(name = "MessageID", header = true,
                                        targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                final String messageId,
                                // WSA: Action
                                @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                        targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                final Holder<String> messageType,

                                // These are in the body
                                @WebParam(name = "FunctionCallIdentifier")
                                String functionCallId,

                                @WebParam(name = "ValidityPeriod")
                                final long validityPeriod,

                                @WebParam(name = "Eid")
                                final String eid,
                                @WebParam(name = "Iccid")
                                final String iccid,

                                @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                        targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                Holder<String> relatesTo
    );
    @WebMethod(operationName = "DisableProfile")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-DisableProfile")
     DisableProfileResponse disableProfile(@WebParam(name = "From", header = true,
            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                 final WsaEndPointReference senderEntity,

                                 @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                 final String receiverEntity,
                                 @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                         ".org/2007/05/addressing/metadata") final
                                 WsaEndPointReference replyTo,
                                 @WebParam(name = "MessageID", header = true,
                                         targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                 final String messageId,
                                 // WSA: Action
                                 @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                         targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                 final Holder<String> messageType,

                                 // These are in the body
                                 @WebParam(name = "FunctionCallIdentifier")
                                 String functionCallId,

                                 @WebParam(name = "ValidityPeriod")
                                 final long validityPeriod,

                                 @WebParam(name = "Eid")
                                 final String eid,
                                 @WebParam(name = "Iccid")
                                 final String iccid,

                                 @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                         targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                 Holder<String> relatesTo
    );
    @WebMethod(operationName = "DeleteISDP")
    @Action(input = "http://gsma.com/ES3/ProfileManagent/ES3-DeleteISDP")
     DeleteISDPResponse deleteISDP(@WebParam(name = "From", header = true,
            targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             final WsaEndPointReference senderEntity,

                             @WebParam(name = "To", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             final String receiverEntity,
                             @WebParam(name = "ReplyTo", header = true, targetNamespace = "http://www.w3" +
                                     ".org/2007/05/addressing/metadata") final
                             WsaEndPointReference replyTo,
                             @WebParam(name = "MessageID", header = true,
                                     targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             final String messageId,
                             // WSA: Action
                             @WebParam(name = "Action", header = true, mode = WebParam.Mode.INOUT,
                                     targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             final Holder<String> messageType,

                             // These are in the body
                             @WebParam(name = "FunctionCallIdentifier")
                             String functionCallId,

                             @WebParam(name = "ValidityPeriod")
                             final long validityPeriod,

                             @WebParam(name = "Eid")
                             final String eid,
                             @WebParam(name = "Iccid")
                             final String iccid,

                             @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                     targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                             Holder<String> relatesTo
    );

}
