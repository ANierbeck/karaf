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
import java.util.Enumeration;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = ListCommand.SCOPE_VALUE, name = ListCommand.FUNCTION_VALUE, description = ListCommand.DESCRIPTION)
@Component(name = ListCommand.ID, description = ListCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ListCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ListCommand.FUNCTION_VALUE)
})
public class ListCommand extends ConfigCommandSupport {

    public static final String ID = "org.apache.karaf.shell.config.list";
    public static final String SCOPE_VALUE = "config";
    public static final String FUNCTION_VALUE =  "list";
    public static final String DESCRIPTION = "Lists existing configurations.";

    @Argument(index = 0, name = "query", description = "Query in LDAP syntax. Example: \"(service.pid=org.apache.karaf.log)\"", required = false, multiValued = false)
    String query;

    protected void doExecute(ConfigurationAdmin admin) throws Exception {
        Configuration[] configs = admin.listConfigurations(query);
        if (configs != null) {
            for (Configuration config : configs) {
                System.out.println("----------------------------------------------------------------");
                System.out.println("Pid:            " + config.getPid());
                if (config.getFactoryPid() != null) {
                    System.out.println("FactoryPid:     " + config.getFactoryPid());
                }
                System.out.println("BundleLocation: " + config.getBundleLocation());
                if (config.getProperties() != null) {
                    System.out.println("Properties:");
                    Dictionary props = config.getProperties();
                    for (Enumeration e = props.keys(); e.hasMoreElements();) {
                        Object key = e.nextElement();
                        System.out.println("   " + key + " = " + props.get(key));
                    }
                }
            }
        }
    }
}
