package z3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Z3Exception;

import indigo.runtime.Parser;

public class Z3Evaluator implements Parser.ASTVisitor {

	static final Pattern bool = Pattern.compile("true|false");
	static final Pattern number = Pattern.compile("\\d+");

	final Z3 z3;

	public Z3Evaluator(Z3 z3) {
		this.z3 = z3;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T> T evalConstant(P parent, String val) {
		try {
			if (bool.matcher(val).matches())
				return (T) z3.Bool(Boolean.valueOf(val));
			else if (number.matcher(val).matches())
				return (T) z3.Int(Integer.valueOf(val));

			return (T) z3.constant(val, 0);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalBinaryOperation(P parent, String op, E left, E right) {
		try {
			switch (op) {
			case "=":
				return (T) z3.ctx.mkEq((Expr) left, (Expr) right);
			case "==":
				return (T) z3.ctx.mkEq((Expr) left, (Expr) right);
			case "<>":
				return (T) z3.Not(z3.ctx.mkEq((Expr) left, (Expr) right));
			case "<":
				return (T) z3.ctx.mkLt((ArithExpr) left, (ArithExpr) right);
			case "<=":
				return (T) z3.ctx.mkLe((ArithExpr) left, (ArithExpr) right);
			case ">":
				return (T) z3.ctx.mkGt((ArithExpr) left, (ArithExpr) right);
			case ">=":
				return (T) z3.ctx.mkGe((ArithExpr) left, (ArithExpr) right);
			case "+":
				return (T) z3.ctx.mkAdd((ArithExpr) left, (ArithExpr) right);
			case "-":
				return (T) z3.ctx.mkSub((ArithExpr) left, (ArithExpr) right);
			case "*":
				return (T) z3.ctx.mkMul((ArithExpr) left, (ArithExpr) right);
			case "/":
				return (T) z3.ctx.mkDiv((ArithExpr) left, (ArithExpr) right);
			case "and":
			case "/\\":
				// System.err.println(parent + "   " + left.getClass() + "   " +
				// right.getClass());
				return (T) z3.ctx.mkAnd((BoolExpr) left, (BoolExpr) right);
			case "or":
			case "\\/":
				return (T) z3.ctx.mkOr((BoolExpr) left, (BoolExpr) right);
			case "=>":
				return (T) z3.ctx.mkImplies((BoolExpr) left, (BoolExpr) right);
			case "<=>":
				return (T) z3.ctx.mkNot(z3.ctx.mkXor((BoolExpr) left, (BoolExpr) right));
			default:
			}
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P, T, E> T evalUnaryOperation(P parent, String op, E right) {
		try {
			switch (op) {
			case "not":
				return (T) z3.ctx.mkNot((BoolExpr) right);
			}
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalFunction(P parent, String name, String ret, List<E> params) {
		try {
			List<Sort> domains = new ArrayList<>();
			List<Expr> args = new ArrayList<>();
			for (E i : params) {
				Expr j = (Expr) i;
				// System.err.println(j.getClass());
				// System.err.println(name + " - > " + i + "/" + z3.Sort(j));
				domains.add(z3.Sort(j));
				args.add(j);
			}
			FuncDecl func = z3.decl_function(name, z3.sortFrom(ret), domains.toArray(new Sort[0]));
			Expr res = func.apply(args.toArray(new Expr[0]));
			return (T) res;
		} catch (Z3Exception e) {
			System.err.println(name);
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalDeclaration(P parent, String type, String var) {
		try {
			z3.sortFrom(type);
			return (T) z3.constant(var, type);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, B, E> T evalQuantiedExpression(P parent, String type, B body, List<E> bounds) {
		return (T) z3.forAll(bounds.toArray(new Expr[0]), (Expr) body);
	}
}
