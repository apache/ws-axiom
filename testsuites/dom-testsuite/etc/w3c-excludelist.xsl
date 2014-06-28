<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements. See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership. The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied. See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  -->
<!--
  This stylesheet produces a list of exclusions for failing W3C DOM test cases. It takes as
  input a JUnit/Surefire report in XML format.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>
    <xsl:template name="substring-after-last">
        <xsl:param name="string"/>
        <xsl:param name="delimiter"/>
        <xsl:choose>
            <xsl:when test="contains($string, $delimiter)">
                <xsl:call-template name="substring-after-last">
                    <xsl:with-param name="string" select="substring-after($string, $delimiter)"/>
                    <xsl:with-param name="delimiter" select="$delimiter"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise><xsl:value-of select="$string"/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="/">
        <xsl:apply-templates select="//testcase[failure or error]"/>
    </xsl:template>
    <xsl:template match="testcase">
        <xsl:text>        suite.addExclude(</xsl:text>
        <xsl:call-template name="substring-after-last">
            <xsl:with-param name="string" select="@name"/>
            <xsl:with-param name="delimiter" select="'/'"/>
        </xsl:call-template>
        <xsl:text>.class);&#10;</xsl:text>
    </xsl:template>
</xsl:stylesheet>