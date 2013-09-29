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
package org.apache.karaf.management.mbeans.packages.internal;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.karaf.management.mbeans.packages.PackagesMBean;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Packages MBean implementation.
 */
@Component(name = "org.apache.karaf.managment.mbeans.packages", immediate = true)
public class PackagesMBeanImpl extends StandardMBean implements PackagesMBean {
    private static final String OBJECT_NAME = "org.apache.karaf:type=packages,name=" + System.getProperty("karaf.name");

    @Reference
    private MBeanServer mBeanServer;
    private BundleContext bundleContext;

    public PackagesMBeanImpl() throws NotCompliantMBeanException {
        super(PackagesMBean.class);
    }

    @Activate
    public void activate(BundleContext bundleContext) throws Exception {
        this.bundleContext = bundleContext;
        mBeanServer.registerMBean(this, new ObjectName(OBJECT_NAME));
    }


    @Deactivate
    public void deactivate() throws Exception {
        mBeanServer.unregisterMBean(new ObjectName(OBJECT_NAME));
    }

    public List<String> getExports() throws Exception {
        return getExports(-1);
    }

    public List<String> getExports(long bundleId) throws Exception {
        List<String> exportPackages = new ArrayList<String>();
        ServiceReference ref = bundleContext.getServiceReference(PackageAdmin.class.getName());
        if (ref == null) {
            throw new IllegalStateException("PackageAdmin is not available");
        }
        PackageAdmin packageAdmin = (PackageAdmin) bundleContext.getService(ref);
        if (packageAdmin == null) {
            throw new IllegalStateException("PackageAdmin is not available");
        }

        Bundle[] bundles;
        if (bundleId >= 0) {
            bundles = new Bundle[]{ bundleContext.getBundle(bundleId) };
        } else {
            bundles = bundleContext.getBundles();
        }

        for (Bundle bundle : bundles) {
            ExportedPackage[] packages = packageAdmin.getExportedPackages(bundle);
            if (packages != null) {
                for (ExportedPackage exportedPackage : packages) {
                    exportPackages.add(exportedPackage.getName());
                }
            }
        }

        return exportPackages;
    }

    public List<String> getImports() throws Exception {
        return getImports(-1);
    }

    public List<String> getImports(long bundleId) throws Exception {
        List<String> importPackages = new ArrayList<String>();
        ServiceReference ref = bundleContext.getServiceReference(PackageAdmin.class.getName());
        if (ref == null) {
            throw new IllegalStateException("PackageAdmin is not available");
        }
        PackageAdmin packageAdmin = (PackageAdmin) bundleContext.getService(ref);
        if (packageAdmin == null) {
            throw new IllegalStateException("PackageAdmin is not available");
        }

        ExportedPackage[] exportedPackages;
        if (bundleId >= 0) {
            exportedPackages = packageAdmin.getExportedPackages(bundleContext.getBundle(bundleId));
        } else {
            exportedPackages = packageAdmin.getExportedPackages((Bundle) null);
        }
        if (exportedPackages != null) {
            for (ExportedPackage exportedPackage : exportedPackages) {
                Bundle[] bundles = exportedPackage.getImportingBundles();
                if (bundles != null && bundles.length > 0) {
                    importPackages.add(exportedPackage.getName());
                }
            }
        }

        return importPackages;
    }

    /**
     * @deprecated use getExports() instead.
     */
    public List<String> exportedPackages() throws Exception {
        return getExports();
    }

    /**
     * @deprecated use getExports()
     */
    public List<String> exportedPackages(long bundleId) throws Exception {
        return getExports(bundleId);
    }

    /**
     * @deprecated use getImports() instead.
     */
    public List<String> importedPackages() throws Exception {
        return getImports();
    }

    /**
     * @deprecated use getImports() instead.
     */
    public List<String> importedPackages(long bundleId) throws Exception {
        return getImports(bundleId);
    }

    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

}
