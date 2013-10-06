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
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.Bundle;
import org.osgi.service.startlevel.StartLevel;

@Command(scope = BundleLevel.SCOPE_VALUE, name = BundleLevel.FUNCTION_VALUE, description = BundleLevel.DESCRIPTION)
@Component(name = BundleLevel.ID, description = BundleLevel.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = BundleLevel.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = BundleLevel.FUNCTION_VALUE)
})
public class BundleLevel extends BundleCommand {

    public static final String ID = "org.apache.karaf.shell.osgi.bundlelevel";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "bundle-level";
    public static final String DESCRIPTION = "Gets or sets the start level of a given bundle.";

    @Argument(index = 1, name = "startLevel", description = "The bundle's new start level", required = false, multiValued = false)
    Integer level;

    @Reference
    private StartLevel startLevel;

    protected void doExecute(Bundle bundle) throws Exception {
        if (level == null) {
            System.out.println("Level " + startLevel.getBundleStartLevel(bundle));
        }
        else if ((level < 50) && (startLevel.getBundleStartLevel(bundle) > 50) && !force){
            for (;;) {
                StringBuffer sb = new StringBuffer();
                System.err.println("You are about to designate bundle as a system bundle.  Do you wish to continue (yes/no): ");
                System.err.flush();
                for (;;) {
                    int c = getSession().getKeyboard().read();
                    if (c < 0) {
                        break;
                    }
                    if (c == '\r' || c == '\n') {
                        System.err.println();
                        System.err.flush();
                        break;
                    }
                    if (c == 127 || c == 'b') {
                        System.err.print((char)'\b');
                        System.err.print((char)' ');
                        System.err.print((char)'\b');
                    } else {
                        System.err.print((char)c);
                    }
                    
                    System.err.flush();
                    if (c == 127 || c == 'b') {
                        if (sb.length() > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                        }
                    } else {
                        sb.append((char)c);
                    }
                }
                String str = sb.toString();
                if ("yes".equals(str)) {
                    startLevel.setBundleStartLevel(bundle, level);
                    break;
                } else if ("no".equals(str)) {
                    break;
                }
            }
        } else {
            startLevel.setBundleStartLevel(bundle, level);
        }
    }

}
