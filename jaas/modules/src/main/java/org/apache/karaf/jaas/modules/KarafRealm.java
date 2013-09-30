/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.apache.karaf.jaas.modules;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.boot.ProxyLoginModule;
import org.apache.karaf.jaas.config.JaasRealm;
import org.osgi.framework.BundleContext;

import javax.security.auth.login.AppConfigurationEntry;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(name = "org.apache.karaf.jaas", description = "Karaf Jaas Realm", immediate = true)
@Service(JaasRealm.class)
public class KarafRealm implements JaasRealm {

    private static final String KARAF_ETC = System.getProperty("karaf.base") + File.separatorChar + "etc";
    private static final String REALM = "karaf";
    private static final String PROPERTIES_MODULE = "org.apache.karaf.jaas.modules.properties.PropertiesLoginModule";
    private static final String PUBLIC_KEY_MODULE = "org.apache.karaf.jaas.modules.publickey.PublickeyLoginModule";

    private static final String ENCRYPTION_NAME = "encryption.name";
    private static final String ENCRYPTION_ENABLED = "encryption.enabled";
    private static final String ENCRYPTION_PREFIX = "encryption.prefix";
    private static final String ENCRYPTION_SUFFIX = "encryption.suffix";
    private static final String ENCRYPTION_ALGORITHM = "encryption.algorithm";
    private static final String ENCRYPTION_ENCODING = "encryption.encoding";
    private static final String MODULE = "org.apache.karaf.jaas.module";

    private final List<AppConfigurationEntry> enties = new ArrayList<AppConfigurationEntry>();

    @Property(name = "encryptionName", label = "Encryption Name", description = "The name of the encryption")
    private String encryptionName;
    @Property(name = "encryptionEnabled", label = "Encryption Enabled", description = "Is encryption enabled", boolValue = false)
    private Boolean encryptionEnabled;
    @Property(name = "encryptionPrefix", label = "Encryption Prefix", description = "The prefix of the encryption", value = "{CRYPT}")
    private String encryptionPrefix;
    @Property(name = "encryptionSuffix", label = "Encryption Suffix", description = "The suffix of the encryption", value = "{CRYPT}")
    private String encryptionSuffix;
    @Property(name = "encryptionAlgorithm", label = "Encryption Algorithm", description = "The algorithm of the encryption", value = "MD5")
    private String encryptionAlgorithm;
    @Property(name = "encryptionEncoding", label = "Encryption Encoding", description = "The encoding of the encryption", value = "hexadecimal")
    private String encryptionEncoding;

    @Activate
    void activate(BundleContext bundleContext, Map<String, Object> properties) {
        try {
            Map<String, Object> propertiesOptions = new HashMap<String, Object>();
            propertiesOptions.putAll(properties);
            propertiesOptions.put(BundleContext.class.getName(), bundleContext);
            propertiesOptions.put(ProxyLoginModule.PROPERTY_MODULE, PROPERTIES_MODULE);
            propertiesOptions.put(ProxyLoginModule.PROPERTY_BUNDLE, Long.toString(bundleContext.getBundle().getBundleId()));
            propertiesOptions.put("users", KARAF_ETC + File.separatorChar + "users.properties");
            enties.add(new AppConfigurationEntry(ProxyLoginModule.class.getName(), AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT, propertiesOptions));

            Map<String, Object> publicKeyOptions = new HashMap<String, Object>();
            publicKeyOptions.putAll(properties);
            publicKeyOptions.put(BundleContext.class.getName(), bundleContext);
            publicKeyOptions.put(ProxyLoginModule.PROPERTY_MODULE, PUBLIC_KEY_MODULE);
            publicKeyOptions.put(ProxyLoginModule.PROPERTY_BUNDLE, Long.toString(bundleContext.getBundle().getBundleId()));
            publicKeyOptions.put("users", KARAF_ETC + File.separatorChar + "keys.properties");
            enties.add(new AppConfigurationEntry(ProxyLoginModule.class.getName(), AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT, publicKeyOptions));
        } catch (RuntimeException rte) {
            throw rte;
        }
    }

    @Deactivate
    void deactivate() {
    }


    @Override
    public String getName() {
        return REALM;
    }

    @Override
    public int getRank() {
        return 0;
    }

    @Override
    public AppConfigurationEntry[] getEntries() {
        return enties.toArray(new AppConfigurationEntry[enties.size()]);
    }

}
