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
package org.apache.karaf.shell.scr.action;

import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.Component;
import org.apache.felix.scr.ScrService;
import org.apache.felix.scr.annotations.Reference;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.scr.ScrCommands;
import org.fusesource.jansi.Ansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

@org.apache.felix.scr.annotations.Component(name = ScrActionSupport.ID, componentAbstract = true)
public abstract class ScrActionSupport extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.scr.base";

    @Option(name = ScrCommands.SHOW_ALL_OPTION,
            aliases = {ScrCommands.SHOW_ALL_ALIAS},
            description = "Show all Components including the System Components (hidden by default)",
            required = false,
            multiValued = false)
    boolean showAll = false;

    @Option(name = ScrCommands.SHOW_COMMANDS_OPTION,
            aliases = {ScrCommands.SHOW_COMMANDS_ALIAS},
            description = "Shows shell commands components",
            required = false,
            multiValued = false)
    boolean showCommands = false;


    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Reference
    private ScrService scrService;

    @Override
    public Object doExecute() throws Exception {
        if (scrService == null) {
            String msg = "ScrService is unavailable";
            System.out.println(msg);
            logger.warn(msg);
        } else {
            doScrAction(scrService);
        }
        return null;
    }

    protected abstract Object doScrAction(ScrService scrService) throws Exception;

    public String getBoldString(String value) {
        return Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(value).a(Ansi.Attribute.INTENSITY_BOLD_OFF).toString();
    }

    public String buildRightPadBracketDisplay(String s, int max) {
        return String.format("%1$-" + max + "s", s);
    }

    public String buildLeftPadBracketDisplay(String s, int max) {
        return String.format("%1$-" + max + "s", s);
    }

    /**
     * Get the scrService Object associated with this instance of
     * ScrActionSupport.
     * 
     * @return the scrService
     */
    public ScrService getScrService() {
        return scrService;
    }

    /**
     * Sets the scrService Object for this ScrActionSupport instance.
     * 
     * @param scrService the scrService to set
     */
    void bindScrService(ScrService scrService) {
        this.scrService = scrService;
    }

    void unbindScrService(ScrService scrService) {
        this.scrService = null;
    }

}
