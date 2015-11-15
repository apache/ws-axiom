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

final class XSDateTimeImpl extends TemporalImpl implements XSDateTime {
    private final boolean bc;
    private final String aeon;
    private final int year;
    private final int month;
    private final int day;
    private final int hour;
    private final int minute;
    private final int second;
    private final int nanoSecond;
    private final String nanoSecondFraction;
    private final SimpleTimeZone timeZone;
    
    XSDateTimeImpl(boolean bc, String aeon, int year, int month, int day, int hour, int minute,
            int second, int nanoSecond, String nanoSecondFraction, SimpleTimeZone timeZone) {
        this.bc = bc;
        this.aeon = aeon;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.nanoSecond = nanoSecond;
        this.nanoSecondFraction = nanoSecondFraction;
        this.timeZone = timeZone;
    }
    
    @Override
    boolean hasDatePart() {
        return true;
    }

    @Override
    boolean hasTimePart() {
        return true;
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

    public XSDate getDate() {
        return new XSDateImpl(bc, aeon, year, month, day, timeZone);
    }

    public XSTime getTime() {
        return new XSTimeImpl(hour, minute, second, nanoSecond, nanoSecondFraction, timeZone);
    }

    public Date getDate(TimeZone defaultTimeZone) {
        // TODO: fast path for dates in the 20th and 21st century
        return getCalendar(defaultTimeZone).getTime();
    }
}
