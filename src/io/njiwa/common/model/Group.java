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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bagyenda on 29/05/2017.
 */
@Entity
@Table(name = "ui_groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_name"},
                name = "groups_idx1")
})
@SequenceGenerator(name = "groups", sequenceName = "groups_seq", allocationSize = 1)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "users"}) // don't generate group users...
@DynamicUpdate
@DynamicInsert
public class Group {

    @javax.persistence.Id
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groups")
    private
    Long Id;
    @Column(name = "group_name", columnDefinition = "TEXT NOT NULL", nullable = false)
    private
    String name;
    @Column
    private
    String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "group_roles")
    @Column(name = "role_name", columnDefinition = "TEXT NOT NULL")
    private Set<String> roles;

    @ElementCollection
    @CollectionTable(name="auth_user_groups", joinColumns = @JoinColumn(name="group_id"),
    indexes = {
        @Index(columnList = "group_id", name = "group_users_idx1")
    })
    @Column(name="group_user")
    private
    Set<String> users;

    public Group() {
    }

    public Group(String name, String[] roles) {
        setName(name);
        setRoles(new HashSet<>(Arrays.asList(roles)));
    }

    public static Group getByName(EntityManager em, String group) {
        try {
            return em.createQuery("from Group where name = :g", Group.class)
                    .setParameter("g", group)
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

    public String getName() {
        return name;
    }

    public void setName(String group) {
        this.name = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public void assignUser(String u)
    {
        Set<String> l = getUsers();
        if (l == null) {
            l = new HashSet<>();
            setUsers(l);
        }
        l.add(u);
    }

    public static List<Group> userGroups(EntityManager em, String u)
    {
        return em.createQuery("from Group where :u MEMBER OF users", Group.class)
                .setParameter("u",u)
                .getResultList();
    }

    public static void removeAllUserGroup(EntityManager em, String userID) throws Exception {
        // Look for all groups that have this user, remove the user from them.

        List<Group> l = userGroups(em, userID);

        for (Group g : l) {
            Set<String> sl = g.getUsers();
            if (sl  != null)
                sl.remove(userID);
        }
    }

    public static Set<String> userRoles(EntityManager em, String u) throws Exception {
        Set<String> s = new HashSet<>();
        List<Group> l = userGroups(em,u);
        for (Group g: l) {
            Set<String> r = g.getRoles();
            s.addAll(r);
        }
        return s;
    }

    public static boolean hasRole(EntityManager em, String user, String role)
    {
        try {

            List<Group> gl = userGroups(em,user);
            for (Group g: gl) {
                Set<String> l = g.getRoles();
                if (l != null && l.contains(role))
                    return true;
            }

        } catch (Exception ex) {

        }
        return false;
    }

    @Override
    public boolean equals(Object x) {
        try {
            Group xg = (Group) x;
            return xg.getName().equals(getName());
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
