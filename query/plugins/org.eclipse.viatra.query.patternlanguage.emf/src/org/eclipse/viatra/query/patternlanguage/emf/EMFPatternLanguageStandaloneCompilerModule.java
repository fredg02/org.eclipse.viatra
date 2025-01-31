/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf;

import org.eclipse.viatra.query.patternlanguage.emf.util.EagerBatchLinkableResource;
import org.eclipse.xtext.resource.XtextResource;

/**
 * @author Zoltan Ujhelyi
 * @since 2.0
 *
 */
public class EMFPatternLanguageStandaloneCompilerModule extends EMFPatternLanguageRuntimeModule {

    @Override
    public Class<? extends XtextResource> bindXtextResource() {
        return EagerBatchLinkableResource.class;
    }
}
