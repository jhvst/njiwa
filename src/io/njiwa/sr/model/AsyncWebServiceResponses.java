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

package io.njiwa.sr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.njiwa.common.model.RpaEntity;
import io.njiwa.common.Utils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.w3c.dom.Node;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @brief Represents asynchronous replies to WS requests
 */
@Entity
@Table(name = "asyncwebserviceresponses", indexes = {
        @Index(columnList = "rpa_id", name = "asyncWS_idx1"),
        @Index(columnList = "rpa_id,date_added", name = "asyncWS_idx2"),
        @Index(columnList = "rpa_id,anonURL", name = "asyncWS_idx4"),
        @Index(columnList = "date_added", name = "asyncWS_idx3")
})
@SequenceGenerator(name = "asyncWS", sequenceName = "asyncws_seq", allocationSize = 1)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer",})
@DynamicUpdate
@DynamicInsert

public class AsyncWebServiceResponses {
    @javax.persistence.Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asyncWS")
    private
    Long Id;
    @Column(nullable = false, name = "date_added", columnDefinition = "timestamp default current_timestamp",
            updatable = false, insertable = false)
    private
    Date dateAdded;
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private RpaEntity rpa;
    @Column(columnDefinition = "TEXT", nullable = true)
    private
    String anonURL; // Can be NULL. Right?
    @Column(nullable = false, columnDefinition = "TEXT NOT NULL")
    private
    String wsAction;
    @Column(nullable = true)
    private
    Date dateFetched;
    @Column(nullable = true)
    private
    String fetchedBy; // The URL/IP of the fetcher
    @Column(nullable = false, columnDefinition = "TEXT NOT NULL")
    private
    String messageXML; // The actual XML of the message to send back

    public AsyncWebServiceResponses() {
    }

    public AsyncWebServiceResponses(RpaEntity entity, String wsAction, Node messageNode, String anonURL) throws Exception {
        setAnonURL(anonURL);
        setRpa(entity);
        setMessageXML(Utils.XML.getNodeString(messageNode));
        setAnonURL(anonURL);
        setWsAction(wsAction);
    }

    public static List<AsyncWebServiceResponses> fetchAFewPending(EntityManager em, RpaEntity rpa, String anonURL) {
        try {
            return em.createQuery("from AsyncWebServiceResponses where rpa.id = :i and anonURL = :u and fetchedBy is null order by dateAdded asc",
                    AsyncWebServiceResponses.class)
                    .setParameter("i", rpa.getId())
                    .setParameter("u", anonURL)
                    .setMaxResults(2)
                    .getResultList();
        } catch (Exception ex) {
        }
        return new ArrayList<AsyncWebServiceResponses>();
    }

    public static void markAsFetched(EntityManager em, long dbId, String address)
            throws Exception {
        em.createQuery("UPDATE AsyncWebServiceResponses set fetchedBy = :f, dateFetched = :d WHERE id = :i")
                .setParameter("f", address)
                .setParameter("d", Calendar.getInstance().getTime())
                .setParameter("i", dbId)
                .executeUpdate();
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public RpaEntity getRpa() {
        return rpa;
    }

    public void setRpa(RpaEntity rpa) {
        this.rpa = rpa;
    }

    public String getAnonURL() {
        return anonURL;
    }

    public void setAnonURL(String messageID) {
        this.anonURL = messageID;
    }

    public String getWsAction() {
        return wsAction;
    }

    public void setWsAction(String requestType) {
        this.wsAction = requestType;
    }

    public Date getDateFetched() {
        return dateFetched;
    }

    public void setDateFetched(Date dateFetched) {
        this.dateFetched = dateFetched;
    }

    public String getFetchedBy() {
        return fetchedBy;
    }

    public void setFetchedBy(String fetchedBy) {
        this.fetchedBy = fetchedBy;
    }

    public String getMessageXML() {
        return messageXML;
    }

    public void setMessageXML(String messageXML) {
        this.messageXML = messageXML;
    }

}
