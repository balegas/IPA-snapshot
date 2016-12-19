/**
-------------------------------------------------------------------

Copyright (c) 2014 SyncFree Consortium.  All Rights Reserved.

This file is provided to you under the Apache License,
Version 2.0 (the "License"); you may not use this file
except in compliance with the License.  You may obtain
a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

-------------------------------------------------------------------
**/
package indigo.impl.javaclass;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import indigo.conflitcs.GenericConflictResolutionPolicy;
import indigo.generic.GenericOperation;
import indigo.generic.GenericVariable;
import indigo.impl.javaclass.effects.AssertionPredicate;
import indigo.impl.javaclass.effects.CounterPredicate;
import indigo.impl.javaclass.effects.PreConditionPredicate;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.operations.Operation;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;
import indigo.specification.AbstractSpecification;

public class JavaClassSpecification extends AbstractSpecification {

	private final Class<?> javaClass;
	private Set<Invariant> invariants;
	private final Map<String, Set<PredicateAssignment>> dependenciesForPredicateName;
	private final Set<String> constrainedSets;

	public JavaClassSpecification(Class<?> javaClass) {
		super(javaClass.getName());
		this.javaClass = javaClass;
		this.dependenciesForPredicateName = Maps.newTreeMap();
		this.constrainedSets = Sets.newTreeSet();
		init();
	}

	@Override
	protected Set<Invariant> readInvariants() {
		if (this.invariants == null) {
			Set<Invariant> invariants = Sets.newHashSet();
			for (indigo.annotations.Invariant i : javaClass.getAnnotationsByType(indigo.annotations.Invariant.class)) {
				LogicExpression exp = new LogicExpression(i.value());
				constrainedSets.addAll(exp.getConstrainedSets());
			}

			for (indigo.annotations.Invariant i : javaClass.getAnnotationsByType(indigo.annotations.Invariant.class)) {
				Invariant ic = new JavaInvariantClause(i.value());
				LogicExpression exp = new LogicExpression(i.value());
				dependenciesForPredicateName.putAll(exp.getConstrainedSetsDependencies(constrainedSets));
				invariants.add(ic);
			}
			this.invariants = ImmutableSet.copyOf(invariants);
		}
		return invariants;
	}

	@Override
	protected Set<Operation> readOperations() {
		Set<Operation> operations = new HashSet<>();
		for (Method m : javaClass.getMethods()) {
			Set<PredicateAssignment> opEffectList = new HashSet<PredicateAssignment>();
			Set<PredicateAssignment> opPreConditionsList = new HashSet<PredicateAssignment>();
			opEffectList.addAll(CounterPredicate.listFor(m).stream().map(e -> new JavaPredicateAssignment(e))
					.collect(Collectors.toSet()));
			opEffectList.addAll(AssertionPredicate.listFor(m).stream().map(e -> new JavaPredicateAssignment(e))
					.collect(Collectors.toSet()));
			opPreConditionsList.addAll(PreConditionPredicate.listFor(m).stream()
					.map(e -> new JavaPredicateAssignment(e)).collect(Collectors.toSet()));
			// opEffectList.addAll(AssignPredicate.listFor(m));
			java.lang.reflect.Parameter[] params = m.getParameters();
			List<Parameter> methodParams = toParameters(params);
			Operation operation = new GenericOperation(m.getName(), opEffectList, methodParams, opPreConditionsList);
			operations.add(operation);
		}
		return Sets.newHashSet(operations);
	}

	private List<Parameter> toParameters(java.lang.reflect.Parameter[] params) {
		List<Parameter> genericParams = Lists.newLinkedList();
		for (java.lang.reflect.Parameter p : params) {
			String type = p.getType().getSimpleName();
			String name = p.getName();
			genericParams.add(new GenericVariable(name, type));
		}
		return genericParams;
	}

	@Override
	public Invariant newEmptyInv() {
		return new JavaInvariantClause("true");
	}

	@Override
	public ConflictResolutionPolicy getDefaultConflictResolutionPolicy() {
		return GenericConflictResolutionPolicy.getDefault();
	}

	@Override
	public Map<String, Set<PredicateAssignment>> getDependenciesForPredicate() {
		return dependenciesForPredicateName;
	}

}
