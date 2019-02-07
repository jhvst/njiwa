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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @brief Represents a command destined for an SD-P or SD-R. Either a CAPDU or a TLV (e.g. for SCP03t)
 */
public abstract class SDCommand {
    public static final short C_APDU_TAG = 0x22;
    protected short tag;
    // Map
    // tag to class
    // public byte[] data;

    /**
     * @param input
     * @return
     * @throws Exception
     * @brief break down the CAPDU list into different TLVs
     */
    public static List<byte[]> deconstruct(byte[] input) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(input);
        List<byte[]> l = new ArrayList<byte[]>();

        while (in.available() > 0) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Utils.Pair<InputStream, Integer> x = Utils.BER.decodeTLV(in);
            int tag = x.l;
            byte[] data = Utils.getBytes(x.k);
            if (tag != C_APDU_TAG)
                throw new Exception("Invalid TAG, expected: %s" + String.format("%02x", C_APDU_TAG));
            Utils.BER.appendTLV(os, C_APDU_TAG, data);
            l.add(os.toByteArray());
        }
        return l;
    }

    public static List<byte[]> deconstruct(String input) throws Exception {
        return deconstruct(Utils.HEX.h2b(input));
    }

    protected List<byte[]> toByteArrays() throws Exception {
        throw new Exception("Not implemented");
    }

    public final byte[] toByteArray() throws Exception {
        List<byte[]> l = toByteArrays();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        for (byte[] b : l)
            Utils.BER.appendTLV(os, C_APDU_TAG, b); // All data sent as CAPDUs
        return os.toByteArray();
    }

    protected void read(InputStream in, int tag) throws Exception {
        throw new Exception("Not implementeed");
    }

    /*
     * Appends the data to be used for computing the CMAC. This the data after the mac chaining value, and including
     * the command data
     */
    public void appendDataForCMAC(OutputStream os, byte[] edata) throws Exception {
        throw new Exception("Not Implemented");
    }

    /**
     * Represents an APDU as per Sec 8.35 of ETSI TS 102 223
     */
    public static class APDU extends SDCommand {
        private static final long serialVersionUID = 1L;
        public static final int STORE_COMMAND = 0xe2;
        private static final Map<String, String> uiccErrorMap = new ConcurrentHashMap<String, String>() {{
            put("62,00", "Warning (no specific info);");
            put("62,81", "Part of the returned data may be corrupted");
            put("62,82", "End of file or record reached before reading Le bytes");
            put("62,83", "Selected file deactivated");
            put("62,84", "FCI not formatted correctly");
            put("63,00", "Warning (no specific info);");
            put("63,81", "File filled up by last write");
            put("64,00", "Error, state of memory unchanged");
            put("65,00", "Error, state of memory changed");
            put("65,81", "Error, memory failure");
            put("67,00", "Wrong Length, Lc/P3");
            put("68,00", "Function in CLA not supported");
            put("68,81", "Logical channels not supported");
            put("68,82", "Secure Messaging not supported");
            put("69,00", "Command not allowed");
            put("69,81", "Command is incompatible with the file structure");
            put("69,82", "Access condition is not fulfilled");
            put("69,83", "Authentication method blocked");
            put("69,84", "Reference data invalidated");
            put("69,85", "Conditions of use not satisfied/Referenced data cannot be deleted");
            put("69,86", "Command not allowed (there is no Current EF);");
            put("69,87", "Expected Secure Message data object missing");
            put("69,88", "Secure Message data object incorrect");
            put("69,E1", "POL1 of the profile prevents this action");
            put("6A,00", "Wrong parameters in P1-P2");
            put("6A,80", "Incorrect Parameter in the data field");
            put("6A,81", "Function not supported");
            put("6A,82", "File not found/Applet not found");
            put("6A,83", "Record not found");
            put("6A,84", "Not enough memory space in file");
            put("6A,85", "Lc/P3 inconsistent");
            put("6A,86", "Incorrect parameters P1-P2");
            put("6A,87", "Lc inconsistent with P1-P2");
            put("6A,88", "Referenced data not found");
            put("6A,89", "File already exists");
            put("6B,00", "Wrong parameters P1-P2");
            put("6C,00", "Wrong length, Le");
            put("6C,00", "Wrong length (XX indicates exact length);");
            put("6D,00", "Instruction code not supported or invalid");
            put("6E,00", "Instruction class not supported");
            put("6F,00", "Technical problem");
            put("94,00", "No EF selected");
            put("94,02", "Out of range (invalid address);");
            put("94,04", "File ID or pattern not found");
            put("94,08", "File is inconsistent with the command");
            put("98,02", "No CHV initialized");
            put("98,04", "Access condition not fulfilled");
            put("98,08", "In contradiction of CHV status");
            put("98,10", "In contradiction of invalidation status");
            put("98,40", "CHV blocked/unblock CHV blocked");
            put("98,50", "Increase cannot be performed, Max value reached");
            put("9F,00", "Command successfully completed, result length given by lower byte of result code");
            put("90,00", "Command successfully completed");
            put("94,84", "Algorithm not supported");

        }};
        public short cla, ins, p1, p2;
        public Short Le; // Optional
        public byte[] data; // Could be NULL. And Lc is implied from this


        public APDU() {
            tag = C_APDU_TAG;
        }

        public APDU(int cla, int ins, int p1, int p2, byte[] data, Short Le) {
            this.cla = (short) cla;
            this.ins = (short) ins;
            this.p1 = (short) p1;
            this.p2 = (short) p2;
            this.data = data;
            this.Le = Le;
            tag = C_APDU_TAG;
        }

        public APDU(int cla, int ins, int p1, int p2, byte[] data) {
            this(cla, ins, p1, p2, data, null);
        }

        public static boolean isSuccessCode(int code) {
            return (code == 0x90) || (code == 0x9F) || (code == 0x61);
        }

        public static String euiccError2Str(int sw1, int sw2) {
            String res;

            if ((res = uiccErrorMap.get(String.format("%02X,%02X", sw1, sw2))) != null)
                return res;
            if ((res = uiccErrorMap.get(String.format("%02X,00", sw1, 00))) != null)
                return res;
            return String.format("%02X%02X", sw1, sw2);
        }

        // Parse it from a byte stream
        @Override
        protected void read(InputStream in, int tag) throws Exception {
            cla = (short) in.read();
            ins = (short) in.read();
            p1 = (short) in.read();
            p2 = (short) in.read();

            if (in.available() > 0) {
                int Lc = in.read();
                data = new byte[Lc];
                in.read(data);
                if (in.available() > 0)
                    Le = (short) in.read();
                else
                    Le = null;
            } else {
                data = null;
                Le = null;
            }
        }

        public List<APDU> splitCommand() {
            List<APDU> l = new ArrayList<APDU>();
            if (data != null && data.length > 255) {
                // Split it and add them.
                for (int offset = 0, blockNo = 0; offset < data.length; offset += 255, blockNo += 1) {
                    boolean lastBlock = (offset + 255) >= data.length;
                    int p1Mask = lastBlock ? 0 : 1 << 7;
                    int xp2 = putBlockNumberInP2() ? blockNo : p2;
                    int len = !lastBlock ? 255 : data.length - offset;
                    byte[] xdata = new byte[len];
                    System.arraycopy(data, offset, xdata, 0, xdata.length);
                    APDU c = new APDU(cla, ins, p1 | p1Mask, xp2, xdata, lastBlock ? Le : null);
                    l.add(c);
                }
            } else
                l.add(this);
            return l;
        }

        protected byte[] bytes() throws Exception {
            if (data != null && data.length > 255)
                throw new Exception("Invalid! Cannot convert C-APDU to bytes with data length > 255. Split it first!");
            ByteArrayOutputStream xos = new ByteArrayOutputStream();
            xos.write(new byte[]{
                    (byte) cla,
                    (byte) ins,
                    (byte) p1,
                    (byte) p2,
            });
            if (data != null) {
                xos.write((byte) data.length);
                xos.write(data);
            }
            if (Le != null)
                xos.write((byte) (short) Le);
            return xos.toByteArray();
        }

        @Override
        protected List<byte[]> toByteArrays() throws Exception {
            List<APDU> l = splitCommand();
            List<byte[]> ol = new ArrayList<byte[]>();

            for (APDU c : l)
                ol.add(c.bytes());
            return ol;
        }

        private boolean putBlockNumberInP2() {
            return ins == STORE_COMMAND;
        }

        @Override
        public void appendDataForCMAC(OutputStream os, byte[] edata) throws Exception {
            // As per Sec 6.2.4 of GPCS Amendment D, combined with Sec 4.9.1 of GPCS Amendment E
            // XX Sec 4.9.1 seems to have the word *without* when it seems to mean *with*
            int lcc = edata.length + 8;
            os.write(0x84); // Class byte
            if (lcc > 255) {
                os.write(p1 & 0x7F);
                int lastblockNo = (lcc / 255) + (lcc % 255 == 0 ? 0 : 1); // add 1 for the extra bit at end
                int xp2 = putBlockNumberInP2() ? lastblockNo : p2;
                os.write(xp2 & 0xFF);
                // Length is written over 3 bytes as per GPCS Amend. E
                // See also http://www.cardwerk.com/smartcards/smartcard_standard_ISO7816-4_5_basic_organizations.aspx#table5
                os.write(0x00); // Leading zero, followed by big-endian representation of the length, over two bytes
                Utils.appendEncodedInteger(os, lcc, 2);
            } else {
                os.write(p1 & 0xFF);
                os.write(p2 & 0xFF);
                os.write(lcc & 0xFF);
            }
            os.write(edata);
        }

    }


    /**
     * @brief Represents an SCP03t command as per Sec 4.1.3.3 of SGP v3.0
     */
    public static class SCP03tCommand extends SDCommand {
        public byte[] data;

        public SCP03tCommand() {
            tag = 0x84; // I guess
            data = null;
        }

        public SCP03tCommand(int tag, byte[] data) {
            this.tag = (short) tag;
            this.data = data;
        }

        public static SCP03tCommand ProfileElement(byte[] chunk) {
            return new SCP03tCommand(0x86, chunk);
        }

        @Override
        protected List<byte[]> toByteArrays() throws Exception {
            List<byte[]> l = new ArrayList<>();
            l.add(data);
            return l;
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = (short) tag;
        }

        @Override
        protected void read(InputStream in, int tag) throws Exception {
            this.tag = (short) tag;
            data = Utils.getBytes(in);
        }

        @Override
        public void appendDataForCMAC(OutputStream os, byte[] edata) throws Exception {
            os.write(getTag() & 0xFF);
            int lcc = Utils.BER.getTlvLength(edata.length + 8);
            Utils.BER.appendTLVlen(os, lcc);
            os.write(edata);
        }

    }
}
