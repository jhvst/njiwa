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

import io.njiwa.common.Utils;
import io.njiwa.common.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bagyenda on 15/06/2017.
 */
@Path("/auth")
public class Auth {

    @PersistenceContext
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/logout")
    public Boolean logout(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();

        try {
            session.invalidate();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/login")
    public Long login(@Context HttpServletRequest request,
                         @QueryParam("userid") String user,
                         @QueryParam("password") String passwd) {
        try {
            request.login(user, passwd);
            return getloggedOnUserId(request);
        } catch (Exception ex) {
            Utils.lg.error(String.format("Failed logon for user [%s]: %s", user, ex));
            return null;
        }
    }

    private Long getloggedOnUserId(HttpServletRequest request)
    {
        try {
            String user =  request.getUserPrincipal().getName();
            return User.find(em, user).getId();
        } catch (Exception ex) {

        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/check")
    public User checkLogon(@Context HttpServletRequest request)
    {
        try {
            Long xid = getloggedOnUserId(request);
            User user = em.find(User.class, xid);
            Utils.lg.info(String.format("Logged on  user  is [%s]", user));
            return user;
        } catch (Exception ex) {
            return null;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/roles")
    public Set<String> roles(@Context HttpServletRequest request)
    {

        try {
            return User.find(em, request.getUserPrincipal().getName()).userRoles();
        } catch (Exception ex){}
        return new HashSet<>();
    }
}
