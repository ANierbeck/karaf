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
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.BundleStateListener;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

@Command(scope = ListBundles.SCOPE_VALUE, name = ListBundles.FUNCTION_VALUE, description = ListBundles.DESCRIPTION)
@Component(name = ListBundles.ID, description = ListBundles.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = ListBundles.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = ListBundles.FUNCTION_VALUE)
})
public class ListBundles extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.osgi.list";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "list";
    public static final String DESCRIPTION = "Lists all installed bundles.";

    @Option(name = "-l", aliases = {}, description = "Show the locations", required = false, multiValued = false)
    boolean showLoc;

    @Option(name = "-s", description = "Shows the symbolic name", required = false, multiValued = false)
    boolean showSymbolic;

    @Option(name = "-u", description = "Shows the update locations", required = false, multiValued = false)
    boolean showUpdate;
    
    @Option(name = "-t", valueToShowInHelp = "", description = "Specifies the bundle threshold; bundles with a start-level less than this value will not get printed out.", required = false, multiValued = false)
    int bundleLevelThreshold = -1;

    @Reference(referenceInterface = BundleStateListener.class, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
            bind = "bindBundleStateListener", unbind = "unbindBundleStateListener")
    private List<BundleStateListener> bundleStateListeners = new CopyOnWriteArrayList<BundleStateListener>();

    @Reference
    private StartLevel startLevel;

    @Reference
    private PackageAdmin packageAdmin;


    public Object doExecute() throws Exception {
        Bundle[] bundles = getBundleContext().getBundles();
        if (bundles != null) {
            // Determine threshold
            final String sbslProp = getBundleContext().getProperty("karaf.systemBundlesStartLevel");
            if (sbslProp != null) {
                try {
                   if (bundleLevelThreshold < 0) {
                       bundleLevelThreshold = Integer.valueOf( sbslProp );
                   }
                }
                catch( Exception ignore ) {
                   // ignore
                }
            }
            // Display active start level.
            if (startLevel != null) {
                System.out.println("START LEVEL " + startLevel.getStartLevel() +
                                   " , List Threshold: " + bundleLevelThreshold);
            }

            // Print column headers.
            String msg = " Name";
            if (showLoc) {
               msg = " Location";
            }
            else if (showSymbolic) {
               msg = " Symbolic name";
            }
            else if (showUpdate) {
               msg = " Update location";
            }
            String level = (startLevel == null) ? "" : "  Level ";
            String headers = "   ID   State       ";
            for (BundleStateListener listener : bundleStateListeners) {
                if (listener != null) {
                    headers += "  " + listener.getName() + " ";
                }
            }
            headers += level + msg;
            System.out.println(headers);
            for (int i = 0; i < bundles.length; i++) {
            	if (startLevel.getBundleStartLevel(bundles[i]) >= bundleLevelThreshold) {
	                // Get the bundle name or location.
	                String name = (String) bundles[i].getHeaders().get(Constants.BUNDLE_NAME);
	                // If there is no name, then default to symbolic name.
	                name = (name == null) ? bundles[i].getSymbolicName() : name;
	                // If there is no symbolic name, resort to location.
	                name = (name == null) ? bundles[i].getLocation() : name;
	
	                // Overwrite the default value is the user specifically
	                // requested to display one or the other.
	                if (showLoc) {
	                    name = bundles[i].getLocation();
	                }
	                else if (showSymbolic) {
	                    name = bundles[i].getSymbolicName();
	                    name = (name == null) ? "<no symbolic name>" : name;
	                }
	                else if (showUpdate) {
	                    name = (String) bundles[i].getHeaders().get(Constants.BUNDLE_UPDATELOCATION);
	                    name = (name == null) ? bundles[i].getLocation() : name;
	                }
	                // Show bundle version if not showing location.
	                String version = (String) bundles[i].getHeaders().get(Constants.BUNDLE_VERSION);
	                name = (!showLoc && !showUpdate && (version != null)) ? name + " (" + version + ")" : name;
	                long l = bundles[i].getBundleId();
	                String id = String.valueOf(l);
	                if (startLevel == null) {
	                    level = "1";
	                }
	                else {
	                    level = String.valueOf(startLevel.getBundleStartLevel(bundles[i]));
	                }
	                while (level.length() < 5) {
	                    level = " " + level;
	                }
	                while (id.length() < 4) {
	                    id = " " + id;
	                }
	                String line = "[" + id + "] [" + getStateString(bundles[i]) + "]";
	                for (BundleStateListener listener : bundleStateListeners) {
	                    if (listener != null) {
	                        String state = listener.getState(bundles[i]);
	                        line += " [" + getStateString(state, listener.getName().length()) + "]";
	                    }
	                }
	                line += " [" + level + "] " + name;
	                System.out.println(line);
	
	                if (packageAdmin != null) {
	                    Bundle[] fragments = packageAdmin.getFragments(bundles[i]);
	                    Bundle[] hosts = packageAdmin.getHosts(bundles[i]);
	
	                    if (fragments != null) {
	                        System.out.print("                                       Fragments: ");
	                        int ii = 0;
	                        for (Bundle fragment : fragments) {
	                            ii++;
	                            System.out.print(fragment.getBundleId());
	                            if ((fragments.length > 1) && ii < (fragments.length)) {
	                                System.out.print(",");
	                            }
	                        }
	                        System.out.println();
	                    }
	
	                    if (hosts != null) {
	                        System.out.print("                                       Hosts: ");
	                        int ii = 0;
	                        for (Bundle host : hosts) {
	                            ii++;
	                            System.out.print(host.getBundleId());
	                            if ((hosts.length > 1) && ii < (hosts.length)) {
	                                System.out.print(",");
	                            }
	                        }
	                        System.out.println();
	                    }
	
	                }
	            }
            }
        }
        else {
            System.out.println("There are no installed bundles.");
        }

        return null;
    }

    public String getStateString(Bundle bundle)
    {
        int state = bundle.getState();
        if (state == Bundle.ACTIVE) {
            return "Active     ";
        } else if (state == Bundle.INSTALLED) {
            return "Installed  ";
        } else if (state == Bundle.RESOLVED) {
            return "Resolved   ";
        } else if (state == Bundle.STARTING) {
            return "Starting   ";
        } else if (state == Bundle.STOPPING) {
            return "Stopping   ";
        } else {
            return "Unknown    ";
        }
    }

    public String getStateString(String state, int length) {
        if (state == null) {
            state = "";
        }
        while (state.length() < length) {
            state += " ";
        }
        return state;
    }

    void bindBundleStateListener(BundleStateListener listener) {
        bundleStateListeners.add(listener);
    }

    void unbindBundleStateListener(BundleStateListener listener) {
        bundleStateListeners.remove(listener);

    }
}
