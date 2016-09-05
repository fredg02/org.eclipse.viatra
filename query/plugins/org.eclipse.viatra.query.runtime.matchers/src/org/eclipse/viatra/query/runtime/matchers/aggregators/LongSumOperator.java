/*******************************************************************************
 * Copyright (c) 2010-2016, Gabor Bergmann, IncQueryLabs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.matchers.aggregators;

import org.eclipse.viatra.query.runtime.matchers.psystem.aggregations.AbstractMemorylessAggregationOperator;

/**
 * Incrementally computes the sum of java.lang.Long values
 * @author Gabor Bergmann
 * @since 1.4
 */
public class LongSumOperator extends AbstractMemorylessAggregationOperator<Long, Long> {
    public static LongSumOperator INSTANCE = new LongSumOperator();
    
    private LongSumOperator() {
        // Singleton, do not call.
    }

    @Override
    public String getShortDescription() {
        return "sum<Long> incrementally computes the sum of java.lang.Long values";
    }
    @Override
    public String getName() {
        return "sum<Long>";
    }
    
    @Override
    public Long createNeutral() {
        return 0L;
    }

    @Override
    public boolean isNeutral(Long result) {
        return createNeutral().equals(result);
    }

    @Override
    public Long update(Long oldResult, Long updateValue, boolean isInsertion) {
        return isInsertion ? 
                oldResult + updateValue : 
                oldResult - updateValue;
    }


}
