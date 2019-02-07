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

package io.njiwa.sr.ws.types;

import io.njiwa.common.ws.types.BaseResponseType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by bagyenda on 09/05/2017.
 */
@XmlRootElement
public class AuthenticateSMSRResponse extends BaseResponseType {
    @XmlElement(name = "RandomChallenge")
    public String randomChallenge;

    public AuthenticateSMSRResponse() {}

    public AuthenticateSMSRResponse(Date startDate, Date endTime, long acceptablevalidity, ExecutionStatus status,  String randomChallenge)
    {
        super(startDate,endTime,acceptablevalidity,status);
        this.randomChallenge = randomChallenge;
    }
}
