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
@Command(scope = IfAction.SCOPE_VALUE, name = IfAction.FUNCTION_VALUE, description = IfAction.DESCRIPTION)
@Component(name = IfAction.ID, description = IfAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = IfAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = IfAction.FUNCTION_VALUE)
})
public class IfAction extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.commands.if";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "if";
    public static final String DESCRIPTION = "If/Then/Else block.";


    @Argument(name = "condition", index = 0, multiValued = false, required = true, description = "The condition")
    Function condition;

    @Argument(name = "ifTrue", index = 1, multiValued = false, required = true, description = "The function to execute if the condition is true")
    Function ifTrue;

    @Argument(name = "ifFalse", index = 2, multiValued = false, required = false, description = "The function to execute if the condition is false")
    Function ifFalse;

    @Override
    public Object doExecute() throws Exception {
        Object result = condition.execute(getSession(), null);
        if (isTrue(result)) {
            return ifTrue.execute(getSession(), null);
        } else {
            if (ifFalse != null) {
                return ifFalse.execute(getSession(), null);
            }
        }
        return null;
    }

    private boolean isTrue(Object result) {
        if (result == null) {
            return false;
        }
        if (result instanceof String && ((String) result).equals("")) {
            return false;
        }
        if (result instanceof Boolean) {
            return ((Boolean) result).booleanValue();
        }
        return true;
    }

}
