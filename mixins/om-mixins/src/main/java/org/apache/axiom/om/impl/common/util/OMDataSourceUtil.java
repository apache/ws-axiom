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
package org.apache.axiom.om.impl.common.util;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.ds.AbstractPullOMDataSource;
import org.apache.axiom.om.ds.AbstractPushOMDataSource;

public final class OMDataSourceUtil {
    private OMDataSourceUtil() {}

    public static boolean isPullDataSource(OMDataSource dataSource) {
        return dataSource instanceof AbstractPullOMDataSource;
    }

    public static boolean isPushDataSource(OMDataSource dataSource) {
        return dataSource instanceof AbstractPushOMDataSource
                || dataSource
                        .getClass()
                        .getName()
                        .equals("org.apache.axis2.jaxws.message.databinding.impl.JAXBBlockImpl");
    }

    public static boolean isDestructiveWrite(OMDataSource dataSource) {
        if (dataSource instanceof OMDataSourceExt) {
            return ((OMDataSourceExt) dataSource).isDestructiveWrite();
        } else {
            return true;
        }
    }

    public static boolean isDestructiveRead(OMDataSource dataSource) {
        if (dataSource instanceof OMDataSourceExt) {
            return ((OMDataSourceExt) dataSource).isDestructiveRead();
        } else {
            return false;
        }
    }
}
