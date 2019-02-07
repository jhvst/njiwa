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

package io.njiwa.sr.ws;

import io.njiwa.common.model.RpaEntity;
import io.njiwa.common.model.TransactionType;
import io.njiwa.common.Utils;
import io.njiwa.common.ws.WSUtils;
import io.njiwa.common.ws.types.BaseResponseType;
import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.sr.ws.interfaces.ES4;
import io.njiwa.sr.ws.types.Eis;

import javax.persistence.EntityManager;
import javax.xml.ws.Holder;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bagyenda on 25/05/2017.
 */
public class ES4Client {

    public static void sendPrepareSMSChangeResponse(EntityManager em,
                                                    BaseResponseType.ExecutionStatus status,
                                                    WsaEndPointReference sendTo, String originallyTo,
                                                    Long requestingEntityId,
                                                    String relatesTO,
                                                    Date startDate) {
        if (status == null) {
            status = new BaseResponseType.ExecutionStatus(BaseResponseType.ExecutionStatus.Status.Failed,
                    new BaseResponseType.ExecutionStatus.StatusCode("8.4", "", "4.2", ""));
        }

        Date endDate = Calendar.getInstance().getTime(); // Set it

        ES4 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES4Port", sendTo, ES4.class,
                RpaEntity.Type.SMSR, em,requestingEntityId);
        final RpaEntity rpa  = em.find(RpaEntity.class, requestingEntityId);
        final WsaEndPointReference sender = new WsaEndPointReference(originallyTo,rpa);
        final Holder<String> msgType = new Holder<String>("http://gsma.com/ES4/eUICCManagementCallBack/ES4-PrepareSMSRChange");
        try {

            proxy.prepareSMSRChangeResponse(sender, sendTo.address, relatesTO, msgType,
                    Utils.gregorianCalendarFromDate(startDate), Utils.gregorianCalendarFromDate(endDate),
                    TransactionType.DEFAULT_VALIDITY_PERIOD,
                    status);
        } catch (WSUtils.SuppressClientWSRequest s) {
        } catch (Exception ex) {
            Utils.lg.error("Failed to issue async prepare SMSR Change Response call: " + ex.getMessage());
        }
    }

    public static void sendSMSChangeResponse(EntityManager em,
                                             BaseResponseType.ExecutionStatus status,
                                             WsaEndPointReference sendTo, String originallyTo,
                                             Long requestingEntityId,
                                             String relatesTO,
                                             Date startDate) {
        if (status == null) {
            status = new BaseResponseType.ExecutionStatus(BaseResponseType.ExecutionStatus.Status.Failed,
                    new BaseResponseType.ExecutionStatus.StatusCode("8.4", "", "4.2", ""));
        }

        Date endDate = Calendar.getInstance().getTime(); // Set it

        ES4 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES4Port", sendTo, ES4.class,
                RpaEntity.Type.SMSR, em,requestingEntityId);
        final RpaEntity rpa  = em.find(RpaEntity.class, requestingEntityId);
        final WsaEndPointReference sender = new WsaEndPointReference(originallyTo,rpa);
        final Holder<String> msgType = new Holder<String>("http://gsma.com/ES4/eUICCManagementCallBack/ES4-SMSRChange");
        try {

            proxy.smsrChangeResponse(sender, sendTo.address, relatesTO, msgType,
                    Utils.gregorianCalendarFromDate(startDate), Utils.gregorianCalendarFromDate(endDate),
                    TransactionType.DEFAULT_VALIDITY_PERIOD,
                    status);
        } catch (WSUtils.SuppressClientWSRequest s) {
        } catch (Exception ex) {
            Utils.lg.error("Failed to issue  SMSR Change Response call: " + ex.getMessage());
        }
    }

    public static void sendSMSRChangeNotification(EntityManager em,
                                                  WsaEndPointReference sendTo, String originallyTo,
                                                  Long requestingEntityId,
                                                  String relatesTO,
                                                  io.njiwa.sr.model.Eis eis,
                                                  Date endDate) throws Exception {

        Eis mEis = Eis.fromModel(eis);
        // Remove unwanted fields
        mEis.hideNotificationFields();

        ES4 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES4Port", sendTo, ES4.class,
                RpaEntity.Type.SMSR, em,requestingEntityId);
        final RpaEntity rpa  = em.find(RpaEntity.class, requestingEntityId);
        final WsaEndPointReference sender = new WsaEndPointReference(originallyTo,rpa);
        final Holder<String> msgType = new Holder<String>("http://gsma.com/ES4/ProfileManagentCallBack/ES4-HandleSMSRChangeNotification");
        try {

            proxy.handleSMSRChangeNotification(sender, sendTo.address, relatesTO, msgType,
                    mEis,
                    Utils.gregorianCalendarFromDate(endDate));
        } catch (WSUtils.SuppressClientWSRequest s) {
        } catch (Exception ex) {
            Utils.lg.error("Failed to issue async SMSR Change notification call: " + ex.getMessage());
        }
    }

}
