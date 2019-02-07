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
import io.njiwa.sr.model.Eis;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by bagyenda on 05/06/2017.
 */
public class ReportsData {
    public int draw;
    public long recordsTotal;
    public long recordsFiltered;
    public List<Map<String, Object>> data;

    public ReportsData(int draw, long recordsFiltered, long recordsTotal) {
        this.data = new ArrayList<>();
        this.draw = draw;
        this.recordsFiltered = recordsFiltered;
        this.recordsTotal = recordsTotal;
    }

    /**
     * @brief because we use CamelCase...
     * @param attr
     * @return
     */
    public static String methodNameFromAttribute(String attr) {
        return attr.substring(0, 1).toUpperCase() + attr.substring(1);
    }

    public static Set<String> getManagedClassAttributes(EntityManager em, Class cls) {
        Metamodel m = em.getMetamodel();
        ManagedType t = m.managedType(cls);
        Set<Attribute> alist = t.getAttributes();
        return alist.stream().map(s -> s.getName()).collect(Collectors.toSet());
    }

    public static <T> ReportsData doQuery(EntityManager em,
                                          Class<T> cls, ReportsInputColumnsData columns,
                                          int draw,
                                          ReportsInputOrderData order, int start, int length,
                                          Set<String> allowedOutputFields) {
        // Get count
        long count;
        try {
            count = em.createQuery("SELECT count(*) FROM " + cls.getName(), Long.class).getSingleResult();
        } catch (Exception ex) {
            count = 0;
        }
        ReportsData resp = new ReportsData(draw, 0, count);
        try {
            List<Eis> l = makeQuery(em, Eis.class, columns, order, start, length, allowedOutputFields);
            resp.addData(l, allowedOutputFields);
            resp.recordsFiltered = l.size(); // Record number of records
        } catch (Exception ex) {
            Utils.lg.error("Failed to make Query in Eis Reports module: " + ex.getMessage());
        }
        return resp;
    }

    public static <T> List<T> makeQuery(EntityManager em,
                                        Class<T> cls, ReportsInputColumnsData columns,
                                        ReportsInputOrderData order, int start, int length,
                                        Set<String> allowedOutputFields
    ) throws Exception {
        StringBuilder sql = new StringBuilder("from " + cls.getName());
        StringBuilder where = new StringBuilder("");
        String sep = " ";
        int i = 0;
        List<String> params = new ArrayList<>();
        Utils.Pair<String, String> sclause;
        // Add WHERE clause
        for (ReportsInputColumnsData.Column c : columns.columns)
            if (!allowedOutputFields.contains(c.data)) // Ensure we can't use it in order of search clause
                c.orderable = false;
            else if ((sclause = c.getSearchClause(cls)) != null) { // Filter them
                String op = sclause.k;
                String value = sclause.l;
                params.add(value);
                where.append(sep).append(c.data).append(" ").append(op).append(" ").append(String.format(":i%d", i));
                sep = " AND ";
                i++;
            }

        String xwhere = where.toString();
        if (xwhere.length() > 0)
            sql.append(" WHERE ");
        sql.append(xwhere);
        // Now get the orderBy
        if (order != null && order.getLength() > 0 && order.order.length > 0) {
            sql.append(" ORDER BY ");
            sep = "";
            String oclause;
            for (ReportsInputOrderData.Order o : order.order)
                if ((oclause = o.makeOrderClause(columns)) != null) {
                    sql.append(sep).append(oclause);
                    sep = " , ";
                }
        }
        String xsql = sql.toString();
        TypedQuery<T> t = em.createQuery(xsql, cls);
        i = 0;
        for (String param : params) {
            t.setParameter(String.format("i%d", i), param);
            i++;
        }
        t.setFirstResult(start);
        t.setMaxResults(length);
        return t.getResultList();
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception ex) {
        }
        return null;
    }

    public <T> void addData(List<T> l, Set<String> allowedFields) {
        try {
            for (T o : l)
                addData(o, allowedFields);
        } catch (Exception ex) {
        }
    }

    private Object cleanValue(Object x) {
        // Some basic cleanups, right?
        if (x instanceof Date) {
            return x.toString();
        }
        return x;
    }

    public void addData(Object obj, Set<String> allowedFields) {
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                try {
                    Object id = obj.getClass().getMethod("getId").invoke(obj);
                    // Put in the IDs
                    put("DT_RowId", id);
                    put("DT_RowData", new HashMap<String, Object>() {
                        {
                            put("_id", id);
                        }
                    });
                } catch (Exception ex) {
                    Utils.lg.error("Failed to add field [id]: " + ex.getMessage());
                }
            }
        };

        for (String prop : allowedFields)
            try {
                Method m = obj.getClass().getMethod("get" + methodNameFromAttribute(prop));
                Object x = m.invoke(obj);
                map.put(prop, cleanValue(x));
            } catch (Exception ex) {
                Utils.lg.error("Failed to add field [" + prop + "]: " + ex.getMessage());
            }

        data.add(map);
    }
}
