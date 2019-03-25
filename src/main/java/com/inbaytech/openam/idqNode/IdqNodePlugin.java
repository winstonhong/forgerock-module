package com.inbaytech.openam.idqNode;

import java.util.Collections;
import java.util.Map;

import org.forgerock.openam.auth.node.api.AbstractNodeAmPlugin;
import org.forgerock.openam.auth.node.api.Node;
import org.forgerock.openam.plugins.PluginException;

/**
  Copyright 2019 inBay Technologies Inc.

  The contents of this file are subject to the terms of the Common Development and
  Distribution License. You may not use this file except in compliance with the License.
  See the License for the specific language governing permission and limitations under the License.
 
  A copy of the License should have been provided in the LICENSE file in the root of this repository.
  If one was not, then one may be requested by contacting inBay Technologies: https://www.inbaytech.com/contact-us
 */

public class IdqNodePlugin extends AbstractNodeAmPlugin {

    /**
     * Specify the Map of list of node classes that the plugin is providing. These will then be installed and
     *  registered at the appropriate times in plugin lifecycle.
     *
     * @return The list of node classes.
     */
    @Override
    protected Map<String, Iterable<? extends Class<? extends Node>>> getNodesByVersion() {
        return Collections.singletonMap(this.getClass().getPackage().getImplementationVersion(), Collections.singletonList(IdqNode.class));
    }

    /**
     * Handle plugin installation. This method will only be called once, on first AM startup once the plugin
     * is included in the classpath. The {@link #onStartup()} method will be called after this one.
     */
    @Override
    public void onInstall() throws PluginException {
        super.onInstall();
    }

    /**
     * Handle plugin startup. This method will be called every time AM starts, after {@link #onInstall()},
     * {@link #onAmUpgrade(String, String)} and {@link #upgrade(String)} have been called (if relevant).
     *
     * @param startupType The type of startup that is taking place.
     */
    @Override
    public void onStartup() throws PluginException {
        super.onStartup();
    }

    /**
     * This method will be called when the version returned by {@link #getPluginVersion()} is higher than the
     * version already installed. This method will be called before the {@link #onStartup()} method.
     *
     * @param fromVersion The old version of the plugin that has been installed.
     */
    @Override
    public void upgrade(String fromVersion) throws PluginException {
        super.upgrade(fromVersion);
    }

    /**
     * The plugin version. This must be in semver (semantic version) format.
     *
     * @return The version of the plugin.
     * @see <a href="https://www.osgi.org/wp-content/uploads/SemanticVersioning.pdf">Semantic Versioning</a>
     */
    @Override
    public String getPluginVersion() {
        return this.getClass().getPackage().getImplementationVersion();
    }
}