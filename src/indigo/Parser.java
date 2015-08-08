package indigo;

import static indigo.Parser.TokenType.Comma;
import static indigo.Parser.TokenType.Constant;
import static indigo.Parser.TokenType.Declaration;
import static indigo.Parser.TokenType.Function;
import static indigo.Parser.TokenType.LeftParenthesis;
import static indigo.Parser.TokenType.Operator;
import static indigo.Parser.TokenType.QuantifiedExpression;
import static indigo.Parser.TokenType.RightParenthesis;
import indigo.Parser.ASTVisitor;
import indigo.Parser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Parses a string into an expression tree based on rules for arithmetic.
 *
 * Uses Dijkstra's Shunting-Yard algorithm for converting infix to postfix:
 * http://en.wikipedia.org/wiki/Shunting_yard_algorithm
 *
 * @author former student solution
 * @author Robert C. Duvall (added comments, exceptions, some functions)
 * @author Sérgio Duarte (smd@fct.unl.pt), (added operators and symbols)
 */
public class Parser {

	static public Bindings match(Expression e1, Expression e2) {

		Bindings res = new Bindings();
		FunctionExpression fe1 = (FunctionExpression) e1;
		FunctionExpression fe2 = (FunctionExpression) e2;

		for (int i = 0; i < fe1.params.size(); i++)
			res.put(fe1.params.get(i), fe2.params.get(i));

		return res;
	}

	public interface ASTVisitor {

		<P, T, B, E> T evalQuantiedExpression(P parent, String type, B body, List<E> bounds);

		<P, T, E> T evalDeclaration(P parent, String type, String name);

		<P, T, E> T evalFunction(P parent, String name, String ret, List<E> params);

		<P, T> T evalConstant(P parent, String val);

		<P, T, E> T evalBinaryOperation(P parent, String op, E left, E right);

		<P, T, E> T evalUnaryOperation(P parent, String op, E right);
	}

	static enum TokenType {
		Constant, Variable, Operator, LeftParenthesis, RightParenthesis, Function, Comma, Declaration, QuantifiedExpression;
	}

	static class Token {
		TokenType type;
		String value;

		Token(TokenType type, String value) {
			this.type = type;
			this.value = value;
		}

		boolean is(TokenType... types) {
			for (TokenType i : types)
				if (type.equals(i))
					return true;
			return false;
		}

		boolean isNot(TokenType... types) {
			return !is(types);
		}

		@Override
		public String toString() {
			return value;
		}
	}

	static Set<String> unaryOperators = Sets.newHashSet("not");

	static Set<String> logicalOperators = Sets.newHashSet("not", "and", "or", "=>", "<=>", ":-", "=");

	static Map<String, Integer> operatorOrder = new HashMap<>();
	static {

		for (String s : new String[] { ":-" })
			operatorOrder.put(s, 0);

		for (String s : new String[] { "=>", "<=>" })
			operatorOrder.put(s, 1);

		for (String s : new String[] { "and", "*" })
			operatorOrder.put(s, 2);

		for (String s : new String[] { "+", "-", "or", "<", "<=", ">", ">=", "=", "==", "<>" })
			operatorOrder.put(s, 3);

		for (String s : new String[] { "*" })
			operatorOrder.put(s, 4);

		for (String s : new String[] { "not", "~", "[" })
			operatorOrder.put(s, 5);

		for (String s : new String[] { ":" })
			operatorOrder.put(s, 6);
	}

	static String operatorType(Token t) {
		return logicalOperators.contains(t.value) ? "Bool" : "Int";
	}

	static boolean unary(Token operator) {
		return unaryOperators.contains(operator.value);
	}

	static int precedence(Token operator) {
		return precedence(operator.value);
	}

	static int precedence(String operator) {
		Integer order = operatorOrder.get(operator);
		return order == null ? -1 : order.intValue();
	}

	static Token tokenFrom(String str) {
		if (str.equals("("))
			return new Token(LeftParenthesis, "(");
		else if (str.endsWith("forall("))
			return new Token(Function, str.substring(0, str.length() - 1));
		else if (str.endsWith("(") || str.equals("["))
			return new Token(Function, str.substring(0, str.length() - 1));
		else if (str.equals(")") || str.equals("]"))
			return new Token(RightParenthesis, ")");
		else if (str.equals(","))
			return new Token(Comma, ",");
		else if (str.equals(":-"))
			return new Token(QuantifiedExpression, ":-");
		else if (str.equals(":"))
			return new Token(Declaration, ":");
		if (precedence(str) > 0)
			return new Token(Operator, str);
		else
			return new Token(Constant, str);
	}

	public static Expression parse(String expression) {
		expression = expression.replace("(", "( ").replace(",", " , ").replace(")", " ) ");
		try {
			return new Parser().makeExpression(expression.trim()).simplify();
		} catch (Exception x) {
			System.err.println("PARSE ERROR:" + expression);
			return null;
		}
	}

	public Expression makeExpression(String infix) {
		// System.err.println("Parsing: " + infix);

		Stack<Token> operators = new Stack<>();
		Stack<Expression> operands = new Stack<>();

		List<Token> tokens = tokenize(infix);
		// for (Token token : tokenize(infix)) {
		while (tokens.size() > 0) {
			Token token = tokens.remove(0);
			// System.err.println(token + "\t" + tokens);
			// System.err.println(operators);
			// System.err.println(operands);
			switch (token.type) {
			case Constant:
				processConstant(token, operands);
				break;
			case LeftParenthesis:
				processLeftParenthesis(token, operators);
				break;
			case RightParenthesis:
				processRightParenthesis(token, operators, operands);
				break;
			case QuantifiedExpression:
			case Operator:
				processOperator(token, operators, operands);
				break;
			case Function:
				processFunction(token, operators, operands);
				break;
			case Declaration:
				processDeclaration(token, operators, operands);
				break;
			case Comma:
				processComma(token, operators, operands);
				break;
			default:
				System.err.println("Unexpected token type..." + token);
			}
		}
		processRemainingOperators(operators, operands);

		// all operators handled, should be only one operand left, the result
		if (operands.size() == 1) {
			// System.err.println("Result: " + operands.peek().root());

			return operands.pop().root();
		} else
			throw ParserException.BAD_SYNTAX;
	}

	protected List<Token> tokenize(String infix) {
		// Arrays.asList(infix.split("\\s+")).stream().map(i ->
		// tokenFrom(i)).collect(Collectors.toList()).forEach(x -> {
		// System.err.printf("|%s|\n", x);
		// });

		return Arrays.asList(infix.split("\\s+")).stream().map(i -> tokenFrom(i)).collect(Collectors.toList());
	}

	private void processComma(Token token, Stack<Token> operators, Stack<Expression> operands) {
		if (operators.size() > 0) {
			if (operators.peek().isNot(LeftParenthesis, Comma)) {
				Token op = operators.pop();
				operands.add(createExpression(op, operands));
			}
			operators.push(token);
		}
	}

	private void processFunction(Token token, Stack<Token> operators, Stack<Expression> operands) {
		operators.push(token);
		operators.push(new Token(LeftParenthesis, "("));
	}

	private void processConstant(Token token, Stack<Expression> operands) {
		operands.add(createExpression(token));
	}

	private void processLeftParenthesis(Token token, Stack<Token> operators) {
		operators.push(token);
	}

	private void processRightParenthesis(Token token, Stack<Token> operators, Stack<Expression> operands) {
		int commas = 0;

		while (operators.size() > 0 && operators.peek().isNot(LeftParenthesis)) {
			Token op = operators.pop();
			if (op.is(Comma))
				commas++;
			else
				operands.add(createExpression(op, operands));
		}

		if (operators.size() > 0 && operators.peek().is(LeftParenthesis)) {
			operators.pop();
			if (operators.size() > 0 && operators.peek().is(Function))
				operands.add(createFunctionExpression(operators.pop(), operands, commas + 1));
		}
	}

	private void processOperator(Token token, Stack<Token> operators, Stack<Expression> operands) {
		int order = precedence(token);
		while (operators.size() > 0 && precedence(operators.peek()) >= order) {
			Token op = operators.pop();
			operands.add(createExpression(op, operands));
		}
		operators.push(token);
	}

	private void processDeclaration(Token token, Stack<Token> operators, Stack<Expression> operands) {
		operators.push(token);
	}

	// Resolve any remaining operators
	private void processRemainingOperators(Stack<Token> operators, Stack<Expression> operands) {
		while (operators.size() > 0) {
			Token op = operators.pop();
			switch (op.type) {
			case Operator:
			case Declaration:
			case QuantifiedExpression:
				operands.add(createExpression(op, operands));
				break;
			case LeftParenthesis:
				return;
			default:
			}
		}
	}

	private Expression createExpression(Token token) {
		return new ConstantExpression(token.value);
	}

	private Expression createFunctionExpression(Token token, Stack<Expression> operands, int params) {
		return new FunctionExpression(token.value, operands, params);
	}

	private Expression createExpression(Token op, Stack<Expression> operands) {
		// convert operator into expression
		if (unary(op) && operands.size() >= 1)
			return new UnaryExpression(op.value, operatorType(op), operands.pop());
		else if (operands.size() >= 2) {
			Expression right = operands.pop();
			Expression left = operands.pop();
			if (op.is(TokenType.Declaration))
				return new DeclarationExpression(left, right);
			else if (op.is(TokenType.QuantifiedExpression)) {
				return new QuantifiedExpression(left, right);
			} else
				return new BinaryExpression(op.value, operatorType(op), left, right);
		}
		throw new RuntimeException("Parse error...");
	}

	static String stringList(String format, List<?> params, String separator) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = params.iterator();

		while (it.hasNext()) {
			sb.append(String.format(format, it.next()));
			if (it.hasNext())
				sb.append(separator);
		}
		return sb.toString();
	}

	public abstract static class Expression {
		String value;
		String type;

		Expression(String value) {
			this.value = value;
		}

		public static Expression merge(Expression... formulas) {
			return merge(Arrays.asList(formulas));
		}

		public static Expression merge(Collection<Expression> formulas) {
			Set<Expression> params = new HashSet<>();
			List<Expression> right = new ArrayList<>();

			for (Expression i : formulas)
				if (i instanceof QuantifiedExpression) {
					QuantifiedExpression qe = (QuantifiedExpression) i;
					params.addAll(qe.quantifier.params);
					right.add(qe.expression);
				} else
					right.add(i);

			StringBuilder sb = new StringBuilder("forall(");
			sb.append(stringList("%s", Lists.newArrayList(params), ",")).append(") :- ");
			sb.append(stringList("( %s )", right, " and "));
			return Parser.parse(sb.toString());
		}

		public String type() {
			return type;
		}

		public Expression push(String type) {
			return this;
		}

		public Expression root() {
			return this;
		}

		public abstract <T> T evaluate(ASTVisitor e);

		public void replace(String target, String expr) {
			if (target.equals(value))
				value = expr;
		}

		public Bindings matches(Expression other) {
			if (value.equals(other.value))
				return new Bindings(this, other);
			else
				return new Bindings();
		}

		@Override
		public int hashCode() {
			return toString().hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return toString().equals(other.toString());
		}

		public Expression simplify() {
			return this;
		}

		public String enclosed() {
			return new StringBuilder("( ").append(toString()).append(" )").toString();
		}
	}
}

class QuantifiedExpression extends Parser.Expression {
	static int counter = 0;
	FunctionExpression quantifier;
	List<Expression> params = new ArrayList<>();

	Expression expression;

	QuantifiedExpression(Expression quantifier, Expression expression) {
		super(quantifier.value + counter++);
		this.quantifier = (FunctionExpression) quantifier;
		this.expression = expression.root();
	}

	@Override
	public <T> T evaluate(ASTVisitor e) {
		List<T> tmp = new ArrayList<>();
		quantifier.params.forEach(i -> {
			tmp.add(i.evaluate(e));
		});
		return e.evalQuantiedExpression(this, value, expression.evaluate(e), tmp);
	}

	@Override
	public String toString() {
		return new StringBuilder(quantifier.toString()).append(" :- ").append(expression).toString();
	}

	@Override
	public Bindings matches(Expression other) {
		return expression.matches(other);
	}

	@Override
	public void replace(String target, String expr) {
		if (target.equals(expression.value))
			expression = Parser.parse(expr);
		else
			expression.replace(target, expr);
		quantifier.replace(target, null);
	}

	@Override
	public Expression simplify() {
		if (quantifier.params.isEmpty())
			return expression;
		else
			return this;
	}
}

class FunctionExpression extends Parser.Expression {

	List<Expression> params = new ArrayList<>();

	public FunctionExpression(String value, Stack<Expression> operands, int numParams) {
		super(value);
		while (numParams-- > 0)
			params.add(operands.pop().root());

		params = Lists.reverse(params);
		type = null;
	}

	@Override
	public Expression push(String type) {
		this.type = type;
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T evaluate(ASTVisitor e) {
		// System.err.println("-->" + value);
		List<T> tmp = new ArrayList<>();
		params.forEach(i -> {
			// System.err.println(i + "/" + i.getClass());
			tmp.add(i.evaluate(e));
		});
		return (T) e.evalFunction(this, value, type, tmp);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(value).append("( ");
		Iterator<Expression> it = params.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext())
				sb.append(" , ");
		}
		return sb.append(" )").toString();
	}

	@Override
	public Bindings matches(Expression other) {
		return (value.equals(other.value)) ? new Bindings(this, other) : null;
	}

	@Override
	public void replace(String target, String expr) {
		for (Iterator<Expression> it = params.iterator(); it.hasNext();) {
			Expression e = it.next();
			if (target.matches(e.value))
				if (expr == null)
					it.remove();
				else
					e.replace(target, expr);
		}
	}
}

class ConstantExpression extends Parser.Expression {

	public ConstantExpression(String value) {
		super(value);
	}

	@Override
	public <T> T evaluate(ASTVisitor e) {
		return e.evalConstant(this, value);
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public Expression push(String type) {
		this.type = type;
		return this;
	}
}

class DeclarationExpression extends ConstantExpression {

	public DeclarationExpression(Expression left, Expression right) {
		super(right.value);
		type = left.value;
	}

	@Override
	public <T> T evaluate(ASTVisitor e) {
		return e.evalDeclaration(this, type, value);
	}

	@Override
	public String toString() {
		return new StringBuffer(type).append(" : ").append(value).toString();
	}
}

class BinaryExpression extends Parser.Expression {
	Expression myLeft;
	Expression myRight;

	boolean isRoot;

	public BinaryExpression(String op, String type, Expression left, Expression right) {
		super(op);
		myLeft = left;
		myRight = right;
		isRoot = false;
		this.type = type;
		myLeft.push(type);
		myRight.push(type);
	}

	@Override
	public Expression push(String type) {
		String t = this.type == null ? type : this.type;
		myLeft.push(t);
		myRight.push(t);
		return this;
	}

	@Override
	public Expression root() {
		isRoot = true;
		return this;
	}

	@Override
	public <T> T evaluate(ASTVisitor e) {
		push(type);
		return e.evalBinaryOperation(this, value, myLeft.evaluate(e), myRight.evaluate(e));
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("(");
		result.append(myLeft.toString());
		result.append(" " + value + " ");
		result.append(myRight.toString()).append(")");
		return result.toString();
	}

	@Override
	public void replace(String target, String expr) {
		if (target.equals(myLeft.toString()))
			myLeft = Parser.parse(expr);
		else
			myLeft.replace(target, expr);

		if (target.equals(myRight.toString()))
			myRight = Parser.parse(expr);
		else
			myRight.replace(target, expr);
	}

	@Override
	public Bindings matches(Expression other) {
		Bindings l = myLeft.matches(other);
		Bindings r = myRight.matches(other);
		return new Bindings(l, r);
	}
}

class UnaryExpression extends Parser.Expression {
	Expression myRight;

	public UnaryExpression(String op, String type, Expression right) {
		super(op);
		this.type = type;
		this.myRight = right;
	}

	@Override
	public <T> T evaluate(ASTVisitor e) {
		return e.evalUnaryOperation(this, value, myRight.evaluate(e));
	}

	@Override
	public String toString() {
		return new StringBuffer(value).append(" ").append(myRight).toString();
	}

	@Override
	public Expression push(String type) {
		myRight.push(type);
		return this;
	}

	@Override
	public void replace(String target, String expr) {
		if (target.equals(myRight.toString()))
			myRight = Parser.parse(expr);
		else
			myRight.replace(target, expr);
	}

	@Override
	public Bindings matches(Expression other) {
		return myRight.matches(other);
	}
}

@SuppressWarnings("serial")
class ParserException extends RuntimeException {
	// BUGBUG: should be extendible, i.e., get message text from file
	public static ParserException BAD_TOKEN = new ParserException("unrecognized input");

	public static ParserException BAD_ARGUMENTS = new ParserException("not enough arguments");

	public static ParserException BAD_SYNTAX = new ParserException("ill-formatted expression");

	/**
	 * Create exception with given message
	 *
	 * @param message
	 *            explanation of problem
	 */
	public ParserException(String message) {
		super(message);
	}
}
