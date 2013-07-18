package org.eclipse.incquery.examples.uml.evm;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.log4j.Level;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.incquery.examples.uml.evm.queries.OnlyInheritedOperationsMatch;
import org.eclipse.incquery.examples.uml.evm.queries.OnlyInheritedOperationsMatcher;
import org.eclipse.incquery.examples.uml.evm.queries.PossibleSuperClassMatch;
import org.eclipse.incquery.examples.uml.evm.queries.PossibleSuperClassMatcher;
import org.eclipse.incquery.examples.uml.evm.queries.SuperClassMatcher;
import org.eclipse.incquery.examples.uml.evm.queries.util.OnlyInheritedOperationsProcessor;
import org.eclipse.incquery.examples.uml.evm.queries.util.PossibleSuperClassProcessor;
import org.eclipse.incquery.examples.uml.evm.queries.util.SuperClassProcessor;
import org.eclipse.incquery.runtime.api.AdvancedIncQueryEngine;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IQuerySpecification;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Context;
import org.eclipse.incquery.runtime.evm.api.ExecutionSchema;
import org.eclipse.incquery.runtime.evm.api.Job;
import org.eclipse.incquery.runtime.evm.api.RuleEngine;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.event.EventFilter;
import org.eclipse.incquery.runtime.evm.specific.ExecutionSchemas;
import org.eclipse.incquery.runtime.evm.specific.Jobs;
import org.eclipse.incquery.runtime.evm.specific.RuleEngines;
import org.eclipse.incquery.runtime.evm.specific.Rules;
import org.eclipse.incquery.runtime.evm.specific.Schedulers;
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryActivationStateEnum;
import org.eclipse.incquery.runtime.evm.specific.lifecycle.DefaultActivationLifeCycle;
import org.eclipse.incquery.runtime.evm.specific.scheduler.UpdateCompleteBasedScheduler.UpdateCompleteBasedSchedulerFactory;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.UMLFactory;
import org.junit.Test;

import com.google.common.collect.Sets;

public class UMLexampleForEVM {

    @Test
    public void RuleEngineExample() {

        ResourceSet resourceSet = new ResourceSetImpl();
        URI fileURI = URI.createPlatformPluginURI("org.eclipse.incquery.examples.uml.evm/testmodels/Testmodel.uml",
                false);
        resourceSet.getResource(fileURI, true);

        try {
            // create IncQueryEngine for the resource set
            IncQueryEngine engine = AdvancedIncQueryEngine.createUnmanagedEngine(resourceSet);
            // create rule engine over IncQueryEngine
            RuleEngine ruleEngine = RuleEngines.createIncQueryRuleEngine(engine);
            // set logger level to debug to see activation life-cycle events
            ruleEngine.getLogger().setLevel(Level.DEBUG);
            // create context for execution
            Context context = Context.create();

            // prepare rule specifications
            RuleSpecification<PossibleSuperClassMatch> createGeneralization = getCreateGeneralizationRule();
            RuleSpecification<OnlyInheritedOperationsMatch> createOperation = getCreateOperationRule();

            // add rule specifications to engine
            ruleEngine.addRule(createGeneralization);
            testFilteredRules(engine, ruleEngine, createGeneralization);

            ruleEngine.addRule(createOperation);

            // check rule applicability
            Set<Activation<PossibleSuperClassMatch>> createClassesActivations = ruleEngine.getActivations(createGeneralization);
            if (!createClassesActivations.isEmpty()) {
                // fire activation of a given rule
                createClassesActivations.iterator().next().fire(context);
            }

            // check for any applicable rules
            while (!ruleEngine.getConflictingActivations().isEmpty()) {
                // fire next activation as long as possible
                ruleEngine.getNextActivation().fire(context);
            }

            // rules that are no longer needed can be removed
            ruleEngine.removeRule(createGeneralization);

            // rule engine manages the activations of the added rules until
            // disposed
            ruleEngine.dispose();

        } catch (IncQueryException e) {
            e.printStackTrace();
        }

    }

    private void testFilteredRules(IncQueryEngine engine, RuleEngine ruleEngine,
            RuleSpecification<PossibleSuperClassMatch> createGeneralization) throws IncQueryException {
        assertFalse(ruleEngine.addRule(createGeneralization));
        
        PossibleSuperClassMatcher matcher = PossibleSuperClassMatcher.on(engine);
        PossibleSuperClassMatch emptyMatch = matcher.newMatch(null, null);
        final PossibleSuperClassMatch arbitraryMatch = matcher.getOneArbitraryMatch();
        EventFilter<PossibleSuperClassMatch> emptyFilter1 = Rules.newMatchFilter(emptyMatch);
        EventFilter<PossibleSuperClassMatch> emptyFilter2 = Rules.newMatchFilter(emptyMatch);
        EventFilter<PossibleSuperClassMatch> filter = Rules.newMatchFilter(arbitraryMatch);
        EventFilter<PossibleSuperClassMatch> filter2 = Rules.newMatchFilter(arbitraryMatch);
        
        EventFilter<IPatternMatch> eventFilter = new EventFilter<IPatternMatch>() {
            
            Class cl = arbitraryMatch.getCl();
            
            @Override
            public boolean isProcessable(IPatternMatch eventAtom) {
                return eventAtom.get("cl").equals(cl);
            }
        };
        
        ruleEngine.addRule(createGeneralization, false, eventFilter);
        assertFalse(ruleEngine.getActivations(createGeneralization, eventFilter).isEmpty());
        
        assertFalse(ruleEngine.addRule(createGeneralization, false, emptyFilter1));
        assertFalse(ruleEngine.addRule(createGeneralization, false, emptyFilter2));
        assertTrue(ruleEngine.addRule(createGeneralization, false, filter));
        assertFalse(ruleEngine.addRule(createGeneralization, false, filter2));
    }

    @Test
    public void ExecutionSchemaExample() {

        ResourceSet resourceSet = new ResourceSetImpl();
        URI fileURI = URI.createPlatformPluginURI("org.eclipse.incquery.examples.uml.evm/testmodels/Testmodel.uml",
                false);
        resourceSet.getResource(fileURI, true);

        try {
            // create IncQueryEngine for the resource set
            IncQueryEngine engine = AdvancedIncQueryEngine.createUnmanagedEngine(resourceSet);
            // use IQBase update callback for scheduling execution
            UpdateCompleteBasedSchedulerFactory schedulerFactory = Schedulers.getIQEngineSchedulerFactory(engine);
            // create execution schema over IncQueryEngine
            ExecutionSchema executionSchema = ExecutionSchemas.createIncQueryExecutionSchema(engine, schedulerFactory);
            // set logger level to debug to see activation life-cycle events
            executionSchema.getLogger().setLevel(Level.DEBUG);

            
            // prepare rule specifications
            RuleSpecification<PossibleSuperClassMatch> createGeneralization = getCreateGeneralizationRule();
            RuleSpecification<OnlyInheritedOperationsMatch> createOperation = getCreateOperationRule();

            // add rule specifications to engine
            executionSchema.addRule(createGeneralization);
            testFilteredRules(engine, executionSchema, createGeneralization);

            executionSchema.addRule(createOperation);

            
            // execution schema waits for a scheduling to fire activations
            // we trigger this by removing one generalization at random
            SuperClassMatcher.querySpecification().getMatcher(engine).forOneArbitraryMatch(new SuperClassProcessor() {

                @Override
                public void process(Class sub, Class sup) {
                    sub.getGeneralizations().remove(0);
                }
            });

            // rules that are no longer needed can be removed
            executionSchema.removeRule(createGeneralization);

            // execution schema manages and fires the activations of the added
            // rules until disposed
            executionSchema.dispose();

        } catch (IncQueryException e) {
            e.printStackTrace();
        }

    }

    private RuleSpecification<PossibleSuperClassMatch> getCreateGeneralizationRule() throws IncQueryException {
        // the job specifies what to do when an activation is fired in the given
        // state
        Job<PossibleSuperClassMatch> job = Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED, new PossibleSuperClassProcessor() {
            @Override
            public void process(Class cl, Class sup) {
                System.out.println("Found cl " + cl + " without superclass");
                Generalization generalization = UMLFactory.eINSTANCE.createGeneralization();
                generalization.setGeneral(sup);
                generalization.setSpecific(cl);
            }
        });
        // the life-cycle determines how events affect the state of activations
        DefaultActivationLifeCycle lifecycle = DefaultActivationLifeCycle.DEFAULT_NO_UPDATE_AND_DISAPPEAR;
        // the factory is used to initialize the matcher for the precondition
        IQuerySpecification<PossibleSuperClassMatcher> factory = PossibleSuperClassMatcher.querySpecification();
        // the rule specification is a model-independent definition that can be
        // used to instantiate a rule
        RuleSpecification<PossibleSuperClassMatch> spec = Rules.newMatcherRuleSpecification(factory, lifecycle, Sets.newHashSet(job));
        return spec;
    }

    private RuleSpecification<OnlyInheritedOperationsMatch> getCreateOperationRule() throws IncQueryException {
        Job<OnlyInheritedOperationsMatch> job = Jobs.newStatelessJob(IncQueryActivationStateEnum.APPEARED, new OnlyInheritedOperationsProcessor() {
            @Override
            public void process(Class cl) {
                System.out.println("Found class " + cl + " without operation");
                Operation operation = UMLFactory.eINSTANCE.createOperation();
                operation.setName("newOp");
                operation.setClass_(cl);
            }
        });
        DefaultActivationLifeCycle lifecycle = DefaultActivationLifeCycle.DEFAULT_NO_UPDATE_AND_DISAPPEAR;
        IQuerySpecification<OnlyInheritedOperationsMatcher> factory = OnlyInheritedOperationsMatcher.querySpecification();
        RuleSpecification<OnlyInheritedOperationsMatch> spec = Rules.newMatcherRuleSpecification(factory, lifecycle, Sets.newHashSet(job));
        return spec;
    }

    
    
}
