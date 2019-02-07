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

package io.njiwa.common.ws;

import io.njiwa.common.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by bagyenda on 22/11/2016.
 */
public class InitialiserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static {
        // Initialise Resteasy

        try {
        /*    ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
            RegisterBuiltin.register(instance);
            instance.registerProvider(ResteasyJacksonProvider.class); */
        } catch (Exception ex) {
            Utils.lg.warn("Error loading resteasy jackson provider: " + ex.getMessage());
        }

    }

    public void init(ServletConfig config) throws ServletException {
        // Get keystore param

        String keystoreFile = config.getInitParameter("keyfile");
        String keystorePass = config.getInitParameter("keyfilepassword");
        String privkeyalias = config.getInitParameter("privatekeyalias");
        String privkeypasswd = config.getInitParameter("privatekeypassword");
        String keyfile = config.getServletContext().getRealPath("/WEB-INF/" + keystoreFile);

        Utils.setPrivateKeyAliasAndPassword(privkeyalias, privkeypasswd);

        // Set the trust store and key store
        // XXX for now we just use the same file... We shouldn't do this really, but...
        // http://stackoverflow.com/questions/6340918/trust-store-vs-key-store-creating-with-keytool/6341566#6341566
        System.setProperty("javax.net.ssl.keyStore", keyfile);
        System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
        System.setProperty("javax.net.ssl.trustStore", keyfile);
        System.setProperty("javax.net.ssl.trustStorePassword", keystorePass);

        try {
            Utils.loadKeyStore(keyfile, keystorePass);
            Utils.lg.info("Initialised keystore and trust store locations");
        } catch (Exception ex) {
            Utils.lg.error("Failed to initialise key store: " + ex.getMessage());
        }

    }
}
