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

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.command.completers.FeatureRepoNameCompleter;
import org.apache.karaf.features.command.completers.FeatureRepositoryCompleter;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;

/**
 * Concatenate and print files and/or URLs.
 */
@Command(scope = ChooseUrlCommand.SCOPE_VALUE, name = ChooseUrlCommand.FUNCTION_VALUE, description = ChooseUrlCommand.DESCRIPTION)
@Component(name = ChooseUrlCommand.ID, description = ChooseUrlCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ChooseUrlCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ChooseUrlCommand.FUNCTION_VALUE)
})
public class ChooseUrlCommand extends FeaturesCommandSupport {

    public static final String ID = "org.apache.karaf.features.command.chooseurl";
    public static final String SCOPE_VALUE = "features";
    public static final String FUNCTION_VALUE =  "chooseurl";
    public static final String DESCRIPTION = "Add a repository url for well known features";

    @Argument(index = 0, name = "", description = "", required = true, multiValued = false)
    private String name;
    
    @Argument(index = 1, name = "", description = "", required = false, multiValued = false)
    private String version;

    @Reference
    private FeatureFinder featureFinder;


    @Reference(target = "(completer.type="+ FeatureRepoNameCompleter.COMPLETER_TYPE+")", bind = "bindCompleter", unbind = "unbindCompleter")
    private Completer repoCompleter;


    public void doExecute(FeaturesService featuresService) throws Exception {
        String effectiveVersion = (version == null) ? "LATEST" : version;
        URI uri = featureFinder.getUriFor(name, effectiveVersion);
        if (uri == null) {
            throw new RuntimeException("No feature found for name " + name + " and version " + version);
        }
        System.out.println("adding feature url " + uri);
        featuresService.addRepository(uri);
    }

    @Override
    public List<Completer> getCompleters() {
        return Arrays.asList(repoCompleter);
    }
}
