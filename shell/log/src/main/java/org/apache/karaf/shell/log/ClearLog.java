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
package org.apache.karaf.shell.log;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;

import java.util.Map;

/**
 * Clear the last log entries.
 */
@Command(scope = ClearLog.SCOPE_VALUE, name = ClearLog.FUNCTION_VALUE, description = ClearLog.DESCRIPTION)
@Component(name = ClearLog.ID, description = ClearLog.DESCRIPTION, configurationPid = Log.PID, policy = ConfigurationPolicy.OPTIONAL)
@Service(CompletableFunction.class)
@Properties({
                @Property(name = ComponentAction.SCOPE, value = ClearLog.SCOPE_VALUE),
                @Property(name = ComponentAction.FUNCTION, value = ClearLog.FUNCTION_VALUE)
})
public class ClearLog extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.log.clear";
    public static final String SCOPE_VALUE = "log";
    public static final String FUNCTION_VALUE =  "clear";
    public static final String DESCRIPTION = "Clear log entries.";

    @Reference
    private LogEvents events;

    @Activate
    void activate(Map<String, ?> props) {
    }

    @Deactivate
    void deactivate() {

    }
    public LogEvents getEvents() {
        return events;
    }

    public void setEvents(LogEvents events) {
        this.events = events;
    }

    public Object doExecute() throws Exception {
        events.clear();
        return null;
    }

}
