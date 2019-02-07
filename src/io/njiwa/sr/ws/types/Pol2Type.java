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

package io.njiwa.sr.ws.types;

import io.njiwa.sr.model.Pol2Rule;
import io.njiwa.sr.model.ProfileInfo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bagyenda on 18/10/2016.
 */
@XmlRootElement(namespace = "http://namespaces.gsma.org/esim-messaging/1")
public class Pol2Type {


    public List<Pol2RuleType> rules;

    public static Pol2Type fromModel(ProfileInfo p) {
        Pol2Type pp = new Pol2Type();
        pp.rules = new ArrayList<>();
        try {
            List<Pol2Rule> xl = p.getPol2();
            for (Pol2Rule pr : xl) {
                Pol2RuleType pt = new Pol2RuleType();
                pt.action = pr.getAction();
                pt.subject = pr.getSubject();
                pt.qualification = pr.getQualification();
                pp.rules.add(pt);
            }
        } catch (Exception ex) {
        }
        return pp;
    }

    public void toModel(ProfileInfo p) {
        List<Pol2Rule> l = new ArrayList<>();
        try {
            for (Pol2RuleType pr : rules)
                l.add(new Pol2Rule(p, pr.action, pr.subject, pr.qualification));
        } catch (Exception ex) {
        }
        p.setPol2(l);
    }

    @XmlRootElement(name = "Rule")
    public static class Pol2RuleType {

        @XmlElement(name = "Subject")
        public Pol2Rule.Subject subject;
        @XmlElement(name = "Action")
        public Pol2Rule.Action action;
        @XmlElement(name = "Qualification")
        public Pol2Rule.Qualification qualification;

    }
}
