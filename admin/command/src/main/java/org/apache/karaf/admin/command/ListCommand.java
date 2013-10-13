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
package org.apache.karaf.admin.command;

import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.admin.Instance;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

/**
 * List available instances
 */
@Command(scope = ListCommand.SCOPE_VALUE, name = ListCommand.FUNCTION_VALUE, description = ListCommand.DESCRIPTION)
@Component(name = ListCommand.ID, description = ListCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ListCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ListCommand.FUNCTION_VALUE)
})
public class ListCommand extends AdminCommandSupport {

    public static final String ID = "org.apache.karaf.admin.command.list";
    public static final String SCOPE_VALUE = "admin";
    public static final String FUNCTION_VALUE =  "list";
    public static final String DESCRIPTION = "Lists all existing container instances.";

    @Option(name = "-l", aliases = { "--location" }, description = "Displays the location of the container instances", required = false, multiValued = false)
    boolean location;

    @Option(name = "-o", aliases = { "--java-opts" }, description = "Displays the Java options used to launch the JVM", required = false, multiValued = false)
    boolean javaOpts;

    public Object doExecute() throws Exception {
        getAdminService().refreshInstance();
        Instance[] instances = getAdminService().getInstances();
        if (javaOpts) {
            System.out.println("  SSH Port   RMI Ports         State       Pid  JavaOpts");
        } else if (location) {
            System.out.println("  SSH Port   RMI Ports         State       Pid  Location");
        } else {
            System.out.println("  SSH Port   RMI Ports         State       Pid  Name");
        }
        for (Instance instance : instances) {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            String s = Integer.toString(instance.getSshPort());
            for (int i = s.length(); i < 8; i++) {
                sb.append(' ');
            }
            sb.append(s);
            sb.append("] [");
            String rmiRegistryPort = Integer.toString(instance.getRmiRegistryPort());
            String rmiServerPort = Integer.toString(instance.getRmiServerPort());
            sb.append(rmiRegistryPort).append("/").append(rmiServerPort);
            for (int i = rmiRegistryPort.length() + rmiServerPort.length() + 1; i < 15; i++) {
                sb.append(' ');
            }
            sb.append("] [");
            String state = instance.getState();
            while (state.length() < "starting".length()) {
                state += " ";
            }
            sb.append(state);
            sb.append("] [");
            s = Integer.toString(instance.getPid());
            for (int i = s.length(); i < 5; i++) {
                sb.append(' ');
            }
            sb.append(s);
            sb.append("] ");
            if (javaOpts) {
                sb.append(instance.getJavaOpts());
            } else if (location) {
                sb.append(instance.getLocation());
            } else {
                sb.append(instance.getName());
            }
            System.out.println(sb.toString());
        }
        return null;
    }

}
