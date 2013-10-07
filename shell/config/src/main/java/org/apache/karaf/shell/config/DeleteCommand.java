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
package org.apache.karaf.shell.config;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.config.completers.ConfigurationCompleter;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.util.Arrays;
import java.util.List;

@Command(scope = DeleteCommand.SCOPE_VALUE, name = DeleteCommand.FUNCTION_VALUE, description = DeleteCommand.DESCRIPTION)
@Component(name = DeleteCommand.ID, description = DeleteCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = DeleteCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = DeleteCommand.FUNCTION_VALUE)
})
public class DeleteCommand extends ConfigCommandSupport {

    public static final String ID = "org.apache.karaf.shell.config.delete";
    public static final String SCOPE_VALUE = "config";
    public static final String FUNCTION_VALUE =  "delete";
    public static final String DESCRIPTION = "Delete a configuration.";

    @Argument(index = 0, name = "pid", description = "PID of the configuration", required = true, multiValued = false)
    String pid;

    @Option(name = "--force", aliases = {}, description = "Force the edition of this config, even if another one was under edition", required = false, multiValued = false)
    boolean force;

    @Option(name = "--no-delete-cfg-file", aliases = { }, description = "Do not delete the configuration file from the etc folder", required = false, multiValued = false)
    boolean noDeleteCfgFile = false;

    @Option(name = "-f", aliases = {"--use-file"}, description = "Configuration lookup using the filename instead of the pid", required = false, multiValued = false)
    boolean useFile;
    @Reference(target = "(completer.type="+ ConfigurationCompleter.COMPLETER_TYPE+")")
    Completer pidCompleter;

    protected void doExecute(ConfigurationAdmin admin) throws Exception {
        String oldPid = (String) getSession().get(PROPERTY_CONFIG_PID);
        if (oldPid != null && oldPid.equals(pid) && !force) {
            System.err.println("This config is being edited.  Cancel / update first, or use the --force option");
            return;
        }

        // User selected to use file instead.
        if (useFile) {
            Configuration configuration = this.findConfigurationByFileName(admin, pid);
            if(configuration == null) {
                System.err.println("Could not find configuration with file install property set to: " + pid);
                return;
            }
            configuration.delete();
        } else {
            Configuration configuration = admin.getConfiguration(pid);
            configuration.delete();
        }
        if (!noDeleteCfgFile) {
            deleteStorage(pid);
        }
        if (oldPid != null && oldPid.equals(pid) && !force) {
            getSession().put(PROPERTY_CONFIG_PID, null);
            getSession().put(PROPERTY_CONFIG_PROPS, null);
        }
    }

    @Override
    public List<Completer> getCompleters() {
        return Arrays.asList(pidCompleter);
    }

}
