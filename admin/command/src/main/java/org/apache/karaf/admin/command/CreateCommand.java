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

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.admin.InstanceSettings;
import org.apache.karaf.features.command.completers.AllFeatureCompleter;
import org.apache.karaf.features.command.completers.FeatureRepositoryCompleter;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;

/**
 * Creates a new Karaf instance
 */
@Command(scope = CreateCommand.SCOPE_VALUE, name = CreateCommand.FUNCTION_VALUE, description = CreateCommand.DESCRIPTION)
@Component(name = CreateCommand.ID, description = CreateCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = CreateCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = CreateCommand.FUNCTION_VALUE)
})
public class CreateCommand extends AdminCommandSupport
{
    public static final String ID = "org.apache.karaf.admin.command.create";
    public static final String SCOPE_VALUE = "admin";
    public static final String FUNCTION_VALUE =  "create";
    public static final String DESCRIPTION = "Changes the Java options of an existing container instance.";

    @Option(name = "-s", aliases = {"--ssh-port"}, description = "Port number for remote secure shell connection", required = false, multiValued = false)
    int sshPort = 0;

    @Option(name = "-r", aliases = {"-rr", "--rmi-port", "--rmi-registry-port"}, description = "Port number for RMI registry connection", required = false, multiValued = false)
    int rmiRegistryPort = 0;

    @Option(name = "-rs", aliases = {"--rmi-server-port"}, description = "Port number for RMI server connection", required = false, multiValued = false)
    int rmiServerPort = 0;

    @Option(name = "-l", aliases = {"--location"}, description = "Location of the new container instance in the file system", required = false, multiValued = false)
    String location;

    @Option(name = "-o", aliases = {"--java-opts"}, description = "JVM options to use when launching the instance", required = false, multiValued = false)
    String javaOpts;
    
    @Option(name = "-f", aliases = {"--feature"},
            description = "Initial features. This option can be specified multiple times to enable multiple initial features", required = false, multiValued = true)
    List<String> features;
    
    @Option(name = "-furl", aliases = {"--featureURL"}, 
            description = "Additional feature descriptor URLs. This option can be specified multiple times to add multiple URLs", required = false, multiValued = true)
    List<String> featureURLs;

    @Argument(index = 0, name = "name", description="The name of the new container instance", required = true, multiValued = false)
    String instance = null;

    @Reference(target = "(completer.type="+ AllFeatureCompleter.COMPLETER_TYPE+")")
    Completer allFeaturesCompleter;

    @Reference(target = "(completer.type="+ FeatureRepositoryCompleter.COMPLETER_TYPE+")")
    Completer featureRepositoryCompleter;


    public Object doExecute() throws Exception {
        InstanceSettings settings = new InstanceSettings(sshPort, rmiRegistryPort, rmiServerPort, location, javaOpts, featureURLs, features);
        getAdminService().createInstance(instance, settings);
        return null;
    }

    void bindAllFeaturesCompleter(Completer completer) {
        getOptionalCompleters().put("-f", completer);
    }

    void unbindAllFeaturesCompleter(Completer completer) {
        getOptionalCompleters().remove("-f");
    }

    void bindFeatureRepositoryCompleter(Completer completer) {
        getOptionalCompleters().put("-furl", completer);
    }

    void unbindFeatureRepositoryCompleter(Completer completer) {
        getOptionalCompleters().remove("-furl");
    }

}
