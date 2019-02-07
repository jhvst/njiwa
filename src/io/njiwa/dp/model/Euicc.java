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

package io.njiwa.dp.model;

import io.njiwa.sr.ws.types.Eis;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by bagyenda on 31/03/2017.
 */
@Entity
@Table(name = "dp_euiccs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"eid"}, name = "dp_euicc_key_ct")
        }, indexes = {
        @Index(columnList = "eid", name = "dp_euicc_idx1"),

}
)
@SequenceGenerator(name = "dp_euicc", sequenceName = "dp_euiccs_seq")
public class Euicc {
    @Transient
    public Eis eis; // Temporarily stored here
    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dp_euicc")
    private
    Long Id;
    @Column(nullable = false, name = "date_added", columnDefinition = "timestamp default current_timestamp", updatable = false, insertable = false)
    private
    Date dateAdded;
    @Column(name = "eid", nullable = false, columnDefinition = "text")
    private
    String eid; // The actual EID
    @Column(name = "smsr_id", nullable = false, columnDefinition = "text")
    private
    String smsrOID; // The handler SMSR
    @Column(nullable = false, name = "lastAccess", columnDefinition = "timestamp default current_timestamp",
            insertable = false)
    private
    Date lastAccess;
    @Column
    private
    byte[] ecasd_public_key_q; // The public key parameter
    @Column
    private
    Integer ecasd_public_key_param_ref;
    @Column
    private
    String ecasd_sin;
    @Column
    private
    String ecasd_sdin;
    @OneToMany(mappedBy = "euicc", cascade = CascadeType.ALL,orphanRemoval = true)
    private
    List<ISDP> isdps;
    public Euicc() {}
    public Euicc(String eid, String smsrOID, List<ISDP> isdps)
    {
        setEid(eid);
        setSmsrOID(smsrOID);
        setIsdps(isdps);
    }

    public static Euicc findByEID(EntityManager em, String eid)
    {
        try {
            return em.createQuery("from Euicc where eid = :e",Euicc.class)
                    .setParameter("e",eid)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception ex){}
        return null;
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

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getSmsrOID() {
        return smsrOID;
    }

    public void setSmsrOID(String smsrOID) {
        this.smsrOID = smsrOID;
    }

    public Date getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Date lastAccess) {
        this.lastAccess = lastAccess;
    }

    public List<ISDP> getIsdps() {
        return isdps;
    }

    public void setIsdps(List<ISDP> isdps) {
        this.isdps = isdps;
    }

    public byte[] getEcasd_public_key_q() {
        return ecasd_public_key_q;
    }

    public void setEcasd_public_key_q(byte[] ecasd_public_key_q) {
        this.ecasd_public_key_q = ecasd_public_key_q;
    }

    public Integer getEcasd_public_key_param_ref() {
        return ecasd_public_key_param_ref;
    }

    public void setEcasd_public_key_param_ref(Integer ecasd_public_key_param_ref) {
        this.ecasd_public_key_param_ref = ecasd_public_key_param_ref;
    }

    public String getEcasd_sin() {
        return ecasd_sin;
    }

    public void setEcasd_sin(String ecasd_sin) {
        this.ecasd_sin = ecasd_sin;
    }

    public String getEcasd_sdin() {
        return ecasd_sdin;
    }

    public void setEcasd_sdin(String ecasd_sdin) {
        this.ecasd_sdin = ecasd_sdin;
    }
}
