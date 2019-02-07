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

package io.njiwa.common.rest.types;

import io.njiwa.common.model.RpaEntity;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;

/**
 * Created by bagyenda on 30/05/2017.
 */
public class RpaEntityForm {

    @FormParam("oid")
    @PartType("text/plain")
    private String oid;
    @FormParam("type")
    @PartType("text/plain")
    private RpaEntity.Type type;
    @FormParam("description")
    @PartType("text/plain")
    private String description;
    @FormParam("id")
    @PartType("text/plain")
    private Long id;
    @FormParam("es1url")
    @PartType("text/plain")
    private String es1Url;
    @FormParam("es2url")
    @PartType("text/plain")
    private String es2Url;
    @FormParam("es3url")
    @PartType("text/plain")
    private String es3Url;
    @FormParam("es4url")
    @PartType("text/plain")
    private String es4Url;
    @FormParam("es7url")
    @PartType("text/plain")
    private String es7Url;

    @FormParam("wsuserid")
    @PartType("text/plain")
    private String wsUserID;
    @FormParam("wspassword")
    @PartType("text/plain")
    private String wsPassword;
    @FormParam("local")
    @PartType("text/plain")
    private String local;
    @FormParam("authMethod")
    @PartType("text/plain")
    private RpaEntity.OutgoingAuthMethod outgoingAuthMethod;
    @FormParam("signature")
    @PartType("application/octet-stream")
    private byte[] signature;

    @FormParam("iin")
    @PartType("text/plain")
    private String iin;

    @FormParam("discretionaryData")
    private String discretionaryData;

    @FormParam("secureMessagingCertificate")
    @PartType("application/octet-stream")
    private byte[] secureMessagingCertificate; //!< The secure server  certificate in X.509 format, for secure messaging
    @FormParam("wsCertificate")
    @PartType("application/octet-stream")
    private byte[] wsCertificate; //!< The secure server  certificate in X.509 format, for web services calls
    @FormParam("wsPrivateKey")
    @PartType("application/octet-stream")
    private byte[] wsPrivateKey; // Only set for Local

    @FormParam("keyParamRef")
    @PartType("text/plain")
    private Integer ECCKeyParameterReference;


    @FormParam("allowedIPs")
    @PartType("text/plain")
    private
    String allowedIPs;

    @FormParam("deniedIPs")
    @PartType("text/plain")
    private
    String deniedIPs;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public RpaEntity.Type getType() {
        return type;
    }

    public void setType(RpaEntity.Type type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEs1Url() {
        return es1Url;
    }

    public void setEs1Url(String es1Url) {
        this.es1Url = es1Url;
    }

    public String getEs2Url() {
        return es2Url;
    }

    public void setEs2Url(String es2Url) {
        this.es2Url = es2Url;
    }

    public String getEs3Url() {
        return es3Url;
    }

    public void setEs3Url(String es3Url) {
        this.es3Url = es3Url;
    }

    public String getEs4Url() {
        return es4Url;
    }

    public void setEs4Url(String es4Url) {
        this.es4Url = es4Url;
    }

    public String getEs7Url() {
        return es7Url;
    }

    public void setEs7Url(String es7Url) {
        this.es7Url = es7Url;
    }

    public String getWsUserID() {
        return wsUserID;
    }

    public void setWsUserID(String wsUserID) {
        this.wsUserID = wsUserID;
    }

    public String getWsPassword() {
        return wsPassword;
    }

    public void setWsPassword(String wsPassword) {
        this.wsPassword = wsPassword;
    }

    public boolean getLocal() {
        return local != null;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public RpaEntity.OutgoingAuthMethod getOutgoingAuthMethod() {
        return outgoingAuthMethod;
    }

    public void setOutgoingAuthMethod(RpaEntity.OutgoingAuthMethod outgoingAuthMethod) {
        this.outgoingAuthMethod = outgoingAuthMethod;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getDiscretionaryData() {
        return discretionaryData;
    }

    public void setDiscretionaryData(String discretionaryData) {
        this.discretionaryData = discretionaryData;
    }

    public byte[] getSecureMessagingCertificate() {
        return secureMessagingCertificate;
    }

    public void setSecureMessagingCertificate(byte[] secureMessagingCertificate) {
        this.secureMessagingCertificate = secureMessagingCertificate;
    }

    public boolean hasSecureMessagingCertificate() {
        return secureMessagingCertificate != null && secureMessagingCertificate.length > 0;
    }


    public byte[] getWsCertificate() {
        return wsCertificate;
    }

    public void setWsCertificate(byte[] wsCertificate) {
        this.wsCertificate = wsCertificate;
    }

    public boolean hasWsCertificate() {
        return wsCertificate != null && wsCertificate.length > 0;
    }

    public byte[] getWsPrivateKey() {
        return wsPrivateKey;
    }

    public void setWsPrivateKey(byte[] wsPrivateKey) {
        this.wsPrivateKey = wsPrivateKey;
    }

    public boolean hasWsPrivateKey() {
        return wsPrivateKey != null && wsPrivateKey.length > 0;
    }

    public Integer getECCKeyParameterReference() {
        return ECCKeyParameterReference;
    }

    public void setECCKeyParameterReference(Integer ECCKeyParameterReference) {
        this.ECCKeyParameterReference = ECCKeyParameterReference;
    }

    public String[] getAllowedIPs() {
        return
                splitIPs(allowedIPs);
    }

    public void setAllowedIPs(String allowedIPs) {

        this.allowedIPs = allowedIPs;
    }

    private String[] splitIPs(String ips) {
        try {
            return ips.split("[,; \t]");
        } catch (Exception ex) {

        }
        return null;
    }

    public String[] getDeniedIPs() {

        return splitIPs(deniedIPs);
    }

    public void setDeniedIPs(String deniedIPs) {
        this.deniedIPs =
                deniedIPs;
    }
}
