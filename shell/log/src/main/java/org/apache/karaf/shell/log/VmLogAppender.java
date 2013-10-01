/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.shell.log;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

/**
 * A Pax Logging appender that keep a list of last events
 */
@Component(name = "org.apache.karaf.log.appnder.vm", description = "Karaf VM Log Appender", immediate = true)
@Service(PaxAppender.class)
@Properties(
        @Property(name = "org.ops4j.pax.logging.appender.name", value = "VmLogAppender")
)
public class VmLogAppender implements PaxAppender {

    @Reference
    private LogEvents events;

    public LogEvents getEvents() {
        return events;
    }

    public void setEvents(LogEvents events) {
        this.events = events;
    }

    public void doAppend(PaxLoggingEvent event) {
        if (events != null) {
            event.getProperties(); // ensure MDC properties are copied
            events.add(event);
        }
    }
}
