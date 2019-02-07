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

package io.njiwa.sr.ws;

import io.njiwa.common.model.RpaEntity;
import io.njiwa.common.PersistenceUtility;
import io.njiwa.common.Utils;
import io.njiwa.common.model.TransactionType;
import io.njiwa.common.ws.WSUtils;
import io.njiwa.common.ws.types.BaseResponseType;
import io.njiwa.common.ws.types.BaseTransactionType;
import io.njiwa.common.ws.types.WsaEndPointReference;
import io.njiwa.sr.model.Pol2Rule;
import io.njiwa.sr.model.ProfileInfo;
import io.njiwa.sr.model.SmSrTransaction;
import io.njiwa.sr.transactions.*;
import io.njiwa.sr.ws.interfaces.ES3;
import io.njiwa.sr.ws.interfaces.ES4;
import io.njiwa.sr.ws.types.Eis;
import io.njiwa.sr.ws.types.Pol2Type;

import javax.persistence.EntityManager;
import javax.xml.ws.Holder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by bagyenda on 25/05/2017.
 */
public class CommonImpl {
    // Functions in common to ES3 and ES4

    public static BaseResponseType updatePolicyRules(PersistenceUtility po, RpaEntity sender, String eid, String
            iccid, Pol2Type pol2, RpaEntity.Type senderType) {
        Date startDate = Calendar.getInstance().getTime();

        final BaseResponseType.ExecutionStatus.StatusCode code = new BaseResponseType.ExecutionStatus.StatusCode();
        code.subjectCode = "8.1.1";
        code.reasonCode = "";
        code.message = "";
        code.subjectIdentifier = "";

        boolean t = po.doTransaction(new PersistenceUtility.Runner<Boolean>() {
            @Override
            public Boolean run(PersistenceUtility po, EntityManager em) throws Exception {
                // Find eis
                io.njiwa.sr.model.Eis eis = io.njiwa.sr.model.Eis.findByEid(em, eid);
                if (eis == null) {
                    code.subjectIdentifier = "EID";
                    code.subjectCode = "8.1.1";
                    code.reasonCode = "3.9";
                    code.message = "Unknown EID";
                    return false;
                }
                // Check for pending euicc handover
                if (eis.verifyPendingEuiCCHandoverTransaction(em)) {
                    code.subjectCode = "1.2";
                    code.reasonCode = "4.4";
                    code.message = "EIS busy: Handover in progress";
                    return false;
                }
                ProfileInfo p = eis.findProfileByICCID(iccid);
                if (p == null) {
                    code.subjectCode = "8.2.1";
                    code.reasonCode = "3.9";
                    code.subjectIdentifier = "Profile ICCID";
                    code.message = "Unknown profile for given EID";
                    return false;
                }
                if (p.getState() != ProfileInfo.State.Enabled && p.getState() != ProfileInfo.State.Disabled) {
                    code.subjectCode = "8.2.1";
                    code.reasonCode = "1.2";
                    code.subjectIdentifier = "Profile ICCID";
                    code.message = "Wrong profile state!";
                    return false;
                }
// Check ownership
                String owner = senderType == RpaEntity.Type.SMDP ? p.getSmdpOID() : p.getMno_id();
                if (sender.getType() != senderType || owner == null || !sender.getOid().equals(owner)) {
                    code.subjectCode = "8.2.3";
                    code.reasonCode = "2.1";
                    code.subjectIdentifier = "Profile owner";
                    code.message = "Not owner!";
                    return false;
                }

                if (pol2 != null)
                    pol2.toModel(p);
                return true;
            }

            @Override
            public void cleanup(boolean success) {

            }
        });


        BaseResponseType.ExecutionStatus.Status status = Utils.toBool(t) ? BaseResponseType.ExecutionStatus.Status
                .ExecutedSuccess
                : BaseResponseType.ExecutionStatus.Status.Failed;
        BaseResponseType.ExecutionStatus executionStatus = new BaseResponseType.ExecutionStatus(status, code);
        Date endDate = Calendar.getInstance().getTime();

        BaseResponseType resp = new BaseResponseType(startDate, endDate, BaseTransactionType.DEFAULT_VALIDITY_PERIOD, executionStatus);
        return resp;

    }

    public static Long auditEIS(PersistenceUtility po, RpaEntity sender,
                                String eid,
                                List<String> iccids,
                                BaseResponseType.ExecutionStatus status,
                                RpaEntity.Type senderType,
                                WsaEndPointReference senderEntity,
                                String receiverEntity,
                                String messageId,
                                long validityPeriod,
                                WsaEndPointReference replyTo, Holder<String>
                                        messageType) {

        return po.doTransaction(new PersistenceUtility.Runner<Long>() {
            @Override
            public Long run(PersistenceUtility po, EntityManager em) throws Exception {
                // Find eis
                io.njiwa.sr.model.Eis eis = io.njiwa.sr.model.Eis.findByEid(em, eid);
                if (eis == null) {
                    status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                    status.statusCodeData.message = "EIS exists";
                    status.statusCodeData.subjectCode = "8.1.1";
                    status.statusCodeData.reasonCode = "1.1";
                    return null;
                } else if (sender.getType() != RpaEntity.Type.MNO && sender.getType() != RpaEntity.Type.SMDP) {
                    status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                    status.statusCodeData.message = "Not allowed";
                    status.statusCodeData.subjectCode = "8.1";
                    status.statusCodeData.reasonCode = "1.2";
                    return null;
                } else if (!eis.managementAllowed(sender.getOid())) {
                    status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                    status.statusCodeData.message = "Not allowed";
                    status.statusCodeData.subjectCode = "8.1";
                    status.statusCodeData.reasonCode = "1.2";
                    return null;
                } else
                    // Check for pending euicc handover
                    if (eis.verifyPendingEuiCCHandoverTransaction(em)) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "1.2";
                        status.statusCodeData.reasonCode = "4.4";
                        status.statusCodeData.message = "EIS busy: Handover in progress";
                        return null;
                    }

                AuditEISTransaction st = new AuditEISTransaction(sender, iccids, eis);
                st.updateBaseData(senderEntity, receiverEntity, messageId, validityPeriod, replyTo, sender.getId());
                st.requestorType = senderType;
                SmSrTransaction transaction = new SmSrTransaction(em, messageType.value, messageId,
                        receiverEntity, eid, validityPeriod, false, st);
                em.persist(transaction);
                return transaction.getId();
            }

            @Override
            public void cleanup(boolean success) {

            }
        });
    }

    public static Long deleteProfile(PersistenceUtility po, RpaEntity sender, String eid,
                                     BaseResponseType.ExecutionStatus status,
                                     String iccid,
                                     RpaEntity.Type senderType,
                                     WsaEndPointReference senderEntity,
                                     String receiverEntity,
                                     String messageId,
                                     long validityPeriod,
                                     WsaEndPointReference replyTo, Holder<String>
                                             messageType) {


        try {

            return po.doTransaction(new PersistenceUtility.Runner<Long>() {
                @Override
                public Long run(PersistenceUtility po, EntityManager em) throws Exception {
                    // Find eis
                    io.njiwa.sr.model.Eis eis = io.njiwa.sr.model.Eis.findByEid(em, eid);
                    if (eis == null) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.message = "EIS exists";
                        status.statusCodeData.subjectCode = "8.1.1";
                        status.statusCodeData.reasonCode = "1.1";
                        return null;
                    } else if (sender.getType() != RpaEntity.Type.MNO && sender.getType() != RpaEntity.Type.SMDP) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.message = "Not allowed";
                        status.statusCodeData.subjectCode = "8.1";
                        status.statusCodeData.reasonCode = "1.2";
                        return null;
                    } else if (!eis.managementAllowed(sender.getOid())) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.message = "Not allowed";
                        status.statusCodeData.subjectCode = "8.1";
                        status.statusCodeData.reasonCode = "1.2";
                        return null;
                    } else

                        // Check for pending euicc handover
                        if (eis.verifyPendingEuiCCHandoverTransaction(em)) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "1.2";
                            status.statusCodeData.reasonCode = "4.4";
                            status.statusCodeData.message = "EIS busy: Handover in progress";
                            return null;
                        }
                    ProfileInfo profileInfo = null;
                    // Now check for one with matching ICCID
                    try {
                        profileInfo = eis.findProfileByICCID(iccid);

                        // Check ownership
                        String owner = senderType == RpaEntity.Type.SMDP ? profileInfo.getSmdpOID() : profileInfo.getMno_id();
                        if (owner == null ||
                                sender.getType() != senderType ||
                                !owner.equalsIgnoreCase(sender.getOid())) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.1";
                            status.statusCodeData.subjectIdentifier = "1.2";
                            status.statusCodeData.message = "Not permitted";
                            return null;
                        }


                        ProfileInfo.State state = profileInfo.getState();
                        if (state == ProfileInfo.State.InstallInProgress ||
                                state == ProfileInfo.State.Created) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.1";
                            status.statusCodeData.subjectIdentifier = "1.2";
                            status.statusCodeData.message = "Profile state is wrong";
                            return null;
                        }
                    } catch (Exception ex) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "8.2.1";
                        status.statusCodeData.subjectIdentifier = "3.9";
                        status.statusCodeData.message = "Unknown ICCID";
                        return null;
                    }
                    // Check if we are busy
                    if (eis.verifyPendingProfileChangeTransaction(em)) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "1.2";
                        status.statusCodeData.subjectIdentifier = "4.4";
                        status.statusCodeData.message = "Profile update in progress";
                        return null;
                    }


                    if (profileInfo.getFallbackAttr()) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "8.2.3";
                        status.statusCodeData.subjectIdentifier = "3.8";
                        status.statusCodeData.message = "Denied by FallBackAttr";
                        return null;
                    }
                    // Check pol2
                    try {
                        List<Pol2Rule> pol2 = profileInfo.getPol2();
                        Pol2Rule.Qualification q = Pol2Rule.qualificationAction(pol2, Pol2Rule.Action.DELETE);
                        if (q != null && q == Pol2Rule.Qualification.NotAllowed) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.3";
                            status.statusCodeData.subjectIdentifier = "3.8";
                            status.statusCodeData.message = "Denied by POL2";
                            return null;
                        }
                    } catch (Exception ex) {
                    }

                    // Make the transaction: If the profile is Enabled, first disable it. Then after that, delete it.
                    ProfileInfo.State state = profileInfo.getState();
                    BaseTransactionType st;

                    if (state == ProfileInfo.State.Enabled)
                        st = new DisableProfileTransaction(profileInfo, true);
                    else
                        st = new DeleteProfileTransaction
                                (profileInfo);
                    st.updateBaseData(senderEntity, receiverEntity, messageId, validityPeriod, replyTo, sender.getId());
                    st.requestorType = senderType;
                    SmSrTransaction transaction = new SmSrTransaction(em, messageType.value, messageId,
                            receiverEntity, eid, validityPeriod, false, st);
                    em.persist(transaction);
                    return transaction.getId();
                }

                @Override
                public void cleanup(boolean success) {

                }
            });
        } catch (Exception ex) {

            Utils.lg.error(String.format("Failed to delete profile: %s", ex));
        }

        return null;

    }


    public static Long updateConnectivityParams(PersistenceUtility po, RpaEntity sender, String eid,
                                                BaseResponseType.ExecutionStatus status,
                                                String iccid,
                                                String params,
                                                RpaEntity.Type senderType,
                                                WsaEndPointReference senderEntity,
                                                String receiverEntity,
                                                String messageId,
                                                long validityPeriod,
                                                WsaEndPointReference replyTo, Holder<String>
                                                        messageType) {


        try {

            return po.doTransaction(new PersistenceUtility.Runner<Long>() {
                @Override
                public Long run(PersistenceUtility po, EntityManager em) throws Exception {
                    // Find eis
                    io.njiwa.sr.model.Eis eis = io.njiwa.sr.model.Eis.findByEid(em, eid);
                    // Check for pending euicc handover
                    if (eis.verifyPendingEuiCCHandoverTransaction(em)) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "1.2";
                        status.statusCodeData.reasonCode = "4.4";
                        status.statusCodeData.message = "EIS busy: Handover in progress";
                        return null;
                    }
                    ProfileInfo profileInfo = null;
                    // Now check for one with matching ICCID
                    try {
                        profileInfo = eis.findProfileByICCID(iccid); // Find ICCID

                        // Check ownership
                        String owner = senderType == RpaEntity.Type.SMDP ? profileInfo.getSmdpOID() : profileInfo.getMno_id();
                        if (owner == null ||
                                sender.getType() != senderType ||
                                !owner.equalsIgnoreCase(sender.getOid())) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.1";
                            status.statusCodeData.subjectIdentifier = "1.2";
                            status.statusCodeData.message = "Not permitted";
                            return null;
                        }

                        if (profileInfo.getState() != ProfileInfo.State.Enabled &&
                                profileInfo.getState() != ProfileInfo.State.Disabled) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.1";
                            status.statusCodeData.subjectIdentifier = "1.2";
                            status.statusCodeData.message = "Profile state is wrong";
                            return null;
                        }
                    } catch (Exception ex) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "8.2.1";
                        status.statusCodeData.subjectIdentifier = "3.9";
                        status.statusCodeData.message = "Unknown ICCID";
                        return null;
                    }

                    // Make the transaction
                    UpdateConnectivityParamsTransaction st = new UpdateConnectivityParamsTransaction(params);
                    st.updateBaseData(senderEntity, receiverEntity, messageId, validityPeriod, replyTo, sender.getId());
                    st.requestorType = senderType;
                    SmSrTransaction transaction = new SmSrTransaction(em, messageType.value, messageId,
                            receiverEntity, eid, validityPeriod, false, st);
                    transaction.setTargetAID(profileInfo.getIsd_p_aid()); // Set target so we send directly. Right?
                    em.persist(transaction);
                    return transaction.getId();
                }

                @Override
                public void cleanup(boolean success) {

                }
            });
        } catch (Exception ex) {

            Utils.lg.error(String.format("Error updating connectivity params: %s", ex));
        }

        return null;
    }


    public static Long disableProfile(PersistenceUtility po, RpaEntity sender, String eid,
                                      BaseResponseType.ExecutionStatus status,
                                      String iccid,

                                      RpaEntity.Type senderType,
                                      WsaEndPointReference senderEntity,
                                      String receiverEntity,
                                      String messageId,
                                      long validityPeriod,
                                      WsaEndPointReference replyTo, Holder<String>
                                              messageType) {
        try {

            return po.doTransaction(new PersistenceUtility.Runner<Long>() {
                @Override
                public Long run(PersistenceUtility po, EntityManager em) throws Exception {
                    // Find eis
                    io.njiwa.sr.model.Eis eis = io.njiwa.sr.model.Eis.findByEid(em, eid);
                    // Check for pending euicc handover
                    if (eis.verifyPendingEuiCCHandoverTransaction(em)) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "1.2";
                        status.statusCodeData.reasonCode = "4.4";
                        status.statusCodeData.message = "EIS busy: Handover in progress";
                        return null;
                    }
                    ProfileInfo profileInfo = null;
                    // Now check for one with matching ICCID
                    try {
                        profileInfo = eis.findProfileByICCID(iccid);

                        // Check ownership
                        String owner = senderType == RpaEntity.Type.SMDP ? profileInfo.getSmdpOID() : profileInfo.getMno_id();
                        if (owner == null ||
                                sender.getType() != senderType ||
                                !owner.equalsIgnoreCase(sender.getOid())) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.1";
                            status.statusCodeData.subjectIdentifier = "1.2";
                            status.statusCodeData.message = "Not permitted";
                            return null;
                        }

                        if (profileInfo.getState() != ProfileInfo.State.Enabled) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.1";
                            status.statusCodeData.subjectIdentifier = "1.2";
                            status.statusCodeData.message = "Profile state is wrong";
                            return null;
                        }
                    } catch (Exception ex) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "8.2.1";
                        status.statusCodeData.subjectIdentifier = "3.9";
                        status.statusCodeData.message = "Unknown ICCID";
                        return null;
                    }
                    // Check if we are busy
                    if (eis.verifyPendingProfileChangeTransaction(em)) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "1.2";
                        status.statusCodeData.subjectIdentifier = "4.4";
                        status.statusCodeData.message = "Profile update in progress";
                        return null;
                    }


                    // Check pol2
                    try {
                        List<Pol2Rule> pol2 = profileInfo.getPol2();
                        Pol2Rule.Qualification q = Pol2Rule.qualificationAction(pol2, Pol2Rule.Action.DISABLE);

                        if (q != null && q == Pol2Rule.Qualification.NotAllowed) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.3";
                            status.statusCodeData.subjectIdentifier = "3.8";
                            status.statusCodeData.message = "Denied by POL2";
                            return null;
                        }
                    } catch (Exception ex) {
                    }

                    // Make the transaction
                    DisableProfileTransaction st = new DisableProfileTransaction
                            (profileInfo, false);
                    st.updateBaseData(senderEntity, receiverEntity, messageId, validityPeriod, replyTo, sender.getId());
                    st.requestorType = senderType;
                    SmSrTransaction transaction = new SmSrTransaction(em, messageType.value, messageId,
                            receiverEntity, eid, validityPeriod, false, st);
                    em.persist(transaction);
                    return transaction.getId();
                }

                @Override
                public void cleanup(boolean success) {

                }
            });
        } catch (Exception ex) {
            Utils.lg.error("Failed to disable profile: " + ex.getMessage());
        }

        return null;
    }

    public static Long enableProfile(PersistenceUtility po, RpaEntity sender, String eid,
                                     BaseResponseType.ExecutionStatus status,
                                     String iccid,

                                     RpaEntity.Type senderType,
                                     WsaEndPointReference senderEntity,
                                     String receiverEntity,
                                     String messageId,
                                     long validityPeriod,
                                     WsaEndPointReference replyTo, Holder<String>
                                             messageType) {
        try {

            return po.doTransaction(new PersistenceUtility.Runner<Long>() {
                @Override
                public Long run(PersistenceUtility po, EntityManager em) throws Exception {
                    // Find eis
                    io.njiwa.sr.model.Eis eis = io.njiwa.sr.model.Eis.findByEid(em, eid);
                    // Check for pending euicc handover
                    if (eis.verifyPendingEuiCCHandoverTransaction(em)) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "1.2";
                        status.statusCodeData.reasonCode = "4.4";
                        status.statusCodeData.message = "EIS busy: Handover in progress";
                        return null;
                    }
                    ProfileInfo profileInfo = null;
                    // Now check for one with matching ICCID
                    try {
                        profileInfo = eis.findProfileByICCID(iccid);

                        // Check ownership
                        String owner = senderType == RpaEntity.Type.SMDP ? profileInfo.getSmdpOID() : profileInfo.getMno_id();
                        if (owner == null ||
                                sender.getType() != senderType ||
                                !owner.equalsIgnoreCase(sender.getOid())) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.1";
                            status.statusCodeData.subjectIdentifier = "1.2";
                            status.statusCodeData.message = "Not permitted";
                            return null;
                        }

                        if (profileInfo.getState() != ProfileInfo.State.Disabled) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.1";
                            status.statusCodeData.subjectIdentifier = "1.2";
                            status.statusCodeData.message = "Profile state is wrong";
                            return null;
                        }
                    } catch (Exception ex) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "8.2.1";
                        status.statusCodeData.subjectIdentifier = "3.9";
                        status.statusCodeData.message = "Unknown ICCID";
                        return null;
                    }
                    // Check if we are busy
                    if (eis.verifyPendingProfileChangeTransaction(em)) {
                        status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                        status.statusCodeData.subjectCode = "1.2";
                        status.statusCodeData.subjectIdentifier = "4.4";
                        status.statusCodeData.message = "Profile update in progress";
                        return null;
                    }

                    // Check Pol2 of the one to be disabled
                    try {
                        ProfileInfo pActive = eis.findEnabledProfile();
                        List<Pol2Rule> pol2 = pActive.getPol2();
                        Pol2Rule.Qualification q = Pol2Rule.qualificationAction(pol2, Pol2Rule.Action.DISABLE);
                        if (q != null && q == Pol2Rule.Qualification.NotAllowed) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.2";
                            status.statusCodeData.subjectIdentifier = "3.8";
                            status.statusCodeData.message = "Denied by POL2 of active ISDP";
                            return null;
                        }
                    } catch (Exception ex) {

                    }
                    // Then check our pol2 rules...
                    try {
                        List<Pol2Rule> pol2 = profileInfo.getPol2();
                        Pol2Rule.Qualification q = Pol2Rule.qualificationAction(pol2, Pol2Rule.Action.ENABLE);

                        if (q != null && q == Pol2Rule.Qualification.NotAllowed) {
                            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
                            status.statusCodeData.subjectCode = "8.2.3";
                            status.statusCodeData.subjectIdentifier = "3.8";
                            status.statusCodeData.message = "Denied by POL2";
                            return null;
                        }
                    } catch (Exception ex) {
                    }

                    // Make the transaction
                    EnableProfileTransaction st = new EnableProfileTransaction
                            (profileInfo);
                    st.updateBaseData(senderEntity, receiverEntity, messageId, validityPeriod, replyTo, sender.getId());
                    SmSrTransaction transaction = new SmSrTransaction(messageType.value, messageId,
                            receiverEntity, eis.getId(), validityPeriod, false, st);
                    em.persist(transaction);
                    return transaction.getId();
                }

                @Override
                public void cleanup(boolean success) {

                }
            });
        } catch (Exception ex) {
            Utils.lg.error("Failed to enable profile: " + ex.getMessage());
        }
        return null;
    }

    public static Eis getEIS(PersistenceUtility po, String eid, RpaEntity.Type senderType, RpaEntity sender,
                             BaseResponseType.ExecutionStatus status) {
        Eis eis = null;

        if (senderType != sender.getType()) {
            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
            status.statusCodeData.reasonCode = "1.1";
            status.statusCodeData.subjectIdentifier = "EID";
            status.statusCodeData.subjectCode = "8.6";
            status.statusCodeData.message = "Not allowed";
            return null;
        }
        try {
            io.njiwa.sr.model.Eis xeis = io.njiwa.sr.model.Eis.findByEid(po, eid);
            eis = xeis == null || xeis.getRegistrationComplete() != true ? null : Eis.fromModel(xeis);
            eis.hideGetEISFields(sender.getOid(), senderType);
        } catch (Exception ex) {
        }

        if (eis == null) {
            status.status = BaseResponseType.ExecutionStatus.Status.Failed;
            status.statusCodeData.reasonCode = "1.1";
            status.statusCodeData.subjectIdentifier = "EID";
            status.statusCodeData.subjectCode = "8.1.1";
            status.statusCodeData.message = "Unknown EID";
        }
        return eis;
    }

    public static void sendDisableProfileResponse(EntityManager em,
                                                  BaseTransactionType tr,
                                                  byte[] response) {
        RpaEntity.Type requestorType = tr.requestorType;
        BaseResponseType.ExecutionStatus status = tr.status;
        String to = tr.originallyTo;
        String relatesTo = tr.relatesTO;
        Date startDate = tr.startDate;
        WsaEndPointReference
                from = tr.getReplyToAddress(em, requestorType == RpaEntity.Type.SMDP ? "ES3" : "ES4");

        if (status == null) {
            status = new BaseResponseType.ExecutionStatus(BaseResponseType.ExecutionStatus.Status.Failed,
                    new BaseResponseType.ExecutionStatus.StatusCode("8.4", "", "4.2", ""));
        }

        Date endDate = Calendar.getInstance().getTime(); // Set it


        // Make params to send
        final WsaEndPointReference sender = new WsaEndPointReference();
        sender.address = to;

        try {
            String resp = response !=
                    null ?
                    Utils.HEX.b2H(response) : null;
            String msgURI = requestorType == RpaEntity.Type.SMDP ? "http://gsma" +
                    ".com/ES3/ProfileManagentCallBack/ES3-DisableProfile" :
                    "http://gsma" +
                            ".com/ES4/ProfileManagentCallBack/ES4-DisableProfile";
            final Holder<String> msgType = new Holder<String>(msgURI);
            if (requestorType == RpaEntity.Type.SMDP) {
                ES3 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES3Port",
                        from, ES3.class, RpaEntity.Type.SMSR, em,tr.requestingEntityId);
                proxy.disableProfileResponse(sender, from.address, relatesTo, msgType, Utils.gregorianCalendarFromDate
                                (startDate),
                        Utils.gregorianCalendarFromDate(endDate), TransactionType.DEFAULT_VALIDITY_PERIOD, status, resp);
            } else {
                ES4 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES4Port",
                        from, ES4.class, RpaEntity.Type.SMSR, em,tr.requestingEntityId);
                proxy.disableProfileResponse(sender, from.address, relatesTo, msgType, Utils.gregorianCalendarFromDate
                                (startDate),
                        Utils.gregorianCalendarFromDate(endDate), TransactionType.DEFAULT_VALIDITY_PERIOD, status, resp);
            }
        } catch (WSUtils.SuppressClientWSRequest s) {
        } catch (Exception ex) {
            Utils.lg.error("Failed to issue async DisableProfile response call: " + ex.getMessage());
        }
    }


    public static void sendAuditEISResponse(EntityManager em, BaseTransactionType tr,
                                            io.njiwa.sr.model.Eis xeis,
                                            List<String> iccids,
                                            RpaEntity requestor)
    {

        RpaEntity.Type requestorType = tr.requestorType;
        BaseResponseType.ExecutionStatus status = tr.status;
        String to = tr.originallyTo;
        String relatesTo = tr.relatesTO;
        Date startDate = tr.startDate;
        WsaEndPointReference
                from = tr.getReplyToAddress(em, requestorType == RpaEntity.Type.SMDP ? "ES3" : "ES4");

        if (status == null) {
            status = new BaseResponseType.ExecutionStatus(BaseResponseType.ExecutionStatus.Status.Failed,
                    new BaseResponseType.ExecutionStatus.StatusCode("8.4", "", "4.2", ""));
        }
        Date endDate = Calendar.getInstance().getTime(); // Set it
        // Make params to send
        final WsaEndPointReference sender = new WsaEndPointReference();
        sender.address = to;

        try {
            Eis eis = Eis.fromModel(xeis);
            eis.hideGetEISFields(requestor.getOid(),requestorType,iccids);
            String msgUri = requestorType == RpaEntity.Type.SMDP ? "http://gsma" +
                    ".com/ES3/ProfileManagentCallBack/ES3-AuditEIS" : "http://gsma" +
                    ".com/ES4/ProfileManagentCallBack/ES4-AuditEIS";
            Holder<String> msgType = new Holder<>(msgUri);
            if (requestorType == RpaEntity.Type.SMDP) {
                ES3 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES3Port", from, ES3.class,
                        RpaEntity.Type.SMSR, em,tr.requestingEntityId
                );
                proxy.auditEISResponse(sender,from.makeAddress(),relatesTo,msgType,Utils.gregorianCalendarFromDate(startDate),
                        Utils.gregorianCalendarFromDate(endDate), TransactionType.DEFAULT_VALIDITY_PERIOD,status,eis);
            } else {
                ES4 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES3Port", from, ES4.class,
                        RpaEntity.Type.SMSR, em,tr.requestingEntityId
                );
                proxy.auditEISResponse(sender,from.makeAddress(),relatesTo,msgType,Utils.gregorianCalendarFromDate(startDate),
                        Utils.gregorianCalendarFromDate(endDate), TransactionType.DEFAULT_VALIDITY_PERIOD,status,eis);
            }
        } catch (Exception ex) {

        }
    }

    public static void sendDeleteProfileResponse(EntityManager em, BaseTransactionType tr, byte[] response) {
        RpaEntity.Type requestorType = tr.requestorType;
        BaseResponseType.ExecutionStatus status = tr.status;
        String to = tr.originallyTo;
        String relatesTo = tr.relatesTO;
        Date startDate = tr.startDate;
        WsaEndPointReference
                from = tr.getReplyToAddress(em, requestorType == RpaEntity.Type.SMDP ? "ES3" : "ES4");

        if (status == null) {
            status = new BaseResponseType.ExecutionStatus(BaseResponseType.ExecutionStatus.Status.Failed,
                    new BaseResponseType.ExecutionStatus.StatusCode("8.4", "", "4.2", ""));
        }

        Date endDate = Calendar.getInstance().getTime(); // Set it
        // Make params to send
        final WsaEndPointReference sender = new WsaEndPointReference();
        sender.address = to;
        try {
            String resp = response !=
                    null ?
                    Utils.HEX.b2H(response) : null;

            String msgUri = requestorType == RpaEntity.Type.SMDP ? "http://gsma" +
                    ".com/ES3/ProfileManagentCallBack/ES3-DeleteISDP" : "http://gsma" +
                    ".com/ES4/ProfileManagentCallBack/ES4-DeleteProfile";
            Holder<String> msgType = new Holder<String>(msgUri);
            if (requestorType == RpaEntity.Type.SMDP) {
                ES3 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES3Port", from, ES3.class,
                        RpaEntity.Type.SMSR, em,tr.requestingEntityId
                );
                proxy.deleteISDPResponse(sender, from.address, relatesTo, msgType, Utils.gregorianCalendarFromDate(startDate),
                        Utils.gregorianCalendarFromDate(endDate), TransactionType.DEFAULT_VALIDITY_PERIOD, status, resp);
            } else {
                ES4 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES4Port", from, ES4.class,
                        RpaEntity.Type.SMSR, em,tr.requestingEntityId
                );
                proxy.deleteISDPResponse(sender, from.address, relatesTo, msgType, Utils.gregorianCalendarFromDate(startDate),
                        Utils.gregorianCalendarFromDate(endDate), TransactionType.DEFAULT_VALIDITY_PERIOD, status, resp);
            }

        } catch (WSUtils.SuppressClientWSRequest s) {
            String xs = null;
        } catch (Exception ex) {
            Utils.lg.error("Failed to issue async DeleteISDP response call: " + ex.getMessage());
        }
    }

    public static void sendEnableProfileResponse(EntityManager em, BaseTransactionType tr, byte[] response) {
        RpaEntity.Type requestorType = tr.requestorType;
        BaseResponseType.ExecutionStatus status = tr.status;
        String to = tr.originallyTo;
        String relatesTo = tr.relatesTO;
        Date startDate = tr.startDate;
        WsaEndPointReference
                from = tr.getReplyToAddress(em, requestorType == RpaEntity.Type.SMDP ? "ES3" : "ES4");

        if (status == null) {
            status = new BaseResponseType.ExecutionStatus(BaseResponseType.ExecutionStatus.Status.Failed,
                    new BaseResponseType.ExecutionStatus.StatusCode("8.4", "", "4.2", ""));
        }

        Date endDate = Calendar.getInstance().getTime(); // Set it
        // Make params to send
        final WsaEndPointReference sender = new WsaEndPointReference();
        sender.address = to;

        try {
            String resp = response !=
                    null ?
                    Utils.HEX.b2H(response) : null;

            String msgUri = requestorType == RpaEntity.Type.SMDP ? "http://gsma" +
                    ".com/ES3/ProfileManagentCallBack/ES3-EnableProfile" : "http://gsma" +
                    ".com/ES4/ProfileManagentCallBack/ES4-EnableProfile";
            Holder<String> msgType = new Holder<>(msgUri);
            if (requestorType == RpaEntity.Type.SMDP) {
                ES3 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES3Port", from, ES3.class, RpaEntity.Type.SMSR, em
                        ,tr.requestingEntityId);
                proxy.enableProfileResponse(sender, from.address, relatesTo, msgType, Utils.gregorianCalendarFromDate
                                (startDate),
                        Utils.gregorianCalendarFromDate(endDate), TransactionType.DEFAULT_VALIDITY_PERIOD, status, resp);
            } else {
                ES4 proxy = WSUtils.getPort("http://namespaces.gsma.org/esim-messaging/1", "ES4Port", from, ES4
                                .class, RpaEntity.Type.SMSR, em,tr.requestingEntityId
                );
                proxy.enableProfileResponse(sender, from.address, relatesTo, msgType, Utils.gregorianCalendarFromDate
                                (startDate),
                        Utils.gregorianCalendarFromDate(endDate), TransactionType.DEFAULT_VALIDITY_PERIOD, status, resp);
            }
        } catch (WSUtils.SuppressClientWSRequest s) {
        } catch (Exception ex) {
            Utils.lg.error("Failed to issue async EnableProfile response call: " + ex.getMessage());
        }
    }

}
