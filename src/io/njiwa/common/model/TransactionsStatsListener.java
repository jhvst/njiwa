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

package io.njiwa.common.model;

import io.njiwa.common.StatsCollector;
import io.njiwa.sr.model.SmSrTransaction;

import javax.persistence.PostPersist;

/**
 * Created by bagyenda on 08/06/2017.
 */
public class TransactionsStatsListener {
    @PostPersist
    void handleTransSave(Object o) {
        RpaEntity.Type type;
        if (o instanceof SmSrTransaction)
            type = RpaEntity.Type.SMSR;
        else
            type = RpaEntity.Type.SMDP;
        StatsCollector.recordTransaction(type);
    }

}
