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

import java.util.List;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.util.collections.CopyOnWriteArrayIdentityList;

@Component(name = "org.apache.karaf.jaas.config.osgi", description = "Karaf Jaas OSGi Configuration",
        immediate = true)
public class OsgiConfiguration extends Configuration {

    @Reference(referenceInterface = JaasRealm.class,
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
            bind = "register", unbind = "unregister")
    private final List<JaasRealm> realms = new CopyOnWriteArrayIdentityList<JaasRealm>();

    @Activate
    public void init() {
        Configuration.setConfiguration(this);
    }

    @Deactivate
    public void close() {
        realms.clear();
        Configuration.setConfiguration(null);
    }

    public void register(JaasRealm realm) {
        if (realm != null) {
            realms.add(realm);
        }
    }

    public void unregister(JaasRealm realm) {
        if (realm != null) {
            realms.remove(realm);
        }
    }

    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
        JaasRealm realm = null;
        for (JaasRealm r : realms) {
            if (r.getName().equals(name)) {
                if (realm == null || r.getRank() > realm.getRank()) {
                    realm = r;
                }
            }
        }
        if (realm != null) {
            return realm.getEntries();
        }
        return null;
    }

    public void refresh() {
        // Nothing to do, as we auto-update the configuration
    }
}
