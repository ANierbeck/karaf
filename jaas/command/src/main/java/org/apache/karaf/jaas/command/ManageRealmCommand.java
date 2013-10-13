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
package org.apache.karaf.jaas.command;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.apache.karaf.jaas.command.completers.LoginModuleNameCompleter;
import org.apache.karaf.jaas.command.completers.RealmCompleter;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Command(scope = ManageRealmCommand.SCOPE_VALUE, name = ManageRealmCommand.FUNCTION_VALUE, description = ManageRealmCommand.DESCRIPTION)
@Component(name = ManageRealmCommand.ID, description = ManageRealmCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ManageRealmCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ManageRealmCommand.FUNCTION_VALUE)
})
public class ManageRealmCommand extends JaasCommandSupport {

    public static final String ID = "org.apache.karaf.jaas.command.manage";
    public static final String SCOPE_VALUE = "jaas";
    public static final String FUNCTION_VALUE =  "manage";
    public static final String DESCRIPTION = "Manage users and roles of a JAAS Realm.";

    @Option(name = "--realm", description = "Realm Name", required = false, multiValued = false)
    String realmName;

    @Option(name = "--index", description = "Realm Index", required = false, multiValued = false)
    int index;

    @Option(name = "--module", aliases = {}, description = "Login ModuleImpl Class Name", required = false, multiValued = false)
    String moduleName;

    @Option(name = "-f", aliases = {"--force"}, description = "Force the management of this realm, even if another one was under management", required = false, multiValued = false)
    boolean force;

    @Reference(target = "(completer.type="+RealmCompleter.COMPLETER_TYPE+")", bind = "bindRealmCompleter", unbind = "unbindRealmCompleter")
    private Completer realmCompleter;
    @Reference(target = "(completer.type="+LoginModuleNameCompleter.COMPLETER_TYPE+")",bind = "bindLoginModuleNameCompleter", unbind = "unbindLoginModuleNameCompleter")
    private Completer loginModuleNameCompleter;

    @Override
    public Object doExecute() throws Exception {
        if (realmName == null && index <= 0) {
            System.err.println("A valid realm or the realm index need to be specified");
            return null;
        }
        JaasRealm oldRealm = (JaasRealm) getSession().get(JAAS_REALM);
        AppConfigurationEntry oldEntry = (AppConfigurationEntry) getSession().get(JAAS_ENTRY);

        if (oldRealm != null && !oldRealm.getName().equals(realmName) && !force) {
            System.err.println("Another JAAS Realm is being edited. Cancel/update first, or use the --force option.");
        } else if (oldEntry != null && !oldEntry.getLoginModuleName().equals(moduleName) && !force) {
            System.err.println("Another JAAS Login ModuleImpl is being edited. Cancel/update first, or use the --force option.");
        } else {

            JaasRealm realm = null;
            AppConfigurationEntry entry = null;

            if (index > 0) {
                // user provided the index, get the realm AND entry from the index
                List<JaasRealm> realms = getRealms();
                if (realms != null && realms.size() > 0) {
                    int i = 1;
                    realms_loop: for (JaasRealm r : realms) {
                        AppConfigurationEntry[] entries = r.getEntries();

                        if (entries != null) {
                            for (int j = 0; j < entries.length; j++) {
                                if (i == index) {
                                    realm = r;
                                    entry = entries[j];
                                    break realms_loop;
                                }
                                i++;
                            }
                        }
                    }
                }
            } else {
                List<JaasRealm> realms = getRealms();
                if (realms != null && realms.size() > 0) {
                    for (JaasRealm r : realms) {
                        if (r.getName().equals(realmName)) {
                            realm = r;
                            break;
                        }
                    }

                }
                AppConfigurationEntry[] entries = realm.getEntries();
                if (entries != null) {
                    for (AppConfigurationEntry e : entries) {
                        String moduleClass = (String) e.getOptions().get(ProxyLoginModule.PROPERTY_MODULE);
                        if (moduleName == null) {
                            entry = e;
                            break;
                        } else {
                            if (moduleName.equals(e.getLoginModuleName()) || moduleName.equals(moduleClass)) {
                                entry = e;
                                break;
                            }
                        }
                    }
                }
            }

            if (realm == null) {
                System.err.println("JAAS realm has not been found.");
                return null;
            }

            if (entry == null) {
                System.err.println("JAAS module has not been found.");
                return null;
            }

            Queue<JaasCommandSupport> commands = null;

            commands = (Queue<JaasCommandSupport>) getSession().get(JAAS_CMDS);
            if (commands == null) {
                commands = new LinkedList<JaasCommandSupport>();
            }

            getSession().put(JAAS_REALM, realm);
            getSession().put(JAAS_ENTRY, entry);
            getSession().put(JAAS_CMDS, commands);
        }
        return null;
    }

    @Override
    protected Object doExecute(BackingEngine engine) throws Exception {
        return null;
    }


    void bindRealmCompleter(Completer completer) {
        this.realmCompleter = completer;
        getOptionalCompleters().put("--realm", realmCompleter);
    }

    void unbindRealmCompleter(Completer completer) {
        this.realmCompleter = null;
        getOptionalCompleters().remove("--realm");
    }

    void bindLoginModuleNameCompleter(Completer completer) {
        this.loginModuleNameCompleter = completer;
        getOptionalCompleters().put("--module", completer);
    }

    void unbindLoginModuleNameCompleter(Completer completer) {
        this.loginModuleNameCompleter = null;
        getOptionalCompleters().remove("--module");
    }

}
