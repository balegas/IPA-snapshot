package indigo.generic;

import indigo.Bindings;
import indigo.Parser;
import indigo.Parser.Expression;
import indigo.impl.json.JSONConstant;
import indigo.impl.json.JSONPredicateAssignment;
import indigo.impl.json.JSONVariable;
import indigo.interfaces.Invariant;
import indigo.interfaces.PREDICATE_TYPE;
import indigo.interfaces.PredicateAssignment;
import indigo.interfaces.Value;
import indigo.invariants.LogicExpression;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

public class GenericPredicateAssignment implements PredicateAssignment {

	private String operationName = "model";
	private String predicateName;
	private Value value;
	private List<JSONVariable> arguments;

	private static final String Z3decl = "\\(define-(fun|const) ([a-zA-Z0-9!|\\s]*) \\(\\s*(.*)\\s*\\) ([a-zA-Z0-9!|\\s]*) ([a-zA-Z0-9!|\\s]*)\\)";
	private static final String Z3vars = "\\s*(\\((\\w*!\\d+) (\\w*)\\))\\s*";
	private static final String funcDecl = "\\s*(\\(" + "\\s*.*\\s*" + "\\(" + "\\s*(.*)\\s*" + "\\)"
			+ "\\s*=\\s*.*\\s*" + "\\))\\s*";
	private static final String argumentsDecl = "([a-zA-Z0-9!|]*)\\s:\\s([a-zA-Z0-9!|]*)";
	private static final String separator = ",";

	public GenericPredicateAssignment(String z3DefineFunc) {
		processZ3Definition(z3DefineFunc);
	}

	public GenericPredicateAssignment(String operationName, String predicateName, Value value,
			List<JSONVariable> arguments) {
		this.operationName = operationName;
		this.predicateName = predicateName;
		this.value = value;
		this.arguments = arguments;
	}

	protected GenericPredicateAssignment(JSONPredicateAssignment effect, Value newValue) {
		this.operationName = effect.getOperationName();
		this.predicateName = effect.getPredicateName();
		this.value = newValue.copyOf();
		List<JSONVariable> arguments = parseArgumentsFromExpressionString(effect.getExpression().toString());
		this.arguments = arguments;
	}

	public static List<JSONVariable> parseArgumentsFromExpressionString(String expression) {
		List<JSONVariable> arguments = Lists.newLinkedList();
		Pattern fdPattern = Pattern.compile(funcDecl);
		Pattern argsPattern = Pattern.compile(argumentsDecl);
		Matcher fdpMatcher = fdPattern.matcher(expression);
		fdpMatcher.find();
		String[] args = fdpMatcher.group(2).split(separator);
		Arrays.stream(args).forEach(arg -> {
			Matcher argsMatcher = argsPattern.matcher(arg);
			argsMatcher.find();
			String type = argsMatcher.group(1);
			String value = argsMatcher.group(2);
			arguments.add(new JSONVariable(value, type));
		});
		return arguments;
	}

	public GenericPredicateAssignment(GenericPredicateAssignment effect, Value newValue) {
		this.operationName = effect.getOperationName();
		this.predicateName = effect.getPredicateName();
		this.value = newValue.copyOf();
		this.arguments = Lists.newLinkedList();
		effect.arguments.forEach(a -> arguments.add(a.copyOf()));
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

	private final void processZ3Definition(String funcDef) {
		Pattern r = Pattern.compile(Z3decl);
		Pattern r1 = Pattern.compile(Z3vars);
		Matcher declM = r.matcher(funcDef);
		declM.find();

		String predicateName = null;
		predicateName = declM.group(2);
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
		return Parser.parse(predName() + "= " + value);
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
		return new GenericPredicateAssignment(operationName, predicateName, value.copyOf(),
				new LinkedList<JSONVariable>(arguments));
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
		if (i > 1) {
			// TODO: Generic predicate assignments have pre-processed
			// arguments...
			System.out.println("CALLED APPLY EFFECTS WITH i > 1");
			System.exit(0);
		}
		// List<JSONVariable> newArgs = new LinkedList<>();
		// for (JSONVariable arg : arguments) {
		// String[] argName = arg.getName().split("-");
		// newArgs.add(new JSONVariable(argName[0] + "-" + i, arg.getType()));
		// }
		// this.arguments = newArgs;

		String predicateAsString = predName();
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

			wpc.assertion(String.format("%s", getExpression()));
		}
	}

	private String predName() {
		String predName = predicateName + "(";
		String pref = "";
		for (JSONVariable arg : arguments) {
			String[] tokens = arg.getName().split("!");
			predName += pref + arg.getType() + " : " + tokens[0];
			pref = ", ";
		}

		predName += ")";
		return predName;
	}

	@Override
	public boolean affects(Invariant otherClause) {
		System.out.println("Z3Predicate -- not expected 1");
		System.exit(0);
		return false;
	}

	@Override
	public String toString() {
		return predName() + " = " + value;
	}

	public static Value newBoolean(boolean b) {
		return new JSONConstant(PREDICATE_TYPE.bool, b + "");
	}

}
