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
package org.apache.karaf.jaas.config.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.osgi.framework.BundleContext;

@Component(name = "org.apache.karaf.jaas.config.proxy.initializer", description = "Karaf Jaas Proxy Initializer",
        immediate = true)
public class ProxyLoginModuleInitializer {

    private BundleContext bundleContext;

    @Activate
    public void init(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        BundleContext context = bundleContext.getBundle(0).getBundleContext();
        ProxyLoginModule.init(context);
    }

    @Deactivate
    public void destroy() {

    }
}
