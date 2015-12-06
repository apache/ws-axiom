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

import java.text.ParseException;

import org.apache.axiom.datatype.InvariantType;
import org.apache.axiom.datatype.TypeHelper;
import org.apache.axiom.datatype.UnexpectedCharacterException;
import org.apache.axiom.datatype.UnexpectedEndOfStringException;

public abstract class TemporalType<T extends Temporal> extends InvariantType<T> {
    abstract boolean hasDatePart();
    abstract boolean hasTimePart();
    abstract T createInstance(boolean bc, String aeon, int year, int month, int day, int hour,
            int minute, int second, int nanoSecond, String nanoSecondFraction,
            SimpleTimeZone timeZone);
    
    public final T parse(String literal) throws ParseException {
        final int end = TypeHelper.getEndIndex(literal);
        int index = TypeHelper.getStartIndex(literal);
        boolean bc;
        String aeon;
        int year;
        int month;
        int day;
        if (hasDatePart()) {
            if (literal.charAt(0) == '-') {
                bc = true;
                index++;
            } else {
                bc = false;
            }
            {
                int digits = countDigits(literal, index, "-", false);
                if (digits < 4) {
                    throw new UnexpectedCharacterException(literal, index+digits);
                }
                if (digits > 4 && literal.charAt(index) == '0') {
                    throw new UnexpectedCharacterException(literal, index);
                }
                if (digits > 9) {
                    aeon = literal.substring(index, index+digits-9);
                    index += digits-9;
                    digits = 9;
                } else {
                    aeon = null;
                }
                year = parseDigitsUnchecked(literal, index, digits);
                if (year == 0) {
                    throw new ParseException("Year can't be zero", index);
                }
                index += digits + 1;
            }
            month = parseDigitsChecked(literal, index, 2);
            index += 2;
            if (index == end) {
                throw new UnexpectedEndOfStringException(literal);
            }
            if (literal.charAt(index) != '-') {
                throw new UnexpectedCharacterException(literal, index);
            }
            index++;
            day = parseDigitsChecked(literal, index, 2);
            index += 2;
        } else {
            bc = false;
            aeon = null;
            year = 0;
            month = 0;
            day = 0;
        }
        if (hasDatePart() && hasTimePart()) {
            if (index == end) {
                throw new UnexpectedEndOfStringException(literal);
            }
            if (literal.charAt(index) != 'T') {
                throw new UnexpectedCharacterException(literal, index);
            }
            index++;
        }
        int hour;
        int minute;
        int second;
        int nanoSecond;
        String nanoSecondFraction;
        if (hasTimePart()) {
            hour = parseDigitsChecked(literal, index, 2);
            index += 2;
            if (index == end) {
                throw new UnexpectedEndOfStringException(literal);
            }
            if (literal.charAt(index) != ':') {
                throw new UnexpectedCharacterException(literal, index);
            }
            index++;
            minute = parseDigitsChecked(literal, index, 2);
            index += 2;
            if (index == end) {
                throw new UnexpectedEndOfStringException(literal);
            }
            if (literal.charAt(index) != ':') {
                throw new UnexpectedCharacterException(literal, index);
            }
            index++;
            second = parseDigitsChecked(literal, index, 2);
            index += 2;
            if (index < end && literal.charAt(index) == '.') {
                index++;
                if (index == end) {
                    throw new UnexpectedEndOfStringException(literal);
                }
                int digits = countDigits(literal, index, "Z+-", true);
                if (digits == 0) {
                    if (index == end) {
                        throw new UnexpectedEndOfStringException(literal);
                    } else {
                        throw new UnexpectedCharacterException(literal, index);
                    }
                }
                // TODO: we should remove trailing zeros
                nanoSecond = parseDigitsUnchecked(literal, index, Math.min(digits, 9));
                if (digits > 9) {
                    nanoSecondFraction = literal.substring(index+9, index+digits-9);
                } else {
                    for (int i = digits; i < 9; i++) {
                        nanoSecond *= 10;
                    }
                    nanoSecondFraction = null;
                }
                index += digits;
            } else {
                nanoSecond = 0;
                nanoSecondFraction = null;
            }
        } else {
            hour = 0;
            minute = 0;
            second = 0;
            nanoSecond = 0;
            nanoSecondFraction = null;
        }
        SimpleTimeZone timeZone;
        if (index == end) {
            timeZone = null;
        } else {
            int delta;
            char c = literal.charAt(index);
            index++;
            if (c == 'Z') {
                delta = 0;
            } else {
                boolean negative = c == '-';
                int deltaHours = parseDigitsChecked(literal, index, 2);
                index += 2;
                if (index == end) {
                    throw new UnexpectedEndOfStringException(literal);
                }
                if (literal.charAt(index) != ':') {
                    throw new UnexpectedCharacterException(literal, index);
                }
                index++;
                int deltaMinutes = parseDigitsChecked(literal, index, 2);
                index += 2;
                delta = 60*deltaHours + deltaMinutes;
                if (negative) {
                    delta = -delta;
                }
            }
            timeZone = SimpleTimeZone.getInstance(delta);
        }
        if (index != end) {
            throw new UnexpectedCharacterException(literal, index);
        }
        return createInstance(bc, aeon, year, month, day, hour, minute, second, nanoSecond,
                nanoSecondFraction, timeZone);
    }
    
    private static int countDigits(String literal, int index, String stopChars, boolean allowEndOfString) throws ParseException {
        final int len = literal.length();
        int count = 0;
        while (true) {
            if (index == len) {
                if (allowEndOfString) {
                    return count;
                } else {
                    throw new UnexpectedEndOfStringException(literal);
                }
            }
            char c = literal.charAt(index);
            if ('0' <= c && c <= '9') {
                index++;
                count++;
            } else if (stopChars.indexOf(c) != -1) {
                return count;
            } else {
                throw new UnexpectedCharacterException(literal, index);
            }
        }
    }
    
    private static int parseDigitsUnchecked(String literal, int index, int count) {
        int value = 0;
        for (; count > 0; count--) {
            value = 10*value + literal.charAt(index++) - '0';
        }
        return value;
    }
    
    private static int parseDigitsChecked(String literal, int index, int count) throws ParseException {
        final int len = literal.length();
        int value = 0;
        for (; count > 0; count--) {
            if (index == len) {
                throw new UnexpectedEndOfStringException(literal);
            }
            char c = literal.charAt(index);
            if ('0' <= c && c <= '9') {
                value = 10*value + c - '0';
            } else {
                throw new UnexpectedCharacterException(literal, index);
            }
            index++;
        }
        return value;
    }
    
    public final String format(T value) {
        return value.toString();
    }
}
