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

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.scr.ScrCommands;
import org.apache.karaf.shell.scr.ScrUtils;
import org.apache.karaf.shell.scr.support.IdComparator;

import java.util.Arrays;

/**
 * Lists all the components currently installed.
 */
@Command(scope = ListAction.SCOPE_VALUE, name = ListAction.FUNCTION_VALUE, description = ListAction.DESCRIPTION)
@org.apache.felix.scr.annotations.Component(name = ListAction.ID, description = ListAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ListAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ListAction.FUNCTION_VALUE)
})
public class ListAction extends ScrActionSupport {

    public static final String ID = "org.apache.karaf.shell.scr.list";
    public static final String SCOPE_VALUE = "scr";
    public static final String FUNCTION_VALUE =  "list";
    public static final String DESCRIPTION = "Displays a list of available components.";

    private final IdComparator idComparator = new IdComparator();

    @Override
    protected Object doScrAction(ScrService scrService) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing the List Action");
        }
        System.out.println(getBoldString("   ID   State             Component Name"));
        Component[] components = scrService.getComponents();
        Arrays.sort(components, idComparator);
        for (Component component : ScrUtils.emptyIfNull(Component.class, components)) {
            if (showAll) {
                // We display all because we are overridden
                printComponent(component);
            } else if (showCommands && ScrCommands.isCommandComponent(component)) {
                printComponent(component);
            } else if (ScrCommands.isHiddenComponent(component)) {
                //Do nothing.
            } else {
                printComponent(component);
            }
        }
        return null;
    }

    private void printComponent(Component component) {
        String name = component.getName();
        String id = buildLeftPadBracketDisplay(component.getId() + "", 4);
        String state = buildRightPadBracketDisplay(ScrUtils.getState(component.getState()), 16);
        System.out.println("[" + id + "] [" + state + "] " + name);
    }

}
