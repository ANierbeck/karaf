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
package org.apache.karaf.shell.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ops4j.pax.web.service.spi.WebEvent;
import org.ops4j.pax.web.service.spi.WebListener;

/**
 * Class implementing {@link WebListener} service to retrieve {@link WebEvent}
 */
@Component(name = "org.apache.karaf.shell.web.event.handler", description = "Karaf Web EventHandler")
@Service({WebListener.class, WebEventHandler.class})
public class WebEventHandlerImpl implements WebListener, WebEventHandler {
	
	private final Map<Long, WebEvent> bundleEvents = new HashMap<Long, WebEvent>();

	public Map<Long, WebEvent> getBundleEvents() {
		return bundleEvents;
	}
		
	public void webEvent(WebEvent event) {
		getBundleEvents().put(event.getBundle().getBundleId(), event);
	}

}
