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
import org.apache.karaf.features.Repository;
import org.apache.karaf.features.command.completers.FeatureRepositoryNameCompleter;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Command(scope = RemoveRepositoryCommand.SCOPE_VALUE, name = RemoveRepositoryCommand.FUNCTION_VALUE, description = RemoveRepositoryCommand.DESCRIPTION)
@Component(name = RemoveRepositoryCommand.ID, description = RemoveRepositoryCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = RemoveRepositoryCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = RemoveRepositoryCommand.FUNCTION_VALUE)
})
public class RemoveRepositoryCommand extends FeaturesCommandSupport {

    public static final String ID = "org.apache.karaf.features.command.removerepository";
    public static final String SCOPE_VALUE = "features";
    public static final String FUNCTION_VALUE =  "removeRepository";
    public static final String DESCRIPTION = "Removes the specified repository features service.";

    @Argument(index = 0, name = "repository", description = "Name of the repository to remove.", required = true, multiValued = false)
    private String repository;

    @Reference(target = "(completer.type="+ FeatureRepositoryNameCompleter.COMPLETER_TYPE+")", bind = "bindCompleter", unbind = "unbindCompleter")
    private Completer repositoryCompleter;

    protected void doExecute(FeaturesService admin) throws Exception {
    	URI uri = null;
    	for (Repository r :admin.listRepositories()) {
    		if (r.getName().equals(repository)) {
    			uri = r.getURI();
    			break;
    		}
    	}

    	if (uri == null) {
    		System.out.println("Repository '" + repository + "' not found.") ;
    	} else {
    		admin.removeRepository(uri);
    	}
    }

    @Override
    public List<Completer> getCompleters() {
        return Arrays.asList(repositoryCompleter);
    }
}
