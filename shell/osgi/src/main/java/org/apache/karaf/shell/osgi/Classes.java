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

import java.util.Collection;
import java.util.List;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

@Command(scope = Classes.SCOPE_VALUE, name = Classes.FUNCTION_VALUE, description = Classes.DESCRIPTION)
@Component(name = Classes.ID, description = Classes.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = Classes.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = Classes.FUNCTION_VALUE)
})public class Classes extends BundlesCommand {

    public static final String ID = "org.apache.karaf.shell.osgi.classes";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "classes";
    public static final String DESCRIPTION = "Displays list of classes contained in bundles";


    @Option(name = "-a", aliases = { "--display-all-files" }, description = "List all classes and files in bundles", required = false, multiValued = false)
    boolean displayAllFiles;

    protected void doExecute(List<Bundle> bundles) throws Exception {
        for (Bundle bundle : bundles) {
            printResources(bundle);
        }
    }

    protected void printResources(Bundle bundle) {
        BundleWiring wiring = (BundleWiring) bundle.adapt(BundleWiring.class);
        if (wiring != null) {
            Collection<String> resources = null;
            if (displayAllFiles) {
                resources = wiring.listResources("/", null, BundleWiring.LISTRESOURCES_RECURSE);
            } else {
                resources = wiring.listResources("/", "*class", BundleWiring.LISTRESOURCES_RECURSE);
            }
            if (resources.size() > 0) {
                System.out.println("\n" + Util.getBundleName(bundle));
            }
            for (String resource : resources) {
                System.out.println(resource);
            }
        } else {
            System.out.println("Bundle " + bundle.getBundleId() + " is not resolved");
        }
    }

}
