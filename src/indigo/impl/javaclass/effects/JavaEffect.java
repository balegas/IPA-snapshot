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
package indigo.impl.javaclass.effects;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import indigo.generic.GenericVariable;
import indigo.impl.javaclass.JavaPredicateValue;
import indigo.interfaces.logic.PredicateAssignment;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;

abstract public class JavaEffect implements Comparable<JavaEffect> {

	private enum ANNOTATION {
		OPERATION_NAME, PRED_NAME, PRED_VALUE, PRED_ARGS
	};

	protected final String annotation;
	protected final Method method;

	protected final String operationName;
	protected final String predicateName;
	protected final JavaPredicateValue predicateValue;
	private final List<Parameter> params;

	JavaEffect(Method method, String annotation) {
		this.method = method;
		this.annotation = annotation;
		Map<ANNOTATION, Object> parsedAnnotation = processAnnotation(method, annotation);
		this.operationName = (String) parsedAnnotation.get(ANNOTATION.OPERATION_NAME);
		this.predicateName = (String) parsedAnnotation.get(ANNOTATION.PRED_NAME);
		this.predicateValue = (JavaPredicateValue) parsedAnnotation.get(ANNOTATION.PRED_VALUE);
		this.params = (List<Parameter>) parsedAnnotation.get(ANNOTATION.PRED_ARGS);
	}

	public JavaEffect(String operationName, String predicateName, Method method, String annotation,
			List<Parameter> params, JavaPredicateValue value) {
		this.method = method;
		this.annotation = annotation;
		this.operationName = operationName;
		this.predicateName = predicateName;
		this.predicateValue = value;
		this.params = params;
	}

	private Map<ANNOTATION, Object> processAnnotation(Method method, String annotation) {
		Map<ANNOTATION, Object> parsedTokens = new HashMap<>();
		// TODO: Should not match true/false multiple times
		String pattern = "\\s*(.*)\\s*\\(\\s*(.*)\\s*\\)(?:\\s*=\\s*(true|false|\\d|\\-\\d)*)?\\s*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(annotation);
		m.find();
		parsedTokens.put(ANNOTATION.PRED_NAME, m.group(1));
		if (m.group(3) != null) {
			parsedTokens.put(ANNOTATION.PRED_VALUE, JavaPredicateValue.newFromString(m.group(3)));
		} else {
			// TODO: MUST IMPROVE THIS!
			// System.out.println("assume " + m.group(1) + " is numeric.");
			// parsedTokens.put(ANNOTATION.PRED_VALUE,
			// JavaPredicateValue.newFromString(Integer.MAX_VALUE + ""));
			parsedTokens.put(ANNOTATION.PRED_VALUE, JavaPredicateValue.newFromString(1 + ""));

		}
		// TODO: Must parse annotation arguments;
		List<Parameter> params = parseParams(m.group(2));
		parsedTokens.put(ANNOTATION.PRED_ARGS, params);
		parsedTokens.put(ANNOTATION.OPERATION_NAME, method.getName());
		return parsedTokens;
	}

	public List<Parameter> parseParams(String paramsString) {
		// TODO: Possible bug when operations have predicates with "_" but there
		// is no argument in the operation for that.
		java.lang.reflect.Parameter[] pm = method.getParameters();
		String[] paramTokens = paramsString.split(",");
		List<Parameter> params = Lists.newLinkedList();
		int param = 0;
		for (String paramT : paramTokens) {
			Pattern p = Pattern.compile("\\$\\d+|\\s.+\\s:\\s_\\s");
			Matcher mm = p.matcher(paramT);
			while (mm.find()) {
				String match = mm.group();
				String type, name;
				if (match.contains(":")) {
					name = match.substring(match.indexOf(":") + 1);
					type = match.substring(0, match.indexOf(":") - 1);
				} else if (match.contains("$")) {
					String[] idx = match.split("\\$");
					name = match;
					type = pm[Integer.parseInt(idx[1])].getType().getSimpleName();
					param++;
				} else {
					name = match;
					type = pm[param].getType().getSimpleName();
					param++;
				}
				GenericVariable parameter = new GenericVariable(name.trim(), type.trim());
				params.add(parameter);
			}
		}
		return params;
	}

	public String applyIterationToEffect(int iteration) {

		java.lang.reflect.Parameter[] pm = method.getParameters();
		Pattern p = Pattern.compile("\\$\\d+");
		Matcher mm = p.matcher(annotation);

		String res = annotation;
		while (mm.find()) {
			String num = annotation.substring(mm.start(), mm.end());
			int param = Integer.valueOf(num.substring(1));

			res = res.replace(num,
					String.format(" %s : %s%s ", pm[param].getType().getSimpleName(), pm[param].getName(), iteration));
		}
		return res;
	}

	@Override
	public int hashCode() {
		return predicateName.hashCode();
		/* return (predicateName + predicateValue.toString()).hashCode(); */
	}

	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof PredicateAssignment)
			return predicateName.equals(((PredicateAssignment) other).getPredicateName());
		else
			return false;
		/*
		 * public boolean equals(Object otherEffect) { JavaEffect other =
		 * (JavaEffect) otherEffect; return
		 * predicateName.equals(other.predicateName) &&
		 * predicateValue.equals(other.predicateValue);
		 */
	}

	public String getPredicateName() {
		return predicateName;
	}

	public JavaPredicateValue getValue() {
		return predicateValue;
	}

	public String getValueAsString() {
		return predicateValue.toString();
	}

	public String getOperationName() {
		return operationName;
	}

	@Override
	public int compareTo(JavaEffect other) {
		return predicateName.compareTo(other.predicateName);
	}

	public List<Parameter> getParameters() {
		return params;
	}

	public abstract boolean applyEffect(LogicExpression e, int iteration);

	// public abstract JavaEffect copyWithNewValue(Value newValue);

}
