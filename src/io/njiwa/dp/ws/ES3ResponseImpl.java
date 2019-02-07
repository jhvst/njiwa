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

package io.njiwa.dp.ws;

import io.njiwa.common.PersistenceUtility;
import io.njiwa.common.Utils;
import io.njiwa.common.model.TransactionType;
import io.njiwa.common.ws.WSUtils;
import io.njiwa.common.ws.handlers.Authenticator;
import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.dp.model.SmDpTransaction;
import io.njiwa.common.SDCommand;
import io.njiwa.common.ws.types.BaseResponseType;
import io.njiwa.dp.model.ISDP;
import io.njiwa.dp.transactions.DownloadProfileTransaction;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Action;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;

/**
 * Created by bagyenda on 06/04/2017.
 */
@WebService(name = "ES3", serviceName = Authenticator.SMDP_SERVICE_NAME,targetNamespace = "http://namespaces.gsma.org/esim-messaging/1")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
@Stateless
@HandlerChain(file = "../../common/ws/handlers/ws-default-handler-chain.xml")
public class ES3ResponseImpl {
    @Inject
    PersistenceUtility po; // For saving objects
    @Resource
    private ManagedExecutorService runner; //!< For use by async callbacks
    @Resource
    private WebServiceContext context;

    @WebMethod(operationName = "CreateISDPResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-CreateISDP")
    public String createISDPResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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

    ) throws Exception {

        final SmDpTransaction tr = SmDpTransaction.findbyRequestID(po, messageId);
        // Get the object
        final DownloadProfileTransaction trObj = (DownloadProfileTransaction)tr.transactionObject(); //
        // Get
        // object
        WSUtils.getRespObject(context).sendError(Response.Status.ACCEPTED.getStatusCode(), "");
        boolean isSuccess;

        byte[] resp;
        try {
            // Parse response as RAPDU
            Utils.Pair<Integer, byte[]> xres =  Utils.BER.decodeTLV(data);
            resp = xres.l;
            // Get response code
            int sw1 = resp[resp.length - 2];
            //  int sw2 = resp[resp.length-1];
            isSuccess = SDCommand.APDU.isSuccessCode(sw1);
        } catch (Exception ex) {
            resp = null;
            isSuccess = false;
        }

        final TransactionType.ResponseType responseType = isSuccess ? TransactionType.ResponseType.SUCCESS :
                TransactionType.ResponseType.ERROR;
        trObj.handleResponse(po, tr.getId(), responseType, messageId, data);
        ISDP isdp;
        try {
            isdp = tr.getIsdp();
        } catch (Exception ex) {
            isdp = null;
        }
        if (isSuccess) {
            isdp.setAid(aid); // Update AID from server
            isdp.setState(ISDP.State.Created); // Move to next status
        } else if (isdp != null) {
            try {
                tr.setIsdp(null);
                po.doTransaction(new PersistenceUtility.Runner<Object>() {
                    @Override
                    public Object run(PersistenceUtility po, EntityManager em) throws Exception {
                        em.remove(tr.getIsdp());
                        return null;
                    }

                    @Override
                    public void cleanup(boolean success) {

                    }
                });
            } catch (Exception ex) {
            }
            // Reply to MNO...
        }

        return "";
    }

    @WebMethod(operationName = "SendDataResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-SendData")
    public String sendDataResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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

    ) throws Exception {

        final SmDpTransaction tr = SmDpTransaction.findbyRequestID(po, messageId);
        // Get the object
        final DownloadProfileTransaction trObj = (DownloadProfileTransaction)tr.transactionObject();
        WSUtils.getRespObject(context).sendError(Response.Status.ACCEPTED.getStatusCode(), "");

        final TransactionType.ResponseType responseType = executionStatus.status == BaseResponseType.ExecutionStatus.Status.ExecutedSuccess ? TransactionType.ResponseType
                .SUCCESS :
                TransactionType.ResponseType.ERROR;
        trObj.handleResponse(po, tr.getId(), responseType, messageId, data); //
        // Pass it on
        return "";
    }

    @WebMethod(operationName = "EnableISDPResponse")
    @Action(input = "http://gsma.com/ES3/ProfileManagentCallBack/ES3-EnableISDP")
    public String enableISDPResponse(@WebParam(name = "From", header = true, targetNamespace = "http://www.w3" +
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

    ) throws Exception {
        final SmDpTransaction tr = SmDpTransaction.findbyRequestID(po, messageId);
        // Get the object
        final DownloadProfileTransaction trObj = (DownloadProfileTransaction)tr.transactionObject();
        WSUtils.getRespObject(context).sendError(Response.Status.ACCEPTED.getStatusCode(), "");

        final TransactionType.ResponseType responseType = executionStatus.status == BaseResponseType.ExecutionStatus.Status.ExecutedSuccess ? TransactionType.ResponseType
                .SUCCESS :
                TransactionType.ResponseType.ERROR;
        tr.recordResponse(po, "EnableProfile", data, responseType); // Record response type
        trObj.handleResponse(po, tr.getId(), responseType, messageId, data);
        return "";
    }
}
