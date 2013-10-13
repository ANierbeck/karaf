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
package org.apache.karaf.shell.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = HeadAction.SCOPE_VALUE, name = HeadAction.FUNCTION_VALUE, description = HeadAction.DESCRIPTION)
@Component(name = HeadAction.ID, description = HeadAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = HeadAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = HeadAction.FUNCTION_VALUE)
})
public class HeadAction extends ComponentAction {

    private static final Logger log = LoggerFactory.getLogger(HeadAction.class);

    public static final String ID = "org.apache.karaf.shell.commands.head";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "head";
    public static final String DESCRIPTION = "Displays the first lines of a file.";


    private static final int DEFAULT_NUMBER_OF_LINES = 10;

    @Option(name = "-n", aliases = {}, description = "The number of lines to display, starting at 1.", required = false, multiValued = false)
    private int numberOfLines;

    @Argument(index = 0, name = "paths or urls", description = "A list of file paths or urls to display separated by whitespaces.", required = false, multiValued = true)
    private List<String> paths;

    public Object doExecute() throws Exception {
        //If no paths provided assume standar input
        if (paths == null || paths.size() == 0) {
            if (log.isDebugEnabled()) {
                log.debug("Heading STDIN");
            }

            head(new BufferedReader(new InputStreamReader(System.in)));
        } else {
            for (String filename : paths) {
                BufferedReader reader;

                // First try a URL
                try {
                    URL url = new URL(filename);
                    if (log.isDebugEnabled()) {
                        log.debug("Heading URL: " + url);
                    }
                    reader = new BufferedReader(new InputStreamReader(url.openStream()));
                }
                catch (MalformedURLException ignore) {
                    // They try a file
                    File file = new File(filename);
                    if (log.isDebugEnabled()) {
                        log.debug("Heading file: " + file);
                    }
                    reader = new BufferedReader(new FileReader(file));
                }

                try {
                    head(reader);
                }
                finally {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
        return null;
    }

    private void head(final BufferedReader reader) throws IOException {
        String line;
        int lineno = 1;

        if (numberOfLines < 1) {
            numberOfLines = DEFAULT_NUMBER_OF_LINES;
        }

        while ((line = reader.readLine()) != null && lineno <= numberOfLines) {
            System.out.println(line);
            lineno++;
        }
    }
}
