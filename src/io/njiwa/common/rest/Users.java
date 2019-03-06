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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.njiwa.common.PersistenceUtility;
import io.njiwa.common.model.Group;
import io.njiwa.common.rest.types.RestResponse;
import io.njiwa.common.rest.types.Roles;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.Condition;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.IdentityQueryBuilder;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by bagyenda on 29/05/2017.
 */
@Path("/operations/users")
public class Users {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Identity identity;

    @Inject
    private IdentityManager identityManager;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public Response get(@PathParam("id") Long id) {
        User u = getUserById(id);
        UserResponse r = new UserResponse(em,u);

        return Response.ok(io.njiwa.common.rest.Utils.buildJSON(r)).build();
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public Response delete(@PathParam("id") Long id) {

        // Search for it
        RestResponse r;
        try {
            org.picketlink.idm.model.basic.User u = getUserById(id);
            String xid = u.getId();
            // Get logged on user
            org.picketlink.idm.model.basic.User currentUser = (org.picketlink.idm.model.basic.User) identity.getAccount();
            if (currentUser.getId().equals(xid))
                r = new RestResponse(RestResponse.Status.Failed, "Cannot remove yourself!");
            else {
                identityManager.remove(u);
                r = new RestResponse(RestResponse.Status.Success, true);
            }
        } catch (Exception ex) {
            r = new RestResponse(RestResponse.Status.Failed, false);
            r.errors.add(ex.getMessage());
        }
        return Response.ok(io.njiwa.common.rest.Utils.buildJSON(r)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin})
    @RequestScoped
    public Response all() {
        IdentityQueryBuilder qb = identityManager.getQueryBuilder();
        IdentityQuery<User> q = qb.createIdentityQuery(org.picketlink.idm.model.basic.User.class);
        List<User> rl = q.getResultList();
        UserListResponse r = new UserListResponse(em,rl);

        return Response.ok(io.njiwa.common.rest.Utils.buildJSON(r)).build();
    }

    private org.picketlink.idm.model.basic.User getUserById(long id) {
        String xid = Long.toString(id);
        try {
            IdentityQueryBuilder qb = identityManager.getQueryBuilder();
            Condition c = qb.equal(org.picketlink.idm.model.basic.User.ID, xid);
            IdentityQuery<org.picketlink.idm.model.basic.User> q = qb.createIdentityQuery(org.picketlink.idm.model.basic.User.class).where(c);
            return q.getResultList().get(0);
        } catch (Exception ex) {
        }
        return null;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    @RolesAllowed({Roles.SMSRAdmin, Roles.SMDPAdmin, Roles.SYSADMIN})
    public Response update(final Long userId,
                           @HeaderParam("FIRSTNAME") final String firstName,
                           @HeaderParam("LASTNAME") final String lastName,
                           @HeaderParam("EMAIL") final String email,
                           @HeaderParam("PASSWORD") final String password,
                           @HeaderParam("XGroups") final String grps) {

        RestResponse r;
        // Get logged on user
        // org.picketlink.idm.model.basic.User currentUser = (org.picketlink.idm.model.basic.User) identity.getAccount();
        try {
            org.picketlink.idm.model.basic.User xuser = getUserById(userId);
            String[] groups = grps == null ? null : new ObjectMapper().readValue(grps, String[].class);
            if (password != null && !password.isEmpty())
                identityManager.updateCredential(xuser, new Password(password));
            if (groups != null) {
                Group.removeAllUserGroup(em, xuser.getLoginName());
                for (String gs : groups) {
                    Group g = Group.getByName(em, gs);
                    if (g != null)
                        g.assignUser(xuser.getLoginName());
                }
                if (firstName != null)
                    xuser.setFirstName(firstName);
                if (lastName != null)
                    xuser.setLastName(lastName);
                if (email != null)
                    xuser.setEmail(email);
                identityManager.update(xuser);
            }
            r = new RestResponse(RestResponse.Status.Success, "updated");

        } catch (Exception ex) {
            r = new RestResponse(RestResponse.Status.Failed, ex.getMessage());
        }
        return Response.ok(io.njiwa.common.rest.Utils.buildJSON(r)).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/password")
    @RolesAllowed({Roles.ALLOWALL})
    public Response update(@HeaderParam("PASSWORD") final String password) {
        RestResponse r;
        org.picketlink.idm.model.basic.User currentUser = (org.picketlink.idm.model.basic.User) identity.getAccount();
        if (password != null && !password.isEmpty()) {
            identityManager.updateCredential(currentUser, new Password(password));
            r = new RestResponse(RestResponse.Status.Success, "updated");
        } else
            r = new RestResponse(RestResponse.Status.Failed, "Empty password");
        return Response.ok(io.njiwa.common.rest.Utils.buildJSON(r)).build();
    }

    class UserResponse extends RestResponse {
        public User response;
        public List<Group> groups;

        public UserResponse() {
        }

        public UserResponse(EntityManager em, User u) {
            this.status = u == null ? Status.Failed : Status.Success;
            this.response = u;
            this.groups = u == null ? null : Group.userGroups(em,u.getLoginName());
        }
    }

    class UserListResponse extends  RestResponse {
        class UserDetail {
            public User user;
            public List<Group> groups;
        }

        public UserDetail[] response;
        public UserListResponse() {
        }

        public UserListResponse(EntityManager em, List<User> ulist) {
            int n = ulist == null ? 0 : ulist.size();
            this.status = Status.Success;
            this.response = new UserDetail[n];
            for (int i = 0; i<n; i++) {
                this.response[i].user = ulist.get(i);
                this.response[i].groups = Group.userGroups(em,ulist.get(i).getLoginName());
            }

        }
    }
}
