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

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.config.completers.ConfigurationCompleter;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.commands.ComponentAction;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Command(scope = PropListCommand.SCOPE_VALUE, name = PropListCommand.FUNCTION_VALUE, description = PropListCommand.DESCRIPTION)
@Component(name = PropListCommand.ID, description = PropListCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = PropListCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = PropListCommand.FUNCTION_VALUE)
})
public class PropListCommand extends ConfigPropertyCommandSupport {

    public static final String ID = "org.apache.karaf.shell.config.proplist";
    public static final String SCOPE_VALUE = "config";
    public static final String FUNCTION_VALUE =  "proplist";
    public static final String DESCRIPTION = "Lists properties from the currently edited configuration.";

    @Override
    public void propertyAction(Dictionary props) {
        for (Enumeration e = props.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            System.out.println("   " + key + " = " + props.get(key));
        }
    }

    /**
     * List commands never requires an update, so it always returns false.
     * @param pid
     * @return
     */
    @Override
    protected boolean requiresUpdate(String pid) {
        return false;
    }
}
