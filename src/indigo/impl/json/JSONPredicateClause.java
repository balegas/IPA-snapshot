package indigo.impl.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableList;

public class JSONPredicateClause extends JSONClause {

	private final String predicateName;
	private Collection<JSONVariable> args;

	public JSONPredicateClause(JSONObject object, JSONClauseContext context) {
		super();
		this.predicateName = (String) object.get("name");
		this.args = getArgs((JSONArray) object.get("args"), context);

	}

	private JSONPredicateClause(String predicateName, Collection<JSONVariable> args) {
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

	private static Collection<JSONVariable> getArgs(JSONArray args, JSONClauseContext context) {
		List<JSONVariable> vars = new LinkedList<>();
		args.forEach(new Consumer<JSONObject>() {

			@Override
			public void accept(JSONObject obj) {
				JSONObject value = (JSONObject) obj.get("value");
				String varName = (String) value.get("var_name");
				String type = (String) value.get("type");
				if (type.equals("_")) {
					type = context.getVarType(varName);
				}
				vars.add(new JSONVariable(varName, type));
			}
		});
		return ImmutableList.copyOf(vars);
	}

	@Override
	public JSONClause copyOf() {
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

	@Override
	public void instantiateVariables(int i) {
		List<JSONVariable> newArgs = new LinkedList<>();
		for (JSONVariable arg : args) {
			String[] argName = arg.getName().split("-");
			newArgs.add(new JSONVariable(argName[0] + "-" + i, arg.getType()));
		}
		this.args = newArgs;
	}
}
