package indigo.impl.json;

import indigo.interfaces.Clause;

import java.util.Collection;
import java.util.Iterator;

import org.json.simple.JSONObject;

public class JSONQuantifiedClause extends JSONClause {

	private final String operator;
	private final Collection<JSONVariable> vars;
	private final Clause quantifiedClause;

	public JSONQuantifiedClause(String operator, Collection<JSONVariable> vars, JSONObject obj,
			JSONClauseContext context) {
		super();
		this.operator = operator;
		this.vars = vars;
		this.quantifiedClause = objectToClause(obj, context);
	}

	private JSONQuantifiedClause(String operator, Collection<JSONVariable> vars, Clause clause) {
		super();
		this.operator = operator;
		this.vars = vars;
		this.quantifiedClause = clause;
	}

	@Override
	public String toString() {
		StringBuilder varsString = new StringBuilder();
		Iterator<JSONVariable> varsIterator = vars.iterator();
		while (varsIterator.hasNext()) {
			JSONVariable var = varsIterator.next();
			varsString.append(var.toString());
			if (varsIterator.hasNext()) {
				varsString.append(" , ");
			}
		}

		return operator + "(" + varsString + ")" + " :- " + quantifiedClause.toString();
	}

	@Override
	public Clause copyOf() {
		Collection<JSONVariable> newVars = JSONClause.copyVars(vars);
		return new JSONQuantifiedClause(operator, newVars, quantifiedClause.copyOf());
	}

	@Override
	public boolean isNumeric() {
		return quantifiedClause.isNumeric();
	}
}
