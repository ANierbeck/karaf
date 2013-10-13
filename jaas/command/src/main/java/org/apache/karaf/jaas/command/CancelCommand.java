/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.jaas.command;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

import java.util.LinkedList;

@Command(scope = CancelCommand.SCOPE_VALUE, name = CancelCommand.FUNCTION_VALUE, description = CancelCommand.DESCRIPTION)
@Component(name = CancelCommand.ID, description = CancelCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = CancelCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = CancelCommand.FUNCTION_VALUE)
})
public class CancelCommand extends JaasCommandSupport {

    public static final String ID = "org.apache.karaf.jaas.command.cancel";
    public static final String SCOPE_VALUE = "jaas";
    public static final String FUNCTION_VALUE =  "cancel";
    public static final String DESCRIPTION = "Cancel the modification on a JAAS Realm.";


    @Override
    public Object doExecute() throws Exception {
        //Cleanup the session
        getSession().put(JAAS_REALM, null);
        getSession().put(JAAS_ENTRY, null);
        getSession().put(JAAS_CMDS, new LinkedList<JaasCommandSupport>());
        return null;
    }

    @Override
    protected Object doExecute(BackingEngine engine) throws Exception {
        return null;
    }
}
