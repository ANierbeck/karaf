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
package org.apache.karaf.shell.console.commands;

import org.apache.felix.gogo.commands.Action;
import org.apache.felix.gogo.commands.basic.AbstractCommand;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.NullCompleter;
import org.osgi.framework.BundleContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Component(name = "org.apache.karaf.shell.console.action.base", componentAbstract = true)
public abstract class ComponentAction extends AbstractCommand implements Action, CompletableFunction {

    public static final String SCOPE = "osgi.command.scope";
    public static final String FUNCTION = "osgi.command.function";

    static final NullCompleter NULL_COMPLETER = new NullCompleter();

    private final List<Completer> completers = new CopyOnWriteArrayList<Completer>();
    private final ConcurrentMap<String, Completer> optionalCompleters = new ConcurrentHashMap<String, Completer>();
    private CommandSession session;

    private BundleContext bundleContext;

    @Activate
   public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public abstract Object doExecute() throws Exception;

    @Override
    public Action createNewAction() {
        return this;
    }

    public CommandSession getSession() {
        return session;
    }

    public Object execute(CommandSession session) throws Exception {
        this.session = session;
        return doExecute();
    }

    public void bindCompleter(Completer completer) {
        completers.add(completer);
    }

    public void unbindCompleter(Completer completer) {
        completers.remove(completer);
    }

    public void bindOptionalCompleter(String option, Completer completer) {
        completers.add(completer);
    }

    public void unbindOptionalCompleter(String option) {
        completers.remove(option);
    }

    public List<Completer> getCompleters() {
        return !completers.isEmpty() ? completers : Arrays.<Completer>asList(NULL_COMPLETER);
    }

    public Map<String, Completer> getOptionalCompleters() {
        return optionalCompleters;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
}
