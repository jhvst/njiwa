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

import io.njiwa.common.Utils;
import io.njiwa.common.model.RpaEntity;
import io.njiwa.common.ws.WSUtils;
import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.sr.ws.interfaces.ES2;
import io.njiwa.sr.ws.types.Eis;
import javax.persistence.EntityManager;
import javax.xml.ws.Holder;
import java.util.Date;

/**
 * Created by bagyenda on 25/05/2017.
 */
public class ES2Client {

    public static void sendSMSRChangeNotification(EntityManager em,
                                                  WsaEndPointReference sendTo, Long requestingEntityId, String originallyTo,
                                                  String relatesTO,
                                                  io.njiwa.sr.model.Eis eis,
                                                  Date endDate) throws Exception {

        Eis mEis = Eis.fromModel(eis);
        // Remove unwanted fields
        mEis.hideNotificationFields();

        ES2 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES2Port", sendTo, ES2.class,
                RpaEntity.Type.SMSR, em,requestingEntityId);
        final RpaEntity rpa  = em.find(RpaEntity.class,requestingEntityId);
        final WsaEndPointReference sender = new WsaEndPointReference(originallyTo,rpa);
        final Holder<String> msgType = new Holder<String>("http://gsma" +
                ".com/ES2/ProfileManagentCallBack/ES2-HandleSMSRChangeNotification");
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
