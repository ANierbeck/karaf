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

import jline.console.history.History;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.fusesource.jansi.Ansi;

/**
 * History command
 */
@Command(scope = HistoryAction.SCOPE_VALUE, name = HistoryAction.FUNCTION_VALUE, description = HistoryAction.DESCRIPTION)
@Component(name = HistoryAction.ID, description = HistoryAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = HistoryAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = HistoryAction.FUNCTION_VALUE)
})
public class HistoryAction extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.commands.history";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "history";
    public static final String DESCRIPTION = "Prints command history.";


    @Override
    public Object doExecute() throws Exception {
        History history = (History) getSession().get(".jline.history");

        for (History.Entry element : history) {
            System.out.println(
                    Ansi.ansi()
                        .a("  ")
                        .a(Ansi.Attribute.INTENSITY_BOLD).render("%3d", element.index()).a(Ansi.Attribute.INTENSITY_BOLD_OFF)
                        .a("  ")
                        .a(element.value())
                        .toString());
        }
        return null;
    }
}
