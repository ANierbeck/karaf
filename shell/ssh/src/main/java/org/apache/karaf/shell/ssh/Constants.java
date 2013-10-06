package org.apache.karaf.shell.ssh;

public class Constants {

    public static final String SSH_PORT = "sshPort";
    public static final String SSH_HOST = "sshHost";
    public static final String SSH_IDLE_TIMEOUT = "sshIdleTimeout";
    public static final String SSH_REALM = "sshRealm";
    public static final String SSH_ROLE = "sshRole";
    public static final String HOST_KEY = "hostKey";
    public static final String AUTHORIZED_KEYS = "authorizedKeys";
    public static final String AUTH_METHODS = "authMethods";
    public static final String KEY_SZIE = "keySize";
    public static final String ALGORITHM = "algorithm";

    public static final int DEFAULT_SSH_PORT = 8101;
    public static final String DEFAULT_SSH_HOST = "0.0.0.0";
    public static final long DEFAULT_SSH_IDLE_TIMEOUT = 1800000L;
    public static final String DEFAULT_SSH_REALM = "karaf";
    public static final String DEFAULT_SSH_ROLE = "admin";
    public static final String DEFAULT_AUTH_METHODS = "password,publickey";
    public static final int DEFAULT_KEY_SIZE = 1024;
    public static final String DEFAULT_ALGORITHM = "DSA";
}
