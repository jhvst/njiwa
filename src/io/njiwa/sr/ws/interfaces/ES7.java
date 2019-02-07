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

import io.njiwa.common.ws.types.BaseResponseType;
import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.sr.ws.types.AuthenticateSMSRResponse;
import io.njiwa.sr.ws.types.CreateAdditionalKeySetResponse;
import io.njiwa.sr.ws.types.Eis;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Action;
import javax.xml.ws.Holder;

/**
 * Created by bagyenda on 09/05/2017.
 */
@WebService(name = "ES7", targetNamespace = "http://namespaces.gsma.org/esim-messaging/1")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public interface ES7 {
    @WebMethod(operationName = "AuthenticateSMSRRequest")
    @Action(input = "http://gsma.com/ES7/eUICCManagement/ES7-AuthenticateSMSR")
    AuthenticateSMSRResponse authenticateSMSR(@WebParam(name = "From", header = true, targetNamespace = "http://www" +
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
                                              @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                                      ".org/esim-messaging/1")
                                              long acceptablevalidity,

                                              @WebParam(name = "Eid", targetNamespace = "http://namespaces.gsma" +
                                                      ".org/esim-messaging/1")
                                              String eid,
                                              @WebParam(name = "SmsrCertificate", targetNamespace = "http://namespaces" +
                                                      ".gsma" +
                                                      ".org/esim-messaging/1")
                                              String smsRCertificate
    );


    @WebMethod(operationName = "AuthenticateSMSRResponse")
    @Action(input = "http://gsma.com/ES7/eUICCManagementCallBack/ES7-AuthenticateSMSR")
    BaseResponseType authenticateSMSRResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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
                                              @WebParam(name = "RandomChallenge", targetNamespace = "http://namespaces.gsma" +
                                                      ".org/esim-messaging/1")
                                              String randomChallenge
    );

    @WebMethod(operationName = "CreateAdditionalKeySetRequest")
    @Action(input = "http://gsma.com/ES7/eUICCManagement/ES7-CreateAdditionalKeySet")
    CreateAdditionalKeySetResponse createAdditionalKeySet(@WebParam(name = "From", header = true, targetNamespace =
            "http://www" +
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
                                                          @WebParam(name = "AcceptableValidityPeriod", targetNamespace = "http://namespaces.gsma" +
                                                                  ".org/esim-messaging/1")
                                                          long acceptablevalidity,

                                                          @WebParam(name = "Eid", targetNamespace = "http://namespaces.gsma" +
                                                                  ".org/esim-messaging/1")
                                                          String eid,
                                                          @WebParam(name = "KeyVersionNumber", targetNamespace = "http://namespaces" +
                                                                  ".gsma" +
                                                                  ".org/esim-messaging/1")
                                                          int keyVersionNumber,
                                                          @WebParam(name = "InitialSequenceCounter", targetNamespace =
                                                                  "http://namespaces" +
                                                                          ".gsma" +
                                                                          ".org/esim-messaging/1")
                                                          int seqNumber,
                                                          @WebParam(name = "ECCKeyLength", targetNamespace =
                                                                  "http://namespaces" +
                                                                          ".gsma" +
                                                                          ".org/esim-messaging/1")
                                                          String eccLength,
                                                          @WebParam(name = "ScenarioParameter", targetNamespace =
                                                                  "http://namespaces" +
                                                                          ".gsma" +
                                                                          ".org/esim-messaging/1")
                                                          byte scenarioParam,
                                                          @WebParam(name = "HostID", targetNamespace = "http://namespaces" +
                                                                  ".gsma" +
                                                                  ".org/esim-messaging/1")
                                                          String hostID,
                                                          @WebParam(name = "EphemeralPublicKey", targetNamespace =
                                                                  "http://namespaces" +
                                                                          ".gsma" +
                                                                          ".org/esim-messaging/1")
                                                          String ePk,
                                                          @WebParam(name = "Signature", targetNamespace = "http://namespaces" +
                                                                  ".gsma" +
                                                                  ".org/esim-messaging/1")
                                                          String signature);


    @WebMethod(operationName = "CreateAdditionalKeySetResponse")
    @Action(input = "http://gsma.com/ES7/eUICCManagementCallBack/ES7-CreateAdditionalKeySet")
    BaseResponseType createAdditionalKeysetResponse(@WebParam(name = "From", header = true, targetNamespace =
            "http://www.w3" +
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
                                                    @WebParam(name = "DerivationRandom", targetNamespace =
                                                            "http://namespaces.gsma" +
                                                                    ".org/esim-messaging/1")
                                                    String dr,
                                                    @WebParam(name = "Receipt", targetNamespace = "http://namespaces" +
                                                            ".gsma" +
                                                            ".org/esim-messaging/1")
                                                    String receipt
    );

    @WebMethod(operationName = "HandoverEUICCResponse")
    @Action(input = "http://gsma.com/ES7/eUICCManagementCallBack/ES7-HandoverEUICC")
    BaseResponseType handoverEUICCResponse(@WebParam(name = "From", header = true, targetNamespace =
            "http://www.w3" +
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

    @Action(input = "http://gsma.com/ES7/eUICCManagement/ES7-HandoverEUICC")
    @WebMethod(operationName = "HandoverEUICC")
    BaseResponseType handoverEuicc(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
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

                                   @WebParam(name = "Eis") final
                                   Eis eis,
                                   @WebParam(name = "RelatesTo", mode = WebParam.Mode.OUT, header = true,
                                           targetNamespace = "http://www.w3.org/2007/05/addressing/metadata")
                                   Holder<String> relatesTo
    );
}
