package io.njiwa.common.rest.auth;

import io.njiwa.common.PersistenceUtility;
import io.njiwa.common.model.Group;
import io.njiwa.common.rest.types.Roles;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.model.basic.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.HashSet;

@Singleton
@Startup
public class Initialiser {
    private static final String DEFAULT_ADMIN_GROUP = "Administrators";

    @Inject
    PersistenceUtility po;

    @Inject
    private PartitionManager partitionManager;

    // Create users
    @PostConstruct
    public void createUsers() {

        try {
            createGroup(DEFAULT_ADMIN_GROUP);
        } catch (Exception ex) {
        } // Ignore error

        try {
            assignGroupRoles(DEFAULT_ADMIN_GROUP, new String[]{Roles.SMDPAdmin, Roles.SMSRAdmin});
        } catch (Exception ex) {
        } // Ignore error

        //  delUser("admin");
        try {
            createUser("admin", DEFAULT_ADMIN_GROUP);
        } catch (Exception ex) {
        } // Ignore error

    }

    private void createUser(String admin, final String defaultAdminGroup) {
        User u = new User(admin);
        IdentityManager identityManager = partitionManager.createIdentityManager();
        identityManager.add(u);
        identityManager.updateCredential(u, admin + "test");

        po.doTransaction((po, em) -> {
            Group g = Group.getByName(em, defaultAdminGroup);

            return null;
        });
    }

    private void assignGroupRoles(String defaultAdminGroup, final String[] slist) {
        po.doTransaction((po, em) -> {
            Group g = Group.getByName(em, defaultAdminGroup);
            HashSet<String> sh = new HashSet<>();
            for (String s : slist)
                sh.add(s);
            g.setRoles(sh);
            return null;
        });
    }

    private void createGroup(String defaultAdminGroup) {
        final Group g = new Group();
        g.setName(defaultAdminGroup);
        po.doTransaction((po, em) -> {
            em.persist(g);
            return g;
        });
    }

}
