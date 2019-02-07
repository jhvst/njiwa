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

import io.njiwa.common.PersistenceUtility;
import io.njiwa.common.Utils;
import io.njiwa.common.model.Group;
import io.njiwa.common.rest.types.Roles;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by bagyenda on 29/05/2017.
 */
@Path("/operations/groups")
public class Groups {
    @Inject
    PersistenceUtility po;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    @RolesAllowed(Roles.USER)
    public Group get(@PathParam("id") Long id) {
        return em.find(Group.class, id);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    @RolesAllowed({Roles.SMDPAdmin,Roles.SMSRAdmin})
    public Boolean delete(@PathParam("id") long id) {

        Boolean res = po.doTransaction(new PersistenceUtility.Runner<Boolean>() {
            @Override
            public Boolean run(PersistenceUtility po, EntityManager em) throws Exception {
                Group g = em.find(Group.class, id);
                if (g != null)
                    em.remove(g);
                return g != null;
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
    @PermitAll
    public List<Group> all() {
        return em.createQuery("from Group", Group.class)
                .getResultList();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    @RolesAllowed({Roles.SMDPAdmin,Roles.SMSRAdmin})
    public Boolean update(Group group) {
        Boolean res = po.doTransaction(new PersistenceUtility.Runner<Boolean>() {
            @Override
            public Boolean run(PersistenceUtility po, EntityManager em) throws Exception {
                Long id = group.getId();
                Group g;
                if (id == null) { // New user
                    g = new Group();
                    g.setName(group.getName());
                    em.persist(g);
                } else
                    g = em.find(Group.class, id);
                try {
                    // Update it
                    if (group.getDescription() != null)
                        g.setDescription(group.getDescription());
                    if (group.getRoles() != null)
                        g.setRoles(group.getRoles());
                    // Leave users alone. Users are added to groups via the Users REST interface
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            }

            @Override
            public void cleanup(boolean success) {

            }
        });

        return Utils.toBool(res);

    }
}
