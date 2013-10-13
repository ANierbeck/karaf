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

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concatenate and print files and/or URLs.
 */
@Command(scope = CatAction.SCOPE_VALUE, name = CatAction.FUNCTION_VALUE, description = CatAction.DESCRIPTION)
@Component(name = CatAction.ID, description = CatAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = CatAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = CatAction.FUNCTION_VALUE)
})
public class CatAction extends ComponentAction {

    private static final Logger log = LoggerFactory.getLogger(CatAction.class);

    public static final String ID = "org.apache.karaf.shell.commands.cat";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "cat";
    public static final String DESCRIPTION = "Displays the content of a file or URL.";


    @Option(name = "-n", aliases = {}, description = "Number the output lines, starting at 1.", required = false, multiValued = false)
    private boolean displayLineNumbers;

    @Argument(index = 0, name = "paths or urls", description = "A list of file paths or urls to display separated by whitespace (use - for STDIN)", required = true, multiValued = true)
    private List<String> paths;

    public Object doExecute() throws Exception {
        //
        // Support "-" if length is one, and read from io.in
        // This will help test command pipelines.
        //
        if (paths.size() == 1 && "-".equals(paths.get(0))) {
            log.info("Printing STDIN");
            cat(new BufferedReader(new InputStreamReader(System.in)));
        }
        else {
            for (String filename : paths) {
                BufferedReader reader;

                // First try a URL
                try {
                    URL url = new URL(filename);
                    log.info("Printing URL: " + url);
                    reader = new BufferedReader(new InputStreamReader(url.openStream()));
                }
                catch (MalformedURLException ignore) {
                    // They try a file
                    File file = new File(filename);
                    log.info("Printing file: " + file);
                    reader = new BufferedReader(new FileReader(file));
                }

                try {
                    cat(reader);
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

    private void cat(final BufferedReader reader) throws IOException
    {
        String line;
        int lineno = 1;

        while ((line = reader.readLine()) != null) {
            if (displayLineNumbers) {
                System.out.print(String.format("%6d  ", lineno++));
            }
            System.out.println(line);
        }
    }
}
