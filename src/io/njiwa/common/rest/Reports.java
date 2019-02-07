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

package io.njiwa.common.rest;

import io.njiwa.common.Properties;
import io.njiwa.common.StatsCollector;
import io.njiwa.common.model.RpaEntity;
import io.njiwa.common.rest.types.*;
import io.njiwa.sr.model.Eis;
import io.njiwa.sr.transports.Transport;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by bagyenda on 05/06/2017.
 */
@Path("/operations/reports")
@RolesAllowed({Roles.REPORTS,Roles.SMSRAdmin,Roles.SMDPAdmin})
public class Reports {
    static final Set<String> allowedEisOutputFields = new HashSet<>(Arrays.asList("meid",
            "eid",
            "platformType",
            "dateAdded",
            "pendingProfileChangeTransaction",
            "remainingMemory",
            "productionDate",
            "cat_tp_support",
            "platformVersion",
            "smsr_id",
            "isd_p_module_aid",
            "availableMemoryForProfiles",
            "cat_tp_version",
            "secure_packet_version",
            "http_support",
            "remote_provisioning_version",
            "http_version",
            "oldSmsRId",
            "lastNetworkAttach",
            "lastAuditDate",
            "pendingEuiccHandoverTransaction",
            "isd_p_loadfile_aid",
            "imei",
            "Id",
            "registrationComplete",
            "eumId"));

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/smsr-eis")
    public ReportsData getSmSrEisEntries(@QueryParam("columns") ReportsInputColumnsData columns,
                                         @QueryParam("length") int len,
                                         @QueryParam("draw") int draw,
                                         @QueryParam("order") ReportsInputOrderData order,
                                         @QueryParam("search") ReportsInputSearchData search,
                                         @QueryParam("start") int start) {


        return ReportsData.doQuery(em, Eis.class, columns, draw, order, start, len, allowedEisOutputFields);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/server-status")

    public StatsCollector.OSData getServerStatus() {
        return StatsCollector.getOperatingSystemData();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/smdp-event-stats")

    public Map<StatsCollector.EventType, double[]> getSMDPEventsStats() {
        return StatsCollector.getOurEventFrequencies(RpaEntity.Type.SMDP);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/smsr-event-stats")
    public Map<StatsCollector.EventType, double[]> getSMSREventsStats() {
        return StatsCollector.getOurEventFrequencies(RpaEntity.Type.SMSR);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/frequency-intervals")
    public int[] getEventIntervals() {
        return Properties.getStatsIntervals();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/entity-request-stats")
    public Map<String, Map<StatsCollector.EventType, double[]>> getEntityRequestStats() {
        return StatsCollector.getOtherEntityFrequencyCounters();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/transport-stats")
    public Map<Transport.TransportType, Map<Transport.PacketType, double[]>> getTransportStats() {
        return StatsCollector.getTransportStats();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles")
    public String[] getRoles() {
        return Roles.ALL_ROLES;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/transaction-stats")
    public Map<RpaEntity.Type, double[]> getTransactionStats() {
        return StatsCollector.getTransactionStats();
    }
}
