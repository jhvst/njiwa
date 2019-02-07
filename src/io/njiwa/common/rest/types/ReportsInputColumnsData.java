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
import io.njiwa.common.Utils;

import java.lang.reflect.Method;

/**
 * Created by bagyenda on 05/06/2017.
 */
public class ReportsInputColumnsData {
    public Column[] columns;

    // So we can de-serialise
    public ReportsInputColumnsData(String val) {
        try {
            columns = new ObjectMapper().readValue(val, Column[].class);
        } catch (Exception ex) {
        }
    }

    public ReportsInputColumnsData(Column[] cols) {
        columns = cols;
    }

    public int getLength() {
        return columns == null ? 0 : columns.length;
    }

    // Represents a column list as sent in a datatables request (JSON encoded as list of references)
    public static class Column {
        public String data;
        public String name;
        public boolean searchable;
        public boolean orderable;
        public Column.Search search;

        public Column() {
        }

        public Column(String data, boolean searchable, boolean orderable) {
            this.data = data;
            this.searchable = searchable;
            this.orderable = orderable;
        }

        public Column(String data, Column.Search search, boolean orderable) {
            this.data = data;
            this.searchable = search != null;
            this.orderable = orderable;
            this.search = search;
        }

        public Utils.Pair<String, String> getSearchClause(Class cls) {
            if (!searchable || data == null || search == null || search.value == null)
                return null;

            try {
                // Determine type
                Method m = cls.getMethod("get" + ReportsData.methodNameFromAttribute(data));
                Object retType = m.getReturnType();
                String op;
                String val = search.value;
                if (retType.equals(String.class)) {
                    op = "LIKE";
                    val = "%" + search.value + "%";
                } else
                    op = "=";
                return new Utils.Pair<>(op, val);
            } catch (Exception ex) {
                Utils.lg.error("Failed to create search clause for column [" + data + "]: " + ex.getMessage());
            }
            return null;
        }

        public static class Search {
            public String value;
            public boolean regex;

            public Search() {
            }

            public Search(String value, boolean regex) {
                this.value = value;
                this.regex = regex;
            }
        }
    }
}
