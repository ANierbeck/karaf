/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.shell.console.help;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jline.Terminal;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.CommandSession;
import org.apache.karaf.shell.console.HelpProvider;
import org.apache.karaf.shell.console.SubShell;
import org.fusesource.jansi.Ansi;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import static org.apache.felix.gogo.commands.basic.DefaultActionPreparator.printFormatted;

@Component(name = "org.apache.karaf.shell.console.help.subshell", description = "Karaf Shell Console Subshell Help Provider", immediate = true)
@Service(HelpProvider.class)
@org.apache.felix.scr.annotations.Properties(
        @Property(name = "ranking", value = "-10")
)
public class SubShellHelpProvider implements HelpProvider {

    @Reference(referenceInterface = SubShell.class,
            policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind = "bindSubshell", unbind = "unbindSubShell"
    )
    private final Map<String, SubShell> subshells = new HashMap<String, SubShell>();

    @Activate
    public void start() {
    }

    @Deactivate
    public void stop() {
    }

    void bindSubshell(SubShell subShell) {
        subshells.put(subShell.getName(), subShell);
    }

    void unbindSubShell(SubShell subShell) {
        subshells.remove(subShell.getName());
    }

    public String getHelp(CommandSession session, String path) {
        if (path.indexOf('|') > 0) {
            if (path.startsWith("subshell|")) {
                path = path.substring("subshell|".length());
            } else {
                return null;
            }
        }
            if (subshells.containsKey(path)) {
                SubShell subShell = subshells.get(path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                printSubShellHelp(session, FrameworkUtil.getBundle(subShell.getClass()), subShell, new PrintStream(baos, true));
                return baos.toString();
            }
        return null;
    }

    private void printSubShellHelp(CommandSession session, Bundle bundle, SubShell subShell, PrintStream out) {
        Terminal term = session != null ? (Terminal) session.get(".jline.terminal") : null;
        out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("SUBSHELL").a(Ansi.Attribute.RESET));
        out.print("        ");
        if (subShell.getName() != null) {
            out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a(subShell.getName()).a(Ansi.Attribute.RESET));
            out.println();
        }
        out.print("\t");
        out.println(subShell.getDescription());
        out.println();
        if (subShell.getDetailedDescription() != null) {
            out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).a("DETAILS").a(Ansi.Attribute.RESET));
            String desc = loadDescription(bundle, subShell.getDetailedDescription());
            while (desc.endsWith("\n")) {
                desc = desc.substring(0, desc.length()  -1);
            }
            printFormatted("        ", desc, term != null ? term.getWidth() : 80, out);
        }
        out.println();
        out.println("${command-list|" + subShell.getName() + ":}");
    }

    protected String loadDescription(Bundle bundle, String desc) {
        if (desc.startsWith("classpath:")) {
            URL url = bundle.getResource(desc.substring("classpath:".length()));
            if (url == null) {
                desc = "Unable to load description from " + desc;
            } else {
                InputStream is = null;
                try {
                    is = url.openStream();
                    Reader r = new InputStreamReader(is);
                    StringWriter sw = new StringWriter();
                    int c;
                    while ((c = r.read()) != -1) {
                        sw.append((char) c);
                    }
                    desc = sw.toString();
                } catch (IOException e) {
                    desc = "Unable to load description from " + desc;
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
        return desc;
    }

}
