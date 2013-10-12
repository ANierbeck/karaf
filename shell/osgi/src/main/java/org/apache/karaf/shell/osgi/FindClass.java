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
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import java.util.Collection;

@Command(scope = FindClass.SCOPE_VALUE, name = FindClass.FUNCTION_VALUE, description = FindClass.DESCRIPTION)
@Component(name = FindClass.ID, description = FindClass.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = FindClass.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = FindClass.FUNCTION_VALUE)
})
public class FindClass extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.osgi.findclass";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "find-class";
    public static final String DESCRIPTION = "Locates a specified class in any deployed bundle";

    @Argument(index = 0, name = "className", description = "Class name or partial class name to be found", required = true, multiValued = false)
    String className;

    public Object doExecute() throws Exception {
        findResource();
        return null;
    }

    protected void findResource() {
        Bundle[] bundles = getBundleContext().getBundles();
        String filter = "*" + className + "*";
        for (Bundle bundle : bundles) {
            BundleWiring wiring = (BundleWiring) bundle.adapt(BundleWiring.class);
            if (wiring != null) {
                Collection<String> resources = wiring.listResources("/", filter, BundleWiring.LISTRESOURCES_RECURSE);
                if (resources.size() > 0) {
                    System.out.println("\n" + Util.getBundleName(bundle));
                }
                for (String resource:resources) {
                    System.out.println(resource);
                }
            } else {
                System.out.println("Bundle " + bundle.getBundleId() + " is not resolved.");
            }
        }
    }
}
