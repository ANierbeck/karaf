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

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.util.process.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute system processes.
 */
@Command(scope = ExecuteAction.SCOPE_VALUE, name = ExecuteAction.FUNCTION_VALUE, description = ExecuteAction.DESCRIPTION)
@Component(name = ExecuteAction.ID, description = ExecuteAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ExecuteAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ExecuteAction.FUNCTION_VALUE)
})
public class ExecuteAction extends ComponentAction {

    private static final Logger log = LoggerFactory.getLogger(ExecuteAction.class);

    public static final String ID = "org.apache.karaf.shell.commands.exec";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "exec";
    public static final String DESCRIPTION = "Executes system processes.";


    @Argument(index = 0, name = "command", description = "Execution command with arguments", required = true, multiValued = true)
    private List<String> args;

    public Object doExecute() throws Exception {
        ProcessBuilder builder = new ProcessBuilder(args);

        PumpStreamHandler handler = new PumpStreamHandler(System.in, System.out, System.err, "Command" + args.toString());

        log.info("Executing: {}", builder.command());
        Process p = builder.start();

        handler.attach(p);
        handler.start();

        log.debug("Waiting for process to exit...");
        
        int status = p.waitFor();

        log.info("Process exited w/status: {}", status);

        handler.stop();

        return null;
    }

}
