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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Set;

import org.apache.felix.gogo.runtime.CommandSessionImpl;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.threadio.ThreadIO;
import org.apache.karaf.shell.console.HelpProvider;

@Component(name = "org.apache.karaf.shell.console.help.single", description = "Karaf Shell Console Single Command Help Provider", immediate = true)
@Service(HelpProvider.class)
@org.apache.felix.scr.annotations.Properties(
        @Property(name = "ranking", value = "-10")
)
public class SingleCommandHelpProvider implements HelpProvider {

    @Reference
    private ThreadIO io;

    @Activate
    void activate(){
    }

    @Deactivate
    void deactivate(){
    }

    public void setIo(ThreadIO io) {
        this.io = io;
    }

    public String getHelp(CommandSession session, String path) {
        if (path.indexOf('|') > 0) {
            if (path.startsWith("command|")) {
                path = path.substring("command|".length());
            } else {
                return null;
            }
        }
        Set<String> names = (Set<String>) session.get(CommandSessionImpl.COMMANDS);
        if (path != null && names.contains(path)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            io.setStreams(new ByteArrayInputStream(new byte[0]), new PrintStream(baos, true), new PrintStream(baos, true));
            try {
                session.execute(path + " --help");
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                io.close();
            }
            return baos.toString();
        }
        return null;
    }
}
