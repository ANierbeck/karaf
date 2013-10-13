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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = UserDeleteCommand.SCOPE_VALUE, name = UserDeleteCommand.FUNCTION_VALUE, description = UserDeleteCommand.DESCRIPTION)
@Component(name = UserDeleteCommand.ID, description = UserDeleteCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = UserDeleteCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = UserDeleteCommand.FUNCTION_VALUE)
})
public class UserDeleteCommand extends JaasCommandSupport {

    public static final String ID = "org.apache.karaf.jaas.command.userdel";
    public static final String SCOPE_VALUE = "jaas";
    public static final String FUNCTION_VALUE =  "userdel";
    public static final String DESCRIPTION = "Delete a user";

    @Argument(index = 0, name = "username", description = "User Name", required = true, multiValued = false)
    private String username;

    /**
     * Execute the RoleAddCommand in the given Excecution Context.
     *
     * @param engine
     * @return
     * @throws Exception
     */
    @Override
    protected Object doExecute(BackingEngine engine) throws Exception {
        engine.deleteUser(username);
        return null;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserDeleteCommand{" +
                "username='" + username + '\'' +
                '}';
    }
}
