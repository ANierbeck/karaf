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
package org.apache.karaf.jaas.config;

import java.util.Properties;

/**
 * POJO for a login module.
 * It contains the class name, flags and a map of options.
 */
public interface Module {


    public String getName();

    public void setName(String name);

    public String getClassName();

    public void setClassName(String className);

    public String getFlags();

    public void setFlags(String flags);

    public Properties getOptions();

    public void setOptions(Properties options);

}
