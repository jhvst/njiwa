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

import java.util.Collection;

/**
 * Created by bagyenda on 06/06/2017.
 */
public class Roles {
    public static final String SMSRAdmin = "SMSR-Admin";
    public static final String SMDPAdmin = "SMDP-Admin";
    public static final String USER = "User";
    public static final String NONE = "None";
    public static final String REPORTS = "Reports";

    public static final String[] ALL_ROLES = {
            SMSRAdmin,SMDPAdmin,USER,NONE,REPORTS
    };

    public static boolean isAdmin(String role)
    {
        return role.equalsIgnoreCase(SMDPAdmin) || role.equalsIgnoreCase(SMSRAdmin);
    }

    public static boolean isAdmin(Collection<String> roles)
    {
        try {
            for (String s: roles)
                if (isAdmin(s))
                    return true;
        } catch (Exception ex) {}
        return false;
    }
}
