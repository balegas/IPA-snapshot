package z3;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import indigo.runtime.Parser;

public class Z3DeclarionCollector implements Parser.ASTVisitor {

	static final Pattern bool = Pattern.compile("true|false");
	static final Pattern number = Pattern.compile("\\d+");

	Map<String, Set<String>> declarations;

	public Z3DeclarionCollector(Map<String, Set<String>> declarations) {
		this.declarations = declarations;
	}

	Set<String> set(String type) {
		Set<String> res = declarations.get(type);
		if (res == null)
			declarations.put(type, res = new HashSet<>());
		return res;
	}

	@Override
	public <P, T> T evalConstant(P parent, String val) {
		return null;
	}

	@Override
	public <P, T, E> T evalBinaryOperation(P parent, String op, E left, E right) {
		return null;
	}

	@Override
	public <P, T, E> T evalUnaryOperation(P parent, String op, E right) {
		return null;
	}

	@Override
	public <P, T, E> T evalFunction(P parent, String name, String ret, List<E> params) {
		return null;
	}

	@Override
	public <P, T, E> T evalDeclaration(P parent, String type, String var) {
		set(type).add(var);
		return null;
	}

	@Override
	public <P, T, B, E> T evalQuantiedExpression(P parent, String type, B body, List<E> bounds) {
		return null;
	}
}
