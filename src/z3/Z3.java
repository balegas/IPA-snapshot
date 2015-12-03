package z3;

import indigo.Parser.Expression;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.IO;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

public class Z3 {

	final Context ctx;
	final Solver solver;
	final boolean show;

	final Set<Expression> tAssertions = new HashSet<>();
	final Set<Expression> fAssertions = new HashSet<>();
	private int counter;

	public Z3(boolean show) {
		try {
			this.show = show;
			ctx = new Context();
			// Context.ToggleWarningMessages(true);
			solver = ctx.mkSolver();
		} catch (Z3Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public Expr interpretation(Model m, Expr e) {
		try {
			return m.getConstInterp(e);
		} catch (Exception x) {
			return null;
		}

	}

	public Sort domain(String name) throws Z3Exception {
		return sortFrom(name);
	}

	public IntExpr Int(int i) throws Z3Exception {
		return ctx.mkInt(i);
	}

	public BoolExpr Bool(boolean val) throws Z3Exception {
		return ctx.mkBool(val);
	}

	public ArithExpr Add(IntExpr a, int i) throws Z3Exception {
		return ctx.mkAdd(a, Int(i));
	}

	ArithExpr Sub(IntExpr a, int i) throws Z3Exception {
		return ctx.mkSub(a, Int(i));
	}

	public BoolExpr True() throws Z3Exception {
		return ctx.mkTrue();
	}

	public BoolExpr False() throws Z3Exception {
		return ctx.mkFalse();
	}

	public BoolExpr Not(BoolExpr e) throws Z3Exception {
		return ctx.mkNot(e);
	}

	public BoolExpr Equals(Expr e, boolean val) throws Z3Exception {
		return ctx.mkEq(e, ctx.mkBool(val));
	}

	public BoolExpr Equals(ArithExpr e, ArithExpr val) throws Z3Exception {
		return ctx.mkEq(e, val);
	}

	public BoolExpr LessThan(ArithExpr e, int val) throws Z3Exception {
		return ctx.mkLt(e, Int(val));
	}

	public BoolExpr And(BoolExpr... list) throws Z3Exception {
		return ctx.mkAnd(list);
	}

	public FuncDecl decl_predicate(String name, String... types) throws Z3Exception {
		FuncDecl res = predicates.get(name);
		if (res == null) {
			List<Sort> domains = new ArrayList<>();
			for (String i : types)
				domains.add(sortFrom(i));
			res = ctx.mkFuncDecl(name, domains.toArray(new Sort[0]), ctx.mkBoolSort());
			System.out.println(res);
			predicates.put(name, res);
		}
		return res;
	}

	public FuncDecl decl_function(String name, Sort ret, Sort... domains) throws Z3Exception {
		FuncDecl res = predicates.get(name);
		if (res == null) {
			res = ctx.mkFuncDecl(name, domains, ret);
			predicates.put(name, res);
			if (show)
				System.out.println(res);
		}
		return res;
	}

	public BoolExpr increment(String name, Integer... delta) throws Z3Exception {
		int inc = delta.length == 0 ? 1 : delta[0];
		if (inc < 0)
			throw new InvalidParameterException("requires positive increment...");
		IntExpr var = constant(name, 0);
		return ctx.mkEq(var, ctx.mkAdd(var, ctx.mkInt(inc)));
	}

	public BoolExpr decrement(String name, Integer... delta) throws Z3Exception {
		int dec = delta.length == 0 ? -1 : delta[0];
		if (dec < 0)
			throw new InvalidParameterException("requires positive decrement...");

		IntExpr var = constant(name, 0);
		return ctx.mkEq(var, ctx.mkSub(var, ctx.mkInt(dec)));
	}

	public static String z3name(String name) {
		return name.replaceAll("\\.", "");
	}

	Sort sortFrom(Object type) throws Z3Exception {
		String typeName;
		if (type instanceof Class)
			typeName = ((Class<?>) type).getSimpleName();
		else {
			if (type instanceof String)
				typeName = (String) type;
			else
				typeName = type.getClass().getSimpleName();
		}

		switch (typeName) {
		case "int":
		case "Int":
		case "Integer":
			return ctx.getIntSort();
		case "bool":
		case "Bool":
		case "Boolean":
			return ctx.getBoolSort();
		case "String":
		default:
			Sort s = sorts.get(typeName);
			if (s == null) {
				sorts.put(typeName, s = ctx.mkUninterpretedSort(typeName));
				if (show) {
					System.out.printf("(declare-sort %s)\n", typeName);
				}
			}
			return s;
		}
	}

	public Expr[] constants(String[] names, String[] types) throws Z3Exception {
		List<Expr> res = new ArrayList<>();
		int j = 0;
		for (String i : names)
			res.add(constant(i + "0", types[j++]));
		return res.toArray(new Expr[res.size()]);
	}

	@SuppressWarnings("unchecked")
	public <T extends Expr> T constant(String name, Object value) throws Z3Exception {
		// String key = name + "_" + sort.Name();
		String key = name;
		Expr res = constants.get(key);
		if (res == null) {
			Sort sort = sortFrom(value);
			constants.put(key, res = ctx.mkConst(name, sort));
			if (name.length() > 1 && show)
				System.out.printf("(declare-const %s %s)\n", name, sort.getName());
		}
		return (T) res;
	}

	public void Assert(Expression e) {
		tAssertions.add(e);
	}

	public void Assert(Expression e, boolean value) {
		if (value == true)
			tAssertions.add(e);
		else
			fAssertions.add(e);
	}

	public void Assert(Collection<Expression> ce) {
		ce.forEach(i -> Assert(i));
	}

	private void doAssert(BoolExpr expr, boolean value) {
		try {
			if (value == true)
				solver.assertAndTrack(expr, ctx.mkBoolConst("P " + (counter++)));
			else
				solver.assertAndTrack(Not(expr), ctx.mkBoolConst("P " + (counter++)));
		} catch (Z3Exception e1) {
			throw new RuntimeException(e1.getMessage());
		}
	}

	private void doAssertions() {
		Z3Evaluator z3e = new Z3Evaluator(this);
		tAssertions.forEach(f -> {
			doAssert(f.evaluate(z3e), true);
		});
		fAssertions.forEach(f -> {
			doAssert(f.evaluate(z3e), false);
		});
	}

	public void Scalars() {
		Map<String, Set<String>> declarations = new HashMap<>();
		tAssertions.forEach(i -> i.evaluate(new Z3DeclarionCollector(declarations)));
		fAssertions.forEach(i -> i.evaluate(new Z3DeclarionCollector(declarations)));
	}

	public Sort Sort(Expr e) {
		try {
			return e.getSort();
		} catch (Z3Exception e1) {
			return null;
		}
	}

	public Expr FreshConst(Expr e, Sort s) {
		try {
			return constant(e.getFuncDecl().getName().toString() + constants.size(), s.getName().toString());
		} catch (Z3Exception e1) {
			return null;
		}
	}

	public Expr forAll(Expr[] vars, Expr body) {
		try {
			return ctx.mkForall(vars, body, 1, null, null, null, null);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void Dispose() {
		try {
			for (Expr e : constants.values())
				e.dispose();
			for (Sort s : sorts.values())
				s.dispose();

			for (FuncDecl f : predicates.values())
				f.dispose();

			solver.dispose();
			ctx.dispose();
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

	public boolean Check(boolean show) {
		try {
			Scalars();
			doAssertions();
			boolean res = solver.check() == Status.SATISFIABLE;
			if (show) {
				dumpAssertions();
				System.out.println("; " + (res ? "SAT" : "UnSAT"));
			}
			return res;
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void dumpAssertions() {
		try {
			Arrays.asList(solver.getAssertions()).forEach(a -> {
				System.out.printf("(assert %s)\n", a.toString());
			});
			System.out.println("; ++++++++++++++++++++++++++++++++++++++++++++++++");
			tAssertions.forEach((a) -> {
				System.out.printf("; indigo\t %s\n", a.toString());
			});
			fAssertions.forEach((a) -> {
				System.out.printf("; indigo\t NOT %s\n", a.toString());
			});
			IO.flush();
		} catch (Z3Exception e) {
		}
	}

	Map<String, Expr> constants = new HashMap<>();
	Map<Object, Sort> sorts = new HashMap<Object, Sort>();
	Map<String, FuncDecl> predicates = new HashMap<>();
}
