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

import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Command(scope = SleepAction.SCOPE_VALUE, name = SleepAction.FUNCTION_VALUE, description = SleepAction.DESCRIPTION)
@Component(name = SleepAction.ID, description = SleepAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = SleepAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = SleepAction.FUNCTION_VALUE)
})
public class SleepAction extends ComponentAction {

    private static final Logger log = LoggerFactory.getLogger(SleepAction.class);

    public static final String ID = "org.apache.karaf.shell.commands.sleep";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "sleep";
    public static final String DESCRIPTION = "Sleeps for a bit then wakes up.";


    @Argument(index = 0, name = "duration", description = "The amount of time to sleep. The default time unit is millisecond, use -s option to use second instead.", required = true, multiValued = false)
    private long time = -1;
    
    @Option(name = "-s", aliases = { "--second" }, description = "Use a duration time in seconds instead of milliseconds.", required = false, multiValued = false)
    private boolean second = false;

    public Object doExecute() throws Exception {
        if (second) {
            log.info("Sleeping for {} second(s)", time);
            time = time * 1000;
        } else {
            log.info("Sleeping for {} millisecond(s)", time);
        }

        try {
            Thread.sleep(time);
        }
        catch (InterruptedException ignore) {
            log.debug("Sleep was interrupted... :-(");
        }

        log.info("Awake now");
        return null;
    }
}
