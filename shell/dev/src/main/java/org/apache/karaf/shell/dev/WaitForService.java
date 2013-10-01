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

import java.util.concurrent.TimeoutException;

import aQute.bnd.annotation.Activate;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Command that can be used to wait for an OSGi service.
 */
@Command(scope = WaitForService.SCOPE_VALUE, name = WaitForService.FUNCTION_VALUE, description = WaitForService.DESCRIPTION)
@Component(name = WaitForService.ID, description = WaitForService.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties(
        {
                @Property(name = ComponentAction.SCOPE, value = WaitForService.SCOPE_VALUE),
                @Property(name = ComponentAction.FUNCTION, value = WaitForService.FUNCTION_VALUE)
        }
)
public class WaitForService extends ComponentAction {

    public static final String ID = "org.apache.karaf.shell.dev.waitforservice";
    public static final String SCOPE_VALUE = "dev";
    public static final String FUNCTION_VALUE =  "wait-for-service";
    public static final String DESCRIPTION = "\"Wait for a given OSGi service.";

    @Option(name = "-e", aliases = { "--exception" }, description = "throw an exception if the service is not found after the timeout")
    boolean exception;

    @Option(name = "-t", aliases = { "--timeout" }, description = "timeout to wait for the service (in milliseconds, negative to not wait at all, zero to wait forever)")
    long timeout = 0;

    @Argument(name = "service", description="The service class or filter", required = true, multiValued = false)
    String service;

    private BundleContext bundleContext;

    @Activate
    void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public Object doExecute() throws Exception {
        ServiceTracker tracker = null;
        try {
            String filter = service;
            if (!filter.startsWith("(")) {
                if (!filter.contains("=")) {
                    filter = Constants.OBJECTCLASS + "=" + filter;
                }
                filter = "(" + filter + ")";
            }
            Filter osgiFilter = FrameworkUtil.createFilter(filter);
            tracker = new ServiceTracker(bundleContext, osgiFilter, null);
            tracker.open(true);
            Object svc = tracker.getService();
            if (timeout >= 0) {
                svc = tracker.waitForService(timeout);
            }
            if (exception && svc == null) {
                throw new TimeoutException("Can not find service '" + service + "' in the OSGi registry");
            }
            return svc != null;
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid filter", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (tracker != null) {
                tracker.close();
            }
        }
    }

}
