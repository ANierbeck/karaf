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
package org.apache.karaf.jaas.command.completers;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component(name = "org.apache.karaf.jaas.command.loginmodule.completer", immediate = true)
@Service(Completer.class)
@Properties(
        @Property(name = "completer.type", value = LoginModuleNameCompleter.COMPLETER_TYPE)
)
public class LoginModuleNameCompleter implements Completer {

    public static final String COMPLETER_TYPE = "jaas.loginmodule";

    @Reference(referenceInterface = JaasRealm.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind = "bindRealm", unbind = "unbindRealm"
    )
    private final List<JaasRealm> realms = new CopyOnWriteArrayList<JaasRealm>();


    public int complete(String buffer, int cursor, List<String> candidates) {
        StringsCompleter delegate = new StringsCompleter();
        try {
            if (realms != null && !realms.isEmpty())
                for (JaasRealm realm : realms) {
                    List<String> moduleClassNames = findLoginModuleClassNames(realm);
                    if (moduleClassNames != null && !moduleClassNames.isEmpty())
                    delegate.getStrings().addAll(moduleClassNames);
                }
        } catch (Exception e) {
            // Ignore
        }
        return delegate.complete(buffer, cursor, candidates);
    }

    /**
     * Finds the login module class name in the {@link JaasRealm} entries.
     * @param realm
     * @return
     */
    private List<String> findLoginModuleClassNames(JaasRealm realm) {
        List<String> moduleClassNames = new LinkedList<String>();
        for (AppConfigurationEntry entry : realm.getEntries()) {
            String moduleClass = (String) entry.getOptions().get(ProxyLoginModule.PROPERTY_MODULE);
            if (moduleClass != null) {
                moduleClassNames.add(moduleClass);
            }

        }
        return moduleClassNames;
    }

    public List<JaasRealm> getRealms() {
        return realms;
    }

    void bindRealm(JaasRealm realm) {
        realms.add(realm);
    }

    void unbindRealm(JaasRealm realm) {
        realms.remove(realm);
    }

}
