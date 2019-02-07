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

package io.njiwa.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.njiwa.common.Utils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.xml.bind.DatatypeConverter;
import java.util.*;

/**
 * Created by bagyenda on 29/05/2017.
 */
@Entity
@Table(name = "ui_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"},
                name = "user_idx1")
})
@SequenceGenerator(name = "users", sequenceName = "users_seq", allocationSize = 1)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@DynamicUpdate
@DynamicInsert
public class User {
    @javax.persistence.Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users")
    private
    Long Id;
    @Column(nullable = false, columnDefinition = "TEXT NOT NULL")
    private
    String username;
    @Column
    private
    String fullname;
    @ManyToMany(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    @JoinTable(name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"), uniqueConstraints =
    @UniqueConstraint(name = "user_grp_idx1", columnNames = {"user_id", "group_id"}))
    private
    Set<Group> groups;

    @Column(nullable = false, columnDefinition = "TEXT NOT NULL DEFAULT ''")
    private String passwdHash; // base64 SHA-256

    @Column(nullable = false, columnDefinition = "TEXT NOT NULL DEFAULT ''", name = "email_address")
    private
    String email;

    @Transient
    private
    String plainPasswd; // Used for setting the password

    public User() {
    }

       public User(String username, String fullname, Set<Group> groups) {
        setFullname(fullname);
        setUsername(username);
        setGroups(groups);
    }

    public User(String username, String fullname, Group[] groups, String plainPasswd) {
        setFullname(fullname);
        setUsername(username);
        setGroups(new HashSet<>(Arrays.asList(groups)));
        setPlainPasswd(plainPasswd);
    }


    public static User find(EntityManager em, String user)
    {
        try {
            return em.createQuery("from User  where username = :u",User.class)
                    .setParameter("u",user)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    // Base64 of SHA-256 of the password
    private static String hash(String passwd) {
        try {
            byte[] digest = Utils.getMessageDigest("SHA-256", passwd.getBytes());
            return DatatypeConverter.printBase64Binary(digest);
        }  catch (Exception ex) {
            Utils.lg.error("Failed to hash password: " + ex.getMessage());
        }
        return null;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @JsonIgnore
    public String getPasswdHash() {
        return passwdHash;
    }

    @JsonIgnore
    public void setPasswdHash(String salted_passwd_hash) {
        this.passwdHash = salted_passwd_hash;
    }


    public String getPlainPasswd() {
        return plainPasswd;
    }

    public void setPlainPasswd(String plainPasswd) {
        this.plainPasswd = plainPasswd; // Store it temporarily
        if (this.plainPasswd != null) {
            String cpass = hash(this.plainPasswd);
            setPasswdHash(cpass);
        }
    }


    public boolean isExpectedPassword(String passwd) {
        String pwdHash = hash(passwd);

        String ahash = getPasswdHash();
        return pwdHash.equals(ahash);
    }

    public Set<String> userRoles()
    {
        Set<String> set = new HashSet<>();
        Set<Group> s = getGroups();
        for (Group g: s)
            try {
                set.addAll(g.getRoles());
            } catch (Exception ex) {}
        return set;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString()
    {
        return getUsername();
    }
}
