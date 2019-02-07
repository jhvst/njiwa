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
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import java.util.Date;
import java.util.List;

/**
 * Created by bagyenda on 20/04/2016.
 */
@Entity
@Table(name = "pol2rules",
         indexes = {
        @Index(columnList = "profile_id", name = "pol2rule_idx1"),

}
)
@SequenceGenerator(name = "pol2rule", sequenceName = "pol2rule_seq", allocationSize = 1)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer","profile"})
@DynamicUpdate
@DynamicInsert
public class Pol2Rule {

    @Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pol2rule")
    private
    Long Id;

    @Column(nullable = false, name = "date_added", columnDefinition = "timestamp default current_timestamp", updatable = false, insertable = false)
    private
    Date dateAdded;


    public Pol2Rule() {}
    public Pol2Rule(ProfileInfo p, Action a, Subject s, Qualification q) {
        setProfile(p);
        setAction(a);
        setSubject(s);
        setQualification(q);
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private
    ProfileInfo profile;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Subject subject;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Qualification qualification;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public ProfileInfo getProfile() {
        return profile;
    }

    public void setProfile(ProfileInfo profile) {
        this.profile = profile;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    @XmlEnum(String.class)
    public enum Action {
        ENABLE,DISABLE,DELETE
    }

    @XmlEnum(String.class)
    public enum Qualification {
       @XmlEnumValue("Not-Allowed") NotAllowed,
        @XmlEnumValue("Auto-Delete")  AutoDelete
    }

    @XmlEnum(String.class)
    public enum Subject {
        PROFILE
    }
    public static Qualification qualificationAction(List<Pol2Rule> l, Action action)
    {
        try {
            for (Pol2Rule p: l)
                if (p.action == action)
                    return p.qualification;
        } catch (Exception ex) {

        }
        return null;
    }
}
