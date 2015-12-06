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

import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A temporal value. This is the base class for {@link XSDate}, {@link XSTime} and
 * {@link XSDateTime}.
 */
public abstract class Temporal {
    abstract boolean hasDatePart();
    abstract boolean hasTimePart();
    abstract boolean isBC();
    abstract String getAeon();
    abstract int getYear();
    abstract int getMonth();
    abstract int getDay();
    abstract int getHour();
    abstract int getMinute();
    abstract int getSecond();
    abstract int getNanoSecond();
    abstract SimpleTimeZone getTimeZone();
    abstract String getNanoSecondFraction();
    
    /**
     * Convert this value to a calendar. For {@link XSDate} and {@link XSTime}, only the
     * corresponding fields are filled in and all other fields are left unspecified.
     * 
     * @param defaultTimeZone
     *            the time zone to use if this temporal object has no time zone; may be
     *            <code>null</code>
     * @return the calendar
     * @throws NoTimeZoneException
     *             if this temporal object doesn't have a time zone and no default time zone was
     *             specified
     */
    public final GregorianCalendar getCalendar(TimeZone defaultTimeZone) {
        // TODO: check aeon
        TimeZone timeZone = getTimeZone();
        if (timeZone == null) {
            if (defaultTimeZone == null) {
                throw new NoTimeZoneException();
            }
            timeZone = defaultTimeZone;
        }
        GregorianCalendar calendar = new GregorianCalendar(timeZone);
        if (hasDatePart()) {
            // TODO: BC
            // TODO: throw exception if aeon is not null
            calendar.set(GregorianCalendar.YEAR, getYear());
            calendar.set(GregorianCalendar.MONTH, getMonth()-1);
            calendar.set(GregorianCalendar.DAY_OF_MONTH, getDay());
        }
        if (hasTimePart()) {
            calendar.set(GregorianCalendar.HOUR_OF_DAY, getHour());
            calendar.set(GregorianCalendar.MINUTE, getMinute());
            calendar.set(GregorianCalendar.SECOND, getSecond());
            calendar.set(GregorianCalendar.MILLISECOND, getNanoSecond()/1000000);
        }
        return calendar;
    }
    
    /**
     * Determine if this temporal value has a time zone.
     * 
     * @return <code>true</code> if the object has a time zone, <code>false</code> otherwise
     */
    public final boolean hasTimeZone() {
        return getTimeZone() != null;
    }
}
