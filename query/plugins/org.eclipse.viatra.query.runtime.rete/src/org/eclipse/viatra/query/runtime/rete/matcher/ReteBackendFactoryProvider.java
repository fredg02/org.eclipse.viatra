/*******************************************************************************
 * Copyright (c) 2010-2018, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.rete.matcher;

import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactoryProvider;

/**
 * @since 2.0
 *
 */
public class ReteBackendFactoryProvider implements IQueryBackendFactoryProvider {

    @Override
    public IQueryBackendFactory getFactory() {
        return ReteBackendFactory.INSTANCE;
    }

    @Override
    public boolean isSystemDefaultEngine() {
        return true;
    }

    @Override
    public boolean isSystemDefaultCachingBackend() {
        return true;
    }

}
