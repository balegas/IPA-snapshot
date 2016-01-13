package indigo.impl.javaclass;

import indigo.AbstractSpecification;
import indigo.generic.GenericConflictResolutionPolicy;
import indigo.impl.javaclass.effects.AssertionPredicate;
import indigo.impl.javaclass.effects.CounterPredicate;
import indigo.impl.javaclass.effects.JavaEffect;
import indigo.interfaces.ConflictResolutionPolicy;
import indigo.interfaces.Invariant;
import indigo.interfaces.Operation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

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
			Operation operation = new JavaOperation(m.getName(), opEffectList);
			operations.add(operation);
		}
		return Sets.newHashSet(operations);
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
