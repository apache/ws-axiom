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

package org.apache.axiom.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Contains utility methods to generate unique IDs of various kinds.
 *
 * <p>Depending on the requested type of ID, this class will either use {@link UUID#randomUUID()}
 * (or an equivalent algorithm) or its own unique ID generator. This implementation generates unique
 * IDs based on the assumption that the following triplet is unique:
 *
 * <ol>
 *   <li>The thread ID.
 *   <li>The timestamp in milliseconds when the first UID is requested by the thread.
 *   <li>A per thread sequence number that is incremented each time a UID is requested by the
 *       thread.
 * </ol>
 *
 * <p>Considering that these three numbers are represented as <code>long</code> values, these
 * assumptions are correct because:
 *
 * <ul>
 *   <li>The probability that two different threads with the same ID exist in the same millisecond
 *       interval is negligibly small.
 *   <li>One can expect that no thread will ever request more than 2^64 UIDs during its lifetime.
 * </ul>
 *
 * <p>Before building an ID from this triplet, the implementation will XOR the three values with
 * random values calculated once when the class is loaded. This transformation preserves the
 * uniqueness of the calculated triplet and serves several purposes:
 *
 * <ul>
 *   <li>It reduces the probability that the same ID is produces by two different systems, i.e. it
 *       increases global uniqueness.
 *   <li>It adds entropy, i.e. it makes an individual ID appear as random. Indeed, without the XOR
 *       transformation, a hexadecimal representation of the triplet would in general contain
 *       several sequences of '0'.
 *   <li>It prevents the implementation from leaking information about the system state.
 * </ul>
 */
public final class UIDGenerator {
    private static final long startTimeXorOperand;
    private static final long threadIdXorOperand;
    private static final long seqXorOperand;

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Array of 16 caches that contain random bytes fetched from {@link #secureRandom} and that are
     * used to compute UUIDs. These caches are used to reduce the number of calls to {@link
     * SecureRandom#nextBytes(byte[])}. The cache used by a given thread is determined by the thread
     * ID. Multiple caches are used to reduce contention between threads.
     */
    private static final UUIDCache[] uuidCaches;

    static {
        Random rand = new Random();
        threadIdXorOperand = rand.nextLong();
        startTimeXorOperand = rand.nextLong();
        seqXorOperand = rand.nextLong();
        uuidCaches = new UUIDCache[16];
        for (int i = 0; i < 16; i++) {
            uuidCaches[i] = new UUIDCache();
        }
    }

    /**
     * Thread local that holds the triplet described in the Javadoc of this class. Note that we use
     * a simple array here (instead of our own class) to avoid class loader leaks (see AXIOM-354).
     */
    private static final ThreadLocal<long[]> triplet =
            new ThreadLocal<long[]>() {
                @Override
                protected long[] initialValue() {
                    long[] values = new long[3];
                    values[0] = Thread.currentThread().getId() ^ threadIdXorOperand;
                    values[1] = System.currentTimeMillis() ^ startTimeXorOperand;
                    return values;
                }
            };

    private UIDGenerator() {}

    private static void writeReverseLongHex(long value, StringBuilder buffer) {
        for (int i = 0; i < 16; i++) {
            int n = (int) (value >> (4 * i)) & 0xF;
            writeNibble(n, buffer);
        }
    }

    private static void writeNibble(int n, StringBuilder buffer) {
        buffer.append((char) (n < 10 ? '0' + n : 'a' + n - 10));
    }

    /**
     * Generate a unique ID as hex value and add it to the given buffer. Note that with respect to
     * the triplet, the order of nibbles is reversed, i.e. the least significant nibble of the
     * sequence is written first. This makes comparing two IDs for equality more efficient.
     *
     * @param buffer
     */
    private static void generateHex(StringBuilder buffer) {
        long[] values = triplet.get();
        writeReverseLongHex(values[2]++ ^ seqXorOperand, buffer);
        writeReverseLongHex(values[1], buffer);
        writeReverseLongHex(values[0], buffer);
    }

    /**
     * Generates a unique ID suitable for usage as a MIME content ID.
     *
     * <p>RFC2045 (MIME) specifies that the value of the {@code Content-ID} header must match the
     * {@code msg-id} production, which is defined by RFC2822 as follows:
     *
     * <pre>
     * msg-id        = [CFWS] "&lt;" id-left "@" id-right "&gt;" [CFWS]
     * id-left       = dot-atom-text / no-fold-quote / obs-id-left
     * id-right      = dot-atom-text / no-fold-literal / obs-id-right
     * dot-atom-text = 1*atext *("." 1*atext)
     * atext         = ALPHA / DIGIT / "!" / "#" / "$" / "%" / "&amp;"
     *                   / "'" / "*" / "+" / "-" / "/" / "=" / "?"
     *                   / "^" / "_" / "`" / "{" / "|" / "}" / "~"</pre>
     *
     * In addition, RFC2392 specifies that when used in an URL with scheme "cid:", the content ID
     * must be URL encoded. Since not all implementations handle this correctly, any characters
     * considered "unsafe" in an URL (and requiring encoding) should be avoided in a content ID.
     *
     * <p>This method generates content IDs that satisfy these requirements. It guarantees a high
     * level of uniqueness, but makes no provisions to guarantee randomness. The implementation is
     * thread safe, but doesn't use synchronization.
     *
     * @return The generated content ID. Note that this value does not include the angle brackets of
     *     the {@code msg-id} production, but only represents the bare content ID.
     */
    public static String generateContentId() {
        StringBuilder buffer = new StringBuilder();
        generateHex(buffer);
        buffer.append("@apache.org");
        return buffer.toString();
    }

    /**
     * Generates a MIME boundary.
     *
     * <p>Valid MIME boundaries are defined by the following production in RFC2046:
     *
     * <pre>
     * boundary      := 0*69&lt;bchars&gt; bcharsnospace
     * bchars        := bcharsnospace / " "
     * bcharsnospace := DIGIT / ALPHA / "'" / "(" / ")" /
     *                  "+" / "_" / "," / "-" / "." /
     *                  "/" / ":" / "=" / "?"</pre>
     *
     * <p>It should be noted that the boundary in general will also appear as a parameter in the
     * content type of the MIME package. According to RFC2045 (which defines the {@code
     * Content-Type} header), it will require quoting if it contains characters from the following
     * production:
     *
     * <pre>
     * tspecials := "(" / ")" / "&lt;" / "&gt;" / "@" /
     *              "," / ";" / ":" / "\" / &lt;"&gt; /
     *              "/" / "[" / "]" / "?" / "="</pre>
     *
     * <p>This method produces a boundary that doesn't contain any of these characters and therefore
     * doesn't need to be quoted. To avoid accidental collisions, the returned value is unique and
     * doesn't overlap with any other type of unique ID returned by methods in this class. The
     * implementation is thread safe, but doesn't use synchronization.
     *
     * @return the generated MIME boundary
     */
    public static String generateMimeBoundary() {
        StringBuilder buffer = new StringBuilder("MIMEBoundary_");
        generateHex(buffer);
        return buffer.toString();
    }

    /**
     * Generate a general purpose unique ID. The returned value is the hexadecimal representation of
     * a 192 bit value, i.e. it is 48 characters long. The implementation guarantees a high level of
     * uniqueness, but makes no provisions to guarantee randomness. It is thread safe, but doesn't
     * use synchronization.
     *
     * <p>The fact that this method doesn't guarantee randomness implies that the generated IDs are
     * predictable and must not be used in contexts where this would cause a security vulnerability.
     * In particular, this method should <b>not</b> be used to generate the following kind of IDs:
     *
     * <ul>
     *   <li>Session IDs.
     *   <li>Message IDs used in WS-Addressing.
     * </ul>
     *
     * @return the generated unique ID
     */
    public static String generateUID() {
        StringBuilder buffer = new StringBuilder(48);
        generateHex(buffer);
        return buffer.toString();
    }

    /**
     * Generate a URN with {@code uuid} NID (namespace identifier). These URNs have the following
     * form: {@code urn:uuid:dae6fae1-93df-4824-bc70-884c9edb5973}. The UUID is generated using a
     * cryptographically strong pseudo random number generator.
     *
     * @return the generated URN
     */
    public static String generateURNString() {
        StringBuilder urn = new StringBuilder(45);
        urn.append("urn:uuid:");
        UUIDCache cache = uuidCaches[(int) Thread.currentThread().getId() & 0xF];
        synchronized (cache) {
            boolean fill;
            int position = cache.position;
            byte[] randomBytes = cache.randomBytes;
            if (randomBytes == null) {
                cache.randomBytes = randomBytes = new byte[4096];
                fill = true;
            } else if (position == 4096) {
                position = 0;
                fill = true;
            } else {
                fill = false;
            }
            if (fill) {
                secureRandom.nextBytes(cache.randomBytes);
            }
            writeHex(randomBytes[position], urn);
            writeHex(randomBytes[position + 1], urn);
            writeHex(randomBytes[position + 2], urn);
            writeHex(randomBytes[position + 3], urn);
            urn.append('-');
            writeHex(randomBytes[position + 4], urn);
            writeHex(randomBytes[position + 5], urn);
            urn.append('-');
            writeHex((byte) (randomBytes[position + 6] & 0x0F | 0x40), urn);
            writeHex(randomBytes[position + 7], urn);
            urn.append('-');
            writeHex((byte) (randomBytes[position + 8] & 0x3F | 0x80), urn);
            writeHex(randomBytes[position + 9], urn);
            urn.append('-');
            writeHex(randomBytes[position + 10], urn);
            writeHex(randomBytes[position + 11], urn);
            writeHex(randomBytes[position + 12], urn);
            writeHex(randomBytes[position + 13], urn);
            writeHex(randomBytes[position + 14], urn);
            writeHex(randomBytes[position + 15], urn);
            cache.position = position + 16;
        }
        return urn.toString();
    }

    private static void writeHex(byte b, StringBuilder buffer) {
        writeNibble(b >> 4 & 0xF, buffer);
        writeNibble(b & 0xF, buffer);
    }

    /**
     * Generate a URN with {@code uuid} NID (namespace identifier). This method does the same as
     * {@link #generateURNString()}, but returns a {@link URI} object.
     *
     * @return the generated URN
     */
    public static URI generateURN() {
        try {
            return new URI(generateURNString());
        } catch (URISyntaxException ex) {
            // If we ever get here, then if would mean that there is something badly broken...
            throw new Error(ex);
        }
    }
}
