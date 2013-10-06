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

import java.util.List;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

@Command(scope = RefreshBundle.SCOPE_VALUE, name = RefreshBundle.FUNCTION_VALUE, description = RefreshBundle.DESCRIPTION)
@Component(name = RefreshBundle.ID, description = RefreshBundle.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = RefreshBundle.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = RefreshBundle.FUNCTION_VALUE)
})
public class RefreshBundle extends BundlesCommandOptional {

    public static final String ID = "org.apache.karaf.shell.osgi.refresh";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "refresh";
    public static final String DESCRIPTION = "Refresh a bundle.";

    protected void doExecute(List<Bundle> bundles) throws Exception {
        // Get package admin service.
        ServiceReference ref = getBundleContext().getServiceReference(PackageAdmin.class.getName());
        if (ref == null) {
            System.out.println("PackageAdmin service is unavailable.");
            return;
        }
        try {
            PackageAdmin pa = (PackageAdmin) getBundleContext().getService(ref);
            if (pa == null) {
                System.out.println("PackageAdmin service is unavailable.");
                return;
            }
            if (bundles == null) {
                pa.refreshPackages(null);
            } else {
                pa.refreshPackages(bundles.toArray(new Bundle[bundles.size()]));
            }
        }
        finally {
            getBundleContext().ungetService(ref);
        }
    }
}
