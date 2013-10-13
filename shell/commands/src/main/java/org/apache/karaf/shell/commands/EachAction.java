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
import java.util.Collections;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.Function;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

/**
 * Execute a closure on a list of arguments.
 */
@Command(scope = EachAction.SCOPE_VALUE, name = EachAction.FUNCTION_VALUE, description = EachAction.DESCRIPTION)
@Component(name = EachAction.ID, description = EachAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = EachAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = EachAction.FUNCTION_VALUE)
})
public class EachAction extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.commands.each";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "each";
    public static final String DESCRIPTION = "Execute a closure on a list of arguments.";


    @Argument(name = "values", index = 0, multiValued = false, required = true, description = "The collection of arguments to iterate on")
    Collection<Object> values;

    @Argument(name = "function", index = 1, multiValued = false, required = true, description = "The function to execute")
    Function function;

    @Override
    public Object doExecute() throws Exception {
        for (Object v : values) {
            function.execute(getSession(), Collections.singletonList(v));
        }
        return null;
    }
}
