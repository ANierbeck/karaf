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

import org.apache.felix.gogo.commands.Argument;
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
import org.apache.karaf.shell.log.completers.LogLevelCompleter;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.Dictionary;

/**
 * Set the log level for a given logger
 */
@Command(scope = SetLogLevel.SCOPE_VALUE, name = SetLogLevel.FUNCTION_VALUE, description = SetLogLevel.DESCRIPTION)
@Component(name = SetLogLevel.ID, description = SetLogLevel.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = SetLogLevel.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = SetLogLevel.FUNCTION_VALUE)
})
public class SetLogLevel extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.log.set";
    public static final String SCOPE_VALUE = "log";
    public static final String FUNCTION_VALUE =  "set";
    public static final String DESCRIPTION = "Sets the log level.";

    @Reference
    private ConfigurationAdmin configurationAdmin;


    @Argument(index = 0, name = "level", description = "The log level to set (TRACE, DEBUG, INFO, WARN, ERROR) or DEFAULT to unset", required = true, multiValued = false)
    String level;

    @Argument(index = 1, name = "logger", description = "Logger name or ROOT (default)", required = false, multiValued = false)
    String logger;

    static final String CONFIGURATION_PID  = "org.ops4j.pax.logging";
    static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
    static final String LOGGER_PREFIX      = "log4j.logger.";
    static final String ROOT_LOGGER        = "ROOT";

    private final LogLevelCompleter LOG_LEVEL_COMPLETER = new LogLevelCompleter();

    @Activate
    void activate() {
        bindCompleter(LOG_LEVEL_COMPLETER);
    }

    @Deactivate
    void deactivate() {
        unbindCompleter(LOG_LEVEL_COMPLETER);
    }

    public Object doExecute() throws Exception {
        if (ROOT_LOGGER.equalsIgnoreCase(this.logger)) {
            this.logger = null;
        }
        
        // make sure both uppercase and lowercase levels are supported
        level = level.toUpperCase();
        
        try {
            Level.valueOf(level);
        } catch (IllegalArgumentException e) {
            System.err.println("level must be set to TRACE, DEBUG, INFO, WARN or ERROR (or DEFAULT to unset it)");
            return null;
        }
        
        if (Level.isDefault(level) && logger == null) {
            System.err.println("Can not unset the ROOT logger");
            return null;
        }

        Configuration cfg = getConfiguration();
        Dictionary props = cfg.getProperties();

        String logger = this.logger;
        String val;
        String prop;
        if (logger == null) {
            prop = ROOT_LOGGER_PREFIX;
        } else {
            prop = LOGGER_PREFIX + logger;
        }
        val = (String) props.get(prop);
        if (Level.isDefault(level)) {
            if (val != null) {
                val = val.trim();
                int idx = val.indexOf(",");
                if (idx < 0) {
                    val = null;
                } else {
                    val = val.substring(idx);
                }
            }
        } else {
            if (val == null) {
                val = level;
            } else {
                val = val.trim();
                int idx = val.indexOf(",");
                if (idx < 0) {
                    val = level;
                } else {
                    val = level + val.substring(idx);
                }
            }
        }
        if (val == null) {
            props.remove(prop);
        } else {
            props.put(prop, val);
        }
        cfg.update(props);

        return null;
    }

    protected Configuration getConfiguration() throws IOException {
        Configuration cfg = configurationAdmin.getConfiguration(CONFIGURATION_PID, null);
        return cfg;
    }

}
