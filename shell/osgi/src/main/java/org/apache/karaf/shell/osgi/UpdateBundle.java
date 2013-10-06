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

import java.io.InputStream;
import java.net.URL;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.Bundle;

@Command(scope = UpdateBundle.SCOPE_VALUE, name = UpdateBundle.FUNCTION_VALUE, description = UpdateBundle.DESCRIPTION)
@Component(name = UpdateBundle.ID, description = UpdateBundle.DESCRIPTION)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = UpdateBundle.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = UpdateBundle.FUNCTION_VALUE)
})
public class UpdateBundle extends BundleCommand {

    public static final String ID = "org.apache.karaf.shell.osgi.update";
    public static final String SCOPE_VALUE = "osgi";
    public static final String FUNCTION_VALUE =  "update";
    public static final String DESCRIPTION = "Update bundle.";

	@Argument(index = 1, name = "location", description = "The bundles update location", required = false, multiValued = false)
	String location;

	protected void doExecute(Bundle bundle) throws Exception {
		if (location != null) {
			InputStream is = new URL(location).openStream();
			bundle.update(is);
		} else {
			bundle.update();
		}
	}
}
