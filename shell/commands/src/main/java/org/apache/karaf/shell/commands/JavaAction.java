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
package org.apache.karaf.shell.commands;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.AbstractAction;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute a Java standard application.
 *
 * <p>By default looks for static main(String[]) to execute, but
 * you can specify a different static method that takes a String[]
 * to execute instead.
 */
@Command(scope = JavaAction.SCOPE_VALUE, name = JavaAction.FUNCTION_VALUE, description = JavaAction.DESCRIPTION)
@Component(name = JavaAction.ID, description = JavaAction.DESCRIPTION, immediate = true)
@Service(CompletableFunction.class)
@Properties({
        @Property(name = ComponentAction.SCOPE, value = JavaAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = JavaAction.FUNCTION_VALUE)
})
public class JavaAction extends ComponentAction {

    private static final Logger log = LoggerFactory.getLogger(JavaAction.class);

    public static final String ID = "org.apache.karaf.shell.commands.java";
    public static final String SCOPE_VALUE = "shell";
    public static final String FUNCTION_VALUE =  "java";
    public static final String DESCRIPTION = "Executes a Java standard application.";


    @Option(name = "-m", aliases = {"--method"}, description = "Invoke a named method", required = false, multiValued = false)
    private String methodName = "main";

    @Argument(index = 0, name = "className", description="The name of the class to invoke", required = true, multiValued = false)
    private String className;

    @Argument(index = 1, name = "arguments", description="Arguments to pass to the method of the given class", required = false, multiValued = false)
    private List<String> args;

    public Object doExecute() throws Exception {
        boolean info = log.isInfoEnabled();

        Class type = Thread.currentThread().getContextClassLoader().loadClass(className);
        if (info) {
            log.info("Using type: " + type);
        }

        Method method = type.getMethod(methodName, String[].class);
        if (info) {
            log.info("Using method: " + method);
        }

        if (info) {
            log.info("Invoking w/arguments: {}", args);
        }

        Object result = method.invoke(null, args);

        if (info) {
            log.info("Result: " + result);
        }

        return null;
    }

}
