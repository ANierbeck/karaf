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
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

/**
 * A command to clear the console buffer
 */
@Command(scope = ClearAction.SCOPE_VALUE, name = ClearAction.FUNCTION_VALUE, description = ClearAction.DESCRIPTION)
@Component(name = ClearAction.ID, description = ClearAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ClearAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ClearAction.FUNCTION_VALUE)
})
public class ClearAction extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.commands.clear";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "clear";
    public static final String DESCRIPTION = "Clears the console buffer.";


    public Object doExecute() throws Exception {
		System.out.print("\33[2J");
		System.out.flush();
		System.out.print("\33[1;1H");
		System.out.flush();
		return null;
	}	

}
