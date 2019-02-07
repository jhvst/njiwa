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

package io.njiwa.dp.transactions;

import io.njiwa.common.model.RpaEntity;
import io.njiwa.common.ws.WSUtils;
import io.njiwa.common.ws.types.BaseTransactionType;
import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.dp.model.Euicc;
import io.njiwa.dp.model.SmDpTransaction;
import io.njiwa.dp.ws.ES2Client;
import io.njiwa.common.ws.types.BaseResponseType;
import io.njiwa.sr.ws.interfaces.ES3;

import javax.persistence.EntityManager;
import javax.xml.ws.Holder;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Created by bagyenda on 09/05/2017.
 */
public class ChangeProfileStatusTransaction extends BaseTransactionType {
    public Action action;


    public String iccid;
    public long smsrId; // The ID of the SM-SR
    public boolean sent;

    public ChangeProfileStatusTransaction() {
    }

    public ChangeProfileStatusTransaction(long smsrId, Action action, String iccid) {
        this.action = action;
        this.smsrId = smsrId;
        this.iccid = iccid;
        sent = false;
    }

    @Override
    public Object sendTransaction(EntityManager em, Object tr) throws Exception {
        final SmDpTransaction trans = (SmDpTransaction) tr; // Grab the transaction
        final String eid = em.find(Euicc.class, trans.getEuicc()).getEid();
        sent = true;
        // Send as is. Get proxy, do the thing.
        try {
            final RpaEntity smsr = em.find(RpaEntity.class, smsrId);
            final WsaEndPointReference rcptTo = new WsaEndPointReference(smsr,"ES3");
            final String toAddress = rcptTo.makeAddress();
            final ES3 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES3Port",
                    rcptTo, ES3.class,
                    RpaEntity.Type.SMDP, em,requestingEntityId);
            WsaEndPointReference sender = new WsaEndPointReference(WSUtils.getMyRpa(em, RpaEntity.Type.SMDP),
                    "ES3");
            String msgID = trans.newRequestMessageID(); // Create new one.
            Holder<String> msgType;
            switch (action) {
                case ENABLE:
                    msgType = new Holder<String>("http://gsma" +
                            ".com/ES3/ProfileManagement/ES3-EnableProfile");
                    proxy.enableProfile(sender, toAddress, null, msgID, msgType, msgID, DEFAULT_VALIDITY_PERIOD,
                            eid, iccid, null);
                    break;
                case DISABLE:
                    msgType = new Holder<String>("http://gsma" +
                            ".com/ES3/ProfileManagement/ES3-DisableProfile");
                    proxy.disableProfile(sender, toAddress, null, msgID, msgType, msgID, DEFAULT_VALIDITY_PERIOD,
                            eid, iccid, null);
                    break;
                case DELETE:
                    msgType = new Holder<String>("http://gsma" +
                            ".com/ES3/ProfileManagement/ES3-DeleteISDP");
                    proxy.deleteISDP(sender, toAddress, null, msgID, msgType, msgID, DEFAULT_VALIDITY_PERIOD,
                            eid, iccid, null);
                    break;
            }
        } catch (WSUtils.SuppressClientWSRequest wsa) {

        } catch (Exception ex) {

            return false;
        }

        return true;
    }

    @Override
    protected synchronized void processResponse(EntityManager em, long tid, ResponseType responseType, String reqId,
                                                byte[] response) {
        // response encodes the execution status. So, get it back
        try {
            ObjectInputStream bin = new ObjectInputStream(new ByteArrayInputStream(response));
            BaseResponseType.ExecutionStatus executionStatus = (BaseResponseType.ExecutionStatus) bin.readObject();

            switch (action) {
                case DISABLE:
                    ES2Client.sendDisableProfileResponse(em, executionStatus,
                            getReplyToAddress(em, "ES2"),
                            requestingEntityId, originallyTo, relatesTO, startDate);
                    break;
                case ENABLE:
                    ES2Client.sendEnableProfileResponse(em, executionStatus, getReplyToAddress(em, "ES2"),
                            requestingEntityId,originallyTo, relatesTO, startDate);
                    break;
                case DELETE:
                    ES2Client.sendDeleteProfileResponse(em, executionStatus, getReplyToAddress(em, "ES2"),
                            requestingEntityId,originallyTo, relatesTO, startDate);
                    break;
            }
            // XX We shouldn't care about the local copy of the ISDP. Right??
        } catch (Exception ex) {
        }
    }

    @Override
    public boolean hasMore() {
        return !sent;
    }

    public enum Action {ENABLE, DISABLE, DELETE}
}
