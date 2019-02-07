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

package io.njiwa.common.rest;

import io.njiwa.common.PersistenceUtility;
import io.njiwa.common.Utils;
import io.njiwa.common.model.User;
import io.njiwa.common.rest.types.Roles;
import io.njiwa.common.model.Group;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bagyenda on 29/05/2017.
 */
@Path("/operations/users")
public class Users {
    @Inject
    PersistenceUtility po; // For saving objects

    @PersistenceContext
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public User get(@PathParam("id") Long id) {
        return em.find(User.class, id);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/search")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public List<User> search(@QueryParam("user") String user) {
        List<User> l = em.createQuery("from User where username like '%'||:u||'%'", User.class)
                .setParameter("u", user == null ? "" : user)
                .getResultList();
        for (User u : l)
            em.detach(u);
        return l;

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public Boolean delete(@PathParam("id") Long id) {

        Boolean res = po.doTransaction(new PersistenceUtility.Runner<Boolean>() {
            @Override
            public Boolean run(PersistenceUtility po, EntityManager em) throws Exception {
                User u = em.find(User.class, id);
                if (u != null)
                    em.remove(u);
                return u != null;
            }

            @Override
            public void cleanup(boolean success) {

            }
        });
        return Utils.toBool(res);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin})
    @RequestScoped
    public List<User> all() {
        return em.createQuery("from User", User.class)
                .getResultList();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public Boolean update(User user,
                          @Context HttpServletRequest request) {


        String xloggedUser = request.getUserPrincipal().getName();

        Boolean res = po.doTransaction(new PersistenceUtility.Runner<Boolean>() {
            @Override
            public Boolean run(PersistenceUtility po, EntityManager em) throws Exception {
                User loggedUser = User.find(em, xloggedUser);

                Long id = user.getId();
                User u;
                if (id == null) { // New user
                    u = new User();
                    u.setUsername(user.getUsername());
                    em.persist(u);
                } else {
                    u = em.find(User.class, id);

                    try {
                        Set<String> roles = u.userRoles();
                        if (Roles.isAdmin(roles) && loggedUser.getId() != id) {
                            Utils.lg.error(String.format("Attempt by user [%s] to update another admin user [%s] denied",
                                    loggedUser, u));
                            return false;
                        }
                    } catch (Exception ex) {

                    }
                }
                try {
                    // Update it
                    if (user.getFullname() != null)
                        u.setFullname(user.getFullname());
                    if (user.getPlainPasswd() != null) // XXX check also that this is tue user or an admin
                        u.setPlainPasswd(user.getPlainPasswd());
                    if (user.getEmail() != null)
                        u.setEmail(user.getEmail());

                    // Now massage groups
                    Set<Group> ug = new HashSet<>();
                    try {
                        for (Group grp : user.getGroups()) {
                            Group g = Group.getByName(em, grp.getName());
                            if (g != null)
                                ug.add(g);
                        }
                    } catch (Exception ex) {
                    }
                    u.setGroups(ug); // Re-set
                    return true;
                } catch (Exception ex) {
                    return false;
                }

            }

            @Override
            public void cleanup(boolean success) {

            }
        });

        return res == null ? false : res;
    }
}
