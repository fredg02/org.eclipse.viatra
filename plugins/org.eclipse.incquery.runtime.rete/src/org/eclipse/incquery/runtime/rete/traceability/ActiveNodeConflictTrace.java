/*******************************************************************************
 * Copyright (c) 2010-2014, Bergmann Gabor, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Bergmann Gabor - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.traceability;

import org.eclipse.incquery.runtime.rete.recipes.ReteNodeRecipe;

public class ActiveNodeConflictTrace extends RecipeTraceInfo { // TODO implement PatternTraceInfo
	RecipeTraceInfo inactiveRecipeTrace;		
	public ActiveNodeConflictTrace(ReteNodeRecipe recipe,
			RecipeTraceInfo parentRecipeTrace,
			RecipeTraceInfo inactiveRecipeTrace) {
		super(recipe, parentRecipeTrace);
		this.inactiveRecipeTrace = inactiveRecipeTrace;
	}
	public RecipeTraceInfo getInactiveRecipeTrace() {
		return inactiveRecipeTrace;
	}
}