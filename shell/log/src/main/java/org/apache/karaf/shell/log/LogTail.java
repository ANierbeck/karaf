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
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.log.layout.PatternConverter;
import org.apache.karaf.shell.log.layout.PatternParser;
import org.ops4j.pax.logging.spi.PaxAppender;
import org.ops4j.pax.logging.spi.PaxLoggingEvent;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Command(scope = LogTail.SCOPE_VALUE, name = LogTail.FUNCTION_VALUE, description = LogTail.DESCRIPTION)
@Component(name = LogTail.ID, description = LogTail.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = LogTail.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = LogTail.FUNCTION_VALUE)
})
public class LogTail extends DisplayLog {

    public static final String ID = "org.apache.karaf.shell.log.tail";
    public static final String SCOPE_VALUE = "log";
    public static final String FUNCTION_VALUE =  "tail";
    public static final String DESCRIPTION = "Continuously display log entries. Use ctrl-c to quit this command";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Activate
    void activate(Map<String, ?> props) {
        super.activate(props);
    }

    @Deactivate
    void deactivate() {
        super.deactivate();
        executorService.shutdownNow();
    }

    public Object doExecute() throws Exception {
        PrintEventThread printThread = new PrintEventThread();
        executorService.execute(printThread);
        new Thread(new ReadKeyBoardThread(this, Thread.currentThread())).start();
        while (!Thread.currentThread().isInterrupted());
        printThread.abort();
        return null;
    }
    
    class ReadKeyBoardThread implements Runnable {
        private LogTail logTail;
        private Thread sessionThread;
        public ReadKeyBoardThread(LogTail logtail, Thread thread) {
            this.logTail = logtail;
            this.sessionThread = thread;
        }
        public void run() {
            for (;;) {
                try {
                    int c = this.logTail.getSession().getKeyboard().read();
                    if (c < 0) {
                        this.sessionThread.interrupt();
                        break;
                    }
                } catch (IOException e) {
                    break;
                }
                
            }
        }
    }
    
    class PrintEventThread implements Runnable {

        boolean doDisplay = true;

        public void run() {
            final PatternConverter cnv = new PatternParser(overridenPattern != null ? overridenPattern : pattern).parse();
            final PrintStream out = System.out;

            Iterable<PaxLoggingEvent> le = events.getEvents(entries == 0 ? Integer.MAX_VALUE : entries);
            for (PaxLoggingEvent event : le) {
                display(cnv, event, out);
            }
            // Tail
            final BlockingQueue<PaxLoggingEvent> queue = new LinkedBlockingQueue<PaxLoggingEvent>();
            PaxAppender appender = new PaxAppender() {
                public void doAppend(PaxLoggingEvent event) {
                    queue.add(event);
                }
            };
            try {
                events.addAppender(appender);
                while (doDisplay) {
                    PaxLoggingEvent logEvent = queue.take();
                    if (logEvent != null) {
                        display(cnv, logEvent, out);
                    }
                }
            } catch (InterruptedException e) {
                // Ignore
            } finally {
                events.removeAppender(appender);
            }
            out.println();
        }

        public void abort() {
            doDisplay = false;
        }
    }

}
