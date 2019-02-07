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
import io.njiwa.common.Properties;
import io.njiwa.common.Utils;
import io.njiwa.sr.transports.Transport;

import javax.ejb.EJB;
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
  * @brief This is the  web servlet which receives  SMS delivery reports (DLR)
 *
  * @details The web servlet receives a [Kannel](http://www.kannel.org)-style HTTP call with the CGI parameters:
 * - data: This is the url-encoded DLRtext as received
 * - from: The original recipient
 * - dlr: The Kannel-style DLR code
 * - Our own parameters stuffed into the DLR URL, that help us track the source of the message sent, the ID, the part number
 *  (for concatenated messages), etc.
 * <p/>
 * It passes the DLR to the transports module for processing
 */
@WebServlet(name = "DlrReceiver",urlPatterns = {Properties.Constants.DLR_URI, "/dlrHandler"})
public class DlrReceiver extends HttpServlet {
     private static final long serialVersionUID = 1L;
   // Inject a persistence util.
    @EJB
   PersistenceUtility po;



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ctype = request.getContentType();
        String params = "";
        if (ctype != null && ctype.toLowerCase().contains("application/x-www-form-urlencoded")) {
            // Get he POST params from the content
            BufferedReader body = request.getReader();
            params = body.readLine(); // Single line?
        }
        handle_request(params, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        handle_request(request.getQueryString(), response);
    }

    private void handle_request(String params, HttpServletResponse response) throws ServletException, IOException {
        try {
            Map<String, Object> plist = Utils.CGIDecoder.parseCGIStr(params);
            String msisdn = new String((byte[]) plist.get("from"), "UTF-8");
            byte[] text = (byte[]) plist.get("data");
            String tag  = new String ((byte[]) plist.get("dlr_tag"), "UTF-8");
            String xsmsId = new String ((byte[]) plist.get("sms_id"), "UTF-8");
            String xtagId = new String ((byte[]) plist.get("dlr_id"), "UTF-8");
            String xdlrCode = new String ((byte[]) plist.get("dlr"), "UTF-8");
            String xpartNo = new String ((byte[]) plist.get("part_no"), "UTF-8");

            int dlrCode = Integer.parseInt(xdlrCode);
            long tagID = Long.parseLong(xtagId);
            long smsId = Long.parseLong(xsmsId);
            int partNo = Integer.parseInt(xpartNo);

            Transport.receiveDlr(po, msisdn, dlrCode, smsId, tag, tagID, partNo);
        } catch (Exception ex) {

        }
        PrintWriter os = response.getWriter();
        os.write("Ok");
        os.close();
    }
}
