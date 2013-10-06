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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.MultiException;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.List;

@Command(scope = InstallBundle.SCOPE_VALUE, name = InstallBundle.FUNCTION_VALUE, description = InstallBundle.DESCRIPTION)
@Component(name = InstallBundle.ID, description = InstallBundle.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = InstallBundle.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = InstallBundle.FUNCTION_VALUE)
})
public class InstallBundle extends ComponentAction {


    public static final String ID = "org.apache.karaf.shell.osgi.install";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "install";
    public static final String DESCRIPTION = "Installs one or more bundles.";

    @Argument(index = 0, name = "urls", description = "Bundle URLs separated by whitespaces", required = true, multiValued = true)
    List<String> urls;

    @Option(name = "-s", aliases={"--start"}, description="Starts the bundles after installation", required = false, multiValued = false)
    boolean start;

    private BundleContext bundleContext;

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    public Object doExecute() throws Exception {
        List<Exception> exceptions = new ArrayList<Exception>();
        List<Bundle> bundles = new ArrayList<Bundle>();
        for (String url : urls) {
            try {
                bundles.add(bundleContext.installBundle(url, null));
            } catch (Exception e) {
                exceptions.add(new Exception("Unable to install bundle " + url, e));
            }
        }
        if (start) {
            for (Bundle bundle : bundles) {
                try {
                    bundle.start();
                } catch (Exception e) {
                    exceptions.add(new Exception("Unable to start bundle " + bundle.getLocation() +
                            (e.getMessage() != null ? ": " + e.getMessage() : ""), e));
                }
            }
        }
        if (bundles.size() == 1) {
            System.out.println("Bundle ID: " + bundles.get(0).getBundleId());
        } else {
            StringBuffer sb = new StringBuffer("Bundle IDs: ");
            for (Bundle bundle : bundles) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(bundle.getBundleId());
            }
            System.out.println(sb);
        }
        MultiException.throwIf("Error installing bundles", exceptions);
        return null;
    }

}
