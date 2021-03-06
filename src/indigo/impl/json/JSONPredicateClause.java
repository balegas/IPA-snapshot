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
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableList;

import indigo.generic.GenericVariable;
import indigo.interfaces.operations.Parameter;

public class JSONPredicateClause extends JSONClause {

	private final String predicateName;
	private Collection<Parameter> args;

	public JSONPredicateClause(JSONObject object, JSONClauseContext context) {
		super();
		this.predicateName = (String) object.get("name");
		this.args = getArgs((JSONArray) object.get("args"), context);

	}

	private JSONPredicateClause(String predicateName, Collection<Parameter> args) {
		this.predicateName = predicateName;
		this.args = args;
	}

	@Override
	public int hashCode() {
		return (predicateName + args.size()).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		JSONPredicateClause otherP = (JSONPredicateClause) other;
		return (predicateName + args.size()).equals(otherP.predicateName + otherP.args.size());
	}

	@SuppressWarnings("unchecked")
	private static Collection<Parameter> getArgs(JSONArray args, JSONClauseContext context) {
		List<GenericVariable> vars = new LinkedList<>();
		args.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONObject value = (JSONObject) obj.get("value");
				String varName = (String) value.get("var_name");
				String type = (String) value.get("var_type");
				if (type.equals("_")) {
					type = context.getVarType(varName);
				}
				vars.add(new GenericVariable(varName, type));
			}
		});
		return ImmutableList.copyOf(vars);
	}

	@Override
	public JSONClause copyOf() {
		Collection<Parameter> newArgs = JSONClause.copyVars(args);
		return new JSONPredicateClause(predicateName, newArgs);
	}

	@Override
	public String toString() {
		StringBuilder argsString = new StringBuilder();
		Iterator<Parameter> varsIterator = args.iterator();
		while (varsIterator.hasNext()) {
			Parameter var = varsIterator.next();
			argsString.append(var.toString());
			if (varsIterator.hasNext()) {
				argsString.append(" , ");
			}
		}
		return predicateName + "(" + argsString + ")";
	}

	@Override
	public void instantiateVariables(int i) {
		List<Parameter> newArgs = new LinkedList<>();
		for (Parameter arg : args) {
			String[] argName = arg.getName().split("-");
			newArgs.add(new GenericVariable(argName[0] + "-" + i, arg.getType()));
		}
		this.args = newArgs;
	}
}
