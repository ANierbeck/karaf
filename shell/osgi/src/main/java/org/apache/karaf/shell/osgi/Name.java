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
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.utils.properties.Properties;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.BundleContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Command(scope = Name.SCOPE_VALUE, name = Name.FUNCTION_VALUE, description = Name.DESCRIPTION)
@Component(name = Name.ID, description = Name.DESCRIPTION)
@Service(CompletableFunction.class)
@org.apache.felix.scr.annotations.Properties({
        @Property(name = ComponentAction.SCOPE, value = Name.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = Name.FUNCTION_VALUE)
})
public class Name extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.osgi.name";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "name";
    public static final String DESCRIPTION = "Show or change Karaf instance name.";

    @Argument(name = "name", index = 0, description = "New name of the Karaf instance.", required = false, multiValued = false)
    String name;

    private BundleContext bundleContext;

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    public Object doExecute() throws Exception {
        if (name == null) {
            System.out.println(bundleContext.getProperty("karaf.name"));
        } else {
            try {
                String karafBase = bundleContext.getProperty("karaf.base");
                File etcDir = new File(karafBase, "etc");
                File syspropsFile = new File(etcDir, "system.properties");
                FileInputStream fis = new FileInputStream(syspropsFile);
                Properties props = new Properties();
                props.load(fis);
                fis.close();
                props.setProperty("karaf.name", name);
                FileOutputStream fos = new FileOutputStream(syspropsFile);
                props.store(fos, "");
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            System.out.println("Instance name changed to " + name + ". Restart needed for this to take effect.");
        }
        return null;
    }

}
