/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.karaf.diagnostic.management.internal;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.karaf.diagnostic.core.DumpDestination;
import org.apache.karaf.diagnostic.core.DumpProvider;
import org.apache.karaf.diagnostic.core.common.DirectoryDumpDestination;
import org.apache.karaf.diagnostic.core.common.ZipDumpDestination;
import org.apache.karaf.diagnostic.management.DiagnosticDumpMBean;

/**
 * Implementation of diagnostic mbean.
 */
@Component(name = "org.apache.karaf.diagonistic.mbeans.config", immediate = true)
public class DiagnosticDumpMBeanImpl extends StandardMBean implements 
    DiagnosticDumpMBean {

    private static final String OBJECT_NAME = "org.apache.karaf:type=diagnostic,name=" + System.getProperty("karaf.name");
    private ObjectName objectName;

    @Reference
    private javax.management.MBeanServer server;

    @Activate
    public void activate() throws Exception {
        this.objectName = new ObjectName(OBJECT_NAME);
        server.registerMBean(this, objectName) ;
    }

    @Deactivate
    public void deactivate() throws Exception {
        if (objectName != null) {
            server.unregisterMBean(objectName);
        }
    }

    /**
     * Registered dump providers.
     */
    @Reference(referenceInterface = DumpProvider.class, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE, policy = ReferencePolicy.DYNAMIC,
            bind = "bindProvider", unbind = "unbindProvider"
    )
    private final List<DumpProvider> providers = new CopyOnWriteArrayList<DumpProvider>();

    /**
     * Creates new diagnostic mbean.
     * 
     * @throws NotCompliantMBeanException
     */
    public DiagnosticDumpMBeanImpl() throws NotCompliantMBeanException {
        super(DiagnosticDumpMBean.class);
    }

    /**
     * Creates dump witch given name
     * 
     * @param name Name of the dump.
     */
    public void createDump(String name) throws Exception {
        createDump(false, name);
    }

    /**
     * {@inheritDoc}
     */
    public void createDump(boolean directory, String name) throws Exception {
        File target = new File(name);

        DumpDestination destination;
        if (directory) {
            destination = new DirectoryDumpDestination(target);
        } else {
            destination = new ZipDumpDestination(target);
        }

        for (DumpProvider provider : providers) {
            provider.createDump(destination);
        }

        destination.save();
    }

    void bindProvider(DumpProvider providers) {
        this.providers.add(providers);
    }

    void unbindProvider(DumpProvider provider) {
        this.providers.remove(provider);
    }

}
