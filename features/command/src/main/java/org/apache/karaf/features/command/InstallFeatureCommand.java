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

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.command.completers.AvailableFeatureCompleter;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;

import java.util.EnumSet;
import java.util.List;

@Command(scope = InstallFeatureCommand.SCOPE_VALUE, name = InstallFeatureCommand.FUNCTION_VALUE, description = InstallFeatureCommand.DESCRIPTION)
@Component(name = InstallFeatureCommand.ID, description = InstallFeatureCommand.DESCRIPTION, immediate = true)
@Service({Action.class, CompletableFunction.class})
@Properties({
        @Property(name = ComponentAction.SCOPE, value = InstallFeatureCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = InstallFeatureCommand.FUNCTION_VALUE)
})
public class InstallFeatureCommand extends FeaturesCommandSupport {

    public static final String ID = "org.apache.karaf.features.command.install";
    public static final String SCOPE_VALUE = "features";
    public static final String FUNCTION_VALUE =  "install";
    public static final String DESCRIPTION = "Installs a feature with the specified name and version.";

    private static String DEFAULT_VERSION = "0.0.0";

    @Argument(index = 0, name = "feature", description = "The name and version of the features to install. A feature id looks like name/version. The version is optional.", required = true, multiValued = true)
    List<String> features;
    @Option(name = "-c", aliases = "--no-clean", description = "Do not uninstall bundles on failure", required = false, multiValued = false)
    boolean noClean;
    @Option(name = "-r", aliases = "--no-auto-refresh", description = "Do not automatically refresh bundles", required = false, multiValued = false)
    boolean noRefresh;
    @Option(name = "-v", aliases = "--verbose", description = "Explain what is being done", required = false, multiValued = false)
    boolean verbose;


    @Reference(target = "(completer.type="+ AvailableFeatureCompleter.COMPLETER_TYPE+")", bind = "bindCompleter", unbind = "unbindCompleter")
    private Completer availableFeaturesCompleter;

    protected void doExecute(FeaturesService admin) throws Exception {
        for (String feature : features) {
            String[] split = feature.split("/");
            String name = split[0];
            String version = null;
            if (split.length == 2) {
                version = split[1];
            }
    	    if (version == null || version.length() == 0) {
                version = DEFAULT_VERSION;
    	    }
            EnumSet<FeaturesService.Option> options = EnumSet.of(FeaturesService.Option.PrintBundlesToRefresh);
            if (noRefresh) {
                options.add(FeaturesService.Option.NoAutoRefreshBundles);
            }
            if (noClean) {
                options.add(FeaturesService.Option.NoCleanIfFailure);
            }
            if (verbose) {
                options.add(FeaturesService.Option.Verbose);
            }
            admin.installFeature(name, version, options);
        }
    }
}
