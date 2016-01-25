package indigo.generic;

import java.util.Set;

import com.google.common.collect.Sets;

import indigo.impl.json.JSONConstant;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;

public class NegateEffects implements EffectSetFunction {

	private static GenericPredicateFactory factory = GenericPredicateFactory.getFactory();

	private Set<PredicateAssignment> negatedEffects(Set<PredicateAssignment> set) {
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
	public Set<PredicateAssignment> transformEffects(Set<PredicateAssignment> set) {
		return negatedEffects(set);
	}

}
