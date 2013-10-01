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
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

@Command(scope = DisplayException.SCOPE_VALUE, name = DisplayException.FUNCTION_VALUE, description = DisplayException.DESCRIPTION)
@Component(name = DisplayException.ID, description = DisplayException.DESCRIPTION, configurationPid = Log.PID, policy = ConfigurationPolicy.OPTIONAL)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = DisplayException.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = DisplayException.FUNCTION_VALUE)
})
public class DisplayException extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.log.display.exception";
    public static final String SCOPE_VALUE = "log";
    public static final String FUNCTION_VALUE =  "display-exception";
    public static final String DESCRIPTION = "Displays the last occurred exception from the log.";

    @Reference
    protected LogEvents events;

    public LogEvents getEvents() {
        return events;
    }

    public void setEvents(LogEvents events) {
        this.events = events;
    }

    public Object doExecute() throws Exception {
        PaxLoggingEvent throwableEvent = null;
        Iterable<PaxLoggingEvent> le = events.getEvents(Integer.MAX_VALUE);
        for (PaxLoggingEvent event : le) {
            if (event.getThrowableStrRep() != null) {
                throwableEvent = event;
                // Do not break, as we iterate from the oldest to the newest event
            }
        }
        if (throwableEvent != null) {
            for (String r : throwableEvent.getThrowableStrRep()) {
                System.out.println(r);
            }
            System.out.println();
        }
        return null;
    }

}
