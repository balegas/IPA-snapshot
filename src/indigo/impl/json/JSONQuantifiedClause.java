package indigo.impl.json;

import indigo.interfaces.Clause;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class JSONQuantifiedClause extends JSONClause {

	private final String operator;
	private Collection<JSONVariable> vars;
	private final JSONClause quantifiedClause;

	public JSONQuantifiedClause(String operator, Collection<JSONVariable> vars, JSONObject obj,
			JSONClauseContext context) {
		super();
		this.operator = operator;
		this.vars = vars;
		this.quantifiedClause = objectToClause(obj, context);
	}

	private JSONQuantifiedClause(String operator, Collection<JSONVariable> vars, JSONClause clause) {
		super();
		this.operator = operator;
		this.vars = vars;
		this.quantifiedClause = clause.copyOf();
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
	public JSONClause copyOf() {
		Collection<JSONVariable> newVars = JSONClause.copyVars(vars);
		return new JSONQuantifiedClause(operator, newVars, quantifiedClause);
	}

	@Override
	public boolean isNumeric() {
		return quantifiedClause.isNumeric();
	}

	@Override
	public void instantiateVariables(int i) {
		System.out.println("SYSTEM - NOT EXPECTED TO BE CALLED WITHOUT SUPPORTING QUANTIFIERS ON EFFECTS");
		System.exit(-1);
		List<JSONVariable> newVars = new LinkedList<>();
		for (JSONVariable var : vars) {
			newVars.add(new JSONVariable(var.getType(), var.getName()));
		}
		this.vars = newVars;
		quantifiedClause.instantiateVariables(i);
	}

	@Override
	public JSONClause mergeClause(Clause other) {
		if (other instanceof JSONQuantifiedClause) {
			JSONQuantifiedClause otherQ = (JSONQuantifiedClause) other;
			Collection<JSONVariable> newVars = Lists.newLinkedList();
			newVars.addAll(vars);
			for (JSONVariable var : otherQ.vars) {
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
