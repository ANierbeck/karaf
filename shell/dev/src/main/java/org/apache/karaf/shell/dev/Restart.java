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
package org.apache.karaf.shell.dev;

import aQute.bnd.annotation.Activate;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.BundleContext;

/**
 * A command to restart karaf
 */
@Command(scope = Restart.SCOPE_VALUE, name = Restart.FUNCTION_VALUE, description = Restart.DESCRIPTION)
@Component(name = Restart.ID, description = Restart.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties(
        {
                @Property(name = ComponentAction.SCOPE, value = Restart.SCOPE_VALUE),
                @Property(name = ComponentAction.FUNCTION, value = Restart.FUNCTION_VALUE)
        }
)
public class Restart  extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.dev.restart";
    public static final String SCOPE_VALUE = "dev";
    public static final String FUNCTION_VALUE =  "restart";
    public static final String DESCRIPTION = "Restart Karaf.";

    @Option(name = "-c", aliases = { "--clean" }, description = "Force a clean restart by deleting the working directory")
    private boolean clean;

    private BundleContext bundleContext;

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public Object doExecute() throws Exception {
        System.setProperty("karaf.restart", "true");
        System.setProperty("karaf.restart.clean", Boolean.toString(clean));
        bundleContext.getBundle(0).stop();
        return null;
    }
}
