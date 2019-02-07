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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by bagyenda on 30/03/2017.
 */
@Entity
@Table(name = "dp_profiles_data", indexes = {
        @Index(name = "dp_prof_data_idx1", columnList = "prof_id")
})
@SequenceGenerator(name = "profile_data", sequenceName = "dp_profile_data_seq", allocationSize = 1)
@DynamicUpdate
@DynamicInsert
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
public class ProfileData {
    // Used when data is from file
    @javax.persistence.Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_data")
    private
    Long Id;

    @Column(nullable = false, name = "date_added", columnDefinition = "timestamp default current_timestamp", updatable = false, insertable = false)
    private
    Date dateAdded;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private
    ProfileTemplate prof;

    @Column(nullable = false, name = "date_used", columnDefinition = "timestamp", updatable =
            false, insertable = false)
    private
    Date dateUsed;

    @Column(name = "eid", columnDefinition = "TEXT")
    private
    String usedByEID;


    // Subscription address
    @Column(name = "msisdn", columnDefinition = "TEXT")
    private
    String msisdn;

    @Column(name = "imsi", columnDefinition = "TEXT")
    private
    String imsi;


    // The data, mapping between keys and replacement data
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dp_profiles_data_elements")
    @MapKeyColumn(name = "key", columnDefinition = "TEXT")
    @Column(name = "data")
    private
    Map<String, byte[]> profileData;



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

    public ProfileTemplate getProf() {
        return prof;
    }

    public void setProf(ProfileTemplate prof) {
        this.prof = prof;
    }

    public Date getDateUsed() {
        return dateUsed;
    }

    public void setDateUsed(Date dateUsed) {
        this.dateUsed = dateUsed;
    }

    public String getUsedByEID() {
        return usedByEID;
    }

    public void setUsedByEID(String usedByEID) {
        this.usedByEID = usedByEID;
    }

    public Map<String, byte[]> getProfileData() {
        return profileData;
    }

    public void setProfileData(Map<String, byte[]> profileData) {
        this.profileData = profileData;
    }

    public void markUsed(String eid) {
        setUsedByEID(eid);
        setDateUsed(Calendar.getInstance().getTime());
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }


}
