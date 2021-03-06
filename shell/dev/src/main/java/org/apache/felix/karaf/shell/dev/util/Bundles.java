/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.felix.karaf.shell.dev.util;

import org.osgi.framework.Bundle;

/**
 * A set of utility methods for working with {@link org.osgi.framework.Bundle}s
 */
public class Bundles {

    /**
     * Return a String representation of a bundle state
     */
    public static String toString(int state) {
        switch (state) {
            case Bundle.UNINSTALLED : return "UNINSTALLED";
            case Bundle.INSTALLED : return "INSTALLED";
            case Bundle.RESOLVED: return "RESOLVED";
            case Bundle.STARTING : return "STARTING";
            case Bundle.STOPPING : return "STOPPING";
            case Bundle.ACTIVE : return "ACTIVE";
            default : return "UNKNOWN";
        }
    }
}
