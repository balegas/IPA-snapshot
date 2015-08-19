package indigo.impl.json;

import indigo.interfaces.Clause;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableSet;

public class JSONPredicateClause extends JSONClause {

	private final String predicateName;
	private final Collection<JSONVariable> args;

	public JSONPredicateClause(JSONObject object, JSONClauseContext context) {
		super();
		this.predicateName = (String) object.get("name");
		this.args = getArgs((JSONArray) object.get("args"), context);

	}

	private JSONPredicateClause(String predicateName, Collection<JSONVariable> args) {
		this.predicateName = predicateName;
		this.args = args;
	}

	private static Collection<JSONVariable> getArgs(JSONArray args, JSONClauseContext context) {
		Set<JSONVariable> vars = new HashSet<>();
		args.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONObject value = (JSONObject) obj.get("value");
				String varName = (String) value.get("var_name");
				vars.add(new JSONVariable(varName, context.getVarType(varName)));
			}
		});
		return ImmutableSet.copyOf(vars);
	}

	@Override
	public Clause copyOf() {
		Collection<JSONVariable> newArgs = JSONClause.copyVars(args);
		return new JSONPredicateClause(predicateName, newArgs);
	}

	@Override
	public String toString() {
		StringBuilder argsString = new StringBuilder();
		Iterator<JSONVariable> varsIterator = args.iterator();
		while (varsIterator.hasNext()) {
			JSONVariable var = varsIterator.next();
			argsString.append(var.toString());
			if (varsIterator.hasNext()) {
				argsString.append(" , ");
			}
		}
		return predicateName + "(" + argsString + ")";
	}

	@Override
	public boolean isNumeric() {
		return false;
	}

}
