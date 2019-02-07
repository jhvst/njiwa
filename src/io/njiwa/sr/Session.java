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

import io.njiwa.common.Utils;
import io.njiwa.common.Properties;
import io.njiwa.sr.model.Eis;
import io.njiwa.sr.transports.Transport;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;


/** @brief This class represents the basic Gateway transport session.
*
* @details
*
* A session consists of:
* - The target or sending SIM
* - The last RFM counter
* - Any errors and/or warnings so far seen
* - The current transport type (SMS, BIP)
*/
// Represents the session, either S@T or OTA or BIP
public class Session {

   public EntityManager entityManager = null;
    public Transport.TransportType transportType = null;

   protected String msisdn = "";
   protected Eis euicc = null;

   public Long last_rfm_counter;
   public Session(EntityManager entityManager)
   {
       this.entityManager = entityManager;
   }

   public Session() {}

    public Session(EntityManager entityManager, Eis eis)
    {
        euicc = eis;
        msisdn = eis.activeMISDN();
        this.entityManager = entityManager;
    }

   public Eis getEuicc() {
       if (euicc == null)
       try {
           euicc = Eis.findByMsisdn(entityManager,msisdn); // Shouldn't we lock?
       } catch (Exception ex) {
           euicc = null;
       }
       return euicc;
   }


   protected void reInit() throws Exception {} // Called when msisdn is changed.


   public void setEuicc(Eis sim) throws Exception {
       // Find active profile, get msisdn
       try {
           msisdn = sim.activeMISDN();
       } catch (Exception ex) {
           this.msisdn = null;
       }
       euicc = sim;
       reInit();
   }

   public String getMsisdn() {
       return msisdn != null ? msisdn : euicc.activeMISDN();
   }

   protected List<String> errors = new ArrayList<String>();
   protected List<String> warnings = new ArrayList<String>();

   public void warn(String msg, Object... args) {
       String x = String.format(msg, args);
       warnings.add(x);
   }

   public void error(String msg, Object... args) {

       String x = String.format(msg, args);
       errors.add(x);
   }

   public void clearErrors()
   {
       errors.clear();
   }

   /**
    * @brief Sometimes we need to overwrite the errors. Say if we compile a S\@TML deck twice
    * @param errors
    */
   public void setErrors(List<String> errors)
   {
       this.errors = errors; // Tricky!
   }
   public final List<String> getErrors()
   {
       return errors;
   }

    public String getBrowserProfileStr() {
        return Properties.Constants.serverName;
    }

    public List<Utils.HTTPCookie> getCookies(String host, String url) {
        return new ArrayList<Utils.HTTPCookie>();
    }

    public void saveCookies(List<Utils.HTTPCookie> cl) {

    }
}
