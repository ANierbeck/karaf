/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.shell.console.help;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.HelpProvider;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.console.completer.CommandNamesCompleter;
import org.apache.karaf.util.InterpolationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Displays help on the available commands
 */
@Command(scope = "*", name = "help", description = "Displays this help or help about a command")
@Component(name = "org.apache.karaf.shell.command.help", description = "Help Command", immediate = true)
@Service(CompletableFunction.class)
@Properties(
        {
                @Property(name = HelpAction.SCOPE, value = "*"),
                @Property(name = HelpAction.FUNCTION, value = "help")
        }
)
public class HelpAction extends ComponentAction {

    @Argument(name = "command", required = false, description = "The command to get help for")
    private String command;

    @Reference(referenceInterface = HelpProvider.class,
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
            bind = "bindProvider", unbind = "unbindProvider"
    )
    private final CommandNamesCompleter commandNamesCompleter = new CommandNamesCompleter();
    private final List<HelpProvider> providers = new CopyOnWriteArrayList<HelpProvider>();

    @Activate
    void activate() {
        bindCompleter(commandNamesCompleter);
    }

    @Deactivate
    void deactivate() {
        unbindCompleter(commandNamesCompleter);
    }


    public Object doExecute() throws Exception {
        String help = getHelp(getSession(), command);
        if (help != null) {
            System.out.println(help);
        }
        return null;
    }

    public String getHelp(final CommandSession session, String path) {
        if (path == null) {
            path = "%root%";
        }
        Map<String,String> props = new HashMap<String,String>();
        props.put("data", "${" + path + "}");
        InterpolationHelper.performSubstitution(props, new InterpolationHelper.SubstitutionCallback() {
            public String getValue(final String key) {
                for (HelpProvider hp : providers) {
                    String help = hp.getHelp(session, key);
                    if (help != null) {
                        if (help.endsWith("\n")) {
                            help = help.substring(0, help.length() - 1);
                        }
                        return help;
                    }
                }
                return null;
            }
        });
        return props.get("data");
    }


    void bindProvider(HelpProvider provider) {
       providers.add(provider);
    }

    void unbindProvider(HelpProvider provider) {
        providers.remove(provider);
    }

}
