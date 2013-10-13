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

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.Repository;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

/**
 * Command which lists feature URLs.
 * 
 */
@Command(scope = ListUrlCommand.SCOPE_VALUE, name = ListUrlCommand.FUNCTION_VALUE, description = ListUrlCommand.DESCRIPTION)
@Component(name = ListUrlCommand.ID, description = ListUrlCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ListUrlCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ListUrlCommand.FUNCTION_VALUE)
})
public class ListUrlCommand extends FeaturesCommandSupport {

    public static final String ID = "org.apache.karaf.features.command.listurl";
    public static final String SCOPE_VALUE = "features";
    public static final String FUNCTION_VALUE =  "listUrl";
    public static final String DESCRIPTION = "Displays a list of all defined repository URLs.";

    @Option(name = "-v", aliases = "-validate", description = "Validate current version of descriptors", required = false, multiValued = false)
    boolean validation = false;

    @Option(name = "-vo", aliases = "-verbose", description = "Shows validation output", required = false, multiValued = false)
    boolean verbose = false;

    protected void doExecute(FeaturesService admin) throws Exception {
        Repository[] repos = admin.listRepositories();

        String header;
        if (validation) {
            header = " Loaded   Now valid   URI ";
        } else {
            header = " Loaded   URI ";
        }

        System.out.println(header);

        String verboseOutput = "";

        if ((repos != null) && (repos.length > 0)) {
            for (int i = 0; i < repos.length; i++) {
                URI uri = repos[i].getURI();

                String line = "";
                line += repos[i].isValid() ? "  true " : "  false";

                try {
                    admin.validateRepository(uri);
                    // append valid flag if validation mode is tuned on
                    line += !validation ? "" : "     true   ";
                } catch (Exception e) {
                    line += !validation ? "" : "     false  ";
                    verboseOutput += uri + ":" + e.getMessage() + "\n";
                }

                System.out.println(line + "   " + uri);
            }

            if (verbose) {
                System.out.println("Validation output:\n" + verboseOutput);
            }
        } else {
            System.out.println("No repository URLs are set.");
        }
    }
}
