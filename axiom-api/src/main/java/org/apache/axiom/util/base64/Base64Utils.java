/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.axiom.blob.Blob;

/**
 * Contains utility methods to work with base64 encoded data.
 */
public class Base64Utils {
    private static int getEncodedSize(int unencodedSize) {
        return (unencodedSize+2) / 3 * 4;
    }
    
    private static int getBufferSize(Blob blob) {
        long size = blob.getSize();
        if (size == -1) {
            // Use a reasonable default capacity.
            return 4096;
        } else if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Blob is too large to encode to string");
        } else {
            return getEncodedSize((int)size);
        }
    }
    
    /**
     * Get a base64 representation of the content of a given {@link Blob} as a string.
     * This method will try to carry out the encoding operation in the most efficient way.
     * 
     * @param blob the blob with the content to encode
     * @return the base64 encoded content
     * @throws IOException if an I/O error occurs when reading the content of the blob
     */
    public static String encode(Blob blob) throws IOException {
        StringBuilder buffer = new StringBuilder(getBufferSize(blob));
        Base64EncodingStringBufferOutputStream out = new Base64EncodingStringBufferOutputStream(buffer);
        blob.writeTo(out);
        out.complete();
        return buffer.toString();
    }

    /**
     * Get a base64 representation of the content of a given {@link Blob} as a char array.
     * This method will try to carry out the encoding operation in the most efficient way.
     * 
     * @param blob the blob with the content to encode
     * @return the base64 encoded content
     * @throws IOException if an I/O error occurs when reading the content of the blob
     */
    public static char[] encodeToCharArray(Blob blob) throws IOException {
        NoCopyCharArrayWriter buffer = new NoCopyCharArrayWriter(getBufferSize(blob));
        Base64EncodingWriterOutputStream out = new Base64EncodingWriterOutputStream(buffer);
        blob.writeTo(out);
        out.complete();
        return buffer.toCharArray();
    }

    private static int decode0(char[] ibuf, byte[] obuf, int wp) {
        int outlen = 3;
        if (ibuf[3] == Base64Constants.S_BASE64PAD)
            outlen = 2;
        if (ibuf[2] == Base64Constants.S_BASE64PAD)
            outlen = 1;
        int b0 = Base64Constants.S_DECODETABLE[ibuf[0]];
        int b1 = Base64Constants.S_DECODETABLE[ibuf[1]];
        int b2 = Base64Constants.S_DECODETABLE[ibuf[2]];
        int b3 = Base64Constants.S_DECODETABLE[ibuf[3]];
        switch (outlen) {
            case 1:
                obuf[wp] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                return 1;
            case 2:
                obuf[wp++] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                obuf[wp] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
                return 2;
            case 3:
                obuf[wp++] = (byte) (b0 << 2 & 0xfc | b1 >> 4 & 0x3);
                obuf[wp++] = (byte) (b1 << 4 & 0xf0 | b2 >> 2 & 0xf);
                obuf[wp] = (byte) (b2 << 6 & 0xc0 | b3 & 0x3f);
                return 3;
            default:
                throw new RuntimeException("internalError00");
        }
    }

    /**
     * @deprecated
     */
    public static byte[] decode(char[] data, int off, int len) {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[len / 4 * 3 + 3];
        int obufcount = 0;
        for (int i = off; i < off + len; i++) {
            char ch = data[i];
            if (ch == Base64Constants.S_BASE64PAD || ch < Base64Constants.S_DECODETABLE.length
                    && Base64Constants.S_DECODETABLE[ch] >= 0) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    obufcount += decode0(ibuf, obuf, obufcount);
                }
            }
        }
        if (obufcount == obuf.length)
            return obuf;
        byte[] ret = new byte[obufcount];
        System.arraycopy(obuf, 0, ret, 0, obufcount);
        return ret;
    }

    /**
     * Decodes a base64 encoded string into a byte array. This method is designed to conform to the
     * <a href="http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#base64Binary">XML Schema</a>
     * specification. It can be used to decode the text content of an element (or the value of an
     * attribute) of type <code>base64Binary</code>.
     * 
     * @param data
     *            the base64 encoded data
     * @return the decoded data
     */
    public static byte[] decode(String data) {
        int symbols = 0;
        int padding = 0;
        for (int i = 0; i < data.length(); i++) {
            switch (Base64Constants.S_DECODETABLE[data.charAt(i)]) {
                case Base64Constants.PADDING:
                    if (padding == 2) {
                        throw new IllegalArgumentException("Too much padding");
                    }
                    padding++;
                    break;
                case Base64Constants.WHITE_SPACE:
                    break;
                case Base64Constants.INVALID:
                    throw new IllegalArgumentException("Invalid character encountered");
                default:
                    // Padding can only occur at the end
                    if (padding > 0) {
                        throw new IllegalArgumentException("Unexpected padding character");
                    }
                    symbols++;
            }
        }
        if ((symbols + padding) % 4 != 0) {
            throw new IllegalArgumentException("Missing padding");
        }
        byte[] result = new byte[(symbols + padding) / 4 * 3 - padding];
        int pos = 0;
        int resultPos = 0;
        byte accumulator = 0;
        int bits = 0;
        while (symbols > 0) {
            byte b = Base64Constants.S_DECODETABLE[data.charAt(pos++)];
            if (b == Base64Constants.WHITE_SPACE) {
                continue;
            }
            if (bits == 0) {
                accumulator = (byte)(b << 2);
                bits = 6;
            } else {
                accumulator |= b >> (bits - 2);
                result[resultPos++] = accumulator;
                accumulator = (byte)(b << (10 - bits));
                bits -= 2;
            }
            symbols--;
        }
        if (accumulator != 0) {
            throw new IllegalArgumentException("Invalid base64 value");
        }
        if (resultPos != result.length) {
            throw new Error("Oops. This is a bug.");
        }
        return result;
    }

    /**
     * @deprecated
     */
    public static boolean isValidBase64Encoding(String data) {
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);

            if (ch == Base64Constants.S_BASE64PAD || ch < Base64Constants.S_DECODETABLE.length
                    && Base64Constants.S_DECODETABLE[ch] >= 0) {
                //valid character.Do nothing
            } else if (ch == '\r' || ch == '\n') {
                //do nothing
            } else {
                return false;
            }
        }//iterate over all characters in the string
        return true;
    }


    /**
     * @deprecated
     */
    public static void decode(char[] data, int off, int len,
                              OutputStream ostream) throws IOException {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[3];
        for (int i = off; i < off + len; i++) {
            char ch = data[i];
            if (ch == Base64Constants.S_BASE64PAD || ch < Base64Constants.S_DECODETABLE.length
                    && Base64Constants.S_DECODETABLE[ch] >= 0) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    int obufcount = decode0(ibuf, obuf, 0);
                    ostream.write(obuf, 0, obufcount);
                }
            }
        }
    }

    /**
     * @deprecated
     */
    public static void decode(String data, OutputStream ostream)
            throws IOException {
        char[] ibuf = new char[4];
        int ibufcount = 0;
        byte[] obuf = new byte[3];
        for (int i = 0; i < data.length(); i++) {
            char ch = data.charAt(i);
            if (ch == Base64Constants.S_BASE64PAD || ch < Base64Constants.S_DECODETABLE.length
                    && Base64Constants.S_DECODETABLE[ch] >= 0) {
                ibuf[ibufcount++] = ch;
                if (ibufcount == ibuf.length) {
                    ibufcount = 0;
                    int obufcount = decode0(ibuf, obuf, 0);
                    ostream.write(obuf, 0, obufcount);
                }
            }
        }
    }

    /**
     * @deprecated
     */
    public static String encode(byte[] data) {
        return encode(data, 0, data.length);
    }

    /**
     * @deprecated
     */
    public static String encode(byte[] data, int off, int len) {
        if (len <= 0)
            return "";
        char[] out = new char[len / 3 * 4 + 4];
        int rindex = off;
        int windex = 0;
        int rest = len - off;
        while (rest >= 3) {
            int i = ((data[rindex] & 0xff) << 16)
                    + ((data[rindex + 1] & 0xff) << 8)
                    + (data[rindex + 2] & 0xff);
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 18];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[(i >> 12) & 0x3f];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[(i >> 6) & 0x3f];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i & 0x3f];
            rindex += 3;
            rest -= 3;
        }
        if (rest == 1) {
            int i = data[rindex] & 0xff;
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 2];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[(i << 4) & 0x3f];
            out[windex++] = Base64Constants.S_BASE64PAD;
            out[windex++] = Base64Constants.S_BASE64PAD;
        } else if (rest == 2) {
            int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[i >> 10];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[(i >> 4) & 0x3f];
            out[windex++] = (char)Base64Constants.S_BASE64CHAR[(i << 2) & 0x3f];
            out[windex++] = Base64Constants.S_BASE64PAD;
        }
        return new String(out, 0, windex);
    }

    /**
     * @deprecated
     */
    public static void encode(byte[] data, int off, int len, StringBuffer buffer) {
        if (len <= 0) {
            return;
        }

        char[] out = new char[4];
        int rindex = off;
        int rest = len - off;
        while (rest >= 3) {
            int i = ((data[rindex] & 0xff) << 16)
                    + ((data[rindex + 1] & 0xff) << 8)
                    + (data[rindex + 2] & 0xff);
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 18];
            out[1] = (char)Base64Constants.S_BASE64CHAR[(i >> 12) & 0x3f];
            out[2] = (char)Base64Constants.S_BASE64CHAR[(i >> 6) & 0x3f];
            out[3] = (char)Base64Constants.S_BASE64CHAR[i & 0x3f];
            buffer.append(out);
            rindex += 3;
            rest -= 3;
        }
        if (rest == 1) {
            int i = data[rindex] & 0xff;
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 2];
            out[1] = (char)Base64Constants.S_BASE64CHAR[(i << 4) & 0x3f];
            out[2] = Base64Constants.S_BASE64PAD;
            out[3] = Base64Constants.S_BASE64PAD;
            buffer.append(out);
        } else if (rest == 2) {
            int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 10];
            out[1] = (char)Base64Constants.S_BASE64CHAR[(i >> 4) & 0x3f];
            out[2] = (char)Base64Constants.S_BASE64CHAR[(i << 2) & 0x3f];
            out[3] = (char)Base64Constants.S_BASE64PAD;
            buffer.append(out);
        }
    }

    /**
     * @deprecated
     */
    public static void encode(byte[] data, int off, int len,
                              OutputStream ostream) throws IOException {
        if (len <= 0)
            return;
        byte[] out = new byte[4];
        int rindex = off;
        int rest = len - off;
        while (rest >= 3) {
            int i = ((data[rindex] & 0xff) << 16)
                    + ((data[rindex + 1] & 0xff) << 8)
                    + (data[rindex + 2] & 0xff);
            out[0] = Base64Constants.S_BASE64CHAR[i >> 18];
            out[1] = Base64Constants.S_BASE64CHAR[(i >> 12) & 0x3f];
            out[2] = Base64Constants.S_BASE64CHAR[(i >> 6) & 0x3f];
            out[3] = Base64Constants.S_BASE64CHAR[i & 0x3f];
            ostream.write(out, 0, 4);
            rindex += 3;
            rest -= 3;
        }
        if (rest == 1) {
            int i = data[rindex] & 0xff;
            out[0] = Base64Constants.S_BASE64CHAR[i >> 2];
            out[1] = Base64Constants.S_BASE64CHAR[(i << 4) & 0x3f];
            out[2] = Base64Constants.S_BASE64PAD;
            out[3] = Base64Constants.S_BASE64PAD;
            ostream.write(out, 0, 4);
        } else if (rest == 2) {
            int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
            out[0] = Base64Constants.S_BASE64CHAR[i >> 10];
            out[1] = Base64Constants.S_BASE64CHAR[(i >> 4) & 0x3f];
            out[2] = Base64Constants.S_BASE64CHAR[(i << 2) & 0x3f];
            out[3] = Base64Constants.S_BASE64PAD;
            ostream.write(out, 0, 4);
        }
    }

    /**
     * @deprecated
     */
    public static void encode(byte[] data, int off, int len, Writer writer)
            throws IOException {
        if (len <= 0)
            return;
        char[] out = new char[4];
        int rindex = off;
        int rest = len - off;
        int output = 0;
        while (rest >= 3) {
            int i = ((data[rindex] & 0xff) << 16)
                    + ((data[rindex + 1] & 0xff) << 8)
                    + (data[rindex + 2] & 0xff);
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 18];
            out[1] = (char)Base64Constants.S_BASE64CHAR[(i >> 12) & 0x3f];
            out[2] = (char)Base64Constants.S_BASE64CHAR[(i >> 6) & 0x3f];
            out[3] = (char)Base64Constants.S_BASE64CHAR[i & 0x3f];
            writer.write(out, 0, 4);
            rindex += 3;
            rest -= 3;
            output += 4;
            if (output % 76 == 0)
                writer.write("\n");
        }
        if (rest == 1) {
            int i = data[rindex] & 0xff;
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 2];
            out[1] = (char)Base64Constants.S_BASE64CHAR[(i << 4) & 0x3f];
            out[2] = (char)Base64Constants.S_BASE64PAD;
            out[3] = (char)Base64Constants.S_BASE64PAD;
            writer.write(out, 0, 4);
        } else if (rest == 2) {
            int i = ((data[rindex] & 0xff) << 8) + (data[rindex + 1] & 0xff);
            out[0] = (char)Base64Constants.S_BASE64CHAR[i >> 10];
            out[1] = (char)Base64Constants.S_BASE64CHAR[(i >> 4) & 0x3f];
            out[2] = (char)Base64Constants.S_BASE64CHAR[(i << 2) & 0x3f];
            out[3] = (char)Base64Constants.S_BASE64PAD;
            writer.write(out, 0, 4);
        }
    }
}
