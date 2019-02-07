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

import io.njiwa.common.model.TransactionType;
import io.njiwa.common.ws.types.BaseResponseType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by bagyenda on 05/05/2017.
 */
@XmlRootElement
public class EnableProfileResponse extends BaseResponseType {
    @XmlElement(name = "EuiccResponseData")
    public String data;

    public EnableProfileResponse() {
    }

    public EnableProfileResponse(Date startDate, Date endDate, ExecutionStatus status, String data) {
        super(startDate, endDate, TransactionType.DEFAULT_VALIDITY_PERIOD, status);
        this.data = data;
    }

}
