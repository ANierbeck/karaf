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

import java.util.Collection;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = PrintfAction.SCOPE_VALUE, name = PrintfAction.FUNCTION_VALUE, description = PrintfAction.DESCRIPTION)
@Component(name = PrintfAction.ID, description = PrintfAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = PrintfAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = PrintfAction.FUNCTION_VALUE)
})
public class PrintfAction extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.commands.printf";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "printf";
    public static final String DESCRIPTION = "Formats and prints arguments.";


    @Argument(index = 0, name = "format", description = "The format pattern to use", required = true, multiValued = false)
    private String format;

    @Argument(index = 1, name = "arguments", description = "The arguments for the given format pattern", required = true, multiValued = true)
    private Collection<Object> arguments = null;

    public Object doExecute() throws Exception {
        System.out.printf(format, arguments.toArray());
        return null;
    }
}
