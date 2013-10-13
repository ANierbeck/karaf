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
import java.util.ArrayList;
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
import org.apache.karaf.features.Repository;
import org.apache.karaf.features.command.completers.FeatureRepositoryCompleter;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.MultiException;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = RefreshUrlCommand.SCOPE_VALUE, name = RefreshUrlCommand.FUNCTION_VALUE, description = RefreshUrlCommand.DESCRIPTION)
@Component(name = RefreshUrlCommand.ID, description = RefreshUrlCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = RefreshUrlCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = RefreshUrlCommand.FUNCTION_VALUE)
})
public class RefreshUrlCommand extends FeaturesCommandSupport {

    public static final String ID = "org.apache.karaf.features.command.refreshurl";
    public static final String SCOPE_VALUE = "features";
    public static final String FUNCTION_VALUE =  "refreshUrl";
    public static final String DESCRIPTION = "Reloads the list of available features from the repositories.";

    @Argument(index = 0, name = "urls", description = "Repository URLs to reload (leave empty for all)", required = false, multiValued = true)
    List<String> urls;

    @Reference(target = "(completer.type="+ FeatureRepositoryCompleter.COMPLETER_TYPE+")", bind = "bindCompleter", unbind = "unbindCompleter")
    private Completer repositoryUrlCompleter;


    protected void doExecute(FeaturesService admin) throws Exception {
        if (urls == null || urls.isEmpty()) {
            urls = new ArrayList<String>();
            for (Repository repo : admin.listRepositories()) {
                urls.add(repo.getURI().toString());
            }
        }
        List<Exception> exceptions = new ArrayList<Exception>();
        for (String strUri : urls) {
            try {
                URI uri = new URI(strUri);
                admin.removeRepository(uri);
                admin.addRepository(uri);
            } catch (Exception e) {
                exceptions.add(e);
                //get chance to restore previous, fix for KARAF-4
                admin.restoreRepository(new URI(strUri));
            }
        }
        MultiException.throwIf("Unable to add repositories", exceptions);
    }

    @Override
    public List<Completer> getCompleters() {
        return Arrays.asList(repositoryUrlCompleter);
    }
}
