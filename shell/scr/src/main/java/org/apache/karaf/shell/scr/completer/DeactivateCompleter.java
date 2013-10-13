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
package org.apache.karaf.shell.scr.completer;

import org.apache.felix.scr.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.Completer;

@org.apache.felix.scr.annotations.Component(name = "org.apache.karaf.shell.scr.deactivate.components.completer", immediate = true)
@Service(Completer.class)
@Properties(
        @Property(name = "completer.type", value = ActivateCompleter.COMPLETER_TYPE)
)
public class DeactivateCompleter extends ScrCompleterSupport {

    public static final String COMPLETER_TYPE = "active.components.completer";

    /**
     * Overrides the super method noted below. See super documentation for
     * details.
     * 
     * @see org.apache.karaf.scr.command.completer.ScrCompleterSupport#availableComponent(org.apache.felix.scr.Component)
     */
    @Override
    public boolean availableComponent(Component component) throws Exception {
        return (component != null && (component.getState() == Component.STATE_ACTIVE));
    }

}
