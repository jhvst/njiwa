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
import io.njiwa.common.rest.annotations.RestRoles;
import io.njiwa.common.rest.types.Roles;
import io.njiwa.common.rest.types.RpaEntityForm;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by bagyenda on 30/05/2017.
 */

@Path("/operations/rpa")
public class RpaEntity {
    @Inject
    PersistenceUtility po;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/get/{id}")
    @RestRoles({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public io.njiwa.common.model.RpaEntity get(@PathParam("id") Long id) {


        io.njiwa.common.model.RpaEntity rpa = em.find(io.njiwa.common.model.RpaEntity.class, id);
        em.detach(rpa);
        rpa.setwSpassword(null);
        rpa.setOutgoingWSpassword(null);
        return rpa;

    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete/{id}")
    @RestRoles({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public Boolean delete(@PathParam("id") Long id) {

        Boolean res = po.doTransaction(new PersistenceUtility.Runner<Boolean>() {
            @Override
            public Boolean run(PersistenceUtility po, EntityManager em) throws Exception {
                io.njiwa.common.model.RpaEntity rpa = em.find(io.njiwa.common.model.RpaEntity.class, id);
                // Delete key store entries then delete it
                KeyStore ks = Utils.getKeyStore();
                String xs;
                if ((xs = rpa.getWskeyStoreAlias()) != null)
                    ks.deleteEntry(xs);
                if ((xs = rpa.getsMkeyStoreAlias()) != null)
                    ks.deleteEntry(xs);
                em.remove(rpa);
                return true;
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
    @RestRoles({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public List<io.njiwa.common.model.RpaEntity> all() {


        List<io.njiwa.common.model.RpaEntity> l = em.createQuery("from RpaEntity ", io.njiwa.common.model.RpaEntity.class)
                .getResultList();
        try {
            for (io.njiwa.common.model.RpaEntity rpa : l) {
                em.detach(rpa);
                rpa.setwSpassword(null);
                rpa.setOutgoingWSpassword(null);
            }
        } catch (Exception ex) {
        }
        return l;

    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/update")
    @RestRoles({Roles.SMSRAdmin, Roles.SMDPAdmin})
    public String update(@MultipartForm RpaEntityForm form) {

        return po.doTransaction((PersistenceUtility po, EntityManager em) -> {
            Long id = form.getId();
            io.njiwa.common.model.RpaEntity rpa;
            if (id != null) {
                rpa = em.find(io.njiwa.common.model.RpaEntity.class, id);
                if (form.getType() != null)
                    rpa.setType(form.getType());

                if (form.getECCKeyParameterReference() != null)
                    rpa.setSignatureKeyParameterReference((byte) (int) form.getECCKeyParameterReference());
                if (form.getDiscretionaryData() != null)
                    rpa.setDiscretionaryData(form.getDiscretionaryData() == null ? null :
                            Utils.HEX.h2b(form.getDiscretionaryData()));
            } else try {
                Integer paramRef = form.getECCKeyParameterReference();
                rpa = new io.njiwa.common.model.RpaEntity(form.getType(), null, null,
                        form.getOid(),
                        Utils.toBool(form.getLocal()),
                        Utils.HEX.h2b(form.getDiscretionaryData()),
                        paramRef == null ? 0 : (byte) (int) paramRef,
                        form.getSignature(),
                        null);
            } catch (Exception ex) {
                Utils.lg.error("Error creating RPA entity: ", ex);
                ex.printStackTrace();
                throw ex;
            }

            if (form.getSignature() != null)
                rpa.setSignature(form.getSignature());
            else if (rpa.getSignature() == null)
                rpa.setSignature(new byte[]{0, 0, 0}); // XXX we need to get this in some standard form, e.g.
            // from certificate. But, for now...
            // Update stuff
            if (form.getDescription() != null)
                rpa.setDescription(form.getDescription());

            if (form.getEs1Url() != null)
                rpa.setEs1URL(form.getEs1Url());
            if (form.getEs2Url() != null)
                rpa.setEs2URL(form.getEs2Url());
            if (form.getEs3Url() != null)
                rpa.setEs3URL(form.getEs3Url());
            if (form.getEs4Url() != null)
                rpa.setEs4URL(form.getEs4Url());
            if (form.getEs7Url() != null)
                rpa.setEs7URL(form.getEs7Url());
            if (form.getOutgoingAuthMethod() != null)
                rpa.setOutgoingAuthMethod(form.getOutgoingAuthMethod());
            if (form.getWsPassword() != null)
                rpa.setwSpassword(form.getWsPassword());
            if (form.getWsUserID() != null)
                rpa.setwSuserid(form.getWsUserID());

            rpa.setIslocal(form.getLocal());

            rpa.updateIPpermissions(form.getAllowedIPs(), form.getDeniedIPs());

            KeyStore ks = Utils.getKeyStore();
            // Handle certificates
            if (form.hasSecureMessagingCertificate()) {
                X509Certificate cert = Utils.certificateFromBytes(form.getSecureMessagingCertificate());
                if (cert == null)
                    return "ERROR: Failed to read secure messaging certificate data. Perhaps not X.509 encoded?";
                String alias;
                if ((alias = rpa.getsMkeyStoreAlias()) == null) {
                    alias = rpa.makeKeyStoreAlias("SM");
                    rpa.setsMkeyStoreAlias(alias);
                }
                ks.setCertificateEntry(alias, cert);
            }

            if (form.hasWsCertificate()) {
                X509Certificate cert = Utils.certificateFromBytes(form.getSecureMessagingCertificate());
                if (cert == null)
                    return "ERROR: Failed to read WS certificate data. Perhaps not X.509 encoded?";
                String alias;
                if ((alias = rpa.getWskeyStoreAlias()) == null) {
                    alias = rpa.makeKeyStoreAlias("WS");
                    rpa.setWskeyStoreAlias(alias);
                }
                String subject = cert.getSubjectDN().getName();
                rpa.setX509Subject(subject);
                // Look for private key data
                if (form.hasWsPrivateKey()) {
                    Key k = Utils.keyFromFile(form.getWsPrivateKey());
                    Utils.saveServerPrivateKey(alias, k, cert);
                } else
                    ks.setCertificateEntry(alias, cert);
            }

            if (id == null)
                try {
                    em.persist(rpa);
                    em.flush();
                } catch (Exception ex) {
                    Utils.lg.error("Error saving RPA entity: ", ex);
                    ex.printStackTrace();
                    throw ex;
                }
            return "Ok";
        });
    }

}
