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

import java.util.List;
import javax.security.auth.login.AppConfigurationEntry;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.jaas.boot.principal.RolePrincipal;
import org.apache.karaf.jaas.boot.principal.UserPrincipal;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = ListUsersCommand.SCOPE_VALUE, name = ListUsersCommand.FUNCTION_VALUE, description = ListUsersCommand.DESCRIPTION)
@Component(name = ListUsersCommand.ID, description = ListUsersCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ListUsersCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ListUsersCommand.FUNCTION_VALUE)
})
public class ListUsersCommand extends JaasCommandSupport {

    public static final String ID = "org.apache.karaf.jaas.command.users";
    public static final String SCOPE_VALUE = "jaas";
    public static final String FUNCTION_VALUE =  "users";
    public static final String DESCRIPTION = "List the users of the selected JAAS Realm/Login ModuleImpl.";

    private static final String OUTPUT_FORMAT = "%-20s %-20s";

    @Override
    public Object doExecute() throws Exception {
        JaasRealm realm = (JaasRealm) getSession().get(JAAS_REALM);
        AppConfigurationEntry entry = (AppConfigurationEntry) getSession().get(JAAS_ENTRY);

        if (realm == null || entry == null) {
            System.err.println("No JAAS Realm / ModuleImpl has been selected.");
            return null;
        }

        BackingEngine engine = backingEngineService.get(entry);

        if (engine == null) {
            System.err.println("Can't get the list of users (no backing engine service registered)");
            return null;
        }

        return doExecute(engine);
    }

    @Override
    protected Object doExecute(BackingEngine engine) throws Exception {
        List<UserPrincipal> users = engine.listUsers();
        System.out.println(String.format(OUTPUT_FORMAT, "User Name", "Role"));

        for (UserPrincipal user : users) {
            String userName = user.getName();
            List<RolePrincipal> roles = engine.listRoles(user);

            if (roles != null && roles.size() >= 1) {
                for (RolePrincipal role : roles) {
                    String roleName = role.getName();
                    System.out.println(String.format(OUTPUT_FORMAT, userName, roleName));
                }
            } else {
                System.out.println(String.format(OUTPUT_FORMAT, userName, ""));
            }

        }
        return null;
    }
}
