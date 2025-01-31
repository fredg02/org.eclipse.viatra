/*******************************************************************************
 * Copyright (c) 2010-2017, Zoltan Ujhelyi, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.transformation;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * Utility class with extensions for writing transformations in Xtend code. It can be used by adding an extra import
 * declaration to the file header:
 * 
 * <code>import static extension org.eclipse.viatra.transformation.runtime.emf.transformation.TransformationExtensions.*</code>
 * 
 * @since 2.0
 */
public class TransformationExtensions {

    private TransformationExtensions() {/* Utility class constructor */}

    /**
     * Provides an operator for parameter mapping by name using the following syntax: "«name»" -> «value» in Xtend code
     * expecting a Map Entry.
     */
    public static Entry<String, Object> operator_mappedTo(String key, Object value) {
        return new SimpleEntry<String, Object>(key, value);
    }
}
