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

package io.njiwa.sr;

import io.njiwa.common.PersistenceUtility;
import io.njiwa.common.Utils;
import io.njiwa.sr.ota.Ota;
import io.njiwa.sr.transports.Transport;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;


/**
 * @brief This is the main web servlet which receives MO SMS
 * @details The web servlet receives a [Kannel](http://www.kannel.org)-style HTTP call with the CGI parameters:
 * - text: This is the url-encoded SMS text as received
 * - udh: This is the user data header
 * - from: The sender
 * <p>
 * It passes the content to the OTA receiver processor and returns no content.
 */
@WebServlet(urlPatterns = {"/mo", "/moHandler"}, name = "MoHandler")
public class MoHandler extends HttpServlet {

    @Inject
    Ota ota;
    @EJB
    private PersistenceUtility po;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ctype = request.getContentType();
        String params = "";
        if (ctype != null && ctype.toLowerCase().contains("application/x-www-form-urlencoded")) {
            // Get he POST params from the content
            BufferedReader body = request.getReader();
            params = body.readLine(); // Single line?
        }
        try {
            handle_request(params, response);
        } catch (Exception ex) {
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        try {
            handle_request(request.getQueryString(), response);
        } catch (Exception ex) {
        }
    }

    private void handle_request(String params, HttpServletResponse response) throws Exception {
        Map<String, Object> plist;

        byte[] x;
        plist = Utils.CGIDecoder.parseCGIStr(params);
        x = (byte[]) plist.get("from");
        final String msisdn = new String(x, "UTF-8");

        final byte[] text = (byte[]) plist.get("text");
        final byte[] udh = (byte[]) plist.get("udh");


        final PrintWriter os = response.getWriter();

        po.doTransaction(new PersistenceUtility.Runner<Object>() {
            @Override
            public Object run(PersistenceUtility po, EntityManager em) throws Exception {
                Ota.receiveMO(text, Transport.TransportType.SMS, msisdn, udh, em);
                return null;
            }

            @Override
            public void cleanup(boolean success) {

            }
        });
        os.write("Ok");
        os.close();
    }


}

