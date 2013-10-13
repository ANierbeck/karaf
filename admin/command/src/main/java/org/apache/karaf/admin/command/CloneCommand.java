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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.admin.InstanceSettings;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

/**
 * Clone an existing instance.
 */
@Command(scope = CloneCommand.SCOPE_VALUE, name = CloneCommand.FUNCTION_VALUE, description = CloneCommand.DESCRIPTION)
@Component(name = CloneCommand.ID, description = CloneCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = CloneCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = CloneCommand.FUNCTION_VALUE)
})
public class CloneCommand extends AdminCommandSupport {

    public static final String ID = "org.apache.karaf.admin.command.clone";
    public static final String SCOPE_VALUE = "admin";
    public static final String FUNCTION_VALUE =  "clone";
    public static final String DESCRIPTION = "Clones an existing container instance.";

    @Option(name = "-s", aliases = {"--ssh-port"}, description = "Port number for remote secure shell connection", required = false, multiValued = false)
    int sshPort = 0;

    @Option(name = "-r", aliases = {"-rr", "--rmi-port", "--rmi-registry-port"}, description = "Port number for RMI registry connection", required = false, multiValued = false)
    int rmiRegistryPort = 0;

    @Option(name = "-rs", aliases = {"--rmi-server-port"}, description = "Port number for RMI server connection", required = false, multiValued = false)
    int rmiServerPort = 0;

    @Option(name = "-l", aliases = {"--location"}, description = "Location of the cloned container instance in the file system", required = false, multiValued = false)
    String location;

    @Option(name = "-o", aliases = {"--java-opts"}, description = "JVM options to use when launching the cloned instance", required = false, multiValued = false)
    String javaOpts;

    @Option(name = "-v", aliases = {"--verbose"}, description = "Display actions performed by the command (disabled by default)", required = false, multiValued = false)
    boolean verbose = false;

    @Argument(index = 0, name = "name", description = "The name of the source container instance", required = true, multiValued = false)
    String name;

    @Argument(index = 1, name = "cloneName", description = "The name of the cloned container instance", required = true, multiValued = false)
    String cloneName;


    public Object doExecute() throws Exception {
        InstanceSettings settings = new InstanceSettings(sshPort, rmiRegistryPort, rmiServerPort, location, javaOpts, null, null);
        getAdminService().cloneInstance(name, cloneName, settings);
        return null;
    }

}
