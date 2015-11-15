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

abstract class TemporalImpl implements Temporal {
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
    
    public boolean hasTimeZone() {
        return getTimeZone() != null;
    }
}
