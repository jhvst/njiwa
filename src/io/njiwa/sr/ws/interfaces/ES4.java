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

package io.njiwa.sr.ws.interfaces;

import io.njiwa.common.ws.types.BaseResponseType;
import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.sr.ws.types.*;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Action;
import javax.xml.ws.Holder;
import java.util.List;

/**
 * Created by bagyenda on 09/05/2017.
 */
@WebService(name = "ES4", targetNamespace = "http://namespaces.gsma.org/esim-messaging/1")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface ES4 {
    String ES4_PREPARE_SMSRCHANGE = "ES4-PrepareSMSRChange";
    String ES4_SMSRCHANGE = "ES4-SMSRChange";

    @WebMethod(operationName = "PrepareSMSRChangeResponse")
    @Action(input = "http://gsma.com/ES4/eUICCManagementCallBack/ES4-PrepareSMSRChange")
    String prepareSMSRChangeResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                                     BaseResponseType.ExecutionStatus executionStatus);


    @WebMethod(operationName = "HandleSMSRChangeNotification")
    @Action(input = "http://gsma.com/ES4/ProfileManagentCallBack/ES4-HandleSMSRChangeNotification")
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

    @WebMethod(operationName = "SMSRChangeResponse")
    @Action(input = "http://gsma.com/ES4/eUICCManagementCallBack/ES4-SMSRChange")
    String smsrChangeResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                                     BaseResponseType.ExecutionStatus executionStatus);

    @WebMethod(operationName = "EnableProfile")
    @Action(input = "http://gsma.com/ES4/ProfileManagent/ES4-EnableProfile")
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
    @Action(input = "http://gsma.com/ES4/ProfileManagent/ES4-DisableProfile")
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
    @WebMethod(operationName = "DeleteProfile")
    @Action(input = "http://gsma.com/ES4/ProfileManagent/ES4-DeleteProfile")
    DeleteProfileResponse deleteISDP(@WebParam(name = "From", header = true,
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


    @WebMethod(operationName = "EnableProfileResponse")
    @Action(input = "http://gsma.com/ES4/ProfileManagentCallBack/ES4-EnableProfile")
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
    @Action(input = "http://gsma.com/ES4/ProfileManagentCallBack/ES4-DisableProfile")
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
    @Action(input = "http://gsma.com/ES4/ProfileManagentCallBack/ES4-DeleteISDP")
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
    @Action(input = "http://gsma.com/ES4/ProfileManagentCallBack/ES4-UpdateConnectivityParameters")
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
    @Action(input = "http://gsma.com/ES4/ProfileManagent/ES4-GetEISRequest",
            output = "http://gsma.com/ES4/ProfileManagent/ES4-GetEISResponse")
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
    @Action(input = "http://gsma.com/ES4/ProfileManagent/ES4-AuditEIS",
            output = "http://gsma.com/ES4/ProfileManagentCallback/ES4-AuditEIS")
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
                              @WebParam(name="Iccid")
                            final List<String> iccids,
                              @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                    targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                            Holder<String> relatesTo
    );


    @WebMethod(operationName = "AuditEISPResponse")
    @Action(input = "http://gsma.com/ES4/ProfileManagentCallBack/ES4-AuditEISResponse")
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
}
