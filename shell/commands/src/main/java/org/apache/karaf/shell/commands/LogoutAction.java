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

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CloseShellException;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = LogoutAction.SCOPE_VALUE, name = LogoutAction.FUNCTION_VALUE, description = LogoutAction.DESCRIPTION)
@Component(name = LogoutAction.ID, description = LogoutAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = LogoutAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = LogoutAction.FUNCTION_VALUE)
})
public class LogoutAction extends ComponentAction {

    private static final Logger log = LoggerFactory.getLogger(LogoutAction.class);

    public static final String ID = "org.apache.karaf.shell.commands.logout";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "logout";
    public static final String DESCRIPTION = "Disconnects shell from current session.";


    public Object doExecute() throws Exception {
        log.info("Disconnecting from current session...");
        throw new CloseShellException();
    }

}
