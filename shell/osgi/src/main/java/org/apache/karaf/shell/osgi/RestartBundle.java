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
package org.apache.karaf.shell.osgi;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.MultiException;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.Bundle;
import org.apache.felix.gogo.commands.Command;

@Command(scope = RestartBundle.SCOPE_VALUE, name = RestartBundle.FUNCTION_VALUE, description = RestartBundle.DESCRIPTION)
@Component(name = RestartBundle.ID, description = RestartBundle.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = RestartBundle.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = RestartBundle.FUNCTION_VALUE)
})
public class RestartBundle extends BundlesCommand {

    public static final String ID = "org.apache.karaf.shell.osgi.restart";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "restart";
    public static final String DESCRIPTION = "Stops and restarts bundle(s).";

    protected void doExecute(List<Bundle> bundles) throws Exception {
        if (bundles.isEmpty()) {
            System.err.println("No bundles specified.");
            return;
        }
        List<Exception> exceptions = new ArrayList<Exception>();
        for (Bundle bundle : bundles) {
            try {
                bundle.stop();
            } catch (Exception e) {
                exceptions.add(new Exception("Unable to stop bundle " + bundle.getBundleId() +
                        (e.getMessage() != null ? ": " + e.getMessage() : ""), e));
            }
        }
        for (Bundle bundle : bundles) {
            try {
                bundle.start();
            } catch (Exception e) {
                exceptions.add(new Exception("Unable to start bundle " + bundle.getBundleId() +
                        (e.getMessage() != null ? ": " + e.getMessage() : ""), e));
            }
        }
        MultiException.throwIf("Error restarting bundles", exceptions);
    }

}
