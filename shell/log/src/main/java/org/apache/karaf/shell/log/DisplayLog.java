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

import java.io.PrintStream;
import java.util.Map;

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
import org.apache.karaf.shell.log.layout.PatternConverter;
import org.apache.karaf.shell.log.layout.PatternParser;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Command;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

import static org.apache.karaf.util.config.ConfigUtils.readString;

/**
 * Displays the last log entries
 */
@Command(scope = DisplayLog.SCOPE_VALUE, name = DisplayLog.FUNCTION_VALUE, description = DisplayLog.DESCRIPTION)
@Component(name = DisplayLog.ID, description = DisplayLog.DESCRIPTION, configurationPid = Log.PID, policy = ConfigurationPolicy.OPTIONAL)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = DisplayLog.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = DisplayLog.FUNCTION_VALUE)
})
public class DisplayLog extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.log.display";
    public static final String SCOPE_VALUE = "log";
    public static final String FUNCTION_VALUE =  "display";
    public static final String DESCRIPTION = "Displays log entries.";

    @Option(name = "-n", aliases = {}, description="Number of entries to display", required = false, multiValued = false)
    protected int entries;

    @Option(name = "-p", aliases = {}, description="Pattern for formatting the output", required = false, multiValued = false)
    protected String overridenPattern;

    @Option(name = "--no-color", description="Disable syntax coloring of log events", required = false, multiValued = false)
    protected boolean noColor;

    @Reference
    protected LogEvents events;

    @Property(name = PATTERN_NAME, label = "Pattern", description = "Pattern used to display log entries")
    protected String pattern;
    protected String fatalColor;
    protected String errorColor;
    protected String warnColor;
    protected String infoColor;
    protected String debugColor;
    protected String traceColor;

    public static final String PATTERN_NAME = "pattern";
    public static final String FATAL_COLOR = "fatalColoer";
    public static final String ERROR_COLOR = "errorColor";
    public static final String WARN_COLOR = "warnColor";
    public static final String INFO_COLOR = "infoColor";
    public static final String DEBUG_COLOR = "debugColor";
    public static final String TRACE_COLOR = "taceColor";

    private static final String FATAL = "fatal";
    private static final String ERROR = "error";
    private static final String WARN = "warn";
    private static final String INFO = "info";
    private static final String DEBUG = "debug";
    private static final String TRACE = "trace";

    private static final char FIRST_ESC_CHAR = 27;
	private static final char SECOND_ESC_CHAR = '[';
    private static final char COMMAND_CHAR = 'm';

    @Activate
    void activate(Map<String, ?> props) {
        pattern = readString(props, PATTERN_NAME);
        errorColor = readString(props, ERROR_COLOR);
        warnColor = readString(props, WARN_COLOR);
        infoColor = readString(props, INFO_COLOR);
        debugColor = readString(props, DEBUG_COLOR);
        traceColor = readString(props, TRACE_COLOR);
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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getFatalColor() {
        return fatalColor;
    }

    public void setFatalColor(String fatalColor) {
        this.fatalColor = fatalColor;
    }

    public String getErrorColor() {
        return errorColor;
    }

    public void setErrorColor(String errorColor) {
        this.errorColor = errorColor;
    }

    public String getWarnColor() {
        return warnColor;
    }

    public void setWarnColor(String warnColor) {
        this.warnColor = warnColor;
    }

    public String getInfoColor() {
        return infoColor;
    }

    public void setInfoColor(String infoColor) {
        this.infoColor = infoColor;
    }

    public String getDebugColor() {
        return debugColor;
    }

    public void setDebugColor(String debugColor) {
        this.debugColor = debugColor;
    }

    public String getTraceColor() {
        return traceColor;
    }

    public void setTraceColor(String traceColor) {
        this.traceColor = traceColor;
    }

    public Object doExecute() throws Exception {
        final PatternConverter cnv = new PatternParser(overridenPattern != null ? overridenPattern : pattern).parse();
        final PrintStream out = System.out;

        Iterable<PaxLoggingEvent> le = events.getEvents(entries == 0 ? Integer.MAX_VALUE : entries);
        for (PaxLoggingEvent event : le) {
            if (event != null) {
            display(cnv, event, out);
        }
        }
        out.println();
        return null;
    }

    protected void display(PatternConverter cnv, PaxLoggingEvent event, PrintStream stream) {
        String color = getColor(event);
        StringBuffer sb = new StringBuffer();
        sb.setLength(0);
        if (color != null) {
            sb.append(FIRST_ESC_CHAR);
            sb.append(SECOND_ESC_CHAR);
            sb.append(color);
            sb.append(COMMAND_CHAR);
        }
        for (PatternConverter pc = cnv; pc != null; pc = pc.next) {
            pc.format(sb, event);
        }
        if (event.getThrowableStrRep() != null) {
            for (String r : event.getThrowableStrRep()) {
                sb.append(r).append('\n');
            }
        }
        if (color != null) {
            sb.append(FIRST_ESC_CHAR);
            sb.append(SECOND_ESC_CHAR);
            sb.append("0");
            sb.append(COMMAND_CHAR);
        }
        stream.print(sb.toString());
    }

    private String getColor(PaxLoggingEvent event) {
        String color = null;
        if (!noColor && event != null && event.getLevel() != null && event.getLevel().toString() != null) {
            String lvl = event.getLevel().toString().toLowerCase();
            if (FATAL.equals(lvl)) {
                color = fatalColor;
            } else if (ERROR.equals(lvl)) {
                color = errorColor;
            } else if (WARN.equals(lvl)) {
                color = warnColor;
            } else if (INFO.equals(lvl)) {
                color = infoColor;
            } else if (DEBUG.equals(lvl)) {
                color = debugColor;
            } else if (TRACE.equals(lvl)) {
                color = traceColor;
            }
            if (color != null && color.length() == 0) {
                color = null;
            }
        }
        return color;
    }

}
