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
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sshd.SshServer;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static org.apache.karaf.shell.ssh.Constants.ALGORITHM;
import static org.apache.karaf.shell.ssh.Constants.AUTHORIZED_KEYS;
import static org.apache.karaf.shell.ssh.Constants.DEFAULT_ALGORITHM;
import static org.apache.karaf.shell.ssh.Constants.DEFAULT_KEY_SIZE;
import static org.apache.karaf.shell.ssh.Constants.DEFAULT_SSH_HOST;
import static org.apache.karaf.shell.ssh.Constants.DEFAULT_SSH_IDLE_TIMEOUT;
import static org.apache.karaf.shell.ssh.Constants.DEFAULT_SSH_PORT;
import static org.apache.karaf.shell.ssh.Constants.DEFAULT_SSH_REALM;
import static org.apache.karaf.shell.ssh.Constants.DEFAULT_SSH_ROLE;
import static org.apache.karaf.shell.ssh.Constants.HOST_KEY;
import static org.apache.karaf.shell.ssh.Constants.KEY_SZIE;
import static org.apache.karaf.shell.ssh.Constants.SSH_HOST;
import static org.apache.karaf.shell.ssh.Constants.SSH_IDLE_TIMEOUT;
import static org.apache.karaf.shell.ssh.Constants.SSH_PORT;
import static org.apache.karaf.shell.ssh.Constants.SSH_REALM;
import static org.apache.karaf.shell.ssh.Constants.SSH_ROLE;

@Component(name = "org.apache.karaf.shell", description ="Karaf SSH Server Factory", policy = ConfigurationPolicy.OPTIONAL, immediate = true)
public class SshServerManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshServerManager.class);

    @Property(name = SSH_PORT, label = "SSH Port", description = "port of the SSH daemon", intValue = DEFAULT_SSH_PORT)
    private int sshPort;
    @Property(name = SSH_HOST, label = "SSH Host", description = "host of the SSH daemon", value = DEFAULT_SSH_HOST)
    private String sshHost;
    @Property(name = SSH_IDLE_TIMEOUT, label = "SSH Idle Timeout", description = "ammount of time to wait on idle connections", longValue = DEFAULT_SSH_IDLE_TIMEOUT)
    private long idleTimeout;
    @Property(name = SSH_REALM, label = "SSH Realm", description = "name of the JAAS realm to use for SSH authentication", value = DEFAULT_SSH_REALM)
    private String sshRealm;
    @Property(name = SSH_ROLE, label = "SSH Role", description = "name of the JAAS role to use for SSH authentication", value = DEFAULT_SSH_ROLE)
    private String sshRole;
    @Property(name = HOST_KEY, label = "Host Key", description = "location of the host key to use for authentication")
    private String hostKey;
    @Property(name = AUTHORIZED_KEYS, label = "Authorized Keys", description = "location of the authorized keys")
    private String authorizedKeys;
    @Property(name = KEY_SZIE, label = "Key Size", description = "Secret key size in 1024, 2048, 3072, or 4096", intValue = DEFAULT_KEY_SIZE)
    private int keySize;
    @Property(name = ALGORITHM, label = "Algorithm", description = "Host key algorithm in DSA, RSA, etc", value = DEFAULT_ALGORITHM)
    private String algorithm;

    private boolean start = Boolean.parseBoolean(System.getProperty("karaf.startRemoteShell", "true"));

    @Reference(target = "(component.factory=" + SshServerFactory.ID + ")")
    private ComponentFactory componentFactory;

    private ComponentInstance instance;
    private SshServer server;


    @Activate
    void activate(Map<String, ?> props) throws IOException {
        Properties properties = new Properties();
        properties.putAll(props);
        instance = componentFactory.newInstance(properties);
        server = ((SshServerFactory) instance.getInstance()).getServer();
        server.start();
    }

    @Deactivate
    void deactivate(){
        if (server != null) {
            stop();
        }
        if (instance != null) {
            instance.dispose();
        }
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public void start() {
        if (start) {
            try {
                server.getProperties().put(SshServer.IDLE_TIMEOUT, new Long(idleTimeout).toString());
                server.start();
            } catch (Exception e) {
                LOGGER.info("Error updating SSH server", e);
            }
        }
    }

    public void stop() {
        if (start && server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                LOGGER.info("Error stopping SSH server", e);
            } finally {
                server = null;
            }
        }
    }
}
