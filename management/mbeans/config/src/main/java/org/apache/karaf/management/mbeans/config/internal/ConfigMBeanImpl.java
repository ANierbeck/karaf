/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.management.mbeans.config.internal;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.utils.properties.Properties;
import org.apache.karaf.management.mbeans.config.ConfigMBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of the ConfigMBean.
 */
@Component(name = "org.apache.karaf.managment.mbeans.config", immediate = true)
public class ConfigMBeanImpl extends StandardMBean implements ConfigMBean {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigMBeanImpl.class);
    private static final String OBJECT_NAME = "org.apache.karaf:type=config,name=" + System.getProperty("karaf.name");
    
    private final String FELIX_FILEINSTALL_FILENAME = "felix.fileinstall.filename";

    @Reference
    private MBeanServer mBeanServer;
    @Reference
    private ConfigurationAdmin configurationAdmin;
    @Reference(referenceInterface = ArtifactInstaller.class,
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
            bind = "bindArtifactInstaller", unbind = "unbindArtifactInstaller")
    private final List<ArtifactInstaller> artifactInstallers = new CopyOnWriteArrayList<ArtifactInstaller>();


    private final File storage = new File(System.getProperty("karaf.base") + File.separator + "etc");

    @Activate
    public void activate() throws Exception {
        mBeanServer.registerMBean(this, new ObjectName(OBJECT_NAME));
    }

    @Deactivate
    public void deactivate() throws Exception {
        mBeanServer.unregisterMBean(new ObjectName(OBJECT_NAME));
    }

    public ConfigurationAdmin getConfigurationAdmin() {
        return this.configurationAdmin;
    }

    public File getStorage() {
        return this.storage;
    }

    public ConfigMBeanImpl() throws NotCompliantMBeanException {
        super(ConfigMBean.class);
    }

    public List<String> getConfigs() throws Exception {
        Configuration[] configurations = configurationAdmin.listConfigurations(null);
        List<String> pids = new ArrayList<String>();
        for (int i = 0; i < configurations.length; i++) {
            pids.add(configurations[i].getPid());
        }
        return pids;
    }

    /**
     * @deprecated used getConfigs() instead.
     */
    public List<String> list() throws Exception {
        return getConfigs();
    }

    public void create(String pid) throws Exception {
        store(pid, new Hashtable(), false);
    }

    public void delete(String pid) throws Exception {
        Configuration configuration = configurationAdmin.getConfiguration(pid);
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration PID " + pid + " doesn't exist");
        }
        configuration.delete();
        if (storage != null) {
            File cfgFile = new File(storage, pid + ".cfg");
            cfgFile.delete();
        }
    }

    public Map<String, String> listProperties(String pid) throws Exception {
        Configuration configuration = configurationAdmin.getConfiguration(pid);
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration PID " + pid + " doesn't exist");
        }
        Dictionary dictionary = configuration.getProperties();

        if (dictionary == null) {
            dictionary = new java.util.Properties();
        }

        Map<String, String> propertiesMap = new HashMap<String, String>();
        for (Enumeration e = dictionary.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            Object value = dictionary.get(key);
            propertiesMap.put(key.toString(), value.toString());
        }
        return propertiesMap;
    }

    /**
     * @deprecated use listProperties() instead.
     */
    public Map<String, String> proplist(String pid) throws Exception {
        return listProperties(pid);
    }

    public void deleteProperty(String pid, String key) throws Exception {
        Configuration configuration = configurationAdmin.getConfiguration(pid);
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration PID " + pid + " doesn't exist");
        }
        Dictionary dictionary = configuration.getProperties();

        if (dictionary == null) {
            dictionary = new java.util.Properties();
        }

        dictionary.remove(key);
        store(pid, dictionary, false);
    }

    /**
     * @deprecated use deleteProperty() instead.
     */
    public void propdel(String pid, String key) throws Exception {
        deleteProperty(pid, key);
    }

    public void appendProperty(String pid, String key, String value) throws Exception {
        Configuration configuration = configurationAdmin.getConfiguration(pid);
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration PID " + pid + " doesn't exist");
        }
        Dictionary dictionary = configuration.getProperties();

        if (dictionary == null) {
            dictionary = new java.util.Properties();
        }

        Object currentValue = dictionary.get(key);
        if (currentValue == null) {
            dictionary.put(key, value);
        } else if (currentValue instanceof String) {
            dictionary.put(key, currentValue + value);
        } else {
            throw new IllegalStateException("Current value is not a String");
        }
        store(pid, dictionary, false);
    }

    /**
     * @deprecated use appendProperty() instead.
     */
    public void propappend(String pid, String key, String value) throws Exception {
        appendProperty(pid, key, value);
    }

    public void setProperty(String pid, String key, String value) throws Exception {
        Configuration configuration = configurationAdmin.getConfiguration(pid);
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration PID " + pid + " doesn't exist");
        }
        Dictionary dictionary = configuration.getProperties();

        if (dictionary == null) {
            dictionary = new java.util.Properties();
        }

        dictionary.put(key, value);
        store(pid, dictionary, false);
    }

    /**
     * @deprecated use setProperty() instead.
     */
    public void propset(String pid, String key, String value) throws Exception {
        setProperty(pid, key, value);
    }

    /**
     * Store/flush a configuration PID into the configuration file.
     *
     * @param pid        the configuration PID.
     * @param properties the configuration properties.
     * @throws Exception
     */
    private void store(String pid, Dictionary properties, boolean bypassStorage) throws Exception {
        if (!bypassStorage && storage != null) {
            File storageFile = new File(storage, pid + ".cfg");
            Configuration configuration = configurationAdmin.getConfiguration(pid, null);
            if (configuration != null && configuration.getProperties() != null) {
                Object val = configuration.getProperties().get(FELIX_FILEINSTALL_FILENAME);
                try {
                    if (val instanceof URL) {
                        storageFile = new File(((URL) val).toURI());
                    }
                    if (val instanceof URI) {
                        storageFile = new File((URI) val);
                    }
                    if (val instanceof String) {
                        storageFile = new File(new URL((String) val).toURI());
                    }
                } catch (Exception e) {
                    throw (IOException) new IOException(e.getMessage()).initCause(e);
                }
            }
            Properties p = new Properties(storageFile);
            p.clear();
            for (Enumeration keys = properties.keys(); keys.hasMoreElements(); ) {
                Object key = keys.nextElement();
                if (!Constants.SERVICE_PID.equals(key)
                        && !ConfigurationAdmin.SERVICE_FACTORYPID.equals(key)
                        && !FELIX_FILEINSTALL_FILENAME.equals(key)) {
                    p.put((String) key, (String) properties.get(key));
                }
            }
            // remove "removed" properties from the file
            ArrayList<String> propertiesToRemove = new ArrayList<String>();
            for (Object key : p.keySet()) {
                if (properties.get(key) == null
                        && !Constants.SERVICE_PID.equals(key)
                        && !ConfigurationAdmin.SERVICE_FACTORYPID.equals(key)
                        && !FELIX_FILEINSTALL_FILENAME.equals(key)) {
                    propertiesToRemove.add(key.toString());
                }
            }
            for (String key : propertiesToRemove) {
                p.remove(key);
            }
            // save the cfg file
            storage.mkdirs();
            p.save();
            updateFileInstall(storageFile);
        } else {
            Configuration cfg = configurationAdmin.getConfiguration(pid, null);
            if (cfg.getProperties() == null) {
                String[] pids = parsePid(pid);
                if (pids[1] != null) {
                    cfg = configurationAdmin.createFactoryConfiguration(pids[0], null);
                }
            }
            if (cfg.getBundleLocation() != null) {
                cfg.setBundleLocation(null);
            }
            cfg.update(properties);
        }
    }
    
    /**
     * Trigger felix fileinstall to update the config so there is no delay till it polls the file
     * 
     * @param storageFile
     * @throws Exception
     */
    private void updateFileInstall(File storageFile) {
        if (artifactInstallers != null) {
            for (ArtifactInstaller installer : artifactInstallers) {
                if (installer.canHandle(storageFile)) {
                    try {
                        installer.update(storageFile);
                    } catch (Exception e) {
                        LOG.warn("Error updating config " + storageFile + " in felix fileinstall" + e.getMessage(), e);
                    }
                }
            }
        }
    }

    private String[] parsePid(String pid) {
        int n = pid.indexOf('-');
        if (n > 0) {
            String factoryPid = pid.substring(n + 1);
            pid = pid.substring(0, n);
            return new String[] { pid, factoryPid };
        } else {
            return new String[] { pid, null };
        }
    }

    void bindArtifactInstaller(ArtifactInstaller artifactInstaller) {
        this.artifactInstallers.add(artifactInstaller);
    }

    void unbindArtifactInstaller(ArtifactInstaller  artifactInstaller) {
        this.artifactInstallers.remove(artifactInstaller);
    }

}
