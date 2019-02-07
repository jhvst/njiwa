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

package io.njiwa.common.rest.types;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by bagyenda on 05/06/2017.
 */
public class ReportsInputOrderData {
    public Order[] order;

    public ReportsInputOrderData(String val) {
        try {
            order = new ObjectMapper().readValue(val, Order[].class);
        } catch (Exception ex) {
        }
    }

    public ReportsInputOrderData(Order[] order) {
        this.order = order;
    }

    public int getLength() {
        return order == null ? 0 : order.length;
    }

    public static class Order {
        public int column;
        public String dir;

        public Order() {
        }

        public Order(int col, String dir) {
            this.column = col;
            this.dir = dir;
        }

        public String makeOrderClause(ReportsInputColumnsData columns) {
            ReportsInputColumnsData.Column c;
            if (column < 0 || column >= columns.getLength() ||
                    !(c = columns.columns[column]).orderable ||
                    c.data == null)
                return null;
            return String.format("%s %s", c.data, dir != null ? dir : "ASC");
        }
    }
}
