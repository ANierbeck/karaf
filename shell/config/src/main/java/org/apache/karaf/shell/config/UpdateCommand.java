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

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.service.cm.ConfigurationAdmin;

@Command(scope = UpdateCommand.SCOPE_VALUE, name = UpdateCommand.FUNCTION_VALUE, description = UpdateCommand.DESCRIPTION)
@Component(name = UpdateCommand.ID, description = UpdateCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = UpdateCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = UpdateCommand.FUNCTION_VALUE)
})
public class UpdateCommand extends ConfigCommandSupport {

    public static final String ID = "org.apache.karaf.shell.config.update";
    public static final String SCOPE_VALUE = "config";
    public static final String FUNCTION_VALUE =  "update";
    public static final String DESCRIPTION = "Saves and propagates changes from the configuration being edited.";


    @Option(name = "-b", aliases = {"--bypass-storage"}, multiValued = false, required = false, description = "Do not store the configuration in a properties file, but feed it directly to ConfigAdmin")
    protected boolean bypassStorage;
    
    protected void doExecute(ConfigurationAdmin admin) throws Exception {
        Dictionary props = getEditedProps();
        if (props == null) {
            System.err.println("No configuration is being edited--run the edit command first");
            return;
        }

        String pid = (String) getSession().get(PROPERTY_CONFIG_PID);
        update(admin, pid, props, bypassStorage);
        getSession().put(PROPERTY_CONFIG_PID, null);
        getSession().put(PROPERTY_CONFIG_PROPS, null);
    }
}
