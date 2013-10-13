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
package org.apache.karaf.shell.scr.action;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.scr.ScrUtils;

/**
 * Activates the given component by supplying its component name.
 */
@Command(scope = ActivateAction.SCOPE_VALUE, name = ActivateAction.FUNCTION_VALUE, description = ActivateAction.DESCRIPTION)
@org.apache.felix.scr.annotations.Component(name = ActivateAction.ID, description = ActivateAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ActivateAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ActivateAction.FUNCTION_VALUE)
})public class ActivateAction extends ScrActionSupport {

    public static final String ID = "org.apache.karaf.shell.scr.activate";
    public static final String SCOPE_VALUE = "scr";
    public static final String FUNCTION_VALUE =  "activate";
    public static final String DESCRIPTION = "Activates a Component for the given name.";

    @Argument(index = 0, 
              name = "name", 
              description = "The name of the Component to activate ", 
              required = true, 
              multiValued = false)
    String name;

    @Override
    protected Object doScrAction(ScrService scrService) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Activate Action");
            logger.debug("  Activating the Component: " + name);
        }
        Component[] components = scrService.getComponents(name);
        for (Component component : ScrUtils.emptyIfNull(Component.class, components)) {
            component.enable();
        }
        return null;
    }

}
