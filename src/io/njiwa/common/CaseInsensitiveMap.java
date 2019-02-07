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

package io.njiwa.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @addtogroup g_utils
 * @{
 */
 /** @brief Implements a basic case-insensitive key-value store. Keys are case-insensitive strings
 */
public class CaseInsensitiveMap<V> extends ConcurrentHashMap<String,V> {
     private static final long serialVersionUID = 1L;
    @Override
    public V put(String key, V value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public V get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    public CaseInsensitiveMap(Map<String, V> map) {
        if (map != null)
            for (Entry<String, V> k : map.entrySet())
                put(k.getKey(), k.getValue());
    }

    public CaseInsensitiveMap() {
    }
}

/** @} */