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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import junit.extensions.TestDecorator;
import junit.framework.Test;
import junit.framework.TestResult;

public class ParserTestSetup extends TestDecorator {
    private final ClassLoader classLoader;
    private final Properties props;
    
    public ParserTestSetup(Test test, ClassLoader classLoader, Properties props) {
        super(test);
        this.classLoader = classLoader;
        this.props = props;
    }

    public void run(TestResult result) {
        Thread currentThread = Thread.currentThread();
        ClassLoader savedClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(classLoader);

        Map savedProps;
        if (props == null) {
            savedProps = null;
        } else {
            // Need a HashMap (instead of Properties) since we store null values
            savedProps = new HashMap();
            for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry)it.next();
                String key = (String)entry.getKey();
                savedProps.put(key, System.getProperty(key));
                System.setProperty(key, (String)entry.getValue());
            }
        }
        
        try {
            super.run(result);
        } finally {
            currentThread.setContextClassLoader(savedClassLoader);
            
            if (savedProps != null) {
                for (Iterator it = savedProps.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry entry = (Map.Entry)it.next();
                    String key = (String)entry.getKey();
                    String value = (String)entry.getValue();
                    if (value == null) {
                        System.getProperties().remove(key);
                    } else {
                        System.setProperty(key, value);
                    }
                }
            }
        }
    }
}
