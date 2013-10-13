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
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

@Command(scope = AliasAction.SCOPE_VALUE, name = AliasAction.FUNCTION_VALUE, description = AliasAction.DESCRIPTION)
@Component(name = AliasAction.ID, description = AliasAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = AliasAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = AliasAction.FUNCTION_VALUE)
})
public class AliasAction extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.commands.alias";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "alias";
    public static final String DESCRIPTION = "Creates or edits a configuration.";

    @Argument(index = 0, name = "command", description = "The command to alias, e.g. 'ldn = { log:display -n $args }'", required = true, multiValued = false)
    private String alias;

    public Object doExecute() throws Exception {
        getSession().execute(alias);
        return null;
    }

}
