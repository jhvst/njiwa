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

import io.njiwa.common.model.TransactionType;
import io.njiwa.common.ws.types.BaseResponseType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by bagyenda on 06/04/2017.
 */
@XmlRootElement
public class DownloadProfileResponse extends BaseResponseType {
    @XmlElement(name="Iccid")
    public String iccid;

    public DownloadProfileResponse() {}
    public DownloadProfileResponse(Date startDate, Date endDate, BaseResponseType.ExecutionStatus
            status, String iccid)
    {
        super(startDate,endDate, TransactionType.DEFAULT_VALIDITY_PERIOD,status);
        this.iccid = iccid;
    }
}
