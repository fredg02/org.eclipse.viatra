/*******************************************************************************
 * Copyright (c) 2004-2013, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.transformation.runtime.emf.rules.batch;

import org.eclipse.viatra.query.runtime.api.IMatchProcessor;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;

public class BatchTransformationRuleFactory {

	public class BatchTransformationRuleBuilder<Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> {
		
		private IQuerySpecification<Matcher> fPrecondition;
		private IMatchProcessor<Match> fAction;
		private String fName = "";
		
		/**
		 * Sets the user-understandable name of the rule. Should be unique if set.
		 */
		public BatchTransformationRuleBuilder<Match, Matcher> name(String name) {
			this.fName = name;
			return this;
		}

		/**
		 * Sets the precondition query of the rule.
		 */
		public BatchTransformationRuleBuilder<Match, Matcher> precondition(IQuerySpecification<Matcher> precondition) {
			this.fPrecondition = precondition;
			return this;
		}

		/**
		 * Sets the model manipulation actions of the rule.
		 */
		public BatchTransformationRuleBuilder<Match, Matcher> action(IMatchProcessor<Match> action) {
			this.fAction = action;
			return this;
		}


		public BatchTransformationRule<Match, Matcher> build() {
		    return new BatchTransformationRule<Match, Matcher>(fName, fPrecondition,
	                BatchTransformationRule.STATELESS_RULE_LIFECYCLE, fAction);
		}
		
		public BatchTransformationRule<Match, Matcher> buildStateful() {
		    return new BatchTransformationRule<Match, Matcher>(fName, fPrecondition,
	                BatchTransformationRule.STATEFUL_RULE_LIFECYCLE, fAction);
		}
	}
	
	public <Match extends IPatternMatch, Matcher extends ViatraQueryMatcher<Match>> BatchTransformationRuleBuilder<Match, Matcher> createRule() {
		return new BatchTransformationRuleBuilder<Match, Matcher>();
	}

}
