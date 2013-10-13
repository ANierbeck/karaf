/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.admin.command;

import java.util.List;

import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = ConnectCommand.SCOPE_VALUE, name = ConnectCommand.FUNCTION_VALUE, description = ConnectCommand.DESCRIPTION)
@Component(name = ConnectCommand.ID, description = ConnectCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ConnectCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ConnectCommand.FUNCTION_VALUE)
})
public class ConnectCommand extends AdminCommandSupport {

    public static final String ID = "org.apache.karaf.admin.command.connect";
    public static final String SCOPE_VALUE = "admin";
    public static final String FUNCTION_VALUE =  "connect";
    public static final String DESCRIPTION = "Connects to an existing container instance.";

    @Option(name="-u", aliases={"--username"}, description="Remote user name", required = false, multiValued = false)
    private String username;

    @Option(name = "-p", aliases = {"--password"}, description = "Remote password", required = false, multiValued = false)
    private String password;

    @Argument(index = 0, name="name", description="The name of the container instance", required = true, multiValued = false)
    private String instance = null;

    @Argument(index = 1, name = "command", description = "Optional command to execute", required = false, multiValued = true)
    private List<String> command;

    public Object doExecute() throws Exception {
        String cmdStr = "";
        if (command != null) {
            StringBuilder sb = new StringBuilder();
            for (String cmd : command) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(cmd);
            }
            cmdStr = "'" + sb.toString().replaceAll("'", "\\'") + "'";
        }

        int port = getExistingInstance(instance).getSshPort();
        if (username != null) {
            if (password == null) {
                getSession().execute("ssh -l " + username + " -p " + port + " localhost " + cmdStr);
            } else {
                getSession().execute("ssh -l " + username + " -P " + password + " -p " + port + " localhost " + cmdStr);
            }
        } else {
            getSession().execute("ssh -p " + port + " localhost " + cmdStr);
        }
        return null;
    }
}
