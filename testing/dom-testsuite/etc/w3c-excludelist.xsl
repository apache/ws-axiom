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
    <xsl:template match="/">
        <xsl:apply-templates select="//testcase[@classname='org.apache.axiom.ts.dom.W3CTestCase' and (failure or error)]"/>
    </xsl:template>
    <xsl:template match="testcase">
        <xsl:text>        builder.exclude(W3CTestCase.class, "(id=</xsl:text><xsl:value-of select="substring-before(substring-after(@name, '[id='), ']')"/><xsl:text>)");&#10;</xsl:text>
    </xsl:template>
</xsl:stylesheet>