/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.axiom.fom;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.abdera.model.AtomDate;
import org.apache.abdera.model.DateTime;
import org.apache.axiom.datatype.xsd.XSDateTime;
import org.apache.axiom.datatype.xsd.XSDateTimeType;
import org.apache.axiom.fom.AbderaDateTime;

public aspect AbderaDateTimeMixin {
    private AtomDate AbderaDateTime.value;

    private XSDateTime AbderaDateTime.getXSDateTime() {
        XSDateTime dateTime;
        try {
            dateTime = coreGetValue(XSDateTimeType.INSTANCE, FOMSemantics.INSTANCE);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Invalid Date Format", ex);
        }
        if (!dateTime.hasTimeZone()) {
            throw new IllegalArgumentException("Not a valid RFC3339 date/time value: no time zone");
        }
        return dateTime;
    }
    
    public final AtomDate AbderaDateTime.getValue() {
        if (value == null) {
            value = new AtomDate(getXSDateTime().getDate(null));
        }
        return value;
    }

    public final DateTime AbderaDateTime.setValue(AtomDate dateTime) {
        value = null;
        setText(dateTime == null ? null : dateTime.getValue());
        return this;
    }

    public final DateTime AbderaDateTime.setDate(Date date) {
        value = null;
        setText(date == null ? null : AtomDate.valueOf(date).getValue());
        return this;
    }

    public final DateTime AbderaDateTime.setCalendar(Calendar date) {
        value = null;
        setText(date == null ? null : AtomDate.valueOf(date).getValue());
        return this;
    }

    public final DateTime AbderaDateTime.setTime(long date) {
        value = null;
        setText(AtomDate.valueOf(date).getValue());
        return this;
    }

    public final DateTime AbderaDateTime.setString(String date) {
        value = null;
        setText(date == null ? null : AtomDate.valueOf(date).getValue());
        return this;
    }

    public final Date AbderaDateTime.getDate() {
        AtomDate ad = getValue();
        return (ad != null) ? ad.getDate() : null;
    }

    public final Calendar AbderaDateTime.getCalendar() {
        AtomDate ad = getValue();
        return (ad != null) ? ad.getCalendar() : null;
    }

    public final long AbderaDateTime.getTime() {
        AtomDate ad = getValue();
        return (ad != null) ? ad.getTime() : null;
    }

    public final String AbderaDateTime.getString() {
        AtomDate ad = getValue();
        return (ad != null) ? ad.getValue() : null;
    }

}
