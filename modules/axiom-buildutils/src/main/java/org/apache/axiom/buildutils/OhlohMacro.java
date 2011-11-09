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
package org.apache.axiom.buildutils;

import org.apache.maven.doxia.macro.AbstractMacro;
import org.apache.maven.doxia.macro.MacroExecutionException;
import org.apache.maven.doxia.macro.MacroRequest;
import org.apache.maven.doxia.sink.Sink;

/**
 * Doxia macro to insert Ohloh widgets.
 * 
 * @plexus.component role="org.apache.maven.doxia.macro.Macro" role-hint="ohloh"
 */
public class OhlohMacro extends AbstractMacro {
    public void execute(Sink sink, MacroRequest request) throws MacroExecutionException {
        String project = (String)request.getParameter("project");
        if (project == null) {
            throw new MacroExecutionException("'project' macro parameter is required");
        }
        String widget = (String)request.getParameter("widget");
        if (widget == null) {
            throw new MacroExecutionException("'widget' macro parameter is required");
        }
        sink.rawText("<div style=\"float: left; border: 1px dotted #777777; background-color: white\"><script type=\"text/javascript\" src=\"http://www.ohloh.net/p/"
                + project + "/widgets/project_" + widget + ".js\"></script></div><div style=\"clear: both\"/>");
    }
}
