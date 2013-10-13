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
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

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


@Command(scope = TailAction.SCOPE_VALUE, name = TailAction.FUNCTION_VALUE, description = TailAction.DESCRIPTION)
@Component(name = TailAction.ID, description = TailAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = TailAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = TailAction.FUNCTION_VALUE)
})
public class TailAction extends ComponentAction {

    private static final Logger log = LoggerFactory.getLogger(TailAction.class);

    public static final String ID = "org.apache.karaf.shell.commands.tail";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "tail";
    public static final String DESCRIPTION = "Displays the last lines of a file.";


    private static final int DEFAULT_NUMBER_OF_LINES = 10;

    private static final int DEFAULT_SLEEP_INTERVAL = 200;

    @Option(name = "-n", aliases = {}, description = "The number of lines to display, starting at 1.", required = false, multiValued = false)
    private int numberOfLines;

    @Option(name = "-f", aliases = {}, description = "Follow file changes", required = false, multiValued = false)
    private boolean continuous;

    @Option(name = "-s", aliases = {}, description = "Sleep interval (used for follow)", required = false, multiValued = false)
    private long sleepInterval;

    @Argument(index = 0, name = "path or url", description = "A file path or url to display.", required = false, multiValued = false)
    private String path;

    public Object doExecute() throws Exception {
        //If no paths provided assume standar input
        if (path == null || path.trim().length() == 0) {
            if (log.isDebugEnabled()) {
                log.debug("Tailing STDIN");
            }
            tail(new BufferedReader(new InputStreamReader(System.in)));
        } else {
            BufferedReader reader;

            // First try a URL
            try {
                URL url = new URL(path);
                if (log.isDebugEnabled()) {
                    log.debug("Tailing URL: " + url);
                }
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
            }
            catch (MalformedURLException ignore) {
                // They try a file
                File file = new File(path);
                if (log.isDebugEnabled()) {
                    log.debug("Tailing file: " + file);
                }
                reader = new BufferedReader(new FileReader(file));
            }

            try {
                tail(reader);
            }
            finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

        return null;
    }

    /**
     * prints the tail of the file / url
     * 
     * @param reader
     * @throws IOException
     */
    private void tail(final BufferedReader reader) throws InterruptedException, IOException {
        
        if (numberOfLines < 1) {
            numberOfLines = DEFAULT_NUMBER_OF_LINES;
        }
        if (sleepInterval < 1) {
            sleepInterval = DEFAULT_SLEEP_INTERVAL;
        }
        
        LinkedList<String> lines = new LinkedList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
            if (lines.size() > numberOfLines) {
                lines.removeFirst();
            }
        }

        for (String l : lines) {
            System.out.println(l);
        }

        //If command is running as continuous
        while (continuous) {
            Thread.sleep(sleepInterval);
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
