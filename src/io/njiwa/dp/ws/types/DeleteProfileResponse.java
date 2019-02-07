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

package io.njiwa.dp.ws.types;

import io.njiwa.common.model.TransactionType;
import io.njiwa.common.ws.types.BaseResponseType;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by bagyenda on 27/04/2017.
 */
@XmlRootElement
public class DeleteProfileResponse extends BaseResponseType {
    public DeleteProfileResponse() {}

    public DeleteProfileResponse(Date startDate, Date endDate, ExecutionStatus status)
    {
        super(startDate, endDate, TransactionType.DEFAULT_VALIDITY_PERIOD, status);
    }
}
