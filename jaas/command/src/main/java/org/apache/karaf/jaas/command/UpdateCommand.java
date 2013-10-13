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
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.LinkedList;
import java.util.Queue;

@Command(scope = UpdateCommand.SCOPE_VALUE, name = UpdateCommand.FUNCTION_VALUE, description = UpdateCommand.DESCRIPTION)
@Component(name = UpdateCommand.ID, description = UpdateCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = UpdateCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = UpdateCommand.FUNCTION_VALUE)
})
public class UpdateCommand extends JaasCommandSupport {

    public static final String ID = "org.apache.karaf.jaas.command.update";
    public static final String SCOPE_VALUE = "jaas";
    public static final String FUNCTION_VALUE =  "update";
    public static final String DESCRIPTION = "Update the selected JAAS Realm.";

    @Override
    public Object doExecute() throws Exception {
        JaasRealm realm = (JaasRealm) getSession().get(JAAS_REALM);
        AppConfigurationEntry entry = (AppConfigurationEntry) getSession().get(JAAS_ENTRY);

        if (realm == null || entry == null) {
            System.err.println("No JAAS Realm/Login ModuleImpl selected");
            return null;
        }

        BackingEngine engine = backingEngineService.get(entry);

        if (engine == null) {
            System.err.println("Can't update the JAAS realm (no backing engine service registered)");
            return null;
        }

        return doExecute(engine);
    }

    @Override
    protected Object doExecute(BackingEngine engine) throws Exception {
        Queue<? extends JaasCommandSupport> commands = (Queue<? extends JaasCommandSupport>) getSession().get(JAAS_CMDS);

        if (commands == null || commands.isEmpty()) {
            System.err.println("No pending modification");
            return null;
        }

        // loop in the commands and execute them.
        while (!commands.isEmpty()) {
            Object obj = commands.remove();
            if (obj instanceof JaasCommandSupport) {
                ((JaasCommandSupport) obj).doExecute(engine);
            }
        }

        // cleanup the session
        getSession().put(JAAS_REALM, null);
        getSession().put(JAAS_ENTRY, null);
        getSession().put(JAAS_CMDS, new LinkedList<JaasCommandSupport>());
        return null;
    }
}
