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

package io.njiwa.dp.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by bagyenda on 06/04/2017.
 */
@Entity
@Table(name = "sm_dp_transaction_responses",
indexes = {
        @Index(columnList = "tr_id",name = "sm_dp_trans_resp_idx1")
})
@SequenceGenerator(name="sm_dp_trans_resp",sequenceName = "sm_dp_transaction_responses_seq")
public class SmDpTransactionResponse {
    @Id
    @Column(name="id",unique = true,nullable = false,updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sm_dp_trans_resp")
    private
    Long id;

    @Column(nullable = false, name = "date_added", columnDefinition = "timestamp default current_timestamp", updatable = false, insertable = false)
    private
    Date dateAdded;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    private
    SmDpTransaction tr;

    @Column(nullable = false)
    private
    String operationType;

    @Column(nullable = false)
    private
    Boolean success;

    @Column
    private
    String response;


    public SmDpTransactionResponse() {}

    public SmDpTransactionResponse(SmDpTransaction tr, String operationType, String response, boolean success) {
        setTr(tr);
        setResponse(response);
        setSuccess(success);
        setOperationType(operationType);
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public SmDpTransaction getTr() {
        return tr;
    }

    public void setTr(SmDpTransaction tr) {
        this.tr = tr;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
