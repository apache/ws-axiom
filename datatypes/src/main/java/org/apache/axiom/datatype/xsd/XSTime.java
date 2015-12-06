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

public final class XSTime extends Temporal {
    private final int hour;
    private final int minute;
    private final int second;
    private final int nanoSecond;
    private final String nanoSecondFraction;
    private final SimpleTimeZone timeZone;
    
    XSTime(int hour, int minute, int second, int nanoSecond, String nanoSecondFraction,
            SimpleTimeZone timeZone) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.nanoSecond = nanoSecond;
        this.nanoSecondFraction = nanoSecondFraction;
        this.timeZone = timeZone;
    }

    @Override
    boolean hasDatePart() {
        return false;
    }

    @Override
    boolean hasTimePart() {
        return true;
    }

    @Override
    boolean isBC() {
        throw new UnsupportedOperationException();
    }

    @Override
    String getAeon() {
        throw new UnsupportedOperationException();
    }

    @Override
    int getYear() {
        throw new UnsupportedOperationException();
    }

    @Override
    int getMonth() {
        throw new UnsupportedOperationException();
    }

    @Override
    int getDay() {
        throw new UnsupportedOperationException();
    }

    @Override
    int getHour() {
        return hour;
    }

    @Override
    int getMinute() {
        return minute;
    }

    @Override
    int getSecond() {
        return second;
    }

    @Override
    int getNanoSecond() {
        return nanoSecond;
    }

    @Override
    String getNanoSecondFraction() {
        return nanoSecondFraction;
    }

    @Override
    SimpleTimeZone getTimeZone() {
        return timeZone;
    }
}
