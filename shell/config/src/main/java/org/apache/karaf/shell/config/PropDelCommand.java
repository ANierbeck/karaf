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
package org.apache.karaf.shell.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.config.completers.ConfigurationCompleter;
import org.apache.karaf.shell.config.completers.ConfigurationPropertyCompleter;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.console.completer.NullCompleter;

@Command(scope = PropDelCommand.SCOPE_VALUE, name = PropDelCommand.FUNCTION_VALUE, description = PropDelCommand.DESCRIPTION)
@Component(name = PropDelCommand.ID, description = PropDelCommand.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = PropDelCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = PropDelCommand.FUNCTION_VALUE)
})
public class PropDelCommand extends ConfigPropertyCommandSupport {

    public static final String ID = "org.apache.karaf.shell.config.propdel";
    public static final String SCOPE_VALUE = "config";
    public static final String FUNCTION_VALUE =  "propdel";
    public static final String DESCRIPTION = "Deletes a property from configuration being edited.";

    @Argument(index = 0, name = "property", description = "The name of the property to delete", required = true, multiValued = false)
    String prop;

    @Reference(target = "(completer.type="+ ConfigurationCompleter.COMPLETER_TYPE+")")
    Completer pidCompleter;

    @Reference(target = "(completer.type="+ ConfigurationPropertyCompleter.COMPLETER_TYPE+")")
    Completer keysCompleters;

    @Override
    public void propertyAction(Dictionary props) {
        props.remove(prop);
    }

    @Override
    public List<Completer> getCompleters() {
        return Arrays.asList(keysCompleters, NullCompleter.INSTANCE);
    }

    @Override
    public Map<String, Completer> getOptionalCompleters() {
        Map<String, Completer> completers = new HashMap<String, Completer>();
        completers.put("-p", pidCompleter);
        return Collections.unmodifiableMap(completers);
    }
}
