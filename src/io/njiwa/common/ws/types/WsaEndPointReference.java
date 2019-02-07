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

package io.njiwa.common.ws.types;


import io.njiwa.common.model.RpaEntity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by bagyenda on 04/05/2016.
 */

// Represents an Endpoint as per http://www.w3.org/TR/ws-addr-core/
@XmlRootElement(namespace = "http://www.w3.org/2007/05/addressing/metadata")
public class WsaEndPointReference implements RpsElement {

    @XmlElement(name = "Address",nillable = false)
    public
    String address;

    @XmlElement(name="ReferenceParameters",nillable = true)
    public String params;

    @XmlElement(name="MetaData",nillable = true)
    public String metadata;

    public String makeAddress()
    {
        String xurl = address;
        if (params != null && params.length() > 0)
            xurl += "?" + params;
        return xurl;
    }

    @XmlTransient
    public RpaEntity rpa;

    public WsaEndPointReference() {}

    public WsaEndPointReference(String address)
    {
        this.address = address;
    }

    public WsaEndPointReference(String address, RpaEntity rpa)
    {
        this.address = address;
        this.rpa = rpa;
    }

    public WsaEndPointReference(RpaEntity rpa, String interf)
    {
        this(rpa.urlForInterface(interf));
        this.rpa = rpa;
    }

}
