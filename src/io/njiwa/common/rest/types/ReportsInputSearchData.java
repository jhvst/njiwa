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

package io.njiwa.common.rest.types;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by bagyenda on 05/06/2017.
 */
public class ReportsInputSearchData {
    public boolean regex;
    public String value;

    public static ReportsInputSearchData fromString(String val) {
        try {
            return new ObjectMapper().readValue(val, ReportsInputSearchData.class);
        } catch (Exception ex) {
        }
        return null;
    }
}
