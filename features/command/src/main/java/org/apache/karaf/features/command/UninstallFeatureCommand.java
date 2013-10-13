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
package org.apache.karaf.features.command;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.command.completers.InstalledFeatureCompleter;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;

import java.util.Arrays;
import java.util.List;

@Command(scope = UninstallFeatureCommand.SCOPE_VALUE, name = UninstallFeatureCommand.FUNCTION_VALUE, description = UninstallFeatureCommand.DESCRIPTION)
@Component(name = UninstallFeatureCommand.ID, description = UninstallFeatureCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = UninstallFeatureCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = UninstallFeatureCommand.FUNCTION_VALUE)
})
public class UninstallFeatureCommand extends FeaturesCommandSupport {

    public static final String ID = "org.apache.karaf.features.command.uninstall";
    public static final String SCOPE_VALUE = "features";
    public static final String FUNCTION_VALUE =  "uninstall";
    public static final String DESCRIPTION = "Uninstalls a feature with the specified name and version.";

    @Argument(index = 0, name = "features", description = "The name and version of the features to uninstall. A feature id looks like name/version. The version is optional.", required = true, multiValued = true)
    List<String> features;

    @Reference(target = "(completer.type="+ InstalledFeatureCompleter.COMPLETER_TYPE+")", bind = "bindCompleter", unbind = "unbindCompleter")
    private Completer installedFeaturesCompleter;


    protected void doExecute(FeaturesService admin) throws Exception {
        // iterate in the provided feature
        for (String feature : features) {
            String[] split = feature.split("/");
            String name = split[0];
            String version = null;
            if (split.length == 2) {
                version = split[1];
            }
    	    if (version != null && version.length() > 0) {
    		    admin.uninstallFeature(name, version);
    	    } else {
    		    admin.uninstallFeature(name );
    	    }
        }
    }
}
