package indigo;

import indigo.Parser.Expression;
import indigo.impl.json.JSONConstant;
import indigo.impl.json.JSONVariable;
import indigo.interfaces.Invariant;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;
import indigo.invariants.LogicExpression;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

public class Z3PredicateAssignment implements PredicateAssignment {

	private String operationName = "model";
	private String predicateName;
	private Value value;
	private List<JSONVariable> arguments;

	private static final String decl = "\\(define-(fun|const) (\\w*|\\|P \\d+\\|) \\(\\s*(.*)\\s*\\) (\\w*) (\\w*)\\)";
	private static final String vars = "\\s*(\\((\\w*!\\d+) (\\w*)\\))\\s*";

	public Z3PredicateAssignment(String funcDef) {
		processDefinition(funcDef);
	}

	private Z3PredicateAssignment(String operationName, String predicateName, Value newValue,
			List<JSONVariable> arguments) {
		this.operationName = operationName;
		this.predicateName = predicateName;
		this.value = newValue;
		this.arguments = arguments;
	}

	@Override
	public int hashCode() {
		return getPredicateName().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof PredicateAssignment) {
			return this.getPredicateName().equals(((PredicateAssignment) other).getPredicateName());
		}
		return false;
	}

	private final void processDefinition(String funcDef) {
		Pattern r = Pattern.compile(decl);
		Pattern r1 = Pattern.compile(vars);
		Matcher declM = r.matcher(funcDef);
		declM.find();

		String predicateName = declM.group(2);
		String unparsedArgs = declM.group(3);
		String valueType = declM.group(4);
		String value = declM.group(5);

		Matcher varsM = r1.matcher(unparsedArgs);
		List<JSONVariable> args = Lists.newLinkedList();
		while (varsM.find()) {
			JSONVariable var = new JSONVariable(varsM.group(2), varsM.group(3));
			args.add(var);
		}

		this.predicateName = predicateName;
		this.operationName += "-" + declM.group(1);
		this.arguments = args;

		PREDICATE_TYPE type = valueType.equals("Bool") ? PREDICATE_TYPE.bool : PREDICATE_TYPE.numeric;
		this.value = new JSONConstant(type, value);
	}

	@Override
	public Expression getExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PREDICATE_TYPE getType() {
		return value.getType();
	}

	@Override
	public boolean isType(PREDICATE_TYPE type) {
		return value.getType().equals(type);
	}

	@Override
	public PredicateAssignment copyOf() {
		return new Z3PredicateAssignment(operationName, predicateName, value.copyOf(), new LinkedList<JSONVariable>(
				arguments));
	}

	@Override
	public String getOperationName() {
		return operationName;
	}

	@Override
	public String getPredicateName() {
		return predicateName;
	}

	@Override
	public Value getAssignedValue() {
		return value;
	}

	@Override
	public void applyEffect(LogicExpression wpc, int i) {
		List<JSONVariable> newArgs = new LinkedList<>();
		for (JSONVariable arg : arguments) {
			String[] argName = arg.getName().split("-");
			newArgs.add(new JSONVariable(argName[0] + "-" + i, arg.getType()));
		}
		this.arguments = newArgs;

		String predName = predicateName + "(";
		String pref = "";
		for (JSONVariable arg : arguments) {
			predName += pref + arg.getName().split("!")[0];
			pref = ", ";
		}

		predName += ")";

		String predicateAsString = predName;
		Bindings matches = wpc.matches(predicateAsString);
		if (!matches.isEmpty()) {
			matches.entrySet().stream().findAny().ifPresent(e -> {
				// if (operator.equals("-") || operator.equals("+")) {
				// wpc.replace(e.getKey().toString(), "" + this.toString());
				// } else {
				wpc.replace(e.getKey().toString(), "" + value.getValue());
				// }
				Bindings vars = Parser.match(e.getKey(), e.getValue());
				vars.forEach((k, v) -> {
					wpc.replace(k.toString(), v.toString());
				});
			});

			wpc.assertion(String.format("%s", predName + " = " + value));
		}
	}

	@Override
	public boolean affects(Invariant otherClause) {
		System.out.println("Z3Predicate -- not expected 1");
		System.exit(0);
		return false;
	}

	@Override
	public PredicateAssignment copyWithNewValue(Value newValue) {
		return new Z3PredicateAssignment(this.operationName, this.predicateName, newValue,
				new LinkedList<JSONVariable>(this.arguments));
	}

	@Override
	public String toString() {
		return "(" + operationName + " , " + predicateName + " , " + arguments + ") = " + value;
	}

}
