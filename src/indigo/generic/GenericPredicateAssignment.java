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
package indigo.generic;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import indigo.impl.javaclass.BooleanValue;
import indigo.impl.json.JSONPredicateAssignment;
import indigo.interfaces.logic.Invariant;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;
import indigo.runtime.Bindings;
import indigo.runtime.Parser;
import indigo.runtime.Parser.Expression;

public class GenericPredicateAssignment implements PredicateAssignment {

	private String operationName = "model";
	private String predicateName;
	private Value value;
	private List<Parameter> params;

	private static final String Z3decl = "\\(define-(fun|const) ([a-zA-Z0-9!_|\\s]*) \\(\\s*(.*)\\s*\\) ([a-zA-Z0-9!|\\s]*) ([a-zA-Z0-9!\\(\\)|\\-\\s]*)\\)";
	private static final String Z3vars = "\\s*(\\((\\w*!\\d+) (\\w*)\\))\\s*";
	private static final String funcDecl = "\\s*(\\(" + "\\s*.*\\s*" + "\\(" + "\\s*(.*)\\s*" + "\\)"
			+ "\\s*=\\s*.*\\s*" + "\\))\\s*";
	private static final String paramsDecl = "([a-zA-Z0-9!_|]*)\\s:\\s([a-zA-Z0-9!_|]*)";
	private static final String separator = ",";

	public GenericPredicateAssignment(String z3DefineFunc) {
		processZ3Definition(z3DefineFunc);
	}

	public GenericPredicateAssignment(String operationName, String predicateName, Value value,
			List<Parameter> parameter) {
		this.operationName = operationName;
		this.predicateName = predicateName;
		this.value = value;
		this.params = parameter;
	}

	protected GenericPredicateAssignment(JSONPredicateAssignment effect, Value newValue) {
		this.operationName = effect.getOperationName();
		this.predicateName = effect.getPredicateName();
		this.value = newValue.copyOf();
		List<Parameter> parameters = parseParametersFromExpressionString(effect.expression().toString());
		this.params = parameters;
	}

	public GenericPredicateAssignment(PredicateAssignment effect, Value newValue) {
		this.operationName = effect.getOperationName();
		this.predicateName = effect.getPredicateName();
		this.value = newValue.copyOf();
		List<Parameter> parameters = parseParametersFromExpressionString(effect.expression().toString());
		this.params = parameters;
	}

	public static List<Parameter> parseParametersFromExpressionString(String expression) {
		List<Parameter> parameters = Lists.newLinkedList();
		Pattern fdPattern = Pattern.compile(funcDecl);
		Pattern paramsPattern = Pattern.compile(paramsDecl);
		Matcher fdpMatcher = fdPattern.matcher(expression);
		fdpMatcher.find();
		String[] params = fdpMatcher.group(2).split(separator);
		Arrays.stream(params).forEach(p -> {
			Matcher paramsMatcher = paramsPattern.matcher(p);
			paramsMatcher.find();
			String type = paramsMatcher.group(1);
			String value = paramsMatcher.group(2);
			parameters.add(new GenericVariable(value, type));
		});
		return parameters;
	}

	public GenericPredicateAssignment(GenericPredicateAssignment effect, Value newValue) {
		this.operationName = effect.getOperationName();
		this.predicateName = effect.getPredicateName();
		this.value = newValue.copyOf();
		this.params = Lists.newLinkedList();
		effect.params.forEach(a -> params.add(a.copyOf()));
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
		String unparsedParams = declM.group(3);
		String valueType = declM.group(4);
		String value = declM.group(5);

		Matcher varsM = r1.matcher(unparsedParams);
		List<Parameter> params = Lists.newLinkedList();
		while (varsM.find()) {
			GenericVariable var = new GenericVariable(varsM.group(2), varsM.group(3));
			params.add(var);
		}

		this.predicateName = predicateName;
		this.operationName += "-" + declM.group(1);
		this.params = params;

		PREDICATE_TYPE type = valueType.equals("Bool") ? PREDICATE_TYPE.bool : PREDICATE_TYPE.Int;
		this.value = new GenericConstant(type, value);
	}

	@Override
	public Expression expression() {
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
				new LinkedList<Parameter>(params));
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

		if (value.getType().equals(PREDICATE_TYPE.Int)) {
			applyNumericEffects(wpc, i);
		} else {
			applyBooleanEffects(wpc, i);
		}

	}

	private void applyBooleanEffects(LogicExpression wpc, int i) {
		String predicateAsString = predName();
		Bindings matches = wpc.matches(predicateAsString);
		if (!matches.isEmpty()) {
			matches.entrySet().stream().findAny().ifPresent(e -> {
				wpc.replace(e.getKey().toString(), "" + value.getValue());
				Bindings vars = Parser.match(e.getKey(), e.getValue());
				vars.forEach((k, v) -> {
					wpc.replace(k.toString(), v.toString());
				});
			});

			wpc.assertion(String.format("%s", expression()));
		}

	}

	String effect(int iteration) {
		Pattern p = Pattern.compile("\\$\\d+");
		Matcher mm = p.matcher(predName());

		String res = predName();
		while (mm.find()) {
			String num = predicateName.substring(mm.start(), mm.end());
			int param = Integer.valueOf(num.substring(1));

			res = res.replace(num,
					String.format(" %s : %s%s ", params.get(param).getType(), params.get(param).getName(), iteration));
		}
		return res;
	}

	public boolean applyNumericEffects(LogicExpression le, int iteration) {
		String function = effect(iteration);
		int integerValue = Integer.parseInt(value.getValue());
		String effect = String.format("(%s %s 1)", function, (integerValue > 0) ? "+" : "-");
		Bindings matches = le.matches(function);
		if (matches != null) {
			matches.entrySet().stream().findAny().ifPresent(e -> {
				le.replace(e.getKey().toString(), effect);
				Bindings vars = Parser.match(e.getKey(), e.getValue());
				vars.forEach((k, v) -> {
					le.replace(k.toString(), v.toString());
				});
			});
			return true;
		}
		return false;
	}

	protected String predName() {
		String predName = predicateName + "(";
		String pref = "";
		for (Parameter p : params) {
			String[] tokens = p.getName().split("!");
			predName += pref + p.getType() + " : " + tokens[0];
			pref = ", ";
		}

		predName += ")";
		return predName;
	}

	@Override
	public boolean affects(Invariant otherClause) {
		Bindings match = otherClause.toLogicExpression().matches(getPredicateName());
		return match != null && !match.isEmpty();
	}

	@Override
	public String toString() {
		if (value.getType().equals(PREDICATE_TYPE.bool))
			return printBoolean();
		else
			return printNumeric();

	}

	private String printNumeric() {
		// return predName() + ((Integer.parseInt(value.getValue()) > 0) ? " + "
		// : " - ") + value.getValue();
		return predName() + " + " + value.getValue();

	}

	private String printBoolean() {
		return predName() + " = " + value;
	}

	public static Value newBoolean(boolean b) {
		return new GenericConstant(PREDICATE_TYPE.bool, b + "");
	}

	@Override
	public List<Parameter> getParams() {
		return params;
	}

	public void toggleBooleanValue() {
		if (value.getType().equals(PREDICATE_TYPE.bool)) {
			if (((BooleanValue) value).getValue().equals("true")) {
				value = BooleanValue.FalseValue();
			} else {
				value = BooleanValue.TrueValue();
			}

		}
	}

	@Override
	public void updateParamTypes(List<Expression> params) {
		for (Parameter this_p : this.params) {
			for (Expression other_p : params) {
				if (other_p.toString().contains(this_p.getName())) {
					this_p.setType(other_p.type());
				}
			}
		}

	}

	@Override
	public Value negateValue() {
		if (value.getType() == PREDICATE_TYPE.bool) {
			if (value instanceof GenericConstant) {
				if (((GenericConstant) value).getValue().equals("true")) {
					return new GenericConstant(PREDICATE_TYPE.bool, "false");
				} else {
					return new GenericConstant(PREDICATE_TYPE.bool, "true");
				}
			} else if (value instanceof BooleanValue) {
				if (((BooleanValue) value).getValue().equals("true")) {
					return new GenericConstant(PREDICATE_TYPE.bool, "false");
				} else {
					return new GenericConstant(PREDICATE_TYPE.bool, "true");
				}
			}
		}
		return value.copyOf();
	}
}
