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

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.dev.watch.BundleWatcher;
import org.apache.karaf.shell.dev.watch.BundleWatcherImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

@Command(scope = Watch.SCOPE_VALUE, name = Watch.FUNCTION_VALUE, description = Watch.DESCRIPTION, detailedDescription="classpath:watch.txt")
@Component(name = Watch.ID, description = Watch.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties(
        {
                @Property(name = ComponentAction.SCOPE, value = Watch.SCOPE_VALUE),
                @Property(name = ComponentAction.FUNCTION, value = Watch.FUNCTION_VALUE)
        }
)
public class Watch extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.dev.watch";
    public static final String SCOPE_VALUE = "dev";
    public static final String FUNCTION_VALUE =  "watch";
    public static final String DESCRIPTION = "Watches and updates bundles.";

    @Argument(index = 0, name = "urls", description = "The bundle IDs or URLs", required = false, multiValued = true)
    List<String> urls;

    @Option(name = "-i", aliases = {}, description = "Watch interval", required = false, multiValued = false)
    private long interval;

    @Option(name = "--start", description = "Starts watching the selected bundles", required = false, multiValued = false)
    protected boolean start;

    @Option(name = "--stop", description = "Stops watching all bundles", required = false, multiValued = false)
    protected boolean stop;

    @Option(name = "--remove", description = "Removes bundles from the watch list", required = false, multiValued = false)
    protected boolean remove;

    @Option(name = "--list", description = "Displays the watch list", required = false, multiValued = false)
    protected boolean list;

    @Reference
    private BundleWatcher watcher;

    @Override
    public Object doExecute() throws Exception {

        if (start && stop) {
            System.err.println("Please use only one of --start and --stop options!");
            return null;
        }

        if (interval > 0) {
            System.out.println("Setting watch interval to " + interval + " ms");
            watcher.setInterval(interval);
        }
        if (stop) {
            System.out.println("Stopping watch");
            watcher.stop();
        }
        if (urls != null) {
            if (remove) {
                for (String url : urls) {
                    watcher.remove(url);
                }
            } else {
                for (String url : urls) {
                    watcher.add(url);
                }
            }
        }
        if (start) {
            System.out.println("Starting watch");
            watcher.start();
        }

        if (list) { //List the watched bundles.
            String format = "%-40s %6s %-80s";
            System.out.println(String.format(format, "URL", "ID", "Bundle Name"));
            for (String url : watcher.getWatchURLs()) {

                List<Bundle> bundleList = watcher.getBundlesByURL(url);
                if (bundleList != null && bundleList.size() > 0) {
                    for (Bundle bundle : bundleList) {
                        System.out.println(String.format(format, url, bundle.getBundleId(), bundle.getHeaders().get(Constants.BUNDLE_NAME)));
                    }
                } else {
                    System.out.println(String.format(format, url, "", ""));
                }
            }
        } else {
            List<String> urls = watcher.getWatchURLs();
            if (urls != null && urls.size()>0) {
                System.out.println("Watched URLs/IDs: ");
                for (String url : watcher.getWatchURLs()) {
                    System.out.println(url);
                }
            } else {
                System.out.println("No watched URLs/IDs");
            }
        }

        return null;
    }

    public BundleWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(BundleWatcher watcher) {
        this.watcher = watcher;
    }
}








