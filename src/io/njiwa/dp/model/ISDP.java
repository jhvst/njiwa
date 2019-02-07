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

package io.njiwa.dp.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by bagyenda on 31/03/2017.
 * XXX Do we need this? Why not simply store template Ids
 */
@Entity
@Table(name = "dp_isdps",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"euicc_id", "iccid"}, name = "dp_isdp_key_ct")
        }, indexes = {
        @Index(columnList = "euicc_id", name = "dp_isdp_idx1"),

}
)
@SequenceGenerator(name = "dp_isdp", sequenceName = "dp_isdps_seq")
public class ISDP {
    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dp_isdp")
    private
    Long Id;

    @Column(nullable = false, name = "date_added", columnDefinition = "timestamp default current_timestamp", updatable = false, insertable = false)
    private
    Date dateAdded;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private
    Euicc euicc;

    @Column(nullable = false, columnDefinition = "text")
    private
    String iccid;

    @Column(nullable = false, columnDefinition = "text", name = "mnoOID")
    private
    String mno_oid;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private
    ProfileTemplate profileTemplate; // Link to the profile

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = true)
    private
    String aid; // AID assigned by SM-SR

    public ISDP() {
    }

    public ISDP(Euicc euicc, String mno_oid, ProfileTemplate prof) {
        setEuicc(euicc);
        setMno_oid(mno_oid);
        setProfileTemplate(prof);
        setIccid(prof.getIccid());
        setState(State.InstallInProgress);
    }

    public static ISDP find(EntityManager em, long eid, String iccid) {
        try {
            return em.createQuery("from ISDP WHERE euicc.id = :e and iccid = :i", ISDP.class)
                    .setParameter("e", eid)
                    .setParameter("i", iccid)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception ex) {
        }
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

    public Euicc getEuicc() {
        return euicc;
    }

    public void setEuicc(Euicc euicc) {
        this.euicc = euicc;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getMno_oid() {
        return mno_oid;
    }

    public void setMno_oid(String mno_id) {
        this.mno_oid = mno_id;
    }

    public ProfileTemplate getProfileTemplate() {
        return profileTemplate;
    }

    public void setProfileTemplate(ProfileTemplate profileTemplate) {
        this.profileTemplate = profileTemplate;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public enum State {
        InstallInProgress, Created, Enabled, Disabled, Deleted
        // This last one can't happen of course, it is only
        // used for transacting internally
    }

}
