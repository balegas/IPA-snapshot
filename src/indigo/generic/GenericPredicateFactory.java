package indigo.generic;

import java.util.List;

import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.Value;
import indigo.interfaces.operations.Parameter;

public class GenericPredicateFactory {

	// private final ProgramSpecification spec;
	private static GenericPredicateFactory instance;

	private GenericPredicateFactory(/* ProgramSpecification spec */) {
		// this.spec = spec;
	}

	public PredicateAssignment newPredicateAssignmentFrom(PredicateAssignment effect, Value newValue) {
		String operationName = effect.getOperationName();
		String predicateName = effect.getPredicateName();
		Value value = newValue.copyOf();
		List<Parameter> arguments = GenericPredicateAssignment
				.parseParametersFromExpressionString(effect.getExpression().toString());
		return new GenericPredicateAssignment(operationName, predicateName, value, arguments);

	}

	public static GenericPredicateFactory getFactory(/*
												 * ProgramSpecification
												 * programSpec
												 */) {
		if (instance == null) {
			instance = new GenericPredicateFactory(/* programSpec */);
		}
		return instance;
	}

}