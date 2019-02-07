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

package io.njiwa.dp.transactions;

import io.njiwa.common.model.RpaEntity;
import io.njiwa.common.ws.WSUtils;
import io.njiwa.dp.model.Euicc;
import io.njiwa.dp.model.SmDpTransaction;
import io.njiwa.common.ws.types.BaseTransactionType;
import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.sr.ws.interfaces.ES3;
import io.njiwa.sr.ws.types.Pol2Type;

import javax.persistence.EntityManager;
import javax.xml.ws.Holder;

/**
 * Created by bagyenda on 25/05/2017.
 */
public class UpdatePolicyRulesTransaction extends BaseTransactionType {
    public long smsrId;
    public String iccid;
    public Pol2Type pol2;
    public boolean sent;
    public UpdatePolicyRulesTransaction() {}

    public UpdatePolicyRulesTransaction(long smsrId,String iccid, Pol2Type pol2)
    {
        this.pol2 = pol2;
        this.smsrId = smsrId;
        this.iccid = iccid;
        sent = false;
    }
    @Override
    public boolean hasMore() {
        return !sent;
    }



    @Override
    public Object sendTransaction(EntityManager em, Object tr) throws Exception {
        final SmDpTransaction trans = (SmDpTransaction) tr; // Grab the transaction
        final String eid = em.find(Euicc.class, trans.getEuicc()).getEid();

        try {
            RpaEntity smsr = em.find(RpaEntity.class,smsrId);
            final WsaEndPointReference rcptTo = new WsaEndPointReference(smsr,"ES3");
            final ES3 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES3Port",
                    rcptTo, ES3.class,
                    RpaEntity.Type.SMDP, em,requestingEntityId);
            WsaEndPointReference sender = new WsaEndPointReference(WSUtils.getMyRpa(em, RpaEntity.Type.SMDP),
                    "ES3");
            String msgID = trans.newRequestMessageID(); // Create new one.
            Holder<String> msgType = new Holder<>("http://gsma.com/ES3/ProfileManagement/ES3-UpdatePolicyRules");
            proxy.updatePolicyRules(sender,rcptTo.makeAddress(),null,msgID,msgType,msgID,DEFAULT_VALIDITY_PERIOD,eid,
                    iccid,pol2);
        } catch (WSUtils.SuppressClientWSRequest wsa) {
        } catch (Exception ex) {

            return false;
        }
        return true;
    }

}
