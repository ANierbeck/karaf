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
package org.apache.karaf.jaas.jasypt.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.jaas.modules.Encryption;
import org.apache.karaf.jaas.modules.EncryptionService;

@Component(name = "org.apache.karaf.jaas.jasypt", description = "Jasypt Encryption Service")
@Service(EncryptionService.class)
public class JasyptEncryptionService implements EncryptionService {

    @Activate
    void activate() {
    }

    @Deactivate
    void deactivate() {
    }

    public Encryption createEncryption(Map<String, String> params) throws IllegalArgumentException {
        return new JasyptEncryption(params);
    }
}
