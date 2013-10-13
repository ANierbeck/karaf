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

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.Repository;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = ListRepositoriesCommand.SCOPE_VALUE, name = ListRepositoriesCommand.FUNCTION_VALUE, description = ListRepositoriesCommand.DESCRIPTION)
@Component(name = ListRepositoriesCommand.ID, description = ListRepositoriesCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ListRepositoriesCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ListRepositoriesCommand.FUNCTION_VALUE)
})
public class ListRepositoriesCommand extends FeaturesCommandSupport {

    public static final String ID = "org.apache.karaf.features.command.listrepositories";
    public static final String SCOPE_VALUE = "features";
    public static final String FUNCTION_VALUE =  "listRepositories";
    public static final String DESCRIPTION = "Displays a list of all defined repositories.";

	private static final String REPOSITORY = "Repository";
	private static final String REPOSITORY_URL = "Repository URL";

	@Option(name = "-u", description = "Display repository URLs.", required = false, multiValued = false)
    boolean showUrl;
	
    protected void doExecute(FeaturesService admin) throws Exception {
    	
        StringBuffer sb = null;      

        Repository[] repos = admin.listRepositories();
          
    	int maxRepositorySize = REPOSITORY.length();    	
        int maxRepositoryUrlSize = REPOSITORY_URL.length();
        for (Repository r : repos) { 
        	maxRepositorySize = Math.max(maxRepositorySize, r.getName().length());
        	maxRepositoryUrlSize = Math.max(maxRepositoryUrlSize, r.getURI().toString().length());
        }
        
        if ((repos != null) && (repos.length > 0)) {
            // Prepare the header
            sb = new StringBuffer();        
            append(sb, REPOSITORY, maxRepositorySize + 2);
            if (showUrl) { 
            	append(sb, REPOSITORY_URL, maxRepositoryUrlSize + 2);   
            }
            System.out.println(sb.toString());
            

        	for (int i = 0; i < repos.length; i++) {
            	sb = new StringBuffer();        
                append(sb, repos[i].getName(), maxRepositorySize + 2);
                if (showUrl) { 
                	append(sb, repos[i].getURI().toString(), maxRepositoryUrlSize + 2); 
                }
            	System.out.println(sb.toString());
            }
        } else {
            System.out.println("No repositories available.");
        }
    }
    
    private void append(StringBuffer sb, String s, int width) { 
    	sb.append(s);
    	for (int i = s.length(); i < width; i++) { 
    		sb.append(" ");
    	}
    }
}
