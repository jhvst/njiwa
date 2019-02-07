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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/*
* Copyright (c) 2008, Daniel Widyanto <kunilkuda at gmail.com>
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY Daniel Widyanto ''AS IS'' AND ANY
* EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL Daniel Widyanto BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/**
 * @addtogroup g_utils
 * @{
 */
 /** @brief  Implement basic character set conversions
 */
public class Charset {


    /**
     * \brief Provides services to translate ASCII (ISO-8859-1 / Latin 1) charset
     *        into GSM 03.38 character set
     */

    /**
     * \brief Escape byte for the extended ISO
     */
    private static final Short ESC_BYTE = new Short((short) 27);

    /**
     * \brief ISO-8859-1 - GSM 03.38 character map
     * <p/>
     * Taken from http://www.dreamfabric.com/sms/default_alphabet.html
     */
    private static final short[] isoGsmMap = {
            // Index = GSM, { ISO }
            64, 163, 36, 165, 232, 233, 249, 236, 242, 199, 10, 216,
            248, 13, 197, 229, 0, 95, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 198, 230, 223, 201, 32, 33, 34, 35,
            164, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47,
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
            60, 61, 62, 63, 161, 65, 66, 67, 68, 69, 70, 71,
            72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83,
            84, 85, 86, 87, 88, 89, 90, 196, 214, 209, 220, 167,
            191, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107,
            108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
            120, 121, 122, 228, 246, 241, 252, 224
    };

    /**
     * \brief Extended ISO-8859-1 - GSM 03.38 character map
     * <p/>
     * Taken from http://www.dreamfabric.com/sms/default_alphabet.html
     */
    private static final short[][] extIsoGsmMap = {
            //{ {Ext GSM,ISO} }
            {10, 12}, {20, 94}, {40, 123}, {41, 125}, {47, 92},
            {60, 91}, {61, 126}, {62, 93}, {64, 124}, {101, 164}
    };

    /**
     * \brief Translate ISO-8859-1 character set into GSM 03.38 character set
     * \param dataIso Data in ISO-8859-1 charset
     * \return GSM03.38 character set in byte array
     */
    public static byte[] translateToGsm0338(String dataIso) throws Exception {
        byte[] dataIsoBytes = dataIso.getBytes();
        ArrayList<Short> dataGsm = new ArrayList<Short>();

        for (int dataIndex = 0; dataIndex < dataIsoBytes.length; dataIndex++) {
            byte currentDataIso = dataIsoBytes[dataIndex];

            // Search currentDataGsm in the isoGsmMap
            short currentDataGsm = findGsmChar(currentDataIso);

            // If the data is not available in the isoGsmMap, search in the extended
            // ISO-GSM map (extIsoGsmMap)
            if (currentDataGsm == -1) {
                currentDataGsm = findExtGsmChar(currentDataIso);

                // If the character is found inside the extended map, add escape byte in
                // the return byte[]
                if (currentDataGsm != -1) {
                    dataGsm.add(ESC_BYTE);
                } else
                    throw new Exception("Cannot convert string to GSM alphabet");
            }

            dataGsm.add(new Short(currentDataGsm));
        }

        Short[] dataGsmShortArray =  dataGsm.toArray(new Short[0]);
        return translateShortToByteArray(dataGsmShortArray);
    }

     public static final int INLINE_7BIT = 1;
     public static final int INLINE_UCS2 = 2;

     public static String convertToUTF8(byte[] input, int attr) throws Exception
     {
         if (attr == INLINE_7BIT)
             return translateToIso(input);
         else if (attr == INLINE_UCS2)
             return new String(input, "UTF-16BE");
         else
             return new String(input); // Default encoding (UTF-8, right?)
     }

     public static byte[] decode7Bituncompressed(byte[] input, int len, int offset, boolean convert_to_utf8) throws Exception
     {
         int septet, octet, prevoctet;
         int i;
         int r = 1;
         int c = 7;
         int pos = 0;
         ByteArrayOutputStream decoded = new ByteArrayOutputStream();

     /* Shift the buffer offset bits to the left */
         if (offset > 0) {
             int ip;
             for (i = 0, ip = 0; i < input.length; i++) {
                 if (i == input.length - 1)
                     input[ip] = (byte) (input[ip] >> offset);
                 else
                     input[ip] = (byte) ((input[ip] >> offset) | (input[ip + 1] << (8 - offset)));
                 ip++;
             }
         }
         octet = input[pos];
         prevoctet = 0;

         for (i = 0; i < len; i++) {
             septet = ((octet & at2_rmask[c]) << (r - 1)) + prevoctet;
             decoded.write(septet);

             prevoctet = (octet & at2_lmask[r]) >> c;

         /* When r=7 we have a full character in prevoctet */
             if ((r == 7) && (i < len - 1)) {
                 i++;
                 decoded.write(prevoctet);
                 prevoctet = 0;
             }

             r = (r > 6) ? 1 : r + 1;
             c = (c < 2) ? 7 : c - 1;

             pos++;
             octet = input[pos];
         }

         if (convert_to_utf8)
             return convertToUTF8(decoded.toByteArray(), INLINE_7BIT).getBytes("UTF-8");
         else
             return decoded.toByteArray();
     }


     /**
     * \brief Translate GSM 03.38 character set set into ISO-8859-1 character
     * \param dataGsm Data in GSM 03.38 charset
     * \return ISO-8859-1 string
     */
    public static String translateToIso(byte[] dataGsm) {
        ArrayList<Short> dataIso = new ArrayList<Short>();

        boolean isEscape = false;
        for (int dataIndex = 0; dataIndex < dataGsm.length; dataIndex++) {
            // Convert to short to avoid negative values
            short currentDataGsm = (short) dataGsm[dataIndex];
            short currentDataIso = -1;

            if (currentDataGsm == ESC_BYTE.shortValue()) {
                isEscape = true;
            } else if (!isEscape) {
                currentDataIso = findIsoChar(currentDataGsm);
                dataIso.add(new Short(currentDataIso));
            } else {
                currentDataIso = findExtIsoChar(currentDataGsm);
                dataIso.add(new Short(currentDataIso));
                isEscape = false;
            }
        }

        Short[] dataIsoShortArray =  dataIso.toArray(new Short[0]);
        byte[] dataIsoByteArray = translateShortToByteArray(dataIsoShortArray);
        return new String(dataIsoByteArray);
    }

    /**
     * \brief Find GSM 03.38 character for the ISO-8859-1 character
     * \param isoChar ISO-8859-1 character
     * \result GSM 03.38 character or -1 if no match
     */
    private static short findGsmChar(byte isoChar) {
        short gsmChar = -1;

        for (short mapIndex = 0; mapIndex < isoGsmMap.length; mapIndex++) {
            if (isoGsmMap[mapIndex] == (short) isoChar) {
                gsmChar = mapIndex;
                break;
            }
        }

        return gsmChar;
    }

    /**
     * \brief Find extended GSM 03.38 character for the ISO-8859-1 character
     * \param isoChar ISO-8859-1 character
     * \result Extended GSM 03.38 character or 0xFFFF (-1) if no match
     */
    private static short findExtGsmChar(byte isoChar) {
        short gsmChar = -1;

        for (short mapIndex = 0; mapIndex < extIsoGsmMap.length; mapIndex++) {
            if (extIsoGsmMap[mapIndex][1] == isoChar) {
                gsmChar = extIsoGsmMap[mapIndex][0];
                break;
            }
        }

        return gsmChar;
    }

    /**
     * \brief Find ISO-8859-1 character for the GSM 03.38 character
     * \param gsmChar GSM 03.38 character
     * \result ISO-8859-1 character or -1 if no match
     */
    private static short findIsoChar(short gsmChar) {
        short isoChar = -1;

        if (gsmChar < isoGsmMap.length) {
            isoChar = isoGsmMap[gsmChar];
        }

        return isoChar;
    }

    /**
     * \brief Find ISO-8859-1 character for the extended GSM 03.38 character
     * \param gsmChar Extended GSM 03.38 character
     * \result ISO-8859-1 character or 0xFFFF (-1) if no match
     */
    private static short findExtIsoChar(short gsmChar) {
        short isoChar = -1;

        for (short mapIndex = 0; mapIndex < extIsoGsmMap.length; mapIndex++) {
            if (extIsoGsmMap[mapIndex][0] == gsmChar) {
                isoChar = extIsoGsmMap[mapIndex][1];
                break;
            }
        }

        return isoChar;
    }

    /**
     * \brief Translate Short[] array to byte[]. Needed since there's no direct
     * Java method/class to do this.
     * \param shortArray Short[] array
     * \return byte[] array
     */
    private static byte[] translateShortToByteArray(Short[] shortArray) {
        byte[] byteArrayResult = new byte[shortArray.length];

        for (int i = 0; i < shortArray.length; i++) {
            byteArrayResult[i] = (byte) shortArray[i].shortValue();
        }

        return byteArrayResult;
    }

       /* Attempts to convert a character to GSM format. Returns (character, length)
 * - Length is 0,if we can not convert.
 * - Length is 1, if we convert into one char
 * - Length is 2, if we require an escape char
 * gsm_char is set to the char itself
 */

    private static Utils.Pair<Short, Integer> char2gsm(int ucs2_char) {
        int len;
        int gsm_char;
        if (ucs2_char <= 255) {
            gsm_char = latin1_to_gsm[ucs2_char];
            if (gsm_char <= 0)
                gsm_char = NRP; // Cannot convert

            len = (gsm_char == NRP && ucs2_char != NRP) ? 0 : 1;
        } else { // Not latin1. Test for allowed gsm chars
            len = 1;
            switch (ucs2_char) {
                case 0x394:
                    gsm_char = 0x10; /* GREEK CAPITAL LETTER DELTA */
                    break;
                case 0x3A6:
                    gsm_char = 0x12; /* GREEK CAPITAL LETTER PHI */
                    break;
                case 0x393:
                    gsm_char = 0x13; /* GREEK CAPITAL LETTER GAMMA */
                    break;
                case 0x39B:
                    gsm_char = 0x14; /* GREEK CAPITAL LETTER LAMBDA */
                    break;
                case 0x3A9:
                    gsm_char = 0x15; /* GREEK CAPITAL LETTER OMEGA */
                    break;
                case 0x3A0:
                    gsm_char = 0x16; /* GREEK CAPITAL LETTER PI */
                    break;
                case 0x3A8:
                    gsm_char = 0x17; /* GREEK CAPITAL LETTER PSI */
                    break;
                case 0x3A3:
                    gsm_char = 0x18; /* GREEK CAPITAL LETTER SIGMA */
                    break;
                case 0x398:
                    gsm_char = 0x19; /* GREEK CAPITAL LETTER THETA */
                    break;
                case 0x39E:
                    gsm_char = 0x1A; /* GREEK CAPITAL LETTER XI */
                    break;
                case 0x20AC:
                    gsm_char = 'e'; /* EURO SIGN */
                    len = 2;
                    break;
                default:
                    gsm_char = -1; /* character cannot be represented in GSM 03.38 */
                    len = 0;
                    break;
            }
        }
        return new Utils.Pair<Short, Integer>((short) gsm_char, len);
    }

    // Build a UCS2 string in form 1 or form2, with the given base pointer
    private static byte[] buildUcs2Str(byte[] s, byte[] gsm_ind, int type, int bp) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int n = s.length;
        byte[] hdr;

        if (type == 2)
            hdr = new byte[]{(byte) 0x81, (byte) (n / 2), (byte) (bp >> 7)};
        else
            hdr = new byte[]{(byte) 0x82,
                    (byte) (n / 2),
                    (byte) ((bp >> 8) & 0xFF),
                    (byte) (bp & 0xFF)};
        os.write(hdr);

        for (int i = 0, j = 0; i < gsm_ind.length; j += 2, i++) {
            int m = gsm_ind[i];

            if (m > 0) {
                int ch = gsm_ind[i + 1];
                i++;
                if (m > 1)
                    os.write(27); // Write escape char
                os.write(ch); // Write the char
            } else { // Put in the UCS2 coded char
                int x0 = s[j], x1 = s[j + 1];
                int ucs2_char = (x0 << 8) | x1;

                os.write(0x80 | (ucs2_char - bp));
            }
        }

        return os.toByteArray();
    }

    public static byte[] alphaConvert(String bytes) throws Exception
    {
        byte[] b = bytes.getBytes("UTF-16BE");
        return stkUcs2AlphaCoding(b).l;
    }

    // Takes UTF16-BE sequence and tries to find most efficient GSM UCS2 encoding (or GSM 7bit if possible)
    // Returns (bool, bytes), where bool is: true if the string is ucs2, false if it was converted to gsm 7bit
    // byte sequence also returned.
    public static Utils.Pair<Boolean, byte[]> stkUcs2AlphaCoding(byte[] s) throws Exception {
        int bp2 = 0xFF << 7, bp3 = 0xFFFF; // Base pointers
        int max_char = 0;
        int l2 = 2, l3 = 3; /* type 2 (starts out as 2: char_len+offset) and 3 (starts out as 3) lengths */
        boolean gsm_supported = true;
        int n = s.length;

        ByteArrayOutputStream tmp = new ByteArrayOutputStream();

        if (n % 2 != 0)
            throw new Exception("Invalid byte sequence: Must be UTF-16BE even number of chars");

        for (int i = 0; i < n; i += 2) {
            int x0 = s[i] & 0xFF;
            int x1 = s[i + 1] & 0XFF;
            int ucs2_char = (x0 << 8) | x1;

            if (ucs2_char > max_char)
                max_char = ucs2_char; // Track largest

            Utils.Pair<Short, Integer> xx = char2gsm(ucs2_char);
            int l = xx.l;
            int gsm_char = xx.k;

            tmp.write(l); // Write the length
            if (l > 0) {
                tmp.write(gsm_char); // then the character
                l2 += l;
                l3 += l;
            } else { // No 7bit/GSM default representation of the char. Use usc2
                gsm_supported = false;

                if ((x0 & 0x80) == 0) { // Try to use ucs2 type 2 repr
                    int u = ucs2_char & (0xFF << 7);
                    if (bp2 > u)
                        bp2 = u;

                    if (max_char - bp2 > 0x7F) /* type 2 not possible: more than single byte offset */
                        l2 = n + 1;
                    else
                        l2++; /* One extra char */
                } else
                    l2 = n + 1; /* type 2 is not possible. */

                if (bp3 > ucs2_char)
                    bp3 = ucs2_char;
                if (max_char - bp3 > 0x7f)
                    l3 = n + 1; /* type 3 not possible */
                else
                    l3++;

            }
        }
        // At this point we either:

        //- string can be represented using GSM default, or

        //- have ideal base pointers for type 1 & 2, and the relative lengths.

        //

        byte[] tmp_is = tmp.toByteArray();
        byte[] out = null;

        if (gsm_supported) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (int i = 0; i < tmp_is.length; i += 2) {
                int m = tmp_is[i];
                int ch = tmp_is[i + 1];

                if (m > 1)
                    os.write(27);

                os.write(ch);
            }
            out = os.toByteArray();
        } else if (l2 < n && l2 < l3)  /* Type 2 wins: build the thing */
            out = buildUcs2Str(s, tmp_is, 2, bp2);
        else if (l3 <= n && l3 < l2)
            out = buildUcs2Str(s, tmp_is, 3, bp3);
        else {
            out = new byte[s.length + 1];
            out[0] = (byte) 0x80; // Default form
            System.arraycopy(s, 0, out, 1, s.length);
        }

        return new Utils.Pair<Boolean, byte[]>(!gsm_supported, out);
    }




    public static byte[] makeUssd7Bit(byte[] in) throws Exception {
        int num_chars = in.length;
        int num_octets = (7 + (num_chars * 7)) / 8;
        int pad = (num_chars % 8) == 7 ? 0x0D : 0;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < num_octets; i++) {
            int chnum = i + (i / 7);
            int ch1 = in[chnum] & 0x7f;
            int ch2 = chnum + 1 < num_chars ? (in[chnum + 1] & 0x7f) : pad;
            int idx = i % 7;
            int ch = (ch1 >> idx) | ((ch2 << (7 - idx)) & 0xFF);

            out.write(ch);
        }

        if ((num_chars % 8 == 0) &&  /* if last char is CR AND output is full octets, add one more.*/
                in[num_chars - 1] == 0x0D)
            out.write(0x0D);

        return out.toByteArray();
    }



    public static abstract class ByteChecker { // Interface of all checkers
        public boolean check(byte b)
        {
            return true;
        }
    }

    public static boolean check_all(byte[] b, int start, ByteChecker chk)
    {
        if (b == null)
            return true;
        for (int i = start; i < b.length; i++)
            if (!chk.check(b[i]))
                return  false;
        return true;
    }

    public static boolean checkAll(byte[] b, ByteChecker chk)
    {
        return check_all(b,0,chk);
    }



    private static final int NRP = '?';

    /* from Kannel GW */
    private static final int[] at2_rmask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127 };
    private static final int[] at2_lmask = new int[] { 0, 128, 192, 224, 240, 248, 252, 254 };





    private static final int latin1_to_gsm[] = new int[] {
/* 0x00 */ NRP, /* pc: NON PRINTABLE */
/* 0x01 */ NRP, /* pc: NON PRINTABLE */
/* 0x02 */ NRP, /* pc: NON PRINTABLE */
/* 0x03 */ NRP, /* pc: NON PRINTABLE */
/* 0x04 */ NRP, /* pc: NON PRINTABLE */
/* 0x05 */ NRP, /* pc: NON PRINTABLE */
/* 0x06 */ NRP, /* pc: NON PRINTABLE */
/* 0x07 */ NRP, /* pc: NON PRINTABLE */
/* 0x08 */ NRP, /* pc: NON PRINTABLE */
/* 0x09 */ NRP, /* pc: NON PRINTABLE */
/* 0x0a */ 0x0a, /* pc: NON PRINTABLE */
/* 0x0b */ NRP, /* pc: NON PRINTABLE */
/* 0x0c */ -0x0a, /* pc: NON PRINTABLE */
/* 0x0d */ 0x0d, /* pc: NON PRINTABLE */
/* 0x0e */ NRP, /* pc: NON PRINTABLE */
/* 0x0f */ NRP, /* pc: NON PRINTABLE */
/* 0x10 */ NRP, /* pc: NON PRINTABLE */
/* 0x11 */ NRP, /* pc: NON PRINTABLE */
/* 0x12 */ NRP, /* pc: NON PRINTABLE */
/* 0x13 */ NRP, /* pc: NON PRINTABLE */
/* 0x14 */ NRP, /* pc: NON PRINTABLE */
/* 0x15 */ NRP, /* pc: NON PRINTABLE */
/* 0x16 */ NRP, /* pc: NON PRINTABLE */
/* 0x17 */ NRP, /* pc: NON PRINTABLE */
/* 0x18 */ NRP, /* pc: NON PRINTABLE */
/* 0x19 */ NRP, /* pc: NON PRINTABLE */
/* 0x1a */ NRP, /* pc: NON PRINTABLE */
/* 0x1b */ NRP, /* pc: NON PRINTABLE */
/* 0x1c */ NRP, /* pc: NON PRINTABLE */
/* 0x1d */ NRP, /* pc: NON PRINTABLE */
/* 0x1e */ NRP, /* pc: NON PRINTABLE */
/* 0x1f */ NRP, /* pc: NON PRINTABLE */
/* 0x20 */ 0x20, /* pc:   */
/* 0x21 */ 0x21, /* pc: ! */
/* 0x22 */ 0x22, /* pc: " */
/* 0x23 */ 0x23, /* pc: # */
/* 0x24 */ 0x02, /* pc: $ */
/* 0x25 */ 0x25, /* pc: % */
/* 0x26 */ 0x26, /* pc: & */
/* 0x27 */ 0x27, /* pc: ' */
/* 0x28 */ 0x28, /* pc: ( */
/* 0x29 */ 0x29, /* pc: ) */
/* 0x2a */ 0x2a, /* pc: * */
/* 0x2b */ 0x2b, /* pc: + */
/* 0x2c */ 0x2c, /* pc: , */
/* 0x2d */ 0x2d, /* pc: - */
/* 0x2e */ 0x2e, /* pc: . */
/* 0x2f */ 0x2f, /* pc: / */
/* 0x30 */ 0x30, /* pc: 0 */
/* 0x31 */ 0x31, /* pc: 1 */
/* 0x32 */ 0x32, /* pc: 2 */
/* 0x33 */ 0x33, /* pc: 3 */
/* 0x34 */ 0x34, /* pc: 4 */
/* 0x35 */ 0x35, /* pc: 5 */
/* 0x36 */ 0x36, /* pc: 6 */
/* 0x37 */ 0x37, /* pc: 7 */
/* 0x38 */ 0x38, /* pc: 8 */
/* 0x39 */ 0x39, /* pc: 9 */
/* 0x3a */ 0x3a, /* pc: : */
/* 0x3b */ 0x3b, /* pc: ; */
/* 0x3c */ 0x3c, /* pc: < */
/* 0x3d */ 0x3d, /* pc: = */
/* 0x3e */ 0x3e, /* pc: > */
/* 0x3f */ 0x3f, /* pc: ? */
/* 0x40 */ 0x00, /* pc: @ */
/* 0x41 */ 0x41, /* pc: A */
/* 0x42 */ 0x42, /* pc: B */
/* 0x43 */ 0x43, /* pc: C */
/* 0x44 */ 0x44, /* pc: D */
/* 0x45 */ 0x45, /* pc: E */
/* 0x46 */ 0x46, /* pc: F */
/* 0x47 */ 0x47, /* pc: G */
/* 0x48 */ 0x48, /* pc: H */
/* 0x49 */ 0x49, /* pc: I */
/* 0x4a */ 0x4a, /* pc: J */
/* 0x4b */ 0x4b, /* pc: K */
/* 0x4c */ 0x4c, /* pc: L */
/* 0x4d */ 0x4d, /* pc: M */
/* 0x4e */ 0x4e, /* pc: N */
/* 0x4f */ 0x4f, /* pc: O */
/* 0x50 */ 0x50, /* pc: P */
/* 0x51 */ 0x51, /* pc: Q */
/* 0x52 */ 0x52, /* pc: R */
/* 0x53 */ 0x53, /* pc: S */
/* 0x54 */ 0x54, /* pc: T */
/* 0x55 */ 0x55, /* pc: U */
/* 0x56 */ 0x56, /* pc: V */
/* 0x57 */ 0x57, /* pc: W */
/* 0x58 */ 0x58, /* pc: X */
/* 0x59 */ 0x59, /* pc: Y */
/* 0x5a */ 0x5a, /* pc: Z */
/* 0x5b */ -0x3c, /* pc: [ */
/* 0x5c */ -0x2f, /* pc: \ */
/* 0x5d */ -0x3e, /* pc: ] */
/* 0x5e */ -0x14, /* pc: ^ */
/* 0x5f */ 0x11, /* pc: _ */
/* 0x60 */ NRP, /* pc: ` */
/* 0x61 */ 0x61, /* pc: a */
/* 0x62 */ 0x62, /* pc: b */
/* 0x63 */ 0x63, /* pc: c */
/* 0x64 */ 0x64, /* pc: d */
/* 0x65 */ 0x65, /* pc: e */
/* 0x66 */ 0x66, /* pc: f */
/* 0x67 */ 0x67, /* pc: g */
/* 0x68 */ 0x68, /* pc: h */
/* 0x69 */ 0x69, /* pc: i */
/* 0x6a */ 0x6a, /* pc: j */
/* 0x6b */ 0x6b, /* pc: k */
/* 0x6c */ 0x6c, /* pc: l */
/* 0x6d */ 0x6d, /* pc: m */
/* 0x6e */ 0x6e, /* pc: n */
/* 0x6f */ 0x6f, /* pc: o */
/* 0x70 */ 0x70, /* pc: p */
/* 0x71 */ 0x71, /* pc: q */
/* 0x72 */ 0x72, /* pc: r */
/* 0x73 */ 0x73, /* pc: s */
/* 0x74 */ 0x74, /* pc: t */
/* 0x75 */ 0x75, /* pc: u */
/* 0x76 */ 0x76, /* pc: v */
/* 0x77 */ 0x77, /* pc: w */
/* 0x78 */ 0x78, /* pc: x */
/* 0x79 */ 0x79, /* pc: y */
/* 0x7a */ 0x7a, /* pc: z */
/* 0x7b */ -0x28, /* pc: { */
/* 0x7c */ -0x40, /* pc: | */
/* 0x7d */ -0x29, /* pc: } */
/* 0x7e */ -0x3d, /* pc: ~ */
/* 0x7f */ NRP, /* pc: NON PRINTABLE */
/* 0x80 */ NRP, /* pc: NON PRINTABLE */
/* 0x81 */ NRP, /* pc: NON PRINTABLE */
/* 0x82 */ NRP, /* pc: NON PRINTABLE */
/* 0x83 */ NRP, /* pc: NON PRINTABLE */
/* 0x84 */ NRP, /* pc: NON PRINTABLE */
/* 0x85 */ NRP, /* pc: NON PRINTABLE */
/* 0x86 */ NRP, /* pc: NON PRINTABLE */
/* 0x87 */ NRP, /* pc: NON PRINTABLE */
/* 0x88 */ NRP, /* pc: NON PRINTABLE */
/* 0x89 */ NRP, /* pc: NON PRINTABLE */
/* 0x8a */ NRP, /* pc: NON PRINTABLE */
/* 0x8b */ NRP, /* pc: NON PRINTABLE */
/* 0x8c */ NRP, /* pc: NON PRINTABLE */
/* 0x8d */ NRP, /* pc: NON PRINTABLE */
/* 0x8e */ NRP, /* pc: NON PRINTABLE */
/* 0x8f */ NRP, /* pc: NON PRINTABLE */
/* 0x90 */ NRP, /* pc: NON PRINTABLE */
/* 0x91 */ NRP, /* pc: NON PRINTABLE */
/* 0x92 */ NRP, /* pc: NON PRINTABLE */
/* 0x93 */ NRP, /* pc: NON PRINTABLE */
/* 0x94 */ NRP, /* pc: NON PRINTABLE */
/* 0x95 */ NRP, /* pc: NON PRINTABLE */
/* 0x96 */ NRP, /* pc: NON PRINTABLE */
/* 0x97 */ NRP, /* pc: NON PRINTABLE */
/* 0x98 */ NRP, /* pc: NON PRINTABLE */
/* 0x99 */ NRP, /* pc: NON PRINTABLE */
/* 0x9a */ NRP, /* pc: NON PRINTABLE */
/* 0x9b */ NRP, /* pc: NON PRINTABLE */
/* 0x9c */ NRP, /* pc: NON PRINTABLE */
/* 0x9d */ NRP, /* pc: NON PRINTABLE */
/* 0x9e */ NRP, /* pc: NON PRINTABLE */
/* 0x9f */ NRP, /* pc: NON PRINTABLE */
/* 0xa0 */ NRP, /* pc: NON PRINTABLE */
/* 0xa1 */ 0x40, /* pc: INVERTED EXCLAMATION MARK */
/* 0xa2 */ NRP, /* pc: NON PRINTABLE */
/* 0xa3 */ 0x01, /* pc: POUND SIGN */
/* 0xa4 */ 0x24, /* pc: CURRENCY SIGN */
/* 0xa5 */ 0x03, /* pc: YEN SIGN*/
/* 0xa6 */ NRP, /* pc: NON PRINTABLE */
/* 0xa7 */ 0x5f, /* pc: SECTION SIGN */
/* 0xa8 */ NRP, /* pc: NON PRINTABLE */
/* 0xa9 */ NRP, /* pc: NON PRINTABLE */
/* 0xaa */ NRP, /* pc: NON PRINTABLE */
/* 0xab */ NRP, /* pc: NON PRINTABLE */
/* 0xac */ NRP, /* pc: NON PRINTABLE */
/* 0xad */ NRP, /* pc: NON PRINTABLE */
/* 0xae */ NRP, /* pc: NON PRINTABLE */
/* 0xaf */ NRP, /* pc: NON PRINTABLE */
/* 0xb0 */ NRP, /* pc: NON PRINTABLE */
/* 0xb1 */ NRP, /* pc: NON PRINTABLE */
/* 0xb2 */ NRP, /* pc: NON PRINTABLE */
/* 0xb3 */ NRP, /* pc: NON PRINTABLE */
/* 0xb4 */ NRP, /* pc: NON PRINTABLE */
/* 0xb5 */ NRP, /* pc: NON PRINTABLE */
/* 0xb6 */ NRP, /* pc: NON PRINTABLE */
/* 0xb7 */ NRP, /* pc: NON PRINTABLE */
/* 0xb8 */ NRP, /* pc: NON PRINTABLE */
/* 0xb9 */ NRP, /* pc: NON PRINTABLE */
/* 0xba */ NRP, /* pc: NON PRINTABLE */
/* 0xbb */ NRP, /* pc: NON PRINTABLE */
/* 0xbc */ NRP, /* pc: NON PRINTABLE */
/* 0xbd */ NRP, /* pc: NON PRINTABLE */
/* 0xbe */ NRP, /* pc: NON PRINTABLE */
/* 0xbf */ 0x60, /* pc: INVERTED QUESTION MARK */
/* 0xc0 */ NRP, /* pc: NON PRINTABLE */
/* 0xc1 */ NRP, /* pc: NON PRINTABLE */
/* 0xc2 */ NRP, /* pc: NON PRINTABLE */
/* 0xc3 */ NRP, /* pc: NON PRINTABLE */
/* 0xc4 */ 0x5b, /* pc: LATIN CAPITAL LETTER A WITH DIAERESIS */
/* 0xc5 */ 0x0e, /* pc: LATIN CAPITAL LETTER A WITH RING ABOVE */
/* 0xc6 */ 0x1c, /* pc: LATIN CAPITAL LETTER AE */
/* 0xc7 */ 0x09, /* pc: LATIN CAPITAL LETTER C WITH CEDILLA (mapped to small) */
/* 0xc8 */ NRP, /* pc: NON PRINTABLE */
/* 0xc9 */ 0x1f, /* pc: LATIN CAPITAL LETTER E WITH ACUTE  */
/* 0xca */ NRP, /* pc: NON PRINTABLE */
/* 0xcb */ NRP, /* pc: NON PRINTABLE */
/* 0xcc */ NRP, /* pc: NON PRINTABLE */
/* 0xcd */ NRP, /* pc: NON PRINTABLE */
/* 0xce */ NRP, /* pc: NON PRINTABLE */
/* 0xcf */ NRP, /* pc: NON PRINTABLE */
/* 0xd0 */ NRP, /* pc: NON PRINTABLE */
/* 0xd1 */ 0x5d, /* pc: LATIN CAPITAL LETTER N WITH TILDE */
/* 0xd2 */ NRP, /* pc: NON PRINTABLE */
/* 0xd3 */ NRP, /* pc: NON PRINTABLE */
/* 0xd4 */ NRP, /* pc: NON PRINTABLE */
/* 0xd5 */ NRP, /* pc: NON PRINTABLE */
/* 0xd6 */ 0x5c, /* pc: LATIN CAPITAL LETTER O WITH DIAEREIS */
/* 0xd7 */ NRP, /* pc: NON PRINTABLE */
/* 0xd8 */ 0x0b, /* pc: LATIN CAPITAL LETTER O WITH STROKE */
/* 0xd9 */ NRP, /* pc: NON PRINTABLE */
/* 0xda */ NRP, /* pc: NON PRINTABLE */
/* 0xdb */ NRP, /* pc: NON PRINTABLE */
/* 0xdc */ 0x5e, /* pc: LATIN CAPITAL LETTER U WITH DIAERESIS */
/* 0xdd */ NRP, /* pc: NON PRINTABLE */
/* 0xde */ NRP, /* pc: NON PRINTABLE */
/* 0xdf */ 0x1e, /* pc: LATIN SMALL LETTER SHARP S */
/* 0xe0 */ 0x7f, /* pc: LATIN SMALL LETTER A WITH GRAVE */
/* 0xe1 */ NRP, /* pc: NON PRINTABLE */
/* 0xe2 */ NRP, /* pc: NON PRINTABLE */
/* 0xe3 */ NRP, /* pc: NON PRINTABLE */
/* 0xe4 */ 0x7b, /* pc: LATIN SMALL LETTER A WITH DIAERESIS */
/* 0xe5 */ 0x0f, /* pc: LATIN SMALL LETTER A WITH RING ABOVE */
/* 0xe6 */ 0x1d, /* pc: LATIN SMALL LETTER AE */
/* 0xe7 */ 0x09, /* pc: LATIN SMALL LETTER C WITH CEDILLA */
/* 0xe8 */ 0x04, /* pc: NON PRINTABLE */
/* 0xe9 */ 0x05, /* pc: NON PRINTABLE */
/* 0xea */ NRP, /* pc: NON PRINTABLE */
/* 0xeb */ NRP, /* pc: NON PRINTABLE */
/* 0xec */ 0x07, /* pc: NON PRINTABLE */
/* 0xed */ NRP, /* pc: NON PRINTABLE */
/* 0xee */ NRP, /* pc: NON PRINTABLE */
/* 0xef */ NRP, /* pc: NON PRINTABLE */
/* 0xf0 */ NRP, /* pc: NON PRINTABLE */
/* 0xf1 */ 0x7d, /* pc: NON PRINTABLE */
/* 0xf2 */ 0x08, /* pc: NON PRINTABLE */
/* 0xf3 */ NRP, /* pc: NON PRINTABLE */
/* 0xf4 */ NRP, /* pc: NON PRINTABLE */
/* 0xf5 */ NRP, /* pc: NON PRINTABLE */
/* 0xf6 */ 0x7c, /* pc: NON PRINTABLE */
/* 0xf7 */ NRP, /* pc: NON PRINTABLE */
/* 0xf8 */ 0x0c, /* pc: NON PRINTABLE */
/* 0xf9 */ 0x06, /* pc: NON PRINTABLE */
/* 0xfa */ NRP, /* pc: NON PRINTABLE */
/* 0xfb */ NRP, /* pc: NON PRINTABLE */
/* 0xfc */ 0x7e, /* pc: NON PRINTABLE */
/* 0xfd */ NRP, /* pc: NON PRINTABLE */
/* 0xfe */ NRP, /* pc: NON PRINTABLE */
/* 0xff */ NRP, /* pc: NON PRINTABLE */
    };


}

/** @} */