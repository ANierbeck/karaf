/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.jaas.modules;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.boot.ProxyLoginModule;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component(name = "org.apache.karaf.jaas.modules.backingengineservice", immediate = true)
@Service(BackingEngineService.class)
public class BackingEngineService {

    @Reference(referenceInterface = BackingEngineFactory.class, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
    bind = "bindBackingEngineFactory", unbind = "unbindBackingEngineFactory")
    private final List<BackingEngineFactory> factories = new CopyOnWriteArrayList<BackingEngineFactory>();

    public BackingEngine get(AppConfigurationEntry entry) {
        for (BackingEngineFactory factory : factories) {
            String loginModuleClass = (String) entry.getOptions().get(ProxyLoginModule.PROPERTY_MODULE);
            if (factory.getModuleClass().equals(loginModuleClass)) {
                return factory.build(entry.getOptions());
            }
        }
        return null;
    }

    public List<BackingEngineFactory> getFactories() {
        return factories;
    }

    public void bindBackingEngineFactory(BackingEngineFactory factory) {
        this.factories.add(factory);
    }

    public void unbindBackingEngineFactory(BackingEngineFactory factory) {
        this.factories.remove(factory);
    }
}
