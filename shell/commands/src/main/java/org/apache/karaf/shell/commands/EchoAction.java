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

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = EchoAction.SCOPE_VALUE, name = EchoAction.FUNCTION_VALUE, description = EchoAction.DESCRIPTION)
@Component(name = EchoAction.ID, description = EchoAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = EchoAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = EchoAction.FUNCTION_VALUE)
})
public class EchoAction extends ComponentAction
{

    public static final String ID = "org.apache.karaf.shell.commands.echo";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "echo";
    public static final String DESCRIPTION = "Echoes or prints arguments to STDOUT.";


    @Option(name = "-n", aliases = {}, description = "Do not print the trailing newline character", required = false, multiValued = false)
    private boolean noTrailingNewline = false;

    @Argument(index = 0, name = "arguments", description="Arguments to display separated by whitespaces", required = false, multiValued = true)
    private List<String> args;

    public Object doExecute() throws Exception {
        if (args != null) {
            boolean first = true;
            for (String arg : args) {
                if (first) {
                    first = false;
                } else {
                    System.out.print(" ");
                }
                System.out.print(arg);
            }
        }

        if (!noTrailingNewline) {
            System.out.println();
        }

        return null;
    }
}
