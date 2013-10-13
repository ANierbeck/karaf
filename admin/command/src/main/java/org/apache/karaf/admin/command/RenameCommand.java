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
package org.apache.karaf.admin.command;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

/**
 * Rename an existing Karaf container instance.
 */
@Command(scope = RenameCommand.SCOPE_VALUE, name = RenameCommand.FUNCTION_VALUE, description = RenameCommand.DESCRIPTION)
@Component(name = RenameCommand.ID, description = RenameCommand.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = RenameCommand.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = RenameCommand.FUNCTION_VALUE)
})
public class RenameCommand extends AdminCommandSupport {

    public static final String ID = "org.apache.karaf.admin.command.rename";
    public static final String SCOPE_VALUE = "admin";
    public static final String FUNCTION_VALUE =  "rename";
    public static final String DESCRIPTION = "Renames an existing container instance.";

    @Argument(index = 0, name = "name", description = "The name of the container instance to rename", required = true, multiValued = false)
    String instance = null;

    @Argument(index = 1, name = "new-name", description = "The new name of the container instance", required = true, multiValued = false)
    String newName = null;

    public Object doExecute() throws Exception {
        getAdminService().renameInstance(instance, newName);
        return null;
    }

}
