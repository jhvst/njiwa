package io.njiwa.common.rest.auth;

import io.njiwa.common.PersistenceUtility;
import io.njiwa.common.Utils;
import io.njiwa.common.model.Group;
import io.njiwa.common.rest.annotations.RestRoles;
import io.njiwa.common.rest.types.Roles;
import org.apache.deltaspike.security.api.authorization.Secures;
import org.picketlink.Identity;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;

/*
 * This manages the picketlink RestRoles annotation where used
 */
@ApplicationScoped
public class Authoriser {
    @Inject
    private PersistenceUtility po;

    @Secures
    @RestRoles
    public boolean hasRoles(InvocationContext invocationContext, Identity identity, IdentityManager identityManager, RelationshipManager relationshipManager) {

        if (identity != null && identity.isLoggedIn() && identity.getAccount() != null) {
            User u = BasicModel.getUser(identityManager, ((User) identity.getAccount()).getLoginName());
            String uid = u.getLoginName();

            Utils.lg.info("Checking role for user [" + uid + "]");
            if (invocationContext.getMethod().isAnnotationPresent(RestRoles.class)) {
                RestRoles ra = invocationContext.getMethod().getAnnotation(RestRoles.class);
                String[] xrl = ra.value();
                for (String xr : xrl) {
                    if (xr.equals(Roles.ALLOWALL))
                        return  true; // ANY is allowed for all users
                    boolean res = po.doTransaction((po, em) -> Group.hasRole(em, uid, xr));
                    if (res) {
                        Utils.lg.info("User [" + uid + "] has role [" + xr + "]");
                        return true;
                    }
                }

            }

            Utils.lg.info("User [" + uid + "] failed ");
        } else
            Utils.lg.info("Not logged on ");

        return false;
    }
}
