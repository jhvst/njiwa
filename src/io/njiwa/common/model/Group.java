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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
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
    @ManyToMany(mappedBy = "groups",cascade = CascadeType.DETACH)
    private
    Set<User> users;

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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
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

    @PreRemove
    private void removeGroupsFromUsers() {
        Set<User> ulist = getUsers();
        try {
            for (User u : ulist)
                u.getGroups().remove(this);
        } catch (Exception ex) {
        }
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
