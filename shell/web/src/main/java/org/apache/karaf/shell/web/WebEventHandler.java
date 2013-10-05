package org.apache.karaf.shell.web;

import org.ops4j.pax.web.service.spi.WebEvent;

import java.util.Map;

public interface WebEventHandler {
    Map<Long, WebEvent> getBundleEvents();
}
