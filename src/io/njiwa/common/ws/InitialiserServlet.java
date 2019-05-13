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

package io.njiwa.common.ws;

import io.njiwa.common.ECKeyAgreementEG;
import io.njiwa.common.Utils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Created by bagyenda on 22/11/2016.
 */
public class InitialiserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    private void outputKeyCerts() throws  Exception {


        KeyStore ciKeyStore = Utils.loadKeyStore("/tmp/ci.jks", "test1234", false);

        // Get CI Private key and cert
        PrivateKey ciPkey = (PrivateKey) ciKeyStore.getKey("ci", "test1234".toCharArray());
        X509Certificate ciCert = (X509Certificate)ciKeyStore.getCertificate("ci");
        byte[] sig = ECKeyAgreementEG.makeCertSigningData(ciCert,
                ECKeyAgreementEG.CI_DEFAULT_DISCRETIONARY_DATA,

                (byte)0,"433322233334444",ECKeyAgreementEG.DST_VERIFY_KEY_TYPE);


        FileOutputStream f = new FileOutputStream("/tmp/ci.cer");
        Utils.DGI.append(f,0x7f21,sig);
       // f.write(os.toByteArray());
        f.close();

        // Get EUM
        X509Certificate certificate = (X509Certificate) Utils.getKeyStore().getCertificate("eum-ec");
        // Now write to file
        sig = ECKeyAgreementEG.makeCertSigningData(certificate, ECKeyAgreementEG.EUM_DEFAULT_DISCRETIONARY_DATA,
                (byte)0,
                "000000",ECKeyAgreementEG.DST_VERIFY_KEY_TYPE);

        // Write to file
        f = new FileOutputStream("/tmp/eum.cer");
        Utils.DGI.append(f,0x7f21,sig);
        f.close();
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

           // outputKeyCerts();
        } catch (Exception ex) {
            Utils.lg.error("Failed to initialise key store: " + ex.getMessage());
        }

    }
}
