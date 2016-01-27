package indigo.interactive.generator;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import indigo.generic.GenericPredicateFactory;
import indigo.impl.json.JSONConstant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.OperationTransformer;

public class NegateEffects implements OperationTransformer {

	private static GenericPredicateFactory factory = GenericPredicateFactory.getFactory();

	private Set<PredicateAssignment> negatedEffects(Collection<PredicateAssignment> set) {
		Set<PredicateAssignment> negatedEffects = Sets.newHashSet();
		for (PredicateAssignment effect : set) {
			Value value = effect.getAssignedValue();
			if (value.toString().equals("true")) {
				value = new JSONConstant(PREDICATE_TYPE.bool, "false");
			} else if (value.toString().equals("false")) {
				value = new JSONConstant(PREDICATE_TYPE.bool, "true");
			} else {
				System.out.println("NOT EXPECTED TYPE");
				System.exit(0);
			}
			PredicateAssignment modEffect = factory.newPredicateAssignmentFrom(effect, value);
			negatedEffects.add(modEffect);
		}
		return negatedEffects;
	}

	@Override
	public Collection<PredicateAssignment> transformEffects(Collection<PredicateAssignment> set) {
		return negatedEffects(set);
	}

}
