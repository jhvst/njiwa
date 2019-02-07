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

package io.njiwa.sr.ws.handlers;


import io.njiwa.common.Utils;
import io.njiwa.common.model.RpaEntity;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.xml.crypto.*;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Set;

/**
 * @brief This verifies the ES1:RegisterEIS DOM signature
 */
public class ES1SignatureVerifyHandler implements SOAPHandler<SOAPMessageContext> {
    public static final String VERIFIED_SIG_CERTIFICATE = ES1SignatureVerifyHandler.class.getCanonicalName() + "" +
            "._CERTIFICATE";
    private static final String SIGNED_INFO_KEY = ES1SignatureVerifyHandler.class.getCanonicalName() + ".SignedInfoXML";
    private static final String SIGNATURE_KEY = ES1SignatureVerifyHandler.class.getCanonicalName() + "" +
            ".SignatureXML";



    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private EntityManager em;

    public static String getSignedInfoXML(MessageContext context) {
        return (String) context.get(SIGNED_INFO_KEY);
    }

    public static String getSignatureXML(MessageContext context) {
        return (String) context.get(SIGNATURE_KEY);
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        SOAPMessage message = context.getMessage();

        boolean isOutgoing = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isOutgoing)
            return true; // Nothing to do on the outgoing front

        // Process incoming signature
        try {
            final SOAPBody spBody = message.getSOAPBody(); // Look in body
            Node sig = spBody.getElementsByTagName("Signature").item(0); // Look for it in the body only. Not header!
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            // Get our CI certificate
            X509Certificate ci_cert = RpaEntity.getCI(em);


            if (ci_cert == null) {
                System.out.println("No CI certificate in storage!");
                return false;
            }
            // Try to look for the EUM info by looking for the X509SubjectName

            NodeList eumIDNodes = spBody.getElementsByTagName("Eum-Id");
            String eumId = eumIDNodes.getLength() > 0 ? eumIDNodes.item(0).getTextContent() : null;

            X509KeySelector keySelector = new X509KeySelector(ci_cert, eumId, context);
            DOMValidateContext validateContext = new DOMValidateContext(keySelector, sig);
            final Node signedInfo = spBody.getElementsByTagName("EumSignedInfo").item(0);
            final Node signatureNode = spBody.getElementsByTagNameNS("*", "Signature").item(0);
            // Copied from https://www.ibm.com/developerworks/lotus/library/forms-digital/
            validateContext.setURIDereferencer(new URIDereferencer() {
                @Override
                public Data dereference(URIReference uriReference, XMLCryptoContext context) throws URIReferenceException {
                    if (uriReference.getURI() == null)
                        try {

                            // return new DOMSubTreeData(signedInfo,true);
                            Utils.removeRecursively(signedInfo, Node.COMMENT_NODE, null); // Remove comments as per spec
                            String xml = Utils.XML.getNodeString(signedInfo);
                            ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
                            return new OctetStreamData(inputStream);
                        } catch (Exception ex) {
                            return null;
                        }
                    else {
                        URIDereferencer defaultDereferencer = XMLSignatureFactory.getInstance("DOM").
                                getURIDereferencer();
                        return defaultDereferencer.dereference(uriReference, context);
                    }
                }
            });
            XMLSignature signature = fac.unmarshalXMLSignature(validateContext);

            boolean cv = signature.validate(validateContext);


// Check core validation status.
            if (cv == false) {
                Utils.lg.error("Signature failed core validation");
                boolean sv = signature.getSignatureValue().validate(validateContext);
                Utils.lg.error("signature validation status: " + sv);
                //if (sv == false) {
                // Check the validation status of each Reference.
                Iterator i = signature.getSignedInfo().getReferences().iterator();
                for (int j = 0; i.hasNext(); j++) {
                    boolean refValid = ((Reference) i.next()).validate(validateContext);
                    Utils.lg.error("ref[" + j + "] validity status: " + refValid);
                }
                // }
            } else {
                // Keep original XML for signature and signed info
                context.put(SIGNATURE_KEY, Utils.XML.getNodeString(signatureNode));
                context.put(SIGNED_INFO_KEY, Utils.XML.getNodeString(signedInfo));
                context.setScope(SIGNED_INFO_KEY, MessageContext.Scope.APPLICATION);
                context.setScope(SIGNATURE_KEY, MessageContext.Scope.APPLICATION);
                Utils.lg.error("Signature passed core validation");
// XXX Should we record the EUM ID? Should we ensure the certificate belongs to the given EUM (not important)
                return true;
            }

        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {

    }

    public class X509KeySelector extends KeySelector {
        private X509Certificate parent_cert;
        private String eumId;
        private SOAPMessageContext msgContext;

        public X509KeySelector(X509Certificate ci_cert, String receivedEumId, SOAPMessageContext context) {
            parent_cert = ci_cert;
            eumId = receivedEumId;
            this.msgContext = context;
        }

        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
                throws KeySelectorException {
            if (keyInfo == null)
                try {
                    // Then we must get key implicitly from eumId
                    X509Certificate cert = RpaEntity.getWSCertificateByOID(em, eumId, RpaEntity.Type.EUM);

                    final PublicKey key = cert.getPublicKey();
                    // Make sure the algorithm is compatible
                    // with the method.
                    if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                        return new KeySelectorResult() {
                            public Key getKey() {
                                return key;
                            }
                        };
                    }
                    throw new Exception("Lookup failed");
                } catch (Exception ex) {
                    throw new KeySelectorException(String.format("No key found for eum [%s]: %s", eumId, ex.getMessage()));
                }

            Iterator ki = keyInfo.getContent().iterator();
            while (ki.hasNext()) {
                XMLStructure info = (XMLStructure) ki.next();
                if (!(info instanceof X509Data))
                    continue;
                X509Data x509Data = (X509Data) info;
                Iterator xi = x509Data.getContent().iterator();
                while (xi.hasNext()) {
                    Object o = xi.next();
                    // Handle strings: Subject name thingies...
                    if (o instanceof String) {
                        // Must be a subject name, try to look for it
                        final String pname = (String) o;
                        X509Certificate xcert = RpaEntity.getCertificateBySubject(em, pname);
                        if (xcert != null)
                            o = xcert;
                        // Else fallthrough and fail.
                    }
                    // Either we got a certificate by look up above, or this node *is* a certificate in itself.
                    // If it is *is* one, perhaps we should check it is one of ours. Right? Or we do that at upper
                    // level?
                    if (!(o instanceof X509Certificate))
                        continue;
                    // Verify it
                    try {
                        ((X509Certificate) o).verify(parent_cert.getPublicKey());
                    } catch (Exception ex) {
                        System.out.println(String.format("Invalid certificate chain, does not match our CI: %s", ex));
                        continue;
                    }
                    final PublicKey key = ((X509Certificate) o).getPublicKey();
                    // Make sure the algorithm is compatible
                    // with the method.
                    if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                        msgContext.put(VERIFIED_SIG_CERTIFICATE, o); // Store the verified
                        msgContext.setScope(VERIFIED_SIG_CERTIFICATE, MessageContext.Scope.APPLICATION);
                        // certificate
                        return new KeySelectorResult() {
                            public Key getKey() {
                                return key;
                            }
                        };
                    }
                }
            }
            throw new KeySelectorException("No key found!");
        }

        private boolean algEquals(String algURI, String algName) {
            try {
                // Parse the uri
                URL u = new URL(algURI);
                String ref = u.getRef();
                if (ref != null && algName != null &&
                        ref.indexOf(algName.toLowerCase()) == 0)
                    return true;
            } catch (Exception ex) {
                return false;
            }
            return false;
        }
    }

}
