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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import jline.Terminal;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CompletableFunction;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.apache.karaf.shell.console.jline.Console;
import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.apache.sshd.agent.SshAgent;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.common.util.NoCloseInputStream;
import org.apache.sshd.common.util.NoCloseOutputStream;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connect to a SSH server.
 */
@Command(scope = SshAction.SCOPE_VALUE, name = SshAction.FUNCTION_VALUE, description = SshAction.DESCRIPTION)
@Component(name = SshAction.ID, description = SshAction.DESCRIPTION)
@Service(CompletableFunction.class)
@org.apache.felix.scr.annotations.Properties({
        @Property(name = ComponentAction.SCOPE, value = SshAction.SCOPE_VALUE),
        @Property(name = ComponentAction.FUNCTION, value = SshAction.FUNCTION_VALUE)
})
public class SshAction extends ComponentAction {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String ID = "org.apache.karaf.shell.ssh.ssh";
    public static final String SCOPE_VALUE = "ssh";
    public static final String FUNCTION_VALUE =  "ssh";
    public static final String DESCRIPTION = "Connects to a remote SSH server";

    @Option(name="-l", aliases={"--username"}, description = "The user name for remote login", required = false, multiValued = false)
    private String username;

    @Option(name = "-P", aliases = {"--password"}, description = "The password for remote login", required = false, multiValued = false)
    private String password;

    @Option(name="-p", aliases={"--port"}, description = "The port to use for SSH connection", required = false, multiValued = false)
    private int port = 22;

    @Argument(index = 0, name = "hostname", description = "The host name to connect to via SSH", required = true, multiValued = false)
    private String hostname;

    @Argument(index = 1, name = "command", description = "Optional command to execute", required = false, multiValued = true)
    private List<String> command;

	private ClientSession sshSession;

    @Reference(target = "(component.factory=" + SshClientFactory.ID + ")")
    private ComponentFactory componentFactory;


    @Override
    public Object doExecute() throws Exception {

        if (hostname.indexOf('@') >= 0) {
            if (username == null) {
                username = hostname.substring(0, hostname.indexOf('@'));
            }
            hostname = hostname.substring(hostname.indexOf('@') + 1, hostname.length());
        }

        System.out.println("Connecting to host " + hostname + " on port " + port);

        // If not specified, assume the current user name
        if (username == null) {
            username = (String) getSession().get("USER");
        }
        // If the username was not configured via cli, then prompt the user for the values
        if (username == null) {
            log.debug("Prompting user for login");
            if (username == null) {
                username = readLine("Login: ");
            }
        }

        // Create the client from prototype

        ComponentInstance instance = componentFactory.newInstance(new Properties());
        SshClient client = (SshClient) componentFactory.newInstance(new Properties()).getInstance();
        log.debug("Created client: {}", client);
        client.start();

        String agentSocket = null;
        if (getSession().get(SshAgent.SSH_AUTHSOCKET_ENV_NAME) != null) {
            agentSocket = getSession().get(SshAgent.SSH_AUTHSOCKET_ENV_NAME).toString();
            client.getProperties().put(SshAgent.SSH_AUTHSOCKET_ENV_NAME,agentSocket);
        }

        try {
            ConnectFuture future = client.connect(hostname, port);
            future.await();
            sshSession = future.getSession();

            Object oldIgnoreInterrupts = getSession().get(Console.IGNORE_INTERRUPTS);
            getSession().put( Console.IGNORE_INTERRUPTS, Boolean.TRUE );

            try {
                System.out.println("Connected");

                boolean authed = false;
                if (agentSocket != null) {
                    sshSession.authAgent(username);
                    int ret = sshSession.waitFor(ClientSession.WAIT_AUTH | ClientSession.CLOSED | ClientSession.AUTHED, 0);
                    if ((ret & ClientSession.AUTHED) == 0) {
                        System.err.println("Agent authentication failed, falling back to password authentication.");
                    } else {
                        authed = true;
                    }
                }
                if (!authed) {
                    log.debug("Prompting user for password");
                    if (password == null) {
                        password = readLine("Password: ");
                    }
                    sshSession.authPassword(username, password);
                    int ret = sshSession.waitFor(ClientSession.WAIT_AUTH | ClientSession.CLOSED | ClientSession.AUTHED, 0);
                    if ((ret & ClientSession.AUTHED) == 0) {
                        System.err.println("Password authentication failed");
                    } else {
                        authed = true;
                    }
                }
                if (!authed) {
                    return null;
                }

                StringBuilder sb = new StringBuilder();
                if (command != null) {
                    for (String cmd : command) {
                        if (sb.length() > 0) {
                            sb.append(' ');
                        }
                        sb.append(cmd);
                    }
                }

                ClientChannel channel;
                if (sb.length() > 0) {
                    channel = sshSession.createChannel("exec", sb.append("\n").toString());
                    channel.setIn(new ByteArrayInputStream(new byte[0]));
                } else {
                    channel = sshSession.createChannel("shell");
                    channel.setIn(new NoCloseInputStream(System.in));
                    ((ChannelShell) channel).setPtyColumns(getTermWidth());
                    ((ChannelShell) channel).setupSensibleDefaultPty();
                    ((ChannelShell) channel).setAgentForwarding(true);
                    Object ctype = getSession().get("LC_CTYPE");
                    if (ctype != null) {
                        ((ChannelShell) channel).setEnv("LC_CTYPE", ctype.toString());
                    }
                }
                channel.setOut(new NoCloseOutputStream(System.out));
                channel.setErr(new NoCloseOutputStream(System.err));
                channel.open();
                channel.waitFor(ClientChannel.CLOSED, 0);
            } finally {
                getSession().put(Console.IGNORE_INTERRUPTS, oldIgnoreInterrupts);
                sshSession.close(false);
            }
        } finally {
            client.stop();
            instance.dispose();
        }

        return null;
    }

    private int getTermWidth() {
        Terminal term = (Terminal) getSession().get(".jline.terminal");
        return term != null ? term.getWidth() : 80;
    }

    public String readLine(String msg) throws IOException {
        StringBuffer sb = new StringBuffer();
        System.err.print(msg);
        System.err.flush();
        for (;;) {
            int c = getSession().getKeyboard().read();
            if (c < 0) {
                return null;
            }
            System.err.print((char) c);
            if (c == '\r' || c == '\n') {
                break;
            }
            sb.append((char) c);
        }
        return sb.toString();
    }

}
