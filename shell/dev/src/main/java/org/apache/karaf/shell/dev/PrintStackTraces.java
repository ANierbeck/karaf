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
package org.apache.karaf.shell.dev;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.console.jline.Console;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * Command for showing the full tree of bundles that have been used to resolve
 * a given bundle.
 */
@Command(scope = PrintStackTraces.SCOPE_VALUE, name = PrintStackTraces.FUNCTION_VALUE, description = PrintStackTraces.DESCRIPTION)
@Component(name = PrintStackTraces.ID, description = PrintStackTraces.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties(
        {
                @Property(name = ComponentAction.SCOPE, value = PrintStackTraces.SCOPE_VALUE),
                @Property(name = ComponentAction.FUNCTION, value = PrintStackTraces.FUNCTION_VALUE)
        }
)
public class PrintStackTraces extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.dev.printstacktraces";
    public static final String SCOPE_VALUE = "dev";
    public static final String FUNCTION_VALUE =  "print-stack-traces";
    public static final String DESCRIPTION = "Prints the full stack trace in the console when the execution of a command throws an exception.\"";

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintStackTraces.class);

    @Argument(name = "print", description="Print stack traces or not", required = false, multiValued = false)
    boolean print = true;

    public Object doExecute() throws Exception {
        getSession().put(Console.PRINT_STACK_TRACES, Boolean.valueOf(print));
        return null;
    }

}
