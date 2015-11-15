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

final class XSDateImpl extends TemporalImpl implements XSDate {
    private final boolean bc;
    private final String aeon;
    private final int year;
    private final int month;
    private final int day;
    private final SimpleTimeZone timeZone;
    
    XSDateImpl(boolean bc, String aeon, int year, int month, int day, SimpleTimeZone timeZone) {
        this.bc = bc;
        this.aeon = aeon;
        this.year = year;
        this.month = month;
        this.day = day;
        this.timeZone = timeZone;
    }

    @Override
    boolean hasDatePart() {
        return true;
    }

    @Override
    boolean hasTimePart() {
        return false;
    }

    @Override
    boolean isBC() {
        return bc;
    }

    @Override
    String getAeon() {
        return aeon;
    }

    @Override
    int getYear() {
        return year;
    }

    @Override
    int getMonth() {
        return month;
    }

    @Override
    int getDay() {
        return day;
    }

    @Override
    int getHour() {
        throw new UnsupportedOperationException();
    }

    @Override
    int getMinute() {
        throw new UnsupportedOperationException();
    }

    @Override
    int getSecond() {
        throw new UnsupportedOperationException();
    }

    @Override
    int getNanoSecond() {
        throw new UnsupportedOperationException();
    }

    @Override
    String getNanoSecondFraction() {
        throw new UnsupportedOperationException();
    }

    @Override
    SimpleTimeZone getTimeZone() {
        return timeZone;
    }

    public XSDateTime getDayStart() {
        return new XSDateTimeImpl(bc, aeon, year, month, day, 0, 0, 0, 0, null, timeZone);
    }
}
