/*******************************************************************************
 * Copyright (c) 2010-2015, Abel Hegedus, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *   Zoltan Ujhelyi - lazy loading support
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactoryProvider;
import org.eclipse.viatra.query.runtime.util.ViatraQueryLoggingUtil;

/**
 * @author Abel Hegedus
 *
 */
public class ExtensionBasedSystemDefaultBackendLoader {

    private static final String EXTENSION_ID = "org.eclipse.viatra.query.runtime.querybackend";
    private static final ExtensionBasedSystemDefaultBackendLoader INSTANCE = new ExtensionBasedSystemDefaultBackendLoader();
    
    public static ExtensionBasedSystemDefaultBackendLoader instance() {
        return INSTANCE;
    }

    public void loadKnownBackends() {
        IQueryBackendFactory defaultBackend = null;
        IQueryBackendFactory defaultCachingBackend = null;
        IQueryBackendFactory defaultSearchBackend = null;
        final IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
        for (IConfigurationElement e : config) {
            try {
                IQueryBackendFactoryProvider provider = (IQueryBackendFactoryProvider) e
                        .createExecutableExtension("provider");
                if (provider.isSystemDefaultEngine()) {
                    defaultBackend = provider.getFactory();
                }
                if (provider.isSystemDefaultCachingBackend()) {
                    defaultCachingBackend = provider.getFactory();
                }
                if (provider.isSystemDefaultSearchBackend()) {
                    defaultSearchBackend = provider.getFactory();
                }
                
            } catch (CoreException ex) {
                // In case errors try to continue with the next one
                ViatraQueryLoggingUtil.getLogger(getClass()).error(
                        String.format("Error while initializing backend %s from plugin %s.",
                                e.getAttribute("backend"), e.getContributor().getName()), ex);
            }
        }
        ViatraQueryEngineOptions.setSystemDefaultBackends(defaultBackend, defaultCachingBackend, defaultSearchBackend);
    }
    
}
