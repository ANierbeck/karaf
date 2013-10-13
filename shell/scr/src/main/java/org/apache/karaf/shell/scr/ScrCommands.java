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
package org.apache.karaf.shell.scr;

import org.apache.felix.scr.Component;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.completer.ArgumentCompleter;
import org.apache.karaf.shell.console.jline.CommandSessionHolder;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class ScrCommands {

    public static final String SHOW_ALL_OPTION = "-a";
    public static final String SHOW_ALL_ALIAS = "--all";

    public static final String SHOW_COMMANDS_OPTION = "-c";
    public static final String SHOW_COMMANDS_ALIAS = "--show-commands";


    public static final String HIDDEN_COMPONENT_KEY = "hidden.component";
    public static final String SHELL_COMMAND_COMPONENT_KEY = "shell.command.component";


    @SuppressWarnings("rawtypes")
    public static boolean isHiddenComponent(Component component) {
        boolean answer = false;

        Hashtable properties = (Hashtable)component.getProperties();
        if (properties != null && properties.containsKey(ScrCommands.HIDDEN_COMPONENT_KEY)) {
            String value = (String)properties.get(ScrCommands.HIDDEN_COMPONENT_KEY);
            // If the value is false show the hidden
            // then someone wants us to display the name
            // of a hidden component
            if (value != null && value.equals("true")) {
                answer = true;
            }
        }

        return answer;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isCommandComponent(Component component) {
        boolean answer = false;

        Hashtable properties = (Hashtable)component.getProperties();
        if (properties != null && properties.containsKey(ScrCommands.SHELL_COMMAND_COMPONENT_KEY)) {
            String value = (String)properties.get(ScrCommands.SHELL_COMMAND_COMPONENT_KEY);
            if (value != null && value.equals("true")) {
                answer = true;
            }
        }

        return answer;
    }

    public static boolean showHiddenComponent(Component component) {
        boolean answer = false;

        // First look to see if the show all options is there.
        // If it is we set showAllFlag to true so the next
        // section will skip
        CommandSession commandSession = CommandSessionHolder.getSession();
        ArgumentCompleter.ArgumentList list = (ArgumentCompleter.ArgumentList)commandSession.get(ArgumentCompleter.ARGUMENTS_LIST);
        if (list != null && list.getArguments() != null && list.getArguments().length > 0) {
            List<String> arguments = Arrays.asList(list.getArguments());
            if (arguments.contains(SHOW_ALL_OPTION) || arguments.contains(SHOW_ALL_ALIAS)) {
                answer = true;
            }
        }

        return answer;
    }

}
