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

import io.njiwa.common.ws.InitialiserServlet;
import io.njiwa.sr.Session;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.BigIntegers;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import redis.clients.jedis.Jedis;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @brief Utility functions that don't logically fit anywhere else.
 */
public class Utils {


    public static final Date infiniteDate; //!< General infinite date
    private static final boolean[] unreserved_url_chars; //!< This is the list allowed URL characters.
    public final static org.apache.log4j.Logger lg = org.apache.log4j.Logger.getLogger(InitialiserServlet.class.getName()); //!< The
    // logger for the entire system
    private static KeyStore ks = null; //!< The key store...
    private static String privKeyAlias, privKeyPassword;

    static {
        Calendar c = Calendar.getInstance();
        c.set(9999, 12, 30);
        infiniteDate = c.getTime(); // Get a year far into the future.

    }

    static {
        unreserved_url_chars = new boolean[256];

        unreserved_url_chars[(int) 'A'] = true;
        unreserved_url_chars[(int) 'B'] = true;
        unreserved_url_chars[(int) 'C'] = true;
        unreserved_url_chars[(int) 'D'] = true;
        unreserved_url_chars[(int) 'E'] = true;
        unreserved_url_chars[(int) 'F'] = true;
        unreserved_url_chars[(int) 'G'] = true;
        unreserved_url_chars[(int) 'H'] = true;
        unreserved_url_chars[(int) 'I'] = true;
        unreserved_url_chars[(int) 'J'] = true;
        unreserved_url_chars[(int) 'K'] = true;
        unreserved_url_chars[(int) 'L'] = true;
        unreserved_url_chars[(int) 'M'] = true;
        unreserved_url_chars[(int) 'N'] = true;
        unreserved_url_chars[(int) 'O'] = true;
        unreserved_url_chars[(int) 'P'] = true;
        unreserved_url_chars[(int) 'Q'] = true;
        unreserved_url_chars[(int) 'R'] = true;
        unreserved_url_chars[(int) 'S'] = true;
        unreserved_url_chars[(int) 'T'] = true;
        unreserved_url_chars[(int) 'U'] = true;
        unreserved_url_chars[(int) 'V'] = true;
        unreserved_url_chars[(int) 'W'] = true;
        unreserved_url_chars[(int) 'X'] = true;
        unreserved_url_chars[(int) 'Y'] = true;
        unreserved_url_chars[(int) 'Z'] = true;
        unreserved_url_chars[(int) 'a'] = true;
        unreserved_url_chars[(int) 'b'] = true;
        unreserved_url_chars[(int) 'c'] = true;
        unreserved_url_chars[(int) 'd'] = true;
        unreserved_url_chars[(int) 'e'] = true;
        unreserved_url_chars[(int) 'f'] = true;
        unreserved_url_chars[(int) 'g'] = true;
        unreserved_url_chars[(int) 'h'] = true;
        unreserved_url_chars[(int) 'i'] = true;
        unreserved_url_chars[(int) 'j'] = true;
        unreserved_url_chars[(int) 'k'] = true;
        unreserved_url_chars[(int) 'l'] = true;
        unreserved_url_chars[(int) 'm'] = true;
        unreserved_url_chars[(int) 'n'] = true;
        unreserved_url_chars[(int) 'o'] = true;
        unreserved_url_chars[(int) 'p'] = true;
        unreserved_url_chars[(int) 'q'] = true;
        unreserved_url_chars[(int) 'r'] = true;
        unreserved_url_chars[(int) 's'] = true;
        unreserved_url_chars[(int) 't'] = true;
        unreserved_url_chars[(int) 'u'] = true;
        unreserved_url_chars[(int) 'v'] = true;
        unreserved_url_chars[(int) 'w'] = true;
        unreserved_url_chars[(int) 'x'] = true;
        unreserved_url_chars[(int) 'y'] = true;
        unreserved_url_chars[(int) 'z'] = true;
        unreserved_url_chars[(int) '0'] = true;
        unreserved_url_chars[(int) '1'] = true;
        unreserved_url_chars[(int) '2'] = true;
        unreserved_url_chars[(int) '3'] = true;
        unreserved_url_chars[(int) '4'] = true;
        unreserved_url_chars[(int) '5'] = true;
        unreserved_url_chars[(int) '6'] = true;
        unreserved_url_chars[(int) '7'] = true;
        unreserved_url_chars[(int) '8'] = true;
        unreserved_url_chars[(int) '9'] = true;
        unreserved_url_chars[(int) '-'] = true;
        unreserved_url_chars[(int) '_'] = true;
        unreserved_url_chars[(int) '.'] = true;
        unreserved_url_chars[(int) '~'] = true;

    }

    /**
     * @param os     The output
     * @param u      The integer
     * @param octets the number of significant octets in the integer
     * @brief BER TL(A)V encoding of an integer
     */
    public static void appendEncodedInteger(OutputStream os, long u, int octets) {

        for (short i = 0; i < octets; i++)
            try {
                long x = (u >> ((octets - i - 1) * 8)) & 0xFF;

                os.write((int) x);
            } catch (Exception ex) {
            }

    }

    /**
     * @param os
     * @param u
     * @param octets
     * @throws Exception
     * @brief Write a little-endian integer
     */
    public static void writeLE(OutputStream os, long u, int octets) throws Exception {
        for (int i = 0; i < octets; i++) {
            int x = (int) (u & 0xFF);

            os.write(x);
            u >>= 8;
        }
    }

    /**
     * @param in
     * @param offset
     * @param octets
     * @return
     * @throws Exception
     * @brief Read a little-endian integer
     */
    public static long readLE(byte[] in, int offset, int octets) throws Exception {
        long u = 0;
        int ct = 0;
        for (int i = offset; i < octets + offset && i < in.length; i++, ct++) {
            int x = in[i] & 0xFF;
            long t = x << (ct * 8);
            u |= t;
        }
        return u;
    }

    /**
     * @param u
     * @param octets
     * @return
     * @brief Write a BER integer
     */
    public static byte[] encodeInteger(long u, int octets) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        appendEncodedInteger(os, u, octets);
        return os.toByteArray();
    }

    /**
     * @param phone_num
     * @return
     * @brief Convert a phone number into its SBC (GSM 11.14) format and return the number of octets.
     */
    public static Pair<byte[], Integer> makePhoneNumber(byte[] phone_num) {
        int ton_npi = 0x81;
        int i = 0;

        if (phone_num == null)
            return new Pair<>(null, 0);
        if (phone_num[0] == '+') {
            ton_npi |= 1 << 4; // Int'l number
            i++;

        }

        // verify format.
        Charset.ByteChecker chk = new Charset.ByteChecker() {
            @Override
            public boolean check(byte b) {
                boolean xx = b >= '0' && b <= '9';
                return xx || b == '*' || b == ',' || b == '#';
            }
        };

        if (!Charset.check_all(phone_num, 1, chk))
            return new Pair<>(null, 0);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int l = phone_num.length - i, len = l;
        l += (l % 2); // Round it up
        os.write(ton_npi);


        for (; i < l; i += 2) {
            int d1 = convChar(phone_num[i]);
            int d2 = convChar(i + 1 < phone_num.length ? phone_num[i + 1] : 0);
            int ch = (d2 << 4) | d1;
            os.write(ch);

        }
        return new Pair<>(os.toByteArray(), len);
    }

    /**
     * @param c
     * @return
     * @brief Convert a phone number character into its base representation
     */
    private static final int convChar(byte c) {
        boolean xx = (c >= '0' && c <= '9');
        if (xx)
            return c - '0';
        else if (c == '*')
            return 0xA;
        else if (c == '#')
            return 0xB;
        else if (c == ',')
            return 0xC;
        else
            return 0x0F;
    }

    private static final String unconvPval(int n) {

        if (n < 0)
            return "";
        if (n <= 9)
            return String.format("%d", n);
        else if (n == 0x0A)
            return "*";
        else if (n == 0x0B)
            return "#";
        else if (n == 0x0C)
            return ",";
        return "";
    }

    public static byte[]getMessageDigest(String type, byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            md.update(data);
            return md.digest();
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * @param cmd
     * @param args
     * @return
     * @brief Execute a command line script, return the output lines
     */
    public static String[] execCommand(String cmd, String[] args, String[] dflt) {
        int argc = args != null ? args.length : 0;
        String[] xcmd = new String[1 + argc];

        xcmd[0] = cmd;
        for (int i = 1; i <= argc; i++)
            xcmd[i] = args[i - 1];

        try {
            Process p = Runtime.getRuntime().exec(xcmd);
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            List<String> l = new ArrayList<String>();
            String line;
            while ((line = r.readLine()) != null)
                l.add(line);
            return l.toArray(new String[0]);
        } catch (Exception ex) {
            lg.error(String.format("Failed to run command [%s]: %s", cmd, ex));
            return dflt;
        }
    }

    /**
     * @param in
     * @param semiOctets
     * @param offset
     * @return
     * @brief Given the semi octets representing a number, parse
     */
    public static Pair<String, Integer> parsePhoneFromSemiOctets(byte[] in, int semiOctets, int offset) {
        // Returns the number and its length in octets.
        int l = semiOctets / 2;
        int ton_npi = in[offset];
        int i;
        String phone = (ton_npi & (1 << 4)) != 0 ? "+" : "";

        for (i = 0; i < l; i++) {
            int ch = in[offset + 1 + i];
            int d1 = ch & 0x0F;
            int d2 = (ch >> 4) & 0x0F;

            phone += unconvPval(d1) + unconvPval(d2);
        }

        if (2 * l != semiOctets) { // Odd number of semi-octets...
            int ch = in[offset + i + 1];
            int d1 = ch & 0x0F;
            phone += unconvPval(d1);
            i++;
        }

        return new Pair<>(phone, i + 1);
    }

    public static byte[] replace(byte[] haystack, byte[] needle, byte[] replacement) {
        // First find it
        int idx = Collections.indexOfSubList(Arrays.asList(haystack), Arrays.asList(needle));

        if (idx < 0)
            return null;
        int newLen = haystack.length - needle.length + replacement.length;
        byte[] data = new byte[newLen];

        System.arraycopy(haystack, 0, data, 0, idx); // Copy first part
        System.arraycopy(replacement, 0, data, idx, replacement.length); // Copy the replacement in place...
        System.arraycopy(haystack, idx + needle.length, data, idx + replacement.length, haystack.length - needle.length);

        return data;
    }

    /**
     * @param url
     * @param method
     * @param req_hdr
     * @param cgi_params
     * @param context
     * @return
     * @throws Exception
     * @brief Get content from URL, return status, reply headers, content
     */
    public static Triple<Integer, Map<String, String>, String> getUrlContent(String url, HttpRequestMethod method,
                                                                             Map<String, String> req_hdr,
                                                                             List<Pair<String, String>> cgi_params,
                                                                             Session context) throws Exception {

        // Deal with params
        String parms = "";
        String sep = "";
        if (cgi_params != null)
            for (Pair<String, String> p : cgi_params) {
                parms += String.format("%s%s=%s", sep,
                        URLEncoder.encode(p.k, "UTF-8"),
                        URLEncoder.encode(p.l, "UTF-8"));
                sep = "&";
            }

        String xurl = method == HttpRequestMethod.GET && parms.length() > 0 ? String.format("%s?%s", url, parms) : url;
        URL u = new URL(xurl);
        String res = null;
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();

        req_hdr = req_hdr == null ? new HashMap<>() : req_hdr;

        // Fixups
        if (req_hdr.get("User-Agent") == null) {
            String browser_ua = context != null ? context.getBrowserProfileStr() : null;
            String ua = browser_ua != null ? browser_ua : String.format("eUICC Server v%s", ServerSettings.Constants.version);
            req_hdr.put("User-Agent", ua);
        }

        // Put in the headers
        for (Map.Entry<String, String> h : req_hdr.entrySet())
            conn.setRequestProperty(h.getKey(), h.getValue());

        // conn.setRequestProperty("Accept", "text/vnd.wap.wml");
        conn.setRequestProperty("Accept", "*/*");
        // Add cookies
        if (context != null) {
            List<HTTPCookie> l = context.getCookies(u.getHost(), url);
            if (l != null)
                HTTPCookie.putCookies(conn, l);
        }

        if (method == HttpRequestMethod.POST) {
            conn.setDoOutput(true);
            if (conn.getRequestProperty("Content-Type") == null) // Only set if not set
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStream os = conn.getOutputStream();
            os.write(parms.getBytes("UTF-8"));
        }

        int status = conn.getResponseCode();
        // Get headers.
        Map<String, String> rph = new HashMap<String, String>();


        if (status == 200)
            try {
                for (Map.Entry<String, List<String>> x : conn.getHeaderFields().entrySet())
                    rph.put(x.getKey(), x.getValue().get(0)); // Only first one.

                // save cookies
                if (context != null) {
                    List<HTTPCookie> cl = HTTPCookie.getCookies(conn);
                    context.saveCookies(cl);
                }
                // Read obeying content type
                String contentType = conn.getHeaderField("Content-Type");
                String charset = null;

                if (contentType != null)
                    for (String param : contentType.replace(" ", "").split(";")) {
                        if (param.startsWith("charset=")) {
                            charset = param.split("=", 2)[1];
                            break;
                        }
                    }

                InputStream os = conn.getInputStream();

                if (charset != null) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(os, charset));
                    res = "";
                    for (String line; (line = r.readLine()) != null; )
                        res += line + "\n";
                } else {
                    // Assume binary encoding. Right?
                    byte[] b = new byte[512];
                    int len;
                    res = "";
                    while ((len = os.read(b)) > 0) {
                        String xs = new String(b, 0, len, "UTF-8");
                        res += xs;
                    }
                }
            } catch (Exception ex) {

            } else if ((status/100) != 2) // i.e. we failed,
                        Utils.lg.warn(String.format("Failed to fetch URL[%s]: http code => %d", url, status));
        try {
            conn.disconnect();
        } catch (Exception ex) {
        }

        return new Triple<>(status, rph, res);
    }

    public static byte[] byteArrayCopy(byte[] in, int offset, int len) {
        // Copy an array from offset for len.
        int realLen = offset + len > in.length ? in.length - offset : len;
        if (realLen <= 0)
            return new byte[0];

        byte[] res = new byte[realLen];

        System.arraycopy(in, offset, res, 0, realLen);
        return res;
    }

    /**
     * @param parm
     * @return
     * @throws Exception
     * @brief perform URL value decoding.
     */
    public static byte[] urlDecode(String parm) throws Exception {
        // Do it the good-old fashioned way.
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        for (int i = 0; i < parm.length(); i++) {
            if (parm.charAt(i) == '%')
                try {
                    // Percent stuff. Get following
                    int ch1 = parm.charAt(i + 1);
                    int ch2 = parm.charAt(i + 2);
                    String pspec = new String(new char[]{(char) ch1, (char) ch2});
                    int xp = Integer.parseInt(pspec, 16);
                    os.write(xp);
                    i += 2; // We shall jump one more later
                } catch (Exception ex) {

                }
            else if (parm.charAt(i) == '+')
                os.write(' '); // Space
            else
                os.write(parm.charAt(i));
        }

        return os.toByteArray();
    }

    /**
     * @param parm
     * @return
     * @throws Exception
     * @brief Perform URL value encoding
     */
    public static String urlEncode(byte[] parm) throws Exception {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < parm.length; i++) {
            int b = parm[i];
            int ch = (b & 0xFF); // as ASCII 8-bit value

            if (ch >= 0 && ch < unreserved_url_chars.length && unreserved_url_chars[ch])
                out.append((char) b);
            else
                out.append(String.format("%%%02X", ch));
        }
        return out.toString();
    }

    public static Pair<Integer, Integer> parseVersionString(String version) {
        int major = 1, minor = 0;
        if (version != null) {
            String[] xx = version.split("[.]");
            try {
                major = Integer.parseInt(xx[0]);
            } catch (Exception ex) {
                major = 1;
            }
            try {
                minor = Integer.parseInt(xx[1]);
            } catch (Exception ex) {
                minor = 0;
            }
        }
        return new Pair<>(major, minor);
    }

    /**
     * @param x
     * @param y
     * @return int
     * @brief Return a negative number if version x < version y, 0 if they are equal, and a positive integer if x > y
     * <p>
     * greater
     */
    public static int compareVersions(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
        int m = x.k - y.k;
        if (m != 0)
            return m;
        return x.l - y.l;
    }

    public static String cleanPhoneNumber(String num) throws Exception {
        String xnum = num.replaceAll("\\s+", "");
        String xx;
        if (xnum.charAt(0) == '+')
            xx = xnum.substring(1);
        else
            xx = xnum;
        // Check it is all Digits
        if (!xx.matches("^[0-9]+$"))
            throw new Exception(String.format("Invalid number format: %s", xnum));


        if (!xx.equalsIgnoreCase(xnum)) { // Then there must be a country code
            int ccLen = ServerSettings.getCountry_code().length();
            String mycc = xx.substring(0, ccLen);
            if (!mycc.equalsIgnoreCase(ServerSettings.getCountry_code()))
                throw new Exception(String.format("Invalid country code [%s]", mycc));
            xnum = xx.substring(ccLen);
        } else if (xnum.charAt(0) == '0')
            xnum = xnum.substring(1); // Remove the leading 0

        // Now we must have the number sans country code and any leading 0
        String network_code = null;
        for (String nc : ServerSettings.getNetwork_codes()) {
            int ncLen = nc.length();
            String x = xnum.substring(0, ncLen);

            if ((x.length() == ncLen) && x.equalsIgnoreCase(nc)) {
                network_code = nc;
                break;
            }
        }

        if (network_code == null)
            throw new Exception(String.format("Invalid number: %s, network code is not one of the expected ones", num));

        if (xnum.length() + 1 != ServerSettings.getNumber_length())
            throw new Exception(String.format("Invalid number: %s, must be %d digits long and of the form 0xxxxxx", num, ServerSettings.getNumber_length()));

        return "+" + ServerSettings.getCountry_code() + xnum;
    }

    public static void removeRecursively(Node node, short nodeType, String name) {

        if (node.getNodeType() == nodeType && (name == null || node.getNodeName().equals(name))) {

            node.getParentNode().removeChild(node);

        } else {

            // check the children recursively

            NodeList list = node.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                removeRecursively(list.item(i), nodeType, name);

            }

        }

    }

    /**
     * Round up to the nearest multiple of n
     *
     * @param a
     * @param n
     * @return
     */
    public static int ROUND(int a, int n) {
        return n * ((a + n - 1) / n);
    }

    public static long toLong(Object o) {
        try {
            if (o instanceof String)
                return Long.parseLong((String) o);
            else if (o instanceof Long)
                return (Long) o;
            else if (o instanceof Integer)
                return (Integer) o;
        } catch (Exception ex) {
        }
        return 0;
    }

    public static boolean toBool(Object o) {
        try {
            if (o instanceof Boolean)
                return (Boolean) o;
            else if (o instanceof String)
                return ((String) o).equalsIgnoreCase("true");
            else if (o instanceof Long)
                return ((Long) o) != 0L;
            else if (o instanceof Integer)
                return ((Integer) o) != 0;
        } catch (Exception ex) {
        }
        return false;
    }

    // Check if an object is empty. Not complete, but still....
    public static boolean isEmpty(Object o) {
        if (o == null) return true;
        if (o instanceof String && ((String) o).length() == 0)
            return true;
        if (o instanceof Integer && 0 == (Integer) o)
            return true;
        if (o instanceof Long && 0L == (Long) o)
            return true;

        return false;
    }

    public static String tarFromAid(String aid) {
        // In accordance with SGP-02-v3-0 Annex H
        String pix = pixFromAid(aid);
        return pix.substring(8, 14);
    }

    public static String pixFromAid(String aid) {
        return aid.substring(11);
    }

    public static String ridFromAID(String aid) {
        return aid.substring(0, 11); // First 5 bytes
    }

    public static XMLGregorianCalendar gregorianCalendarFromDate(Date t) throws Exception {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(t);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    }

    // Use JAXB to serialise an object to XML
    public static String toXML(Object o) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(o.getClass());
        Marshaller m = jc.createMarshaller();
        StringWriter w = new StringWriter();
        m.marshal(o, w);
        return w.toString();
    }

    public static <T> T fromXML(String xml, Class<T> cls) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(cls);
        Unmarshaller um = jc.createUnmarshaller();
        StringReader r = new StringReader(xml);

        T o = (T) um.unmarshal(r);
        return o;
    }

    public static String ramHTTPPartIDfromAID(String aid) throws Exception {
        String rid, pix;

        rid = aid.substring(0, 10);
        pix = aid.substring(11);
        return String.format("%s/%s", rid, pix);
    }

    public static void setPrivateKeyAliasAndPassword(String alias, String passwd) {
        privKeyAlias = alias;
        privKeyPassword = passwd;
    }

    public static void loadKeyStore(String keyfile, String password) throws Exception {
        loadKeyStore(keyfile, password, true);
    }

    public static KeyStore loadKeyStore(String keyfile, String password, boolean setDefault) throws Exception {
        KeyStore xks = KeyStore.getInstance(KeyStore.getDefaultType());

        try {
            FileInputStream fis = new FileInputStream(keyfile);

            xks.load(fis, password.toCharArray());
        } catch (Exception ex) {
            //     System.out.println();
            throw ex;
        }
        if (setDefault)
            ks = xks;
        return xks;
    }

    public static KeyStore getKeyStore() throws Exception {
        if (ks == null)
            throw new Exception("KeyStore not set");
        return ks;
    }

    public static KeyStore.PrivateKeyEntry getServerPrivateKey(String alias) throws Exception {
        // Not idea, but...
        KeyStore ks = getKeyStore();
        KeyStore
                .PasswordProtection prot = new KeyStore.PasswordProtection(privKeyPassword.toCharArray());
        try {
            KeyStore.PrivateKeyEntry pKey = (KeyStore.PrivateKeyEntry) ks.getEntry(alias, prot);
            if (pKey != null)
                return pKey;
        } catch (Exception ex) {
        }
        // Else, cycle through all keys and look for the first one that is a Private key
        Enumeration<String> sl = ks.aliases();
        KeyStore.Entry k;
        while (sl.hasMoreElements())
            try {
                k = ks.getEntry(sl.nextElement(), prot);
                if (k instanceof KeyStore.PrivateKeyEntry)
                    return (KeyStore.PrivateKeyEntry) k;
            } catch (Exception ex) {

            }
        return null;
    }

    public static ECPrivateKey getServerECPrivateKey(String alias) throws Exception {
        KeyStore.PrivateKeyEntry k = getServerPrivateKey(alias);
        return (ECPrivateKey) k.getPrivateKey();
    }

    public static KeyStore.PrivateKeyEntry getServerPrivateKey() throws Exception {
        return getServerPrivateKey(privKeyAlias);
    }

    public static void saveServerPrivateKey(String alias, Key key, Certificate cert) {
        try {
            KeyStore ks = getKeyStore();
            ks.setKeyEntry(alias, key, privKeyPassword.toCharArray(), new Certificate[]{cert});
        } catch (Exception ex) {
        }
    }

    public static String getRandString() {
        return UUID.randomUUID().toString();
    }

    public static byte[] getBytes(InputStream in) throws Exception {
        byte[] data = new byte[in.available()];
        in.read(data);
        return data;
    }

    public static byte[] readBERorDGI(InputStream in, int expectedTag)
            throws Exception {
        int tag;
        byte[] data;
        if ((expectedTag & 0xFF00) != 0) {
            // Expect DGI
            Pair<Integer, byte[]> x = DGI.decode(in);
            tag = x.k;
            data = x.l;
        } else {
            Pair<InputStream, Integer> x = BER.decodeTLV(in);
            tag = x.l;
            data = getBytes(x.k);
        }
        if (tag != expectedTag)
            throw new Exception("Invalid! Expected Tag " + String.format("%04x", expectedTag) + ", got: " + String
                    .format("%04X", tag));
        return data;
    }


    public static byte[] aesMAC(byte[] input, byte[] key) {
        BlockCipher cp = new AESEngine();
        CMac cmac = new CMac(cp);
        cmac.init(new KeyParameter(key));
        byte[] data = pad80(input, 16);
        cmac.update(data, 0, data.length);
        byte[] out = new byte[cmac.getMacSize()];
        cmac.doFinal(out, 0);
        return out;
    }

    public static byte[] pad80(final byte[] input, final int blockbits) {
        try {
            return new ByteArrayOutputStream() {
                {
                    write(input);
                    write((byte) 0x80); // See "Bit padding" https://en.wikipedia.org/wiki/Padding_(cryptography)#ANSI_X.923
                    while (size() % (blockbits / 8) != 0)
                        write(0);
                }
            }.toByteArray();
        } catch (Exception ex) {
        }
        return null;
    }

    public static Jedis redisConnect() {
        return new Jedis(ServerSettings.getRedis_server(), ServerSettings.getRedis_port());
    }

    public enum HttpRequestMethod {
        GET, POST
    }

    public interface Predicate<T> {
        boolean eval(T obj);
    }

    public static class DGI {

        public static void appendLen(OutputStream os, long len) throws Exception {
            // According to Appendix B of "GP System Scripting Language v1.1.0
            if (len < 255)
                os.write((byte) len);
            else {
                os.write(0xFF);
                appendEncodedInteger(os, len, 2);
            }
        }

        public static void append(OutputStream os, int tag, byte[] value) throws Exception {
            os.write(new byte[]{(byte) ((tag >> 8) & 0xFF), (byte) (tag & 0xFF)});
            appendLen(os, value.length);
            os.write(value);
        }

        public static Pair<Integer, byte[]> decode(InputStream in) throws Exception {
            int n1 = ((in.read() << 8)) & 0xFF;
            int n2 = in.read() & 0xff;
            int tag = n1 | n2;

            // Now look at length
            long len = in.read() & 0xFF;
            if (len == 0xFF)
                len = BER.decodeInt(in, 2);
            byte[] data = new byte[(int) len];
            in.read(data);
            return new Pair<>(tag, data);
        }
    }

    public static class HEX {
        // Convert from object to base byte
        public static byte[] B2b(Byte[] in_b) {
            byte[] out_b = new byte[in_b.length];
            for (int i = 0; i < in_b.length; i++)
                out_b[i] = in_b[i].byteValue();
            return out_b;
        }

        /**
         * @param a
         * @return
         * @brief Convert a byte sequence to (uppercase) hex
         */
        public static String b2H(byte[] a) {
            return b2H(a, 0, a.length);
        }

        /**
         * @param a
         * @param offset
         * @param len
         * @return
         * @brief Convert a slice of a byte sequence to hex
         */
        public static String b2H(byte[] a, int offset, int len) {
            StringBuilder sb = new StringBuilder(a.length * 2);
            for (int i = offset; i < len && i < a.length; i++)
                sb.append(String.format("%02X", a[i] & 0xff));
            return sb.toString();
        }

        /**
         * @param a
         * @return
         * @brief Convert a byte sequence to (lowercase) hex
         */
        public static String b2h(byte[] a) {
            return b2H(a).toLowerCase();
        }

        /**
         * @param in  - The char array
         * @param len - The prefix to convert
         * @return - The byte array
         * @brief Convert a char array to a byte array, assuming ASCI encoding
         */
        public static byte[] c2b(char[] in, int len) {
            byte[] out = new byte[len > in.length ? in.length : len];
            for (int i = 0; i < out.length; i++)
                out[i] = (byte) in[i];
            return out;
        }

        /**
         * @param in - The char array
         * @return - The byte array
         * @brief Convert a char array to a byte array, assuming ASCI encoding
         */
        public static byte[] c2b(char[] in) {
            return c2b(in, in.length);
        }

        /**
         * @param hex
         * @param nullmapstoempty Whether to treat a input array as equivalent to an empty response
         * @return
         * @brief Convert a hex string to a byte sequence
         */
        public static byte[] h2b(byte[] hex, boolean nullmapstoempty) {
            if (hex == null)
                return nullmapstoempty ? new byte[0] : null;

            int len = hex.length / 2;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (int i = 0; i < len; i++) {
                int x1 = hex[2 * i] & 0xFF;
                int x2 = hex[2 * i + 1] & 0xFF;
                String s = Character.toString((char) x1) + Character.toString((char) x2);
                int x = Integer.parseInt(s, 16);
                os.write(x);

            }
            return os.toByteArray();
        }

        public static byte[] h2b(byte[] hex) {
            return h2b(hex, false);
        }

        public static byte[] h2b(String hex) {
            return h2b(hex, false);
        }

        public static byte[] h2b(String hex, boolean nullmapstoempty) {

            if (hex == null)
                return nullmapstoempty ? new byte[0] : null;

            // By design, it returns the empty byte sequence if the argument is null. Right?
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            for (int i = 0; i < hex.length(); i++) {
                int ch = hex.charAt(i);
                os.write(ch & 0xFF);
            }

            return h2b(os.toByteArray());
        }
    }

    public static class XML {

        /**
         * @param tagName
         * @param nodes
         * @return
         * @brief Given a list of XML nodes, find the one with the given tag name
         */
        public static Node getNode(String tagName, NodeList nodes) {
            for (int x = 0; x < nodes.getLength(); x++) {
                Node node = nodes.item(x);
                if (node.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                String name = node.getLocalName();
                if (name == null)
                    name = node.getNodeName();
                if (name != null && name.equalsIgnoreCase(tagName))
                    return node;

            }
            return null;
        }

        public static String getNodeString(Node node) throws Exception {
            StringWriter w = new StringWriter();
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); // XXX right??
            t.transform(new DOMSource(node), new StreamResult(w));
            String o = w.toString();
            return o;
        }

        public static Node findNode(NodeList nl, Predicate<Node> pred) {
            try {
                Node res;

                for (int i = 0; i < nl.getLength(); i++)
                    if ((res = findNode(nl.item(i), pred)) != null)
                        return res;
            } catch (Exception ex) {
            }
            return null;
        }

        public static Node findNode(Node n, Predicate<Node> pred) {
            if (pred.eval(n))
                return n;
            // Else go over children
            return findNode(n.getChildNodes(), pred);
        }

        /**
         * @param n
         * @param id
         * @return
         * @brief we need this because Id attributes are *not* IDs by default (unless XML doc has a schema that says so)
         */
        public static Node findElementById(Node n, final String id) {
            Predicate<Node> predicate = (Node obj) -> {
                    String xid = getNodeAttr("id", obj);
                    return xid != null && xid.equals(id);
                };
            return findNode(n, predicate);
        }

        public static String getNodeValue(Node node) {
            NodeList childNodes = node.getChildNodes();
            for (int x = 0; x < childNodes.getLength(); x++) {
                Node data = childNodes.item(x);
                if (data.getNodeType() == Node.TEXT_NODE)
                    return data.getNodeValue();
            }
            return "";
        }

        /**
         * @param attrName
         * @param node
         * @return
         * @brief Get the attribute value for an attribute in a node
         */
        public static String getNodeAttr(String attrName, Node node) {
            NamedNodeMap attrs = node.getAttributes();
            // Go through the pain:
            try {
                for (int i = 0; i < attrs.getLength(); i++) {
                    String name = attrs.item(i).getLocalName();
                    if (name == null)
                        name = attrs.item(i).getNodeName();

                    if (name != null && name.equalsIgnoreCase(attrName))
                        return attrs.item(i).getNodeValue();
                }
            } catch (Exception ex) {
            }
            return null;
        }

    }

    public static class ECC {

        /**
         * @brief known curves according to Table 4-3 of GPC Ammendment E
         */
        private static final Map<Integer, AlgorithmParameterSpec> KNOWN_ECC_CURVES = new ConcurrentHashMap<Integer,
                AlgorithmParameterSpec>() {{
            put(0, new ECGenParameterSpec("P-256"));
            put(1, new ECGenParameterSpec("P-384"));
            put(2, new ECGenParameterSpec("P-512"));
            put(3, new ECGenParameterSpec("brainpoolP256r1"));
            put(4, new ECGenParameterSpec("brainpoolP256t1"));
            put(5, new ECGenParameterSpec("brainpoolP384r1"));
            put(6, new ECGenParameterSpec("brainpoolP384t1"));
            put(7, new ECGenParameterSpec("brainpoolP512r1"));
            put(8, new ECGenParameterSpec("brainpoolP512t1"));

            // For this one we use explicit Parameters for ANSSI FRP256v1:
            // https://github.com/bcgit/bc-java/blob/master/core/src/main/java/org/bouncycastle/asn1/anssi/ANSSINamedCurves.java
            BigInteger a = new BigInteger("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C00", 16);
            BigInteger b = new BigInteger("EE353FCA5428A9300D4ABA754A44C00FDFEC0C9AE4B1A1803075ED967B7BB73F", 16);
            ECFieldFp p = new ECFieldFp(new BigInteger
                    ("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C03", 16));
            EllipticCurve curve = new EllipticCurve(p, a, b);
            ECPoint G = ECPointUtil.decodePoint(curve, HEX.h2b("04"
                    + "B6B3D4C356C139EB31183D4749D423958C27D2DCAF98B70164C97A2DD98F5CFF"
                    + "6142E0F7C8B204911F9271F0F3ECEF8C2701C307E8E4C9E183115A1554062CFB"));
            BigInteger n = new BigInteger("F1FD178C0B3AD58F10126DE8CE42435B53DC67E140D2BF941FFDD459C6D655E1", 16);
            put(0x40, new ECParameterSpec(curve, G, n, 1));
        }};

        public static AlgorithmParameterSpec getParamSpec(int keyParamRef) {
            return KNOWN_ECC_CURVES.get(keyParamRef);
        }

        public static byte[] encode(ECPublicKey publicKey) throws Exception {
            // X9.62 encoding (See also Sec 3.2 of BSI TS 03111
            int f = publicKey.getParams().getCurve().getField().getFieldSize();
            int size = (f + 7) / 8;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(0x04);
            os.write(BigIntegers.asUnsignedByteArray(size, publicKey.getW().getAffineX()));
            os.write(BigIntegers.asUnsignedByteArray(size, publicKey.getW().getAffineY()));
            return os.toByteArray();
        }

        public static byte[] encode(ECPublicKey publicKey, int keyParamRef) throws Exception {
            byte[] Q = encode(publicKey);
            // Table 24 of SGP 02 v3.1 and 4-26 of Ammend. E
            return new ByteArrayOutputStream() {
                {
                    Utils.BER.appendTLV(this, (short) 0xB0, Q);
                    write(new byte[]{(byte) 0xF0, (byte) keyParamRef});
                }
            }.toByteArray();
        }

        public static byte[] encode(ECPrivateKey privateKey) throws Exception {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream baos = new ObjectOutputStream(os);
            baos.writeObject(privateKey.getS());
            return os.toByteArray();
        }

        public static ECPrivateKey decodePrivateKey(byte[] input, int keyParamRef) throws Exception {
            KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            // Get Algo
            AlgorithmParameterSpec spec = KNOWN_ECC_CURVES.get(keyParamRef);

            ECParameterSpec params;
            // http://stackoverflow.com/questions/26159149/how-can-i-get-a-publickey-object-from-ec-public-key-bytes
            if (spec instanceof ECGenParameterSpec) {
                ECGenParameterSpec xspec = (ECGenParameterSpec) spec;
                String cname = xspec.getName();
                ECNamedCurveParameterSpec ecspec = ECNamedCurveTable.getParameterSpec(cname);
                params = new ECNamedCurveSpec(cname, ecspec.getCurve(), ecspec.getG(), ecspec.getN());

            } else
                params = (ECParameterSpec) spec;
            // Decode BigInt
            ObjectInputStream bis = new ObjectInputStream(new ByteArrayInputStream(input));

            BigInteger s = (BigInteger) bis.readObject();
            ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(s, params);
            return (ECPrivateKey) kf.generatePrivate(privateKeySpec);
        }

        public static ECPublicKey decodePublicKey(byte[] input, int keyParamRef) throws Exception {
            KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
            // Get Algo
            AlgorithmParameterSpec spec = KNOWN_ECC_CURVES.get(keyParamRef);

            ECParameterSpec params;
            // http://stackoverflow.com/questions/26159149/how-can-i-get-a-publickey-object-from-ec-public-key-bytes
            if (spec instanceof ECGenParameterSpec) {
                ECGenParameterSpec xspec = (ECGenParameterSpec) spec;
                String cname = xspec.getName();
                ECNamedCurveParameterSpec ecspec = ECNamedCurveTable.getParameterSpec(cname);
                params = new ECNamedCurveSpec(cname, ecspec.getCurve(), ecspec.getG(), ecspec.getN());

            } else
                params = (ECParameterSpec) spec;

            ECPoint point = ECPointUtil.decodePoint(params.getCurve(), input);
            ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(point, params);
            return (ECPublicKey) kf.generatePublic(publicKeySpec);
        }

        public static int keyLength(ECKey key) {

            return key.getParams().getCurve().getField().getFieldSize();
        }

        public static String getHashAlgo(ECKey key) {
            // Table B-3 of GPC v2.3
            int l = keyLength(key);
            if (l <= 383)
                return "SHA256withECDSA";
            else if (l <= 511)
                return "SHA384withECDSA";
            else
                return "SHA512withECDSA";
        }

        public static byte[] sign(ECPrivateKey key, byte[] data) throws Exception {
            String algo = Utils.ECC.getHashAlgo(key);
            Signature ecdaSign = Signature.getInstance(algo, "BC");
            ecdaSign.initSign(key);

            ecdaSign.update(data);
            return ecdaSign.sign();
        }

        public static boolean verifySignature(ECPublicKey key, byte[] signature, byte[] data) throws Exception {
            String algo = ECC.getHashAlgo(key);
            Signature ecdaSign = Signature.getInstance(algo, "BC");
            ecdaSign.initVerify(key);
            ecdaSign.update(data);
            return ecdaSign.verify(signature);
        }

        public static KeyPair genKeyPair(AlgorithmParameterSpec parameterSpec) throws Exception {
            KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
            g.initialize(parameterSpec, new SecureRandom());
            KeyPair pair = g.generateKeyPair();
            return pair;
        }

        /**
         * @param keyParamRef the public key ECC parameters of the ECASD on the eUICC (as gotten from the SM-SR EIS info)
         * @return
         * @throws Exception
         * @brief Generate an ephemeral key pair using the domain parameters of the CI public key
         */
        public static KeyPair genKeyPair(int keyParamRef) throws Exception {
            return genKeyPair(getParamSpec(keyParamRef));
        }
    }


    public static class BER {

        public static int decodeTLVLen(InputStream os) throws Exception {
            Pair<Integer, Boolean> res = decodeTLVLen(os, false);
            return res.k;
        }

        /**
         * @param os
         * @param allow_ff
         * @return
         * @throws Exception
         * @brief Decode BER TLAV length, returning the length and whether FF was found as length
         */
        private static Pair<Integer, Boolean> decodeTLVLen(InputStream os, boolean allow_ff) throws Exception {
            int len = os.read() & 0xFF;
            boolean has_ff = false;

            if (len == 0x81) // Then length field is 2 bytes
                len = os.read();
            else if (len == 0x82)
                len = (int) decodeInt(os, 2);
            else if (len == 0x83)
                len = (int) decodeInt(os, 3);
            else if (allow_ff && len == 0xFF) {
                len = 1;
                has_ff = true;
            } else if (len > 127)
                len = -1; // Length unused. right?

            return new Pair<>(len, has_ff);
        }

        /**
         * @param os
         * @param len
         * @throws Exception
         * @brief append the length part of a BER TLV element
         */
        public static void appendTLVlen(OutputStream os, long len) throws Exception {
            if (len <= 127) /* code over one octet */
                os.write((int) len);
            else if (len <= 255) { /* .. over two */
                os.write(0x81);
                os.write((int) len);
            } else if (len <= 65535) {
                os.write(0x82);
                appendEncodedInteger(os, len, 2);
            } else if (len <= 16777215) {
                os.write(0x83);
                appendEncodedInteger(os, len, 3);
            } else
                throw new Exception(String.format("Invalid length [%d] in TLV argument exceeds 4 Length bytes", len));

        }

        /**
         * @param len
         * @return
         * @brief Get the size (in bytes) of the length element of a TLV, given the length element value
         */
        public static int getTlvLength(int len) {
            if (len <= 127)
                return 1;
            else if (len <= 255)
                return 2;
            else if (len <= 65535)
                return 3;
            else
                return 4;
        }

        /**
         * @param os
         * @param tag
         * @param attr
         * @param value
         * @throws Exception
         * @brief append a BER TL(A)V to the output stream, given the tag, attribute and value
         */
        public static void appendTlav(OutputStream os, short tag, int attr, byte[] value) throws Exception {
            int l = value.length;

            if (attr != 0) {
                tag |= 0x80; // Put the tag + attr flag
                l += 1;
            }
            os.write(tag);
            appendTLVlen(os, l);
            if (attr != 0)
                os.write(attr);
            os.write(value);
        }

        /**
         * @param os
         * @param tag
         * @param value
         * @throws Exception
         * @brief append a BER TLV to the output
         */
        public static void appendTLV(OutputStream os, short tag, byte[] value) throws Exception {
            appendTlav(os, tag, (short) 0, value);
        }

        /**
         * @param os
         * @param is_tlav
         * @param allow_ff
         * @return
         * @throws Exception
         * @brief Decode a BER TL(A)V
         * XXX There is a discrepancy in SGP's use of TAG 9F85 in a response TLV in SCP03t -- how?!
         */
        public static Quad<InputStream, Integer, Integer, Boolean> decodeTlav(InputStream os, boolean is_tlav, boolean allow_ff) throws Exception {

            if (os.available() < 1)
                throw new Exception("Insufficient data");
            int tag = os.read() & 0xFF;
            int len = 0, attr = 0;
            boolean had_ff = false;
            try {
                Pair<Integer, Boolean> p = decodeTLVLen(os, allow_ff);
                len = p.k; // If given.
                had_ff = p.l;
                if (len < 0)
                    tag = -1;
            } catch (Exception ex) {
                tag = -1;
            }

            if (is_tlav && (tag & 0x80) != 0) {
                // Get the attribute.
                attr = os.read() & 0xFF;
                tag &= 0x7F; // Clear top bit.
                len--; // Reduce length.
            }
            // now get the value
            byte[] out = new byte[len > 0 ? len : 0];

            int xlen = os.read(out); // Unless input is mal-formed, we should get this many bytes.


            return new Quad<>(new ByteArrayInputStream(out, 0, xlen), tag, attr, had_ff);
        }

        public static Pair<InputStream, Integer> decodeTLV(InputStream os) throws Exception {
            Quad<InputStream, Integer, Integer, Boolean> p = decodeTlav(os, false, false);
            return new Pair<>(p.k, p.l);
        }

        public static Pair<Integer, byte[]> decodeTLV(String data) throws Exception {
            return decodeTLV(HEX.h2b(data));
        }

        public static Pair<Integer, byte[]> decodeTLV(byte[] data) throws Exception {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            Pair<InputStream, Integer> xres = decodeTLV(in);
            byte[] b = Utils.getBytes(xres.k);
            return new Pair<>(xres.l, b);
        }

        // Decode the length attribute
        public static long decodeInt(InputStream os, int octets) throws Exception {
            long u = 0;
            for (int i = 0; i < octets; i++)
                u = (u << 8) | (os.read() & 0xFF);
            return u;
        }

        public static long decodeInt(byte[] in, int octets) throws Exception {
            return decodeInt(new ByteArrayInputStream(in), octets);
        }

        public static List<Pair<Integer, byte[]>> decodeTLVs(InputStream in)
                throws Exception {
            List<Pair<Integer, byte[]>> l = new ArrayList<Pair<Integer, byte[]>>();
            while (in.available() > 0) {
                Pair<InputStream, Integer> xres = decodeTLV(in);
                int tag = xres.l;
                byte[] b = Utils.getBytes(xres.k);
                l.add(new Pair<>(tag, b));
            }
            return l;
        }

        public static List<Pair<Integer, byte[]>> decodeTLVs(byte[] data) throws Exception {
            return decodeTLVs(new ByteArrayInputStream(data));
        }
    }

    public static java.security.cert.X509Certificate certificateFromBytes(byte[] cert) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            return (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(cert));
        } catch (Exception ex) {

        }
        return null;
    }

    public static Key keyFromFile(byte[] data) {
        try {
            // Adapted from https://stackoverflow.com/questions/28430603/reading-ssleay-format-private-key-using-bouncy-castle
            String s = new String(data, "UTF-8");
            PEMParser pp = new PEMParser(new StringReader(s));
            Object keyObj = pp.readObject();

            JcaPEMKeyConverter keyConverter = new JcaPEMKeyConverter();
            if (keyObj instanceof PEMKeyPair) {
                // Decide if we had a private key or public key
                KeyPair kp = keyConverter.getKeyPair((PEMKeyPair) keyObj);
                return s.contains("PRIVATE") ? kp.getPrivate() : kp.getPublic();
            } else if (keyObj instanceof PrivateKeyInfo)
                return keyConverter.getPrivateKey((PrivateKeyInfo) keyObj);
            else if (keyObj instanceof SubjectPublicKeyInfo)
                return keyConverter.getPublicKey((SubjectPublicKeyInfo) keyObj);
        } catch (Exception ex) {
            Utils.lg.error("Failed to get key from string/file: " + ex.getMessage());
        }

        return null;
    }

    /**
     * @param <K>
     * @param <L>
     * @brief A helper class representing a pair of items
     */
    public static final class Pair<K, L> {
        public K k;
        public L l;


        public Pair(final K xk, final L xl) {
            k = xk;
            l = xl;
        }
    }

    /**
     * @param <K>
     * @param <L>
     * @param <M>
     * @brief a helper class representing a tuple with three items
     */
    public static final class Triple<K, L, M> {
        public K k;
        public L l;
        public M m;

        public Triple(final K xk, final L xl, final M xm) {
            k = xk;
            l = xl;
            m = xm;
        }
    }

    /**
     * @param <K>
     * @param <L>
     * @param <M>
     * @param <O>
     * @brief a helper class representing four items
     */
    public static final class Quad<K, L, M, O> {
        public K k;
        public L l;
        public M m;
        public O o;

        public Quad(final K xk, final L xl, final M xm, final O xo) {
            k = xk;
            l = xl;
            m = xm;
            o = xo;
        }
    }

    /**
     * @brief HTTP Cookie representation
     */
    public static class HTTPCookie {
        // Cookie jar base object
        public String name = null;
        public String value = null;
        public String version = "1";
        public String path = null;
        public String domain = null;
        public String comment = null;
        public Date expires = null;

        private static boolean domainMatches(String a, String b) {
            // Domains match if they are the same.
            // Or if the second one begins with a '.' (wildcard), and the first matches it suffix-only
            return a.equalsIgnoreCase(b) ||
                    (a.length() > b.length() &&
                            (b.charAt(0) == '.' &&
                                    a.substring(a.length() - b.length()).equalsIgnoreCase(b)));
        }

        public static List<HTTPCookie> getCookies(HttpURLConnection conn) {
            List<String> l = conn.getHeaderFields().get("Set-Cookie");
            String path = conn.getURL().getPath();
            String domain = conn.getURL().getHost();

            return parseCookies(l, path, domain);
        }

        public static List<HTTPCookie> parseCookies(List<String> l, String path, String domain) {
            List<HTTPCookie> res = new ArrayList<HTTPCookie>();

            if (l != null)
                for (String cookie : l) {
                    HTTPCookie c = new HTTPCookie();
                    String cs = cookie.trim();
                    String[] xl = cs.split(";");
                    // Now go over the params
                    for (String param : xl) {
                        int i = param.indexOf('=');
                        int len = param.length();
                        String pname = param.substring(0, i >= 0 ? i : len).trim();
                        String value = i >= 0 ? param.substring(i + 1).trim() : "";

                        if (value.length() > 0 && value.charAt(0) == '"') {
                            // Remove quotes
                            value = value.substring(1, value.length() - 1);
                        }
                        // Get the type of param and use.
                        if (pname.equalsIgnoreCase("domain"))
                            c.domain = value;
                        else if (pname.equalsIgnoreCase("path"))
                            c.path = value;
                        else if (pname.equalsIgnoreCase("version"))
                            c.version = value;
                        else if (pname.equalsIgnoreCase("comment"))
                            c.comment = value;
                        else if (pname.equalsIgnoreCase("max-age"))
                            try {
                                int max_age = Integer.parseInt(value);
                                Calendar now = Calendar.getInstance();
                                now.add(Calendar.SECOND, max_age);
                                c.expires = now.getTime();
                            } catch (Exception ex) {

                            }
                        else if (pname.equalsIgnoreCase("expires")) {
                            // int _i = value.indexOf(' ');
                            // value = _i >= 0 ? value = value.substring(_i + 1) : value; // Strip day of week

                            DateFormat format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z");
                            // format.setTimeZone(TimeZone.getTimeZone("UTC")); // Is UTC time. right?
                            try {
                                c.expires = format.parse(value);
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        } else if (c.name == null) {
                            c.name = pname;
                            c.value = value;
                        }

                    }
                    // Clean up.
                    if (c.path == null) {
                        if (path != null) {
                            // As per  RFC 2109 Sec. 4.3.1.
                            int xslash = path.lastIndexOf('/');
                            c.path = path.substring(0, xslash >= 0 ? xslash : path.length());
                        } else
                            c.path = "";
                    }
                    boolean explicit_domain = false;
                    if (c.domain == null)
                        c.domain = domain;
                    else
                        explicit_domain = true;


                    // Determine whether to record the cookie. Prevent cross-site cookie stuff.
                    if (domain == null && c.name != null)
                        res.add(c);
                    else if (c.name != null) {
                        int r;
                        boolean path_matches = path.toLowerCase().startsWith(c.path.toLowerCase());
                        boolean wildcard_domain = c.domain.charAt(0) == '.' && c.domain.indexOf('.', 1) > 0;
                        boolean domains_match = domain != null ? domainMatches(domain, c.domain) : false;
                        boolean suffix_match = domains_match ? (r = domain.indexOf('.')) >= domain.length() - c.domain.length() ||
                                r < 0 : false;

                        if (path_matches &&
                                (explicit_domain == false ||
                                        wildcard_domain && domains_match && suffix_match))
                            res.add(c); // Add it
                    }
                }
            return res;
        }

        public static void putCookies(HttpURLConnection conn, List<HTTPCookie> clist) {
            String sep = "";
            String cs = "";
            for (HTTPCookie c : clist) {
                cs += String.format("%s%s=\"%s\"", sep, c.name, c.value);
                sep = ";";
            }
            if (cs.length() > 0)
                conn.setRequestProperty("Cookie", cs);
        }

        public String toString() {
            String res = String.format("%s=\"%s\"", name, value);
            if (path != null)
                res += String.format(";path=\"%s\"", path);

            if (version != null)
                res += String.format(";version=\"%s\"", version);
            if (domain != null)
                res += String.format(";domain=\"%s\"", domain);

            if (comment != null)
                res += String.format(";comment=\"%s\"", comment);

            if (expires != null) {
                DateFormat format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z");
                format.setTimeZone(TimeZone.getTimeZone("UTC")); // Is UTC time. right?

                res += String.format(";expires=\"%s\"", format.format(expires));
            }

            return res;
        }


    }

    /**
     * @brief A CGI parameter decoder
     */
    public static class CGIDecoder {
        /**
         * @param params
         * @return
         * @throws Exception
         * @brief Our own URL decoder, so that we can get binary param values
         * Value is either a byte[] or a List<byte[]> if multiple params exist.
         */
        public static Map<String, Object> parseCGIStr(String params) throws Exception {
            String[] l = params.split("&");
            Map<String, Object> plist = new HashMap<String, Object>();

            for (String xs : l) {
                String[] xl = xs.split("=", 2);
                String pname = xl[0];
                String pvalue = xl[1];

                byte[] bVal = urlDecode(pvalue);
                Object o = plist.get(pname);
                if (o == null)
                    plist.put(pname, bVal);
                else {
                    // Value should become an array.
                    List<byte[]> ol;
                    if (o instanceof List)
                        ol = (List<byte[]>) o;
                    else { // Must be a byte[]
                        byte[] existing = (byte[]) o;
                        ol = new ArrayList<byte[]>();
                        ol.add(existing);
                        plist.put(pname, ol); // Put it back.
                    }
                    ol.add(bVal);
                }
            }
            return plist;
        }

        private static byte[] urlDecode(String val) throws Exception {
            int len = val.length();
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            for (int i = 0; i < len; i++) {
                char ch = val.charAt(i);
                int b;
                if (ch == '+')
                    b = ' ';
                else if (ch == '%') {
                    String xs = val.substring(i + 1, i + 1 + 2);
                    b = Integer.parseInt(xs, 16);
                    i += 2;
                } else
                    b = ch;
                os.write(b);
            }
            return os.toByteArray();
        }
    }

    /**
     * @brief This class is a helper for parsing HTTP Messages (requests and response). We need it because
     * the Smartcard Webserver makes a RAW request after a PSK-TLS handshake -- No clean way to hand that off to the
     * JBOSS JAX APIs as far as we know, so best we parse ourselves
     */
    public static class Http {
        public enum Method {
            GET, POST, HEAD, PUT, DELETE;

            public static Method fromString(String method) {
                try {
                    if (method.equalsIgnoreCase("POST"))
                        return POST;
                    if (method.equalsIgnoreCase("PUT"))
                        return PUT;
                    if (method.equalsIgnoreCase("DELETE"))
                        return DELETE;
                    if (method.equalsIgnoreCase("HEAD"))
                        return HEAD;
                } catch (Exception ex) {
                }
                return GET;
            }

            public boolean hasBody() {
                return this == POST || this == PUT;
            }
        }

        /**
         * @brief This represents a standard MIME/HTTP Message type
         */
        private static abstract class Message {
            private static final boolean USE_CHUNKED_IN_OUTPUT = false;
            public byte[] body = null; //!< The message body
            public Map<String, String> headers = new HashMap<String, String>(); //!< The message headers
            public double version = 1.0; //!< The HTTP Version
            public boolean keepAlive = false; //!< Whether keep-alive is in use

            protected boolean chunked = false; //!< Whether chunked encoding is in use
            protected boolean hasTrailers = false; //!< Whether the chunked body has trailers
            protected int bodyLen = 0; //!< The HTTP Body length

            /**
             * @param in
             * @return
             * @throws Exception
             * @brief Read a single UTF-8 line from the input as per HTTP specifications
             */
            private static String readLine(InputStream in) throws Exception {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                while (true) {
                    int b = in.read();
                    if (b < 0) {
                        throw new IOException("Data truncated");
                    }
                    if (b == 0x0A) {
                        break;
                    }
                    buffer.write(b);
                }
                String res = new String(buffer.toByteArray(), "UTF-8");
                if (res.length() > 0 && res.charAt(res.length() - 1) == '\r')
                    res = res.substring(0, res.length() - 1); // Remove the '\r'
                return res;
            }

            /**
             * @param in
             * @param len
             * @return
             * @throws Exception
             * @brief Read a fixed-length chunk of data from the HTTP input
             */
            private static byte[] readBytes(InputStream in, int len) throws Exception {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] out = new byte[len];
                int bleft = len;

                while (bleft > 0) {
                    int x = in.read(out, 0, bleft);
                    bleft -= x;
                    os.write(out, 0, x);
                }
                return os.toByteArray();
            }

            /**
             * @param line
             * @return
             * @brief Get the chunked element size from the proceeding flow
             */
            private int getChunkSize(String line) {
                int len;
                try {
                    len = Integer.parseInt(line.trim().split("\\s+")[0], 16);
                } catch (Exception ex) {
                    len = 0;
                }
                return len;
            }

            /**
             * @return
             * @brief Return the server time in HTTP standard format
             */
            protected final String getServerTime() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                return dateFormat.format(calendar.getTime());
            }

            /**
             * @return
             * @brief Tell us whether this HTTP request/response has a body.
             */
            protected boolean hasBody() {
                return false;
            }

            /**
             * @param closeConn
             * @brief Issue a HTTP close header
             */
            public void setCloseConnection(boolean closeConn) {
                headers.put("Connection", closeConn ? "close" : "keep-alive");
            }

            /**
             * @param in
             * @throws Exception
             * @brief Read a full HTTP message: Start line, headers, body
             */
            protected void readMessage(InputStream in) throws Exception {
                parseStartLine(readLine(in));
                readHeaders(in);
                readBody(in);
            }

            /**
             * @param in
             * @throws Exception
             * @brief Read the message headers, return whether body is chunked and whether we have trailers.
             */
            private void readHeaders(InputStream in) throws Exception {
                String s;
                while ((s = readLine(in)) != null && !s.isEmpty()) {
                    String[] xl = s.split(":");
                    String header = xl[0].trim();
                    String value = xl[1].trim();

                    if (header.equalsIgnoreCase("Connection") &&
                            !value.equalsIgnoreCase("keep-alive"))
                        keepAlive = false; // Close the connection
                    if (header.equalsIgnoreCase("Content-Length")) {
                        try {
                            bodyLen = Integer.parseInt(value);
                        } catch (Exception ex) {
                            bodyLen = 0;
                        }
                    } else if (header.equalsIgnoreCase("TE")) {
                        hasTrailers = value.equalsIgnoreCase("trailers"); // As per sec 14.39 of RFC 2616
                    } else if (header.equalsIgnoreCase("Transfer-Encoding")) {
                        chunked = value.toLowerCase().contains("chunked");
                    } else
                        headers.put(header, value);

                }
                String xout = "";
                for (Map.Entry<String, String> e : headers.entrySet())
                    xout += String.format("\nHttp Incoming Header %s: %s", e.getKey(), e.getValue());
                Utils.lg.info(xout);
            }


            /**
             * @param in
             * @throws Exception
             * @brief Read an HTTP message body, taking into account whether the headers indicated
             * chunked encoding, or it has a body, and the body length (from the Content-Length header)
             */
            private void readBody(InputStream in) throws Exception {
                // Body follows.
                String s;
                if (!hasBody())
                    bodyLen = 0; // Regardless
                else if (chunked) {
                    // Read chunked: Sec 3.6.1 of the RFC
                    int len;
                    ByteArrayOutputStream xos = new ByteArrayOutputStream();
                    while ((s = readLine(in)) != null && !s.trim().isEmpty() && (len = getChunkSize(s)) > 0) {
                        xos.write(readBytes(in, len)); // Read each chunk, append it.
                        String trailer = readLine(in); // Remove the chunk trailer.
                    }

                    body = xos.toByteArray();
                    if (hasTrailers) { // Get and add trailer headers
                        while ((s = readLine(in)) != null && !s.isEmpty()) {
                            String[] xl = s.split(":");
                            String header = xl[0].trim();
                            String value = xl[1].trim();

                            headers.put(header, value);
                        }
                        readLine(in); // Remove the last CRLF
                    }

                } else
                    body = readBytes(in, bodyLen);

                Utils.lg.info(String.format("HTTP server, body received: %s", HEX.b2H(body)));
            }

            /**
             * @param line
             * @throws Exception
             * @brief Get the start line of the HTTP Message
             */
            protected void parseStartLine(String line) throws Exception {
                throw new UnsupportedOperationException();
            }

            /**
             * @param proto
             * @brief Get/parse the HTTP protocol version
             */
            protected final void parseVersionFromProtocol(String proto) {
                // Get version
                try {
                    String ver = proto.split("/")[1];
                    version = Double.parseDouble(ver);
                } catch (Exception ex) {
                }
                keepAlive = (version > 1.0); // Keep alive...
            }

            /**
             * @param out
             * @throws Exception
             * @brief Print out the first line of an HTTP Message
             */
            protected void printFirstLine(OutputStream out) throws Exception {
                throw new UnsupportedOperationException();
            }

            /**
             * @param out
             * @throws Exception
             * @brief Print/send out a HTTP Message: First line, then headers, then body (if any).
             */
            public final void outputMessage(OutputStream out) throws Exception {
                ByteArrayOutputStream xos = new ByteArrayOutputStream();
                printFirstLine(xos);
                xos.write("\r\n".getBytes("UTF-8"));
                // Now print the headers
                // Write headers
                for (Map.Entry<String, String> m : headers.entrySet()) {
                    String k = m.getKey().replace("\n", "").replace("\r", "");
                    String v = m.getValue().replace("\n", "").replace("\r", "");
                    if (!k.equalsIgnoreCase("content-length")) // Send it last. Right? Stupid SIMs which don't read the HTTP spec!!!
                        xos.write(String.format("%s: %s\r\n", k, v).getBytes("UTF-8"));
                }
                boolean xhasBody = hasBody() && body != null;
                boolean useChunked = USE_CHUNKED_IN_OUTPUT && version > 1.0;
                if (!useChunked) {
                    String ctype = headers.get("Content-Length");
                    if (ctype != null && hasBody()) // Don't output content length unless it is expected
                        xos.write(String.format("%s: %s\r\n", "Content-Length", ctype).getBytes("UTF-8"));
                } else if (xhasBody) { // Using chunked
                    xos.write(String.format("Transfer-Encoding: chunked\r\n").getBytes("UTF-8"));
                }

                xos.write("\r\n".getBytes("UTF-8"));
                if (xhasBody) {
                    byte[] pre = (useChunked) ? String.format("%X\r\n", body.length).getBytes("UTF-8") : new byte[0];
                    byte[] post = (useChunked) ? "\r\n".getBytes("UTF-8") : new byte[0];

                    xos.write(pre);
                    xos.write(body);
                    xos.write(post);
                }
                byte[] xout = xos.toByteArray();
                out.write(xout); // Write all at once. Right?
                out.flush();
                try {
                    // Log it:
                    String xs = new String(xout, "ASCII");
                    Utils.lg.info("HTTP Response going out as txt[" + xs + "], bin[" + HEX.b2H(xout) + "]");
                } catch (Exception ex) {
                    String ss = ex.getLocalizedMessage();
                }
            }
        }

        /**
         * @brief This is an HTTP Request. It is essentially an HTTP Message with special rules
         */
        public static class Request extends Message {
            public Method method = Method.GET;
            public String uri;
            public String uriVerb;
            public String[] args;
            public Map<String, Object> cgiParams; // CGI Parameters
            public String fragment;

            /**
             * @param in
             * @throws Exception
             * @brief Read a Request message from the input
             */
            public Request(InputStream in) throws Exception {
                readMessage(in);
            }

            /**
             * @param method
             * @param uri
             * @param rHeaders
             * @param body
             * @param closeConn
             * @param ctype
             * @brief Make a HTTP Request directly
             */
            public Request(Method method, String uri, Map<String, String> rHeaders, byte[] body, boolean closeConn, String ctype) {
                version = 1.1;

                this.method = method;
                headers = new HashMap<String, String>(rHeaders != null ? rHeaders : new HashMap<String, String>());
                headers.put("Date", getServerTime());
                headers.put("User-Agent", "MY server");
                headers.put("Content-Length", "" + body.length);

                setCloseConnection(closeConn);


                // Fixup the content type
                switch (method) {
                    case GET:
                    case DELETE:
                    case HEAD:
                        ctype = null;
                        break;
                    case POST:
                        if (ctype == null)
                            ctype = "application/x-www-form-urlencoded";
                        break;
                    case PUT:
                        if (ctype == null)
                            ctype = "application/octet-stream";
                        break;
                }

                if (ctype != null)
                    headers.put("Content-Type", ctype);
                this.body = body;
                this.uri = uri;
            }

            /**
             * @param line
             * @throws Exception
             * @brief a Request message always has a request line of the form METHOD URL VERSION
             */
            @Override
            protected void parseStartLine(String line) throws Exception {
                String[] xl = line.split("\\s+", 3);

                method = Method.fromString(xl[0].trim().toUpperCase());
                uri = xl[1].trim();
                parseVersionFromProtocol(xl[2]);

                // Look for fragment
                int i = uri.indexOf("#");
                if (i >= 0) {
                    fragment = uri.substring(i + 1);
                    uri = uri.substring(0, i);
                }
                // Look for ? and therefore parameters

                i = uri.indexOf("?");
                if (i >= 0) {
                    String xs = uri.substring(i + 1);
                    uri = uri.substring(0, i);
                    cgiParams = CGIDecoder.parseCGIStr(xs);
                } else
                    cgiParams = new ConcurrentHashMap<String, Object>();
                // Get verbs
                xl = uri.split("/");
                int offset = xl[0].length() == 0 ? 1 : 0; // If the URL begins with a '/' then the verb is the string right after it.
                uriVerb = xl[offset];
                int argsLen = xl.length - 1 - offset;
                args = new String[argsLen >= 0 ? argsLen : 0];
                System.arraycopy(xl, offset + 1, args, 0, args.length);
            }

            /**
             * @return
             * @brief Whether it has a body depends on the request method
             */
            @Override
            protected boolean hasBody() {
                return method.hasBody();
            }

            /**
             * @param out
             * @throws Exception
             * @brief The request first line is always of the form METHOD URI VERSION
             */
            @Override
            protected void printFirstLine(OutputStream out) throws Exception {
                String xs = String.format("%s %s HTTP/%.1f", method, uri, version);
                out.write(xs.getBytes("UTF-8"));
            }

        }

        /**
         * @brief this is an HTTP Response message
         */
        public static class Response extends Message {
            public javax.ws.rs.core.Response.Status status = javax.ws.rs.core.Response.Status.OK;
            public String statusMsg;

            public Response(InputStream in) throws Exception {
                readMessage(in);
            }

            public Response(javax.ws.rs.core.Response.Status rStatus, Map<String, String> rHeaders,
                            String ctype,
                            byte[] body,
                            boolean closeConn) {
                headers = new HashMap<String, String>(rHeaders != null ? rHeaders : new HashMap<String, String>());

                headers.put("Date", getServerTime());
                //  headers.put("Server", "My server");

                if (closeConn)
                    headers.put("Connection", "close");
                if (ctype != null)
                    headers.put("Content-Type", ctype);
                this.status = rStatus;
                this.statusMsg = rStatus.getReasonPhrase();
                this.body = body != null ? body : new byte[0];
                if (this.body.length == 0 && rStatus == javax.ws.rs.core.Response.Status.OK)
                    rStatus = javax.ws.rs.core.Response.Status.NO_CONTENT; // Fix it up
                if (rStatus != javax.ws.rs.core.Response.Status.NO_CONTENT)
                    headers.put("Content-Length", "" + this.body.length);
            }

            @Override
            protected boolean hasBody() {
                int code = status.getStatusCode();
                if ((code / 100 == 1) ||
                        (code == 204) ||
                        (code == 304))
                    return false;
                return true;
            }

            /**
             * @param out
             * @throws Exception
             * @brief The first line is always of the form VERSION STATUS [STATUS_MESSAGE]
             */
            @Override
            protected void printFirstLine(OutputStream out) throws Exception {
                String xs = String.format("HTTP/%.1f %s %s", version, status.getStatusCode(),
                        statusMsg != null ? statusMsg : status.getReasonPhrase());
                out.write(xs.getBytes("UTF-8"));
            }

            /**
             * @param line
             * @throws Exception
             * @brief The first line is always of the form VERSION STATUS [STATUS_MESSAGE]
             */
            @Override
            protected void parseStartLine(String line) throws Exception {
                String[] xl = line.split("\\s+", 3);

                parseVersionFromProtocol(xl[0]);

                try {
                    int xstatus = Integer.parseInt(xl[1]);
                    status = javax.ws.rs.core.Response.Status.fromStatusCode(xstatus);
                } catch (Exception ex) {
                    status = javax.ws.rs.core.Response.Status.BAD_REQUEST;
                }
                try {
                    statusMsg = xl[2]; // Status
                } catch (Exception ex) {
                    statusMsg = "";
                }


            }
        }
    }


}
