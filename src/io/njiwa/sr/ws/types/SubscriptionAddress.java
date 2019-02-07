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

package io.njiwa.sr.ws.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by bagyenda on 18/10/2016.
 */
@XmlRootElement(namespace = "http://namespaces.gsma.org/esim-messaging/1",name = "SubscriptionAddressType")
public class SubscriptionAddress {

    @XmlElement(name = "Imsi")
    public String imsi;

    @XmlElement(name = "Msisdn")
    public String msisdn;

    public SubscriptionAddress() {}
    public SubscriptionAddress(String imsi, String msisdn) {
        this.imsi = imsi;
        this.msisdn = msisdn;
    }
}
