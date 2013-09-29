package org.apache.karaf.management;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;

import static org.apache.karaf.util.config.ConfigUtils.readBoolean;
import static org.apache.karaf.util.config.ConfigUtils.readInt;
import static org.apache.karaf.util.config.ConfigUtils.readString;

@Component(name = ManagementComponent.MANAGEMENT_PID,
        policy = ConfigurationPolicy.REQUIRE,
        immediate = true)
public class ManagementComponent {

    public static final String MANAGEMENT_PID = "org.apache.karaf.management";

    public static final String REGISTRY_HOST = "rmiRegistryHost";
    public static final String REGISTRY_PORT = "rmiRegistryPort";
    public static final String SERVER_HOST = "rmiServerHost";
    public static final String SERVER_PORT = "rmiServerPort";
    public static final String JMX_REALM = "jmxRealm";
    public static final String JMX_ROLE = "jmxRole";
    public static final String SERVICE_URL = "serviceUrl";
    public static final String DAEMON = "daemon";
    public static final String THREADED = "threaded";
    public static final String OBJECT_NAME = "objectName";
    public static final String KEYSTORE_AVAILABILITY_TIMEOUT = "keyStoreAvailabilityTimeout";
    public static final String AUTHENTICATOR_TYPE = "authenticatorType";
    public static final String SECURED = "secured";
    public static final String SECURE_ALGORITHM = "secureAlgorithm";
    public static final String SECURE_PROTOCOL = "secureProtocol";
    public static final String KEYSTORE = "keyStore";
    public static final String KEY_ALIAS = "keyAlias";
    public static final String TRUSTSTORE = "trustStore";
    public static final String CLIENT_AUTH = "clientAuth";


    @Property(name = "rmiRegistryHost", label = "RMI Registry Host", description = "host of the registry for the exported RMI service. Blank for all interfaces", value = "0.0.0.0")
    private String rmiRegistryHost;

    @Property(name = "rmiRegistryPort", label = "RMI Registry Port", description = "port of the registry for the exported RMI service", intValue = 1099)
    private Integer rmiRegistryPort;

    @Property(name = "rmiServerHost", label = "RMI Server Host", description = "host of the server for the exported RMI objects. Blank for all interfaces", value = "0.0.0.0")
    private String rmiServerHost;

    @Property(name = "rmiServerPort", label = "RMI Server Port", description = "port of the server for the exported RMI objects", intValue = 44444)
    private Integer rmiServerPort;

    @Property(name = "jmxRealm", label = "Realm", description = "name of the JAAS realm used for authentication", value = "karaf")
    private String jmxRealm;

    @Property(name = "jmxRole", label = "Role", description = "name of the required role", value = "admin")
    private String jmxRole;

    @Property(name = "serviceUrl", label = "Service Url", description = "the service URL for the JMXConnectorServer")
    private String serviceUrl;

    @Property(name = "daemon", label = "Daemon", description = "whether any threads started for the JMXConnectorServer should be started as daemon threads", boolValue = true)
    private boolean daemon;

    @Property(name = "threaded", label = "Threaded", description = "whether the JMXConnectorServer should be started in a separate threads", boolValue = true)
    private boolean threaded;

    @Property(name = "objectName", label = "Object Name", description = "the ObjectName used to register the JMXConnectorServer", value = "connector:name=rmi")
    private String objectName;

    @Property(name = "keyStoreAvailabilityTimeout", label = "KeyStore Availability Timeout", description = "number of milliseconds waiting for keystore to be loaded", longValue = 5000L)
    private Long keyStoreAvailabilityTimeout;

    @Property(name = "authenticatorType", label = "Authenticator Type", description = "Authenticator to use. Available values are \"none\", \"password\", and \"certificate\"", value = "password")
    private String authenticatorType;

    @Property(name = "secured", label = "Secured", description = "whether to start MBean server with SSL", boolValue = false)
    private boolean secured;

    @Property(name = "secureAlgorithm", label = "Secure Algorithm", description = "Algorithm to use", value = "default")
    private String secureAlgorithm;

    @Property(name = "secureProtocol", label = "Secure Protocol", description = "Protocol to use", value = "TLS")
    private String secureProtocol;

    @Property(name = "keyStore", label = "Keystore Name", description = "Keystore name from keystore manager", value = "karaf.ks")
    private String keyStore;

    @Property(name = "keyAlias", label = "Key Alias", description = "Key alias to be used with secured connector", value = "karaf")
    private String keyAlias;

    @Property(name = "trustStore", label = "Truststore Name", description = "Truststore name from keystore manager", value = "karaf.ts")
    private String trustStore;

    @Property(name = "clientAuth", label = "Client Auth", description = "whether client should authenticate", boolValue = false)
    private boolean clientAuth;

    private final Map environment = new HashMap();
    private final JaasAuthenticator authenticator = new JaasAuthenticator();

    private ConnectorServerFactory connectorServerFactory;
    private RmiRegistryFactory rmiRegistryFactory;
    private MBeanServerFactory mBeanServerFactory;
    private MBeanServer mBeanServer;

    private BundleContext bundleContext;
    private ServiceRegistration registration;


    @Activate
    public void activate(BundleContext bundleContext, Map<String, Object> properties) throws Exception {
        this.bundleContext = bundleContext;
        update(properties);

        authenticator.setRealm(jmxRealm);
        authenticator.setRole(jmxRole);
        environment.put("jmx.remote.authenticator", authenticator);

        rmiRegistryFactory = new RmiRegistryFactory(rmiRegistryPort, rmiRegistryHost, true, true);
        rmiRegistryFactory.init();

        mBeanServerFactory = new MBeanServerFactory();
        mBeanServerFactory.setLocateExistingServerIfPossible(true);
        mBeanServerFactory.init();
        mBeanServer = mBeanServerFactory.getServer();

        connectorServerFactory = createConnectorServerFactory();
        connectorServerFactory.init();

        bundleContext.registerService(MBeanServer.class.getName(), mBeanServer, null);
    }

    @Modified
    public void modified(Map<String, Object> properties) throws Exception {
        update(properties);
        authenticator.setRealm(jmxRealm);
        authenticator.setRole(jmxRole);
        environment.put("jmx.remote.authenticator", authenticator);

        rmiRegistryFactory = new RmiRegistryFactory(rmiRegistryPort, rmiRegistryHost, true, true);
        rmiRegistryFactory.init();

        connectorServerFactory = createConnectorServerFactory();
        connectorServerFactory.init();
    }

    @Deactivate
    public void deactivate() throws Exception {
        if (registration != null) {
            registration.unregister();
        }

        if (connectorServerFactory != null) {
            connectorServerFactory.destroy();
        }

        if (mBeanServerFactory != null) {
            mBeanServerFactory.destroy();
        }

        if (rmiRegistryFactory != null) {
            rmiRegistryFactory.destroy();
        }

    }

    private void update(Map<String, Object> properties) {
        rmiRegistryHost = readString(properties, REGISTRY_HOST);
        rmiRegistryPort = readInt(properties, REGISTRY_PORT);
        rmiServerHost = readString(properties, SERVER_HOST);
        rmiServerPort = readInt(properties, SERVER_PORT);
        jmxRealm = readString(properties, JMX_REALM);
        jmxRole = readString(properties, JMX_ROLE);
        serviceUrl = readString(properties, SERVICE_URL);
        daemon = readBoolean(properties, DAEMON);
        threaded = readBoolean(properties, THREADED);
        objectName = readString(properties, OBJECT_NAME);
        keyStoreAvailabilityTimeout = (Long) properties.get(KEYSTORE_AVAILABILITY_TIMEOUT);
        authenticatorType = readString(properties, AUTHENTICATOR_TYPE);
        secured = (Boolean) properties.get(SECURED);
        secureAlgorithm = readString(properties, SECURE_ALGORITHM);
        secureProtocol = readString(properties, SECURE_PROTOCOL);
        keyStore = readString(properties, KEYSTORE);
        keyAlias = readString(properties, KEY_ALIAS);
        trustStore = readString(properties, TRUSTSTORE);
        clientAuth = readBoolean(properties, CLIENT_AUTH);
    }

    private ConnectorServerFactory createConnectorServerFactory() throws MalformedObjectNameException {
        ConnectorServerFactory connectorServerFactory = new ConnectorServerFactory();
        connectorServerFactory.setServiceUrl(serviceUrl);
        connectorServerFactory.setServer(mBeanServer);
        connectorServerFactory.setDaemon(daemon);
        connectorServerFactory.setThreaded(threaded);
        connectorServerFactory.setSecured(secured);
        connectorServerFactory.setAlgorithm(secureAlgorithm);
        connectorServerFactory.setSecureProtocol(secureProtocol);
        connectorServerFactory.setAuthenticatorType(authenticatorType);
        connectorServerFactory.setKeyStore(keyStore);
        connectorServerFactory.setKeyStoreAvailabilityTimeout(keyStoreAvailabilityTimeout);
        connectorServerFactory.setKeyAlias(keyAlias);
        connectorServerFactory.setTrustStore(trustStore);
        connectorServerFactory.setObjectName(new ObjectName(objectName));
        connectorServerFactory.setRmiServerHost(rmiServerHost);
        connectorServerFactory.setEnvironment(environment);
        return connectorServerFactory;
    }
}
