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

package io.njiwa.dp.ws.types;

import io.njiwa.common.ws.types.BaseResponseType;
import io.njiwa.sr.ws.types.Eis;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by bagyenda on 05/04/2017.
 */
@XmlRootElement
public class GetEISResponse extends BaseResponseType {
    @XmlElement(name = "Eis")
    public Eis eis;

    public GetEISResponse(){}

    public GetEISResponse(Date startDate, Date endTime, long acceptablevalidity,
                          ExecutionStatus status, Eis eis)
    {
        super(startDate,endTime,acceptablevalidity,status);
        this.eis = eis;
    }
}
