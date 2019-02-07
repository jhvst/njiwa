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

package io.njiwa.common.rest.faulthandlers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by bagyenda on 08/06/2017.
 */
@Provider
public class NullPointerMapper implements ExceptionMapper<NullPointerException> {

    public Response toResponse(NullPointerException e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}
