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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

import static org.apache.karaf.util.config.ConfigUtils.readInt;

/**
 * A list that only keep the last N elements added
 */
@Component(name = "org.apache.karaf.log.event.manager", configurationPid = Log.PID, policy = ConfigurationPolicy.OPTIONAL, description = "Karaf Log Events Holder", immediate = true)
@Service(LogEvents.class)
public class LogEvents {

    public static final String ID = "org.apache.karaf.log.events";

    private static final String SIZE = "size";

    private PaxLoggingEvent[] elements;
    private transient int start = 0;
    private transient int end = 0;
    private transient boolean full = false;
    private int maxElements;
    private final List<PaxAppender> appenders = new CopyOnWriteArrayList<PaxAppender>();


    @Property(name = "size", label = "Size", description = "Log Event List size", intValue = 500)
    private int size;

    @Activate
    synchronized void activate(Map<String, ?> props) {
        this.size = readInt(props, SIZE);
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        elements = new PaxLoggingEvent[size];
        maxElements = elements.length;
    }

    @Deactivate
    synchronized void deactivate() {
        appenders.clear();
    }

    public synchronized int size() {
        int size = 0;
        if (end < start) {
            size = maxElements - start + end;
        } else if (end == start) {
            size = (full ? maxElements : 0);
        } else {
            size = end - start;
        }
        return size;
    }

    public synchronized void clear() {
        start = 0;
        end = 0;
        elements = new PaxLoggingEvent[maxElements];
    }

    public synchronized void add(PaxLoggingEvent element) {
        if (null == element) {
             throw new NullPointerException("Attempted to add null object to buffer");
        }
        if (size() == maxElements) {
            Object e = elements[start];
            if (null != e) {
                elements[start++] = null;
                if (start >= maxElements) {
                    start = 0;
                }
                full = false;
            }
        }
        elements[end++] = element;
        if (end >= maxElements) {
            end = 0;
        }
        if (end == start) {
            full = true;
        }
        for (PaxAppender appender : appenders) {
            try {
                appender.doAppend(element);
            } catch (Throwable t) {
                // Ignore
            }
        }
    }

    public synchronized Iterable<PaxLoggingEvent> getEvents() {
        return getEvents(size());
    }

    public synchronized Iterable<PaxLoggingEvent> getEvents(int nb) {
        int s = size();
        nb = Math.min(Math.max(0, nb), s);
        PaxLoggingEvent[] e = new PaxLoggingEvent[nb];
        for (int i = 0; i < nb; i++) {
            e[i] = elements[(i + s - nb + start) % maxElements];
        }
        return Arrays.asList(e);
    }

    public void addAppender(PaxAppender appender) {
        this.appenders.add(appender);
    }

    public void removeAppender(PaxAppender appender) {
        this.appenders.remove(appender);
    }

}
