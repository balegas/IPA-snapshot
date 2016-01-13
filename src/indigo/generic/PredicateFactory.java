package indigo.generic;

import indigo.impl.json.JSONVariable;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;

import java.util.List;

public class PredicateFactory {

	// private final ProgramSpecification spec;
	private static PredicateFactory instance;

	private PredicateFactory(/* ProgramSpecification spec */) {
		// this.spec = spec;
	}

	public PredicateAssignment newPredicateAssignmentFrom(PredicateAssignment effect, Value newValue) {
		String operationName = effect.getOperationName();
		String predicateName = effect.getPredicateName();
		Value value = newValue.copyOf();
		List<JSONVariable> arguments = GenericPredicateAssignment.parseArgumentsFromExpressionString(effect
				.getExpression().toString());
		return new GenericPredicateAssignment(operationName, predicateName, value, arguments);

	}

	public static PredicateFactory getFactory(/* ProgramSpecification programSpec */) {
		if (instance == null) {
			instance = new PredicateFactory(/* programSpec */);
		}
		return instance;
	}

}