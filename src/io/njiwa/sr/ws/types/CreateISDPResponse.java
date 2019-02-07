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

import io.njiwa.common.ws.types.BaseResponseType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by bagyenda on 05/05/2017.
 */
@XmlRootElement
public class CreateISDPResponse extends BaseResponseType {
    @XmlElement(name="Isd-p-aid")
    public String aid;
    @XmlElement(name = "EuiCCResponseData")
    public String data;
    public CreateISDPResponse() {}

    public CreateISDPResponse(Date startDate, Date endTime, long acceptablevalidity, ExecutionStatus status, String
            data, String aid)
    {
        super(startDate,endTime,acceptablevalidity,status);
        this.data = data;
        this.aid = aid;
    }
}
