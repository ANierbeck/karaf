/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.shell.ssh;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.sshd.SshServer;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.gogo.commands.Command;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Start a SSH server.
 */
@Command(scope = SshServerAction.SCOPE_VALUE, name = SshServerAction.FUNCTION_VALUE, description = SshServerAction.DESCRIPTION)
@Component(name = SshServerAction.ID, description = SshServerAction.DESCRIPTION)
@Service(CompletableFunction.class)
@org.apache.felix.scr.annotations.Properties({
        @Property(name = ComponentAction.SCOPE, value = SshServerAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = SshServerAction.FUNCTION_VALUE)
})
public class SshServerAction extends ComponentAction {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String ID = "org.apache.karaf.shell.ssh.sshd";
    public static final String SCOPE_VALUE = "ssh";
    public static final String FUNCTION_VALUE =  "sshd";
    public static final String DESCRIPTION = "Creates a SSH server";


    @Option(name = "-p", aliases = { "--port" }, description = "The port to setup the SSH server (Default: 8101)", required = false, multiValued = false)
    private int port = 8101;

    @Option(name = "-b", aliases = { "--background"}, description = "The service will run in the background (Default: true)", required = false, multiValued = false)
    private boolean background = true;

    @Option(name = "-i", aliases = { "--idle-timeout" }, description = "The session idle timeout (Default: 1800000ms)", required = false, multiValued = false)
    private long idleTimeout = 1800000;

    @Reference(target = "(component.factory=" + SshServerFactory.ID + ")")
    private ComponentFactory componentFactory;


    public Object doExecute() throws Exception {
        ComponentInstance instance = componentFactory.newInstance(new Properties());
        SshServer server = (SshServer) componentFactory.newInstance(new Properties()).getInstance();

        log.debug("Created server: {}", server);

        // port number
        server.setPort(port);

        // idle timeout
        server.getProperties().put(SshServer.IDLE_TIMEOUT, new Long(idleTimeout).toString());

        // starting the SSHd server
        server.start();

        System.out.println("SSH server listening on port " + port + " (idle timeout " + idleTimeout + "ms)");

        if (!background) {
            synchronized (this) {
                log.debug("Waiting for server to shutdown");

                wait();
            }

            server.stop();
            instance.dispose();
        }

        return null;
    }
}
