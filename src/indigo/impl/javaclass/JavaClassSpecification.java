package indigo.impl.javaclass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import indigo.conflitcs.GenericConflictResolutionPolicy;
import indigo.generic.GenericOperation;
import indigo.generic.GenericVariable;
import indigo.impl.javaclass.effects.AssertionPredicate;
import indigo.impl.javaclass.effects.CounterPredicate;
import indigo.impl.javaclass.effects.JavaEffect;
import indigo.interfaces.interactive.ConflictResolutionPolicy;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.operations.Operation;
import indigo.interfaces.operations.Parameter;
import indigo.specification.AbstractSpecification;

public class JavaClassSpecification extends AbstractSpecification {

	private final Class<?> javaClass;

	public JavaClassSpecification(Class<?> javaClass) {
		super(javaClass.getName());
		this.javaClass = javaClass;
		init();
	}

	@Override
	protected Set<Invariant> readInvariants() {
		Set<Invariant> invariants = Sets.newHashSet();
		for (indigo.annotations.Invariant i : javaClass.getAnnotationsByType(indigo.annotations.Invariant.class)) {
			Invariant ic = new JavaInvariantClause(i.value());
			invariants.add(ic);
		}
		return ImmutableSet.copyOf(invariants);
	}

	@Override
	protected Set<Operation> readOperations() {
		Set<Operation> operations = new HashSet<>();
		for (Method m : javaClass.getMethods()) {
			ArrayList<JavaEffect> opEffectList = new ArrayList<JavaEffect>();
			opEffectList.addAll(CounterPredicate.listFor(m));
			opEffectList.addAll(AssertionPredicate.listFor(m));
			// opEffectList.addAll(AssignPredicate.listFor(m));
			java.lang.reflect.Parameter[] params = m.getParameters();
			List<Parameter> methodParams = toParameters(params);
			Operation operation = new GenericOperation(m.getName(), opEffectList, methodParams);
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
		return new GenericConflictResolutionPolicy();
	}

}
