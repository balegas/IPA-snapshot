package indigo.runtime;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import indigo.generic.GenericPredicateAssignment;
import indigo.generic.GenericVariable;
import indigo.impl.javaclass.BooleanValue;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.operations.Parameter;

public class DependencyChecker implements Parser.ASTVisitor {

	static final Pattern bool = Pattern.compile("true|false");
	static final Pattern number = Pattern.compile("\\d+");

	Map<String, Set<PredicateAssignment>> dependeciesForPredicate;
	private final Set<String> constrainedSets;

	public DependencyChecker(Map<String, Set<PredicateAssignment>> dependeciesForPredicate, Set<String> constrainedSets) {
		this.dependeciesForPredicate = dependeciesForPredicate;
		this.constrainedSets = constrainedSets;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T> T evalConstant(P parent, String val) {
		return (T) ImmutableSet.of();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalBinaryOperation(P parent, String op, E left, E right) {
		Set<PredicateAssignment> predicateAssignments = Sets.newHashSet();
		switch (op) {
		case "<=>":
			for (PredicateAssignment l : (Collection<PredicateAssignment>) left) {
				if (constrainedSets.contains(l.getPredicateName())) {
					Set<PredicateAssignment> deps = dependeciesForPredicate.get(l);
					if (deps == null) {
						deps = Sets.newHashSet();
						dependeciesForPredicate.put(l.getPredicateName(), deps);
					}
					deps.addAll((Collection<PredicateAssignment>) right);
				}
			}
		case "=>":
			for (PredicateAssignment r : (Collection<PredicateAssignment>) right) {
				if (constrainedSets.contains(r.getPredicateName())) {
					Set<PredicateAssignment> deps = dependeciesForPredicate.get(r);
					if (deps == null) {
						deps = Sets.newHashSet();
						dependeciesForPredicate.put(r.getPredicateName(), deps);
					}
					deps.addAll((Collection<PredicateAssignment>) left);
				}
			}
			predicateAssignments.addAll((Collection<PredicateAssignment>) left);
			predicateAssignments.addAll((Collection<PredicateAssignment>) right);
			break;

		case "and":
		case "/\\":
		case "or":
		case "\\/":
			predicateAssignments.addAll((Collection<PredicateAssignment>) left);
			predicateAssignments.addAll((Collection<PredicateAssignment>) right);

			break;
		default:
		}

		return (T) ImmutableSet.copyOf(predicateAssignments);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P, T, E> T evalUnaryOperation(P parent, String op, E right) {
		if (op.equals("not")) {
			((Collection<GenericPredicateAssignment>) right).stream().forEach(p -> p.toggleBooleanValue());
			return (T) right;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalFunction(P parent, String name, String ret, List<E> params) {
		List<Parameter> paramsForOp = ((FunctionExpression) parent).params.stream().map(p -> new GenericVariable(((ConstantExpression) p).value, ((ConstantExpression) p).type))
				.collect(Collectors.toList());

		return (T) ImmutableSet.of(new GenericPredicateAssignment("inv", name, BooleanValue.TrueValue(), paramsForOp));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalDeclaration(P parent, String type, String var) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, B, E> T evalQuantiedExpression(P parent, String type, B body, List<E> bounds) {
		Collection<PredicateAssignment> predicateSet = (Collection<PredicateAssignment>) body;
		for (PredicateAssignment predicate : predicateSet) {
			predicate.updateParamTypes(((QuantifiedExpression) parent).quantifier.params);
		}
		return (T) body;
	}
}
