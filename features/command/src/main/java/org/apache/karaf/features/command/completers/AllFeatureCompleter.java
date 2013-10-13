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
package org.apache.karaf.features.command.completers;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.features.Feature;
import org.apache.karaf.shell.console.Completer;

/**
 * {@link Completer} for available features.
 */

@Component(name = "org.apache.karaf.command.completer." + AllFeatureCompleter.COMPLETER_TYPE, immediate = true)
@Service(Completer.class)
@Properties(
        @Property(name = "completer.type", value = AllFeatureCompleter.COMPLETER_TYPE)
)
public class AllFeatureCompleter extends FeatureCompleterSupport {

    public static final String COMPLETER_TYPE = "features.all";

    @Override
    protected boolean acceptsFeature(Feature feature) {
        return true;
    }

}
