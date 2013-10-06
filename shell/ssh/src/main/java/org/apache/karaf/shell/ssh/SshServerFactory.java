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

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sshd.SshServer;
import org.apache.sshd.agent.SshAgentFactory;
import org.apache.sshd.common.Factory;
import org.apache.sshd.common.KeyPairProvider;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.io.IOException;
import java.util.Map;

import static org.apache.karaf.shell.ssh.Constants.ALGORITHM;
import static org.apache.karaf.shell.ssh.Constants.AUTH_METHODS;
import static org.apache.karaf.shell.ssh.Constants.KEY_SZIE;
import static org.apache.karaf.shell.ssh.Constants.SSH_HOST;
import static org.apache.karaf.shell.ssh.Constants.SSH_IDLE_TIMEOUT;
import static org.apache.karaf.shell.ssh.Constants.SSH_PORT;
import static org.apache.karaf.shell.ssh.Constants.SSH_REALM;
import static org.apache.karaf.shell.ssh.Constants.SSH_ROLE;
import static org.apache.karaf.util.config.ConfigUtils.readInt;
import static org.apache.karaf.util.config.ConfigUtils.readLong;
import static org.apache.karaf.util.config.ConfigUtils.readString;

@Component(name = SshServerFactory.ID, description ="Karaf SSH Server Factory", factory = SshServerFactory.ID)
public class SshServerFactory {

    public static final String ID = "org.apache.karaf.shell.ssh.server.factory";

    @Reference
    private CommandFactory commandFactory;
    @Reference
    private SshAgentFactory sshAgentFactory;
    @Reference
    private Factory<Command> shellFactory;

    private ScpCommandFactory scpCommandFactory;
    private KeyPairProvider keyPairProvider;
    private KarafJaasAuthenticator authenticator;

    private final KarafFileSystemFactory fileSystemFactory = new KarafFileSystemFactory();
    private final UserAuthFactoriesFactory userAuthFactoriesFactory = new UserAuthFactoriesFactory();

    private volatile SshServer server;

    @Activate
    void activate(Map<String, ?> options) throws IOException {
       int sshPort = readInt(options, SSH_PORT);
       String sshHost = readString(options, SSH_HOST);
       String sshRealm = readString(options, SSH_REALM);
       String sshRole = readString(options, SSH_ROLE);
       String hostKey = readString(options, SSH_PORT);
       int keySize = readInt(options, KEY_SZIE);
       String algorithm = readString(options, ALGORITHM);

        scpCommandFactory = createScpCommandFactory(scpCommandFactory);
        keyPairProvider = createKeyPairProvider(hostKey, algorithm, keySize);
        authenticator = createAuthenticator(sshRealm, sshRole);
        server = SshServer.setUpDefaultServer();
        server.setHost(sshHost);
        server.setPort(sshPort);
        server.setShellFactory(shellFactory);
        server.setCommandFactory(scpCommandFactory);
        server.setAgentFactory(sshAgentFactory);
        server.setFileSystemFactory(fileSystemFactory);
        server.setPasswordAuthenticator(authenticator);
        server.setPublickeyAuthenticator(authenticator);
        server.setKeyPairProvider(keyPairProvider);
        server.setUserAuthFactories(userAuthFactoriesFactory.getFactories());
    }

    @Deactivate
    void deactivate() throws InterruptedException {
        if (server != null) {
            server.stop();
        }
    }

    public SshServer getServer() {
        return server;
    }


    /**
     * Creates a an {@link ScpCommandFactory}.
     * @param commandFactory   The {@link CommandFactory} delegate.
     * @return                 An {@link ScpCommandFactory}.
     */
    private static ScpCommandFactory createScpCommandFactory(CommandFactory commandFactory) {
        return new ScpCommandFactory(commandFactory);
    }

    /**
     * Creates an instance of {@link KarafJaasAuthenticator}.
     * @param realm     The jaas realm to use.
     * @param role      The role principal to use.
     * @return
     */
    private static KarafJaasAuthenticator createAuthenticator(String realm, String role) {
        KarafJaasAuthenticator authenticator =  new KarafJaasAuthenticator();
        authenticator.setRealm(realm);
        authenticator.setRole(role);
        return authenticator;
    }

    /**
     * Creates a {@link KeyPairProvider}.
     * @param path          The path to the host key.
     * @param algorithm     The algorithm.
     * @param size          The key size.
     * @return
     */
    private static KeyPairProvider createKeyPairProvider(String path, String algorithm, int size) {
        return new SimpleGeneratorHostKeyProvider(path, algorithm, size);
    }

}


