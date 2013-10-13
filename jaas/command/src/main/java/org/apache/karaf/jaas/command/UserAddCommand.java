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

@Command(scope = UserAddCommand.SCOPE_VALUE, name = UserAddCommand.FUNCTION_VALUE, description = UserAddCommand.DESCRIPTION)
@Component(name = UserAddCommand.ID, description = UserAddCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = UserAddCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = UserAddCommand.FUNCTION_VALUE)
})
public class UserAddCommand extends JaasCommandSupport {

    public static final String ID = "org.apache.karaf.jaas.command.useradd";
    public static final String SCOPE_VALUE = "jaas";
    public static final String FUNCTION_VALUE =  "useradd";
    public static final String DESCRIPTION = "Add a user";

    @Argument(index = 0, name = "username", description = "User Name", required = true, multiValued = false)
    private String username;

    @Argument(index = 1, name = "password", description = "Password", required = true, multiValued = false)
    private String password;


    /**
     * Execute the RoleAddCommand in the given Excecution Context.
     *
     * @param engine
     * @return
     * @throws Exception
     */
    @Override
    protected Object doExecute(BackingEngine engine) throws Exception {
        engine.addUser(username, password);
        return null;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserAddCommand{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
