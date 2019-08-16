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
package indigo.impl.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import indigo.generic.GenericVariable;
import indigo.interfaces.operations.Parameter;

public class JSONQuantifiedClause extends JSONClause {

	private final String operator;
	private Collection<Parameter> vars;
	private final JSONClause quantifiedClause;

	public JSONQuantifiedClause(String operator, Collection<Parameter> vars, JSONObject obj,
			JSONClauseContext context) {
		super();
		this.operator = operator;
		this.vars = vars;
		this.quantifiedClause = objectToClause(obj, context);
	}

	private JSONQuantifiedClause(String operator, Collection<Parameter> vars, JSONClause clause) {
		super();
		this.operator = operator;
		this.vars = vars;
		this.quantifiedClause = clause.copyOf();
	}

	@Override
	public String toString() {
		StringBuilder varsString = new StringBuilder();
		Iterator<Parameter> varsIterator = vars.iterator();
		while (varsIterator.hasNext()) {
			Parameter var = varsIterator.next();
			varsString.append(var.toString());
			if (varsIterator.hasNext()) {
				varsString.append(" , ");
			}
		}

		return operator + "(" + varsString + ")" + " :- " + quantifiedClause.toString();
	}

	@Override
	public JSONClause copyOf() {
		Collection<Parameter> newVars = JSONClause.copyVars(vars);
		return new JSONQuantifiedClause(operator, newVars, quantifiedClause);
	}

	@Override
	public void instantiateVariables(int i) {
		System.out.println("SYSTEM - NOT EXPECTED TO BE CALLED WITHOUT SUPPORTING QUANTIFIERS ON EFFECTS");
		System.exit(-1);
		List<Parameter> newVars = new LinkedList<>();
		for (Parameter var : vars) {
			newVars.add(new GenericVariable(var.getName(), var.getType()));
		}
		this.vars = newVars;
		quantifiedClause.instantiateVariables(i);
	}

	@Override
	public JSONClause mergeClause(JSONClause other) {
		if (other instanceof JSONQuantifiedClause) {
			JSONQuantifiedClause otherQ = (JSONQuantifiedClause) other;
			Collection<Parameter> newVars = Lists.newLinkedList();
			newVars.addAll(vars);
			for (Parameter var : otherQ.vars) {
				if (!newVars.contains(var)) {
					newVars.add(var);
				}
			}
			return new JSONQuantifiedClause(operator, ImmutableList.copyOf(newVars),
					quantifiedClause.mergeClause(otherQ.quantifiedClause));

		} else {
			return new JSONQuantifiedClause(operator, ImmutableList.copyOf(vars), quantifiedClause.mergeClause(other));
		}

	}
}
