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
package org.apache.karaf.shell.config;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.felix.fileinstall.ArtifactInstaller;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.utils.properties.Properties;
import org.apache.karaf.shell.console.commands.ComponentAction;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class from which all commands related to the ConfigurationAdmin
 * service should derive.
 * This command retrieves a reference to the ConfigurationAdmin service before
 * calling another method to actually process the command.
 */
@Component(name = ConfigCommandSupport.ID, configurationPid = "org.apache.karaf.shell.config", componentAbstract = true)
public abstract class ConfigCommandSupport extends ComponentAction {

    private static final Logger log = LoggerFactory.getLogger(ConfigCommandSupport.class);

    public static final String ID = "org.apache.karaf.shell.confg.base";

    public static final String PROPERTY_CONFIG_PID = "ConfigCommand.PID";
    public static final String PROPERTY_CONFIG_PROPS = "ConfigCommand.Props";
    private static final String PID_FILTER = "(service.pid=%s*)";
    private static final String CONFIG_SUFFIX = ".cfg";
    private static final String FACTORY_SEPARATOR = "-";
    private static final String FILEINSTALL_FILE_NAME = "felix.fileinstall.filename";
    private static final String STORAGE_NAME = "storage";
    private static final String DEFAULT_STORAGE = System.getProperty("karaf.base") + File.separatorChar + "etc";

    protected File storageDir;
    private List<ArtifactInstaller> artifactInstallers;
    private BundleContext bundleContext;

    @Reference
    private ConfigurationAdmin configurationAdmin;

    @Property(name = "storage", label = "Storage", description = "Location where configuration is persisted.")
    private String storage;

    @Activate
    void activate(BundleContext bundleContext, Map<String, ?> options) {
        this.bundleContext = bundleContext;
        this.storage = options.containsKey(STORAGE_NAME) ? (String) options.get(STORAGE_NAME) : DEFAULT_STORAGE;
        this.storageDir = new File(storage);
    }


    public Object doExecute() throws Exception {
        doExecute(configurationAdmin);
        return null;
    }

    protected Dictionary getEditedProps() throws Exception {
        return (Dictionary) this.getSession().get(PROPERTY_CONFIG_PROPS);
    }

    protected ConfigurationAdmin getConfigurationAdmin() {
        ServiceReference ref = getBundleContext().getServiceReference(ConfigurationAdmin.class.getName());
        if (ref == null) {
            return null;
        }
        try {
            ConfigurationAdmin admin = (ConfigurationAdmin) getBundleContext().getService(ref);
            if (admin == null) {
                return null;
            } else {
                return admin;
            }
        } finally {
            getBundleContext().ungetService(ref);
        }
    }

    protected abstract void doExecute(ConfigurationAdmin admin) throws Exception;

    /**
     * <p>
     * Returns the Configuration object of the given (felix fileinstall) file name.
     * </p>
     *
     * @param fileName
     * @return
     */
    public Configuration findConfigurationByFileName(ConfigurationAdmin admin, String fileName) throws IOException, InvalidSyntaxException {
        if (fileName != null) {
            String factoryPid = fileName;
            if (fileName.contains(FACTORY_SEPARATOR)) {
                factoryPid = fileName.substring(0, fileName.lastIndexOf(FACTORY_SEPARATOR));
            }
            Configuration[] configurations = admin.listConfigurations(String.format(PID_FILTER, factoryPid));
            if (configurations != null) {
                for (Configuration configuration : configurations) {
                    Dictionary dictionary = configuration.getProperties();
                    if (dictionary != null) {
                        String fileInstallFileName = (String) dictionary.get(FILEINSTALL_FILE_NAME);
                        int indexOfFileNameStart = fileInstallFileName.lastIndexOf("/");
                        int indexOfFileNameEnd = fileInstallFileName.lastIndexOf(CONFIG_SUFFIX);
                        String relativeFileName = fileInstallFileName.substring(indexOfFileNameStart + 1, indexOfFileNameEnd);
                        if (fileName.equals(relativeFileName)) {
                            return configuration;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Saves config to storageDir or ConfigurationAdmin.
     *
     * @param admin
     * @param pid
     * @param props
     * @param bypassStorage
     * @throws IOException
     */
    protected void update(ConfigurationAdmin admin, String pid, Dictionary props, boolean bypassStorage) throws IOException {
        if (!bypassStorage && storageDir != null) {
            persistConfiguration(admin, pid, props);
        } else {
            updateConfiguration(admin, pid, props);
        }
    }

    /**
     * Persists configuration to storageDir.
     *
     * @param admin
     * @param pid
     * @param props
     * @throws IOException
     */
    protected void persistConfiguration(ConfigurationAdmin admin, String pid, Dictionary props) throws IOException {
        File storageFile = new File(storageDir, pid + ".cfg");
        Configuration cfg = admin.getConfiguration(pid, null);
        if (cfg != null && cfg.getProperties() != null) {
            Object val = cfg.getProperties().get(FILEINSTALL_FILE_NAME);
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
        for (Enumeration keys = props.keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            if (!Constants.SERVICE_PID.equals(key)
                    && !ConfigurationAdmin.SERVICE_FACTORYPID.equals(key)
                    && !FILEINSTALL_FILE_NAME.equals(key)) {
                p.put((String) key, (String) props.get(key));
            }
        }
        // remove "removed" properties from the file
        ArrayList<String> propertiesToRemove = new ArrayList<String>();
        for (Object key : p.keySet()) {
            if (props.get(key) == null
                    && !Constants.SERVICE_PID.equals(key)
                    && !ConfigurationAdmin.SERVICE_FACTORYPID.equals(key)
                    && !FILEINSTALL_FILE_NAME.equals(key)) {
                propertiesToRemove.add(key.toString());
            }
        }
        for (String key : propertiesToRemove) {
            p.remove(key);
        }
        // save the cfg file
        storageDir.mkdirs();
        p.save();
        updateFileInstall(storageFile);
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
                        log.warn("Error updating config " + storageFile + " in felix fileinstall" + e.getMessage(), e);
                    }
                }
            }
        }
    }

    /**
     * Updates the configuration to the {@link ConfigurationAdmin} service.
     *
     * @param admin
     * @param pid
     * @param props
     * @throws IOException
     */
    public void updateConfiguration(ConfigurationAdmin admin, String pid, Dictionary props) throws IOException {
        Configuration cfg = admin.getConfiguration(pid, null);
        if (cfg.getProperties() == null) {
            String[] pids = parsePid(pid);
            if (pids[1] != null) {
                cfg = admin.createFactoryConfiguration(pids[0], null);
            }
        }
        if (cfg.getBundleLocation() != null) {
            cfg.setBundleLocation(null);
        }
        cfg.update(props);
    }

    protected String[] parsePid(String pid) {
        int n = pid.indexOf('-');
        if (n > 0) {
            String factoryPid = pid.substring(n + 1);
            pid = pid.substring(0, n);
            return new String[]{pid, factoryPid};
        } else {
            return new String[]{pid, null};
        }
    }

    protected void deleteStorage(String pid) throws Exception {
        File cfgFile = new File(storageDir, pid + ".cfg");
        cfgFile.delete();
    }

    public File getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(File storageDir) {
        this.storageDir = storageDir;
    }
    
    public void setArtifactInstallers(List<ArtifactInstaller> artifactInstallers) {
        this.artifactInstallers = artifactInstallers;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    //Just for testing
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }
}
