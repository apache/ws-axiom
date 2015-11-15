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
package org.apache.axiom.datatype.xsd;

import java.util.Date;
import java.util.TimeZone;

final class SimpleTimeZone extends TimeZone {
    private static final long serialVersionUID = 1L;

    private static final SimpleTimeZone[] instances;
    
    static {
        instances = new SimpleTimeZone[57];
        for (int i=0; i<instances.length; i++) {
            instances[i] = new SimpleTimeZone(30*(-28 + i));
        }
    }
    
    private final int delta;
    
    SimpleTimeZone(int delta) {
        this.delta = delta;
        if (delta == 0) {
            super.setID("GMT");
        } else {
            StringBuilder buffer = new StringBuilder("GMT");
            buffer.append(delta < 0 ? '-' : '+');
            delta = Math.abs(delta);
            append2Digits(buffer, delta/60);
            buffer.append(':');
            append2Digits(buffer, delta % 60);
            super.setID(buffer.toString());
        }
    }

    private static void append2Digits(StringBuilder buffer, int value) {
        buffer.append((char)('0' + value/10));
        buffer.append((char)('0' + value%10));
    }
    
    static SimpleTimeZone getInstance(int delta) {
        return delta % 30 == 0 ? instances[delta/30 + 28] : new SimpleTimeZone(delta);
    }
    
    @Override
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
        return getRawOffset();
    }

    @Override
    public void setRawOffset(int offsetMillis) {
        if (getRawOffset() != offsetMillis) {
            throw new UnsupportedOperationException("Immutable time zone object");
        }
    }

    @Override
    public int getRawOffset() {
        return delta * 60000;
    }

    @Override
    public boolean useDaylightTime() {
        return false;
    }

    @Override
    public boolean inDaylightTime(Date date) {
        return false;
    }

    @Override
    public void setID(String ID) {
        if (!getID().equals(ID)) {
            throw new UnsupportedOperationException("Immutable time zone object");
        }
    }
}
