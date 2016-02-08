/*******************************************************************************
 * Copyright (c) 2010-2013, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.evm.specific;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.specific.job.EventAtomEditingDomainProvider;
import org.eclipse.incquery.runtime.evm.specific.job.RecordingJob;

/**
 * Provides static methods acting on or generating a {@link Job}.
 *
 * @author Abel Hegedus
 *
 */
public final class TransactionalJobs {

    /**
     *
     */
    private TransactionalJobs() {
    }

    public static <Match extends IPatternMatch> EventAtomEditingDomainProvider<Match> createMatchBasedEditingDomainProvider() {
        return new EventAtomEditingDomainProvider<Match>() {

            @Override
            public EditingDomain findEditingDomain(final Activation<? extends Match> activation, final Context context) {
                final Match match = activation.getAtom();
                final int arity = match.parameterNames().size();
                if (arity > 0) {
                    for (int i = 0; i < arity; i++) {
                        if (match.get(i) instanceof EObject) {
                            final EObject eo = (EObject) match.get(i);
                            return AdapterFactoryEditingDomain.getEditingDomainFor(eo);
                        }
                    }
                }
                return null;
            }
        };
    }

    /**
     * Creates a {@link RecordingJob} decorating the given job. A recording job attempts to find the transactional
     * editing domain from the context and wraps the execution inside a command, that is accessible from the context
     * afterwards.
     *
     * @param job
     */
    public static <EventAtom> Job<EventAtom> newRecordingJob(final Job<EventAtom> job) {
        return new RecordingJob<EventAtom>(job);
    }

    /**
     * Creates a {@link RecordingJob} decorating the given job. A recording job attempts to find the transactional
     * editing domain using the given provider and wraps the execution inside a command, that is accessible from the
     * context afterwards.
     *
     * @param job
     */
    public static <EventAtom> Job<EventAtom> newRecordingJob(final Job<EventAtom> job,
            final EventAtomEditingDomainProvider<EventAtom> provider) {
        return new RecordingJob<EventAtom>(job, provider);
    }

    /**
     * Creates a {@link RecordingJob} decorating the given job. A recording job attempts to find the transactional
     * editing domain for the match in the event atom and wraps the execution inside a command, that is accessible from
     * the context afterwards.
     *
     * @param job
     */
    public static <EventAtom extends IPatternMatch> Job<EventAtom> newRecordingJobForMatchActivation(
            final Job<EventAtom> job) {
        final EventAtomEditingDomainProvider<EventAtom> provider = createMatchBasedEditingDomainProvider();
        return new RecordingJob<EventAtom>(job, provider);
    }

}
