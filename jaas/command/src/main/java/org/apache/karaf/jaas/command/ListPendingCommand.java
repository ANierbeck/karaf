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

import java.util.Queue;
import javax.security.auth.login.AppConfigurationEntry;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = ListPendingCommand.SCOPE_VALUE, name = ListPendingCommand.FUNCTION_VALUE, description = ListPendingCommand.DESCRIPTION)
@Component(name = ListPendingCommand.ID, description = ListPendingCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ListPendingCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ListPendingCommand.FUNCTION_VALUE)
})
public class ListPendingCommand extends JaasCommandSupport {

    public static final String ID = "org.apache.karaf.jaas.command.pending";
    public static final String SCOPE_VALUE = "jaas";
    public static final String FUNCTION_VALUE =  "pending";
    public static final String DESCRIPTION = "List the modification on the selected JAAS Realm/Login ModuleImpl.";

    @Override
    public Object doExecute() throws Exception {
        JaasRealm realm = (JaasRealm) getSession().get(JAAS_REALM);
        AppConfigurationEntry entry = (AppConfigurationEntry) getSession().get(JAAS_ENTRY);
        Queue<JaasCommandSupport> commandQueue = (Queue<JaasCommandSupport>) getSession().get(JAAS_CMDS);

        if (realm != null && entry != null) {
            String moduleClass = (String) entry.getOptions().get(ProxyLoginModule.PROPERTY_MODULE);
            System.out.println(String.format("JAAS Realm %s/JAAS Login ModuleImpl %s", realm.getName(), moduleClass));

            if (commandQueue != null && !commandQueue.isEmpty()) {
                for (JaasCommandSupport command : commandQueue) {
                    System.out.println(command);
                }
            } else {
                System.err.println("No JAAS pending modification");
            }
        } else {
            System.err.println("No JAAS Realm/Login ModuleImpl has been selected");
        }
        return null;
    }

    @Override
    protected Object doExecute(BackingEngine engine) throws Exception {
        return null;
    }
}