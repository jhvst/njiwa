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

package io.njiwa.dp;

import io.njiwa.common.GenericPeriodicProcessor;
import io.njiwa.common.Utils;
import io.njiwa.common.model.TransactionType;
import io.njiwa.dp.model.SmDpTransaction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bagyenda on 05/05/2017.
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class SmDpTransactionsPeriodicProcessor extends GenericPeriodicProcessor<SmDpTransaction> {
    private static final String query = "SELECT id from SmDpTransaction WHERE status = :s ";
    private static final Map<String, Object> params = new ConcurrentHashMap<String, Object>() {{
        put("s", SmDpTransaction.Status.Ready);
    }};

    @PostConstruct
    public void doStart() {
        Utils.lg.info(String.format("Starting SM-DP Transaction Log Processor..."));
        start(true, "SM-DP Transactions Processor", query, params);
    }

    @PreDestroy
    public void doStop() {
        stop();
        Utils.lg.info(String.format("Stopped SM-DP Transaction Log Processor."));
    }

    @Override
    protected Object processTask(EntityManager em, SmDpTransaction t) throws Exception {
        TransactionType tObj = t.transactionObject();
        try {
            boolean hasMore = tObj.hasMore();
            if (!hasMore)
                t.setStatus(SmDpTransaction.Status.Completed); // Done. Go away
            else {
                t.setStatus(SmDpTransaction.Status.Sent);
                t.setLastSend(Calendar.getInstance().getTime());
                boolean sent = (Boolean) tObj.sendTransaction(em, t);
                if (!sent)
                    t.setStatus(SmDpTransaction.Status.Failed); // XXX right?
            }
        } catch (Exception ex) {
        }
        return null;
    }

    // After Task, use default
}
