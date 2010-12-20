/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.main;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;

public class MainStartTest {

	@Test
    public void testAutoStart() throws Exception {
        File basedir = new File(getClass().getClassLoader().getResource("foo").getPath()).getParentFile();
        File home = new File(basedir, "test-karaf-home");
        File data = new File(home, "data");

        Utils.deleteDirectory(data);

		String[] args = new String[0];
		String fileMVNbundle = new File(home, "bundles/pax-url-mvn.jar").toURI().toURL().toExternalForm();
		String mvnUrl = "mvn:org.osgi/org.osgi.compendium/4.2.0";
		System.setProperty("karaf.home", home.toString());
		System.setProperty("karaf.data", data.toString());
		System.setProperty("karaf.auto.start.1", "\""+fileMVNbundle+"|unused\"");
		System.setProperty("karaf.auto.start.2", "\""+mvnUrl+"|unused\"");
		System.setProperty("karaf.maven.convert", "false");

		Main main = new Main(args);
		main.launch();
		Thread.sleep(1000);
		Framework framework = main.getFramework();
		Bundle[] bundles = framework.getBundleContext().getBundles();
		Assert.assertEquals(3, bundles.length);
		Assert.assertEquals(fileMVNbundle, bundles[1].getLocation());
		Assert.assertEquals(mvnUrl, bundles[2].getLocation());
		Assert.assertEquals(Bundle.ACTIVE, bundles[1].getState());
		Assert.assertEquals(Bundle.ACTIVE, bundles[2].getState());
		main.destroy(false);
	}
}
