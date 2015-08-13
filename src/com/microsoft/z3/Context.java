/**
 * This file was automatically generated from Context.cs 
 * w/ further modifications by:
 * @author Christoph M. Wintersteiger (cwinter)
 **/

package com.microsoft.z3;

import java.util.Map;

import com.microsoft.z3.enumerations.Z3_ast_print_mode;

/**
 * The main interaction with Z3 happens via the Context.
 **/
public class Context extends IDisposable {
	/**
	 * Constructor.
	 **/
	public Context() throws Z3Exception {
		super();
		m_ctx = Native.mkContextRc(0);
		InitContext();
	}

	/**
	 * Constructor.
	 **/
	public Context(Map<String, String> settings) throws Z3Exception {
		super();
		long cfg = Native.mkConfig();
		for (Map.Entry<String, String> kv : settings.entrySet())
			Native.setParamValue(cfg, kv.getKey(), kv.getValue());
		m_ctx = Native.mkContextRc(cfg);
		Native.delConfig(cfg);
		InitContext();
	}

	private Context(long ctx, long refCount) {
		super();
		this.m_ctx = ctx;
		this.m_refCount = refCount;
	}

	/**
	 * Creates a new symbol using an integer. <remarks> Not all integers can be
	 * passed to this function. The legal range of unsigned integers is 0 to
	 * 2^30-1. </remarks>
	 **/
	public IntSymbol MkSymbol(int i) throws Z3Exception {
		return new IntSymbol(this, i);
	}

	/**
	 * Create a symbol using a string.
	 **/
	public StringSymbol MkSymbol(String name) throws Z3Exception {
		return new StringSymbol(this, name);
	}

	/**
	 * Create an array of symbols.
	 **/
	Symbol[] MkSymbols(String[] names) throws Z3Exception {
		if (names == null)
			return null;
		Symbol[] result = new Symbol[names.length];
		for (int i = 0; i < names.length; ++i)
			result[i] = MkSymbol(names[i]);
		return result;
	}

	private BoolSort m_boolSort = null;
	private IntSort m_intSort = null;
	private RealSort m_realSort = null;

	/**
	 * Retrieves the Boolean sort of the context.
	 **/
	public BoolSort BoolSort() throws Z3Exception {
		if (m_boolSort == null)
			m_boolSort = new BoolSort(this);
		return m_boolSort;
	}

	/**
	 * Retrieves the Integer sort of the context.
	 **/
	public IntSort IntSort() throws Z3Exception {
		if (m_intSort == null)
			m_intSort = new IntSort(this);
		return m_intSort;
	}

	/**
	 * Retrieves the Real sort of the context.
	 **/
	public RealSort RealSort() throws Z3Exception {
		if (m_realSort == null)
			m_realSort = new RealSort(this);
		return m_realSort;
	}

	/**
	 * Create a new Boolean sort.
	 **/
	public BoolSort MkBoolSort() throws Z3Exception {

		return new BoolSort(this);
	}

	/**
	 * Create a new uninterpreted sort.
	 **/
	public UninterpretedSort MkUninterpretedSort(Symbol s) throws Z3Exception {

		CheckContextMatch(s);
		return new UninterpretedSort(this, s);
	}

	/**
	 * Create a new uninterpreted sort.
	 **/
	public UninterpretedSort MkUninterpretedSort(String str) throws Z3Exception {

		return MkUninterpretedSort(MkSymbol(str));
	}

	/**
	 * Create a new integer sort.
	 **/
	public IntSort MkIntSort() throws Z3Exception {

		return new IntSort(this);
	}

	/**
	 * Create a real sort.
	 **/
	public RealSort MkRealSort() throws Z3Exception {

		return new RealSort(this);
	}

	/**
	 * Create a new bit-vector sort.
	 **/
	public BitVecSort MkBitVecSort(int size) throws Z3Exception {

		return new BitVecSort(this, Native.mkBvSort(nCtx(), size));
	}

	/**
	 * Create a new array sort.
	 **/
	public ArraySort MkArraySort(Sort domain, Sort range) throws Z3Exception {

		CheckContextMatch(domain);
		CheckContextMatch(range);
		return new ArraySort(this, domain, range);
	}

	/**
	 * Create a new tuple sort.
	 **/
	public TupleSort MkTupleSort(Symbol name, Symbol[] fieldNames, Sort[] fieldSorts) throws Z3Exception {

		CheckContextMatch(name);
		CheckContextMatch(fieldNames);
		CheckContextMatch(fieldSorts);
		return new TupleSort(this, name, (int) fieldNames.length, fieldNames, fieldSorts);
	}

	/**
	 * Create a new enumeration sort.
	 **/
	public EnumSort MkEnumSort(Symbol name, Symbol[] enumNames) throws Z3Exception {

		CheckContextMatch(name);
		CheckContextMatch(enumNames);
		return new EnumSort(this, name, enumNames);
	}

	/**
	 * Create a new enumeration sort.
	 **/
	public EnumSort MkEnumSort(String name, String[] enumNames) throws Z3Exception {

		return new EnumSort(this, MkSymbol(name), MkSymbols(enumNames));
	}

	/**
	 * Create a new list sort.
	 **/
	public ListSort MkListSort(Symbol name, Sort elemSort) throws Z3Exception {

		CheckContextMatch(name);
		CheckContextMatch(elemSort);
		return new ListSort(this, name, elemSort);
	}

	/**
	 * Create a new list sort.
	 **/
	public ListSort MkListSort(String name, Sort elemSort) throws Z3Exception {

		CheckContextMatch(elemSort);
		return new ListSort(this, MkSymbol(name), elemSort);
	}

	/**
	 * Create a new finite domain sort.
	 **/
	public FiniteDomainSort MkFiniteDomainSort(Symbol name, long size) throws Z3Exception {

		CheckContextMatch(name);
		return new FiniteDomainSort(this, name, size);
	}

	/**
	 * Create a new finite domain sort.
	 **/
	public FiniteDomainSort MkFiniteDomainSort(String name, long size) throws Z3Exception {

		return new FiniteDomainSort(this, MkSymbol(name), size);
	}

	/**
	 * Create a datatype constructor. <param name="name">constructor
	 * name</param> <param name="recognizer">name of recognizer
	 * function.</param> <param name="fieldNames">names of the constructor
	 * fields.</param> <param name="sorts">field sorts, 0 if the field sort
	 * refers to a recursive sort.</param> <param name="sortRefs">reference to
	 * datatype sort that is an argument to the constructor; if the
	 * corresponding sort reference is 0, then the value in sort_refs should be
	 * an index referring to one of the recursive datatypes that is
	 * declared.</param>
	 **/
	public Constructor MkConstructor(Symbol name, Symbol recognizer, Symbol[] fieldNames, Sort[] sorts, int[] sortRefs) throws Z3Exception {

		return new Constructor(this, name, recognizer, fieldNames, sorts, sortRefs);
	}

	/**
	 * Create a datatype constructor. <param name="name"></param> <param
	 * name="recognizer"></param> <param name="fieldNames"></param> <param
	 * name="sorts"></param> <param name="sortRefs"></param>
	 * 
	 * @return
	 **/
	public Constructor MkConstructor(String name, String recognizer, String[] fieldNames, Sort[] sorts, int[] sortRefs) throws Z3Exception {

		return new Constructor(this, MkSymbol(name), MkSymbol(recognizer), MkSymbols(fieldNames), sorts, sortRefs);
	}

	/**
	 * Create a new datatype sort.
	 **/
	public DatatypeSort MkDatatypeSort(Symbol name, Constructor[] constructors) throws Z3Exception {

		CheckContextMatch(name);
		CheckContextMatch(constructors);
		return new DatatypeSort(this, name, constructors);
	}

	/**
	 * Create a new datatype sort.
	 **/
	public DatatypeSort MkDatatypeSort(String name, Constructor[] constructors) throws Z3Exception {

		CheckContextMatch(constructors);
		return new DatatypeSort(this, MkSymbol(name), constructors);
	}

	/**
	 * Create mutually recursive datatypes. <param name="names">names of
	 * datatype sorts</param> <param name="c">list of constructors, one list per
	 * sort.</param>
	 **/
	public DatatypeSort[] MkDatatypeSorts(Symbol[] names, Constructor[][] c) throws Z3Exception {

		CheckContextMatch(names);
		int n = (int) names.length;
		ConstructorList[] cla = new ConstructorList[n];
		long[] n_constr = new long[n];
		for (int i = 0; i < n; i++) {
			Constructor[] constructor = c[i];

			CheckContextMatch(constructor);
			cla[i] = new ConstructorList(this, constructor);
			n_constr[i] = cla[i].NativeObject();
		}
		long[] n_res = new long[n];
		Native.mkDatatypes(nCtx(), n, Symbol.ArrayToNative(names), n_res, n_constr);
		DatatypeSort[] res = new DatatypeSort[n];
		for (int i = 0; i < n; i++)
			res[i] = new DatatypeSort(this, n_res[i]);
		return res;
	}

	/**
	 * Create mutually recursive data-types. <param name="names"></param> <param
	 * name="c"></param>
	 * 
	 * @return
	 **/
	public DatatypeSort[] MkDatatypeSorts(String[] names, Constructor[][] c) throws Z3Exception {

		return MkDatatypeSorts(MkSymbols(names), c);
	}

	/**
	 * Creates a new function declaration.
	 **/
	public FuncDecl MkFuncDecl(Symbol name, Sort[] domain, Sort range) throws Z3Exception {

		CheckContextMatch(name);
		CheckContextMatch(domain);
		CheckContextMatch(range);
		return new FuncDecl(this, name, domain, range);
	}

	/**
	 * Creates a new function declaration.
	 **/
	public FuncDecl MkFuncDecl(Symbol name, Sort domain, Sort range) throws Z3Exception {

		CheckContextMatch(name);
		CheckContextMatch(domain);
		CheckContextMatch(range);
		Sort[] q = new Sort[] { domain };
		return new FuncDecl(this, name, q, range);
	}

	/**
	 * Creates a new function declaration.
	 **/
	public FuncDecl MkFuncDecl(String name, Sort[] domain, Sort range) throws Z3Exception {

		CheckContextMatch(domain);
		CheckContextMatch(range);
		return new FuncDecl(this, MkSymbol(name), domain, range);
	}

	/**
	 * Creates a new function declaration.
	 **/
	public FuncDecl MkFuncDecl(String name, Sort domain, Sort range) throws Z3Exception {

		CheckContextMatch(domain);
		CheckContextMatch(range);
		Sort[] q = new Sort[] { domain };
		return new FuncDecl(this, MkSymbol(name), q, range);
	}

	/**
	 * Creates a fresh function declaration with a name prefixed with <paramref
	 * name="prefix"/>. <seealso cref="MkFuncDecl(string,Sort,Sort)"/> <seealso
	 * cref="MkFuncDecl(string,Sort[],Sort)"/>
	 **/
	public FuncDecl MkFreshFuncDecl(String prefix, Sort[] domain, Sort range) throws Z3Exception {

		CheckContextMatch(domain);
		CheckContextMatch(range);
		return new FuncDecl(this, prefix, domain, range);
	}

	/**
	 * Creates a new constant function declaration.
	 **/
	public FuncDecl MkConstDecl(Symbol name, Sort range) throws Z3Exception {

		CheckContextMatch(name);
		CheckContextMatch(range);
		return new FuncDecl(this, name, null, range);
	}

	/**
	 * Creates a new constant function declaration.
	 **/
	public FuncDecl MkConstDecl(String name, Sort range) throws Z3Exception {

		CheckContextMatch(range);
		return new FuncDecl(this, MkSymbol(name), null, range);
	}

	/**
	 * Creates a fresh constant function declaration with a name prefixed with
	 * <paramref name="prefix"/>. <seealso cref="MkFuncDecl(string,Sort,Sort)"/>
	 * <seealso cref="MkFuncDecl(string,Sort[],Sort)"/>
	 **/
	public FuncDecl MkFreshConstDecl(String prefix, Sort range) throws Z3Exception {

		CheckContextMatch(range);
		return new FuncDecl(this, prefix, null, range);
	}

	/**
	 * Creates a new bound variable. <param name="index">The de-Bruijn index of
	 * the variable</param> <param name="ty">The sort of the variable</param>
	 **/
	public Expr MkBound(int index, Sort ty) throws Z3Exception {
		return Expr.Create(this, Native.mkBound(nCtx(), index, ty.NativeObject()));
	}

	/**
	 * Create a quantifier pattern.
	 **/
	public Pattern MkPattern(Expr[] terms) throws Z3Exception {
		if (terms.length == 0)
			throw new Z3Exception("Cannot create a pattern from zero terms");

		long[] termsNative = AST.ArrayToNative(terms);
		return new Pattern(this, Native.mkPattern(nCtx(), (int) terms.length, termsNative));
	}

	/**
	 * Creates a new Constant of sort <paramref name="range"/> and named
	 * <paramref name="name"/>.
	 **/
	public Expr MkConst(Symbol name, Sort range) throws Z3Exception {

		CheckContextMatch(name);
		CheckContextMatch(range);

		return Expr.Create(this, Native.mkConst(nCtx(), name.NativeObject(), range.NativeObject()));
	}

	/**
	 * Creates a new Constant of sort <paramref name="range"/> and named
	 * <paramref name="name"/>.
	 **/
	public Expr MkConst(String name, Sort range) throws Z3Exception {

		return MkConst(MkSymbol(name), range);
	}

	/**
	 * Creates a fresh Constant of sort <paramref name="range"/> and a name
	 * prefixed with <paramref name="prefix"/>.
	 **/
	public Expr MkFreshConst(String prefix, Sort range) throws Z3Exception {

		CheckContextMatch(range);
		return Expr.Create(this, Native.mkFreshConst(nCtx(), prefix, range.NativeObject()));
	}

	/**
	 * Creates a fresh constant from the FuncDecl <paramref name="f"/>. <param
	 * name="f">A decl of a 0-arity function</param>
	 **/
	public Expr MkConst(FuncDecl f) throws Z3Exception {

		return MkApp(f, (Expr[]) null);
	}

	/**
	 * Create a Boolean constant.
	 **/
	public BoolExpr MkBoolConst(Symbol name) throws Z3Exception {

		return (BoolExpr) MkConst(name, BoolSort());
	}

	/**
	 * Create a Boolean constant.
	 **/
	public BoolExpr MkBoolConst(String name) throws Z3Exception {

		return (BoolExpr) MkConst(MkSymbol(name), BoolSort());
	}

	/**
	 * Creates an integer constant.
	 **/
	public IntExpr MkIntConst(Symbol name) throws Z3Exception {

		return (IntExpr) MkConst(name, IntSort());
	}

	/**
	 * Creates an integer constant.
	 **/
	public IntExpr MkIntConst(String name) throws Z3Exception {

		return (IntExpr) MkConst(name, IntSort());
	}

	/**
	 * Creates a real constant.
	 **/
	public RealExpr MkRealConst(Symbol name) throws Z3Exception {

		return (RealExpr) MkConst(name, RealSort());
	}

	/**
	 * Creates a real constant.
	 **/
	public RealExpr MkRealConst(String name) throws Z3Exception {

		return (RealExpr) MkConst(name, RealSort());
	}

	/**
	 * Creates a bit-vector constant.
	 **/
	public BitVecExpr MkBVConst(Symbol name, int size) throws Z3Exception {

		return (BitVecExpr) MkConst(name, MkBitVecSort(size));
	}

	/**
	 * Creates a bit-vector constant.
	 **/
	public BitVecExpr MkBVConst(String name, int size) throws Z3Exception {

		return (BitVecExpr) MkConst(name, MkBitVecSort(size));
	}

	/**
	 * Create a new function application.
	 **/
	public Expr MkApp(FuncDecl f, Expr arg) throws Z3Exception {
		CheckContextMatch(f);
		CheckContextMatch(arg);
		Expr[] args = { arg };
		return Expr.Create(this, f, args);
	}

	/**
	 * Create a new function application.
	 **/
	public Expr MkApp(FuncDecl f, Expr... args) throws Z3Exception {

		CheckContextMatch(f);
		CheckContextMatch(args);
		return Expr.Create(this, f, args);
	}

	/**
	 * The true Term.
	 **/
	public BoolExpr MkTrue() throws Z3Exception {
		return new BoolExpr(this, Native.mkTrue(nCtx()));
	}

	/**
	 * The false Term.
	 **/
	public BoolExpr MkFalse() throws Z3Exception {
		return new BoolExpr(this, Native.mkFalse(nCtx()));
	}

	/**
	 * Creates a Boolean value.
	 **/
	public BoolExpr MkBool(boolean value) throws Z3Exception {
		return value ? MkTrue() : MkFalse();
	}

	/**
	 * Creates the equality <paramref name="x"/> = <paramref name="y"/>.
	 **/
	public BoolExpr MkEq(Expr x, Expr y) throws Z3Exception {
		CheckContextMatch(x);
		CheckContextMatch(y);
		return new BoolExpr(this, Native.mkEq(nCtx(), x.NativeObject(), y.NativeObject()));
	}

	/**
	 * Creates a <code>distinct</code> term.
	 **/
	public BoolExpr MkDistinct(Expr... args) throws Z3Exception {
		CheckContextMatch(args);
		return new BoolExpr(this, Native.mkDistinct(nCtx(), (int) args.length, AST.ArrayToNative(args)));
	}

	/**
	 * Mk an expression representing <code>not(a)</code>.
	 **/
	public BoolExpr MkNot(BoolExpr a) throws Z3Exception {

		CheckContextMatch(a);
		return new BoolExpr(this, Native.mkNot(nCtx(), a.NativeObject()));
	}

	/**
	 * Create an expression representing an if-then-else:
	 * <code>ite(t1, t2, t3)</code>. <param name="t1">An expression with Boolean
	 * sort</param> <param name="t2">An expression </param> <param name="t3">An
	 * expression with the same sort as <paramref name="t2"/></param>
	 **/
	public Expr MkITE(BoolExpr t1, Expr t2, Expr t3) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		CheckContextMatch(t3);
		return Expr.Create(this, Native.mkIte(nCtx(), t1.NativeObject(), t2.NativeObject(), t3.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 iff t2</code>.
	 **/
	public BoolExpr MkIff(BoolExpr t1, BoolExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkIff(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 -> t2</code>.
	 **/
	public BoolExpr MkImplies(BoolExpr t1, BoolExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkImplies(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 xor t2</code>.
	 **/
	public BoolExpr MkXor(BoolExpr t1, BoolExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkXor(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t[0] and t[1] and ...</code>.
	 **/
	public BoolExpr MkAnd(BoolExpr... t) throws Z3Exception {

		CheckContextMatch(t);
		return new BoolExpr(this, Native.mkAnd(nCtx(), (int) t.length, AST.ArrayToNative(t)));
	}

	/**
	 * Create an expression representing <code>t[0] or t[1] or ...</code>.
	 **/
	public BoolExpr MkOr(BoolExpr... t) throws Z3Exception {

		CheckContextMatch(t);
		return new BoolExpr(this, Native.mkOr(nCtx(), (int) t.length, AST.ArrayToNative(t)));
	}

	/**
	 * Create an expression representing <code>t[0] + t[1] + ...</code>.
	 **/
	public ArithExpr MkAdd(ArithExpr... t) throws Z3Exception {

		CheckContextMatch(t);
		return (ArithExpr) Expr.Create(this, Native.mkAdd(nCtx(), (int) t.length, AST.ArrayToNative(t)));
	}

	/**
	 * Create an expression representing <code>t[0] * t[1] * ...</code>.
	 **/
	public ArithExpr MkMul(ArithExpr... t) throws Z3Exception {

		CheckContextMatch(t);
		return (ArithExpr) Expr.Create(this, Native.mkMul(nCtx(), (int) t.length, AST.ArrayToNative(t)));
	}

	/**
	 * Create an expression representing <code>t[0] - t[1] - ...</code>.
	 **/
	public ArithExpr MkSub(ArithExpr... t) throws Z3Exception {

		CheckContextMatch(t);
		return (ArithExpr) Expr.Create(this, Native.mkSub(nCtx(), (int) t.length, AST.ArrayToNative(t)));
	}

	/**
	 * Create an expression representing <code>-t</code>.
	 **/
	public ArithExpr MkUnaryMinus(ArithExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return (ArithExpr) Expr.Create(this, Native.mkUnaryMinus(nCtx(), t.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 / t2</code>.
	 **/
	public ArithExpr MkDiv(ArithExpr t1, ArithExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return (ArithExpr) Expr.Create(this, Native.mkDiv(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 mod t2</code>. <remarks>The
	 * arguments must have int type.</remarks>
	 **/
	public IntExpr MkMod(IntExpr t1, IntExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new IntExpr(this, Native.mkMod(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 rem t2</code>. <remarks>The
	 * arguments must have int type.</remarks>
	 **/
	public IntExpr MkRem(IntExpr t1, IntExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new IntExpr(this, Native.mkRem(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 ^ t2</code>.
	 **/
	public ArithExpr MkPower(ArithExpr t1, ArithExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return (ArithExpr) Expr.Create(this, Native.mkPower(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 &lt; t2</code>
	 **/
	public BoolExpr MkLt(ArithExpr t1, ArithExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkLt(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 &lt;= t2</code>
	 **/
	public BoolExpr MkLe(ArithExpr t1, ArithExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkLe(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 &gt; t2</code>
	 **/
	public BoolExpr MkGt(ArithExpr t1, ArithExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkGt(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an expression representing <code>t1 &gt;= t2</code>
	 **/
	public BoolExpr MkGe(ArithExpr t1, ArithExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkGe(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Coerce an integer to a real. <remarks> There is also a converse operation
	 * exposed. It follows the semantics prescribed by the SMT-LIB standard.
	 * 
	 * You can take the floor of a real by creating an auxiliary integer Term
	 * <code>k</code> and and asserting
	 * <code>MakeInt2Real(k) &lt;= t1 &lt; MkInt2Real(k)+1</code>. The argument
	 * must be of integer sort. </remarks>
	 **/
	public RealExpr MkInt2Real(IntExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new RealExpr(this, Native.mkInt2real(nCtx(), t.NativeObject()));
	}

	/**
	 * Coerce a real to an integer. <remarks> The semantics of this function
	 * follows the SMT-LIB standard for the function to_int. The argument must
	 * be of real sort. </remarks>
	 **/
	public IntExpr MkReal2Int(RealExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new IntExpr(this, Native.mkReal2int(nCtx(), t.NativeObject()));
	}

	/**
	 * Creates an expression that checks whether a real number is an integer.
	 **/
	public BoolExpr MkIsInteger(RealExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BoolExpr(this, Native.mkIsInt(nCtx(), t.NativeObject()));
	}

	/**
	 * Bitwise negation. <remarks>The argument must have a bit-vector
	 * sort.</remarks>
	 **/
	public BitVecExpr MkBVNot(BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkBvnot(nCtx(), t.NativeObject()));
	}

	/**
	 * Take conjunction of bits in a vector, return vector of length 1.
	 * <remarks>The argument must have a bit-vector sort.</remarks>
	 **/
	public BitVecExpr MkBVRedAND(BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkBvredand(nCtx(), t.NativeObject()));
	}

	/**
	 * Take disjunction of bits in a vector, return vector of length 1.
	 * <remarks>The argument must have a bit-vector sort.</remarks>
	 **/
	public BitVecExpr MkBVRedOR(BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkBvredor(nCtx(), t.NativeObject()));
	}

	/**
	 * Bitwise conjunction. <remarks>The arguments must have a bit-vector
	 * sort.</remarks>
	 **/
	public BitVecExpr MkBVAND(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvand(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Bitwise disjunction. <remarks>The arguments must have a bit-vector
	 * sort.</remarks>
	 **/
	public BitVecExpr MkBVOR(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvor(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Bitwise XOR. <remarks>The arguments must have a bit-vector
	 * sort.</remarks>
	 **/
	public BitVecExpr MkBVXOR(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvxor(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Bitwise NAND. <remarks>The arguments must have a bit-vector
	 * sort.</remarks>
	 **/
	public BitVecExpr MkBVNAND(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvnand(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Bitwise NOR. <remarks>The arguments must have a bit-vector
	 * sort.</remarks>
	 **/
	public BitVecExpr MkBVNOR(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvnor(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Bitwise XNOR. <remarks>The arguments must have a bit-vector
	 * sort.</remarks>
	 **/
	public BitVecExpr MkBVXNOR(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvxnor(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Standard two's complement unary minus. <remarks>The arguments must have a
	 * bit-vector sort.</remarks>
	 **/
	public BitVecExpr MkBVNeg(BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkBvneg(nCtx(), t.NativeObject()));
	}

	/**
	 * Two's complement addition. <remarks>The arguments must have the same
	 * bit-vector sort.</remarks>
	 **/
	public BitVecExpr MkBVAdd(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvadd(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Two's complement subtraction. <remarks>The arguments must have the same
	 * bit-vector sort.</remarks>
	 **/
	public BitVecExpr MkBVSub(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvsub(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Two's complement multiplication. <remarks>The arguments must have the
	 * same bit-vector sort.</remarks>
	 **/
	public BitVecExpr MkBVMul(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvmul(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Unsigned division. <remarks> It is defined as the floor of
	 * <code>t1/t2</code> if \c t2 is different from zero. If <code>t2</code> is
	 * zero, then the result is undefined. The arguments must have the same
	 * bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVUDiv(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvudiv(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Signed division. <remarks> It is defined in the following way:
	 * 
	 * - The \c floor of <code>t1/t2</code> if \c t2 is different from zero, and
	 * <code>t1*t2 >= 0</code>.
	 * 
	 * - The \c ceiling of <code>t1/t2</code> if \c t2 is different from zero,
	 * and <code>t1*t2 &lt; 0</code>.
	 * 
	 * If <code>t2</code> is zero, then the result is undefined. The arguments
	 * must have the same bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVSDiv(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvsdiv(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Unsigned remainder. <remarks> It is defined as
	 * <code>t1 - (t1 /u t2) * t2</code>, where <code>/u</code> represents
	 * unsigned division. If <code>t2</code> is zero, then the result is
	 * undefined. The arguments must have the same bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVURem(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvurem(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Signed remainder. <remarks> It is defined as
	 * <code>t1 - (t1 /s t2) * t2</code>, where <code>/s</code> represents
	 * signed division. The most significant bit (sign) of the result is equal
	 * to the most significant bit of \c t1.
	 * 
	 * If <code>t2</code> is zero, then the result is undefined. The arguments
	 * must have the same bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVSRem(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvsrem(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Two's complement signed remainder (sign follows divisor). <remarks> If
	 * <code>t2</code> is zero, then the result is undefined. The arguments must
	 * have the same bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVSMod(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvsmod(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Unsigned less-than <remarks> The arguments must have the same bit-vector
	 * sort. </remarks>
	 **/
	public BoolExpr MkBVULT(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvult(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Two's complement signed less-than <remarks> The arguments must have the
	 * same bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVSLT(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvslt(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Unsigned less-than or equal to. <remarks> The arguments must have the
	 * same bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVULE(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvule(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Two's complement signed less-than or equal to. <remarks> The arguments
	 * must have the same bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVSLE(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvsle(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Unsigned greater than or equal to. <remarks> The arguments must have the
	 * same bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVUGE(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvuge(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Two's complement signed greater than or equal to. <remarks> The arguments
	 * must have the same bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVSGE(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvsge(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Unsigned greater-than. <remarks> The arguments must have the same
	 * bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVUGT(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvugt(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Two's complement signed greater-than. <remarks> The arguments must have
	 * the same bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVSGT(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvsgt(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Bit-vector concatenation. <remarks> The arguments must have a bit-vector
	 * sort. </remarks>
	 * 
	 * @return The result is a bit-vector of size <code>n1+n2</code>, where
	 *         <code>n1</code> (<code>n2</code>) is the size of <code>t1</code>
	 *         (<code>t2</code>).
	 * 
	 **/
	public BitVecExpr MkConcat(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkConcat(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Bit-vector extraction. <remarks> Extract the bits <paramref name="high"/>
	 * down to <paramref name="low"/> from a bitvector of size <code>m</code> to
	 * yield a new bitvector of size <code>n</code>, where
	 * <code>n = high - low + 1</code>. The argument <paramref name="t"/> must
	 * have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkExtract(int high, int low, BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkExtract(nCtx(), high, low, t.NativeObject()));
	}

	/**
	 * Bit-vector sign extension. <remarks> Sign-extends the given bit-vector to
	 * the (signed) equivalent bitvector of size <code>m+i</code>, where \c m is
	 * the size of the given bit-vector. The argument <paramref name="t"/> must
	 * have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkSignExt(int i, BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkSignExt(nCtx(), i, t.NativeObject()));
	}

	/**
	 * Bit-vector zero extension. <remarks> Extend the given bit-vector with
	 * zeros to the (unsigned) equivalent bitvector of size <code>m+i</code>,
	 * where \c m is the size of the given bit-vector. The argument <paramref
	 * name="t"/> must have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkZeroExt(int i, BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkZeroExt(nCtx(), i, t.NativeObject()));
	}

	/**
	 * Bit-vector repetition. <remarks> The argument <paramref name="t"/> must
	 * have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkRepeat(int i, BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkRepeat(nCtx(), i, t.NativeObject()));
	}

	/**
	 * Shift left. <remarks> It is equivalent to multiplication by
	 * <code>2^x</code> where \c x is the value of <paramref name="t2"/>.
	 * 
	 * NB. The semantics of shift operations varies between environments. This
	 * definition does not necessarily capture directly the semantics of the
	 * programming language or assembly architecture you are modeling.
	 * 
	 * The arguments must have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVSHL(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvshl(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Logical shift right <remarks> It is equivalent to unsigned division by
	 * <code>2^x</code> where \c x is the value of <paramref name="t2"/>.
	 * 
	 * NB. The semantics of shift operations varies between environments. This
	 * definition does not necessarily capture directly the semantics of the
	 * programming language or assembly architecture you are modeling.
	 * 
	 * The arguments must have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVLSHR(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvlshr(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Arithmetic shift right <remarks> It is like logical shift right except
	 * that the most significant bits of the result always copy the most
	 * significant bit of the second argument.
	 * 
	 * NB. The semantics of shift operations varies between environments. This
	 * definition does not necessarily capture directly the semantics of the
	 * programming language or assembly architecture you are modeling.
	 * 
	 * The arguments must have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVASHR(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkBvashr(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Rotate Left. <remarks> Rotate bits of \c t to the left \c i times. The
	 * argument <paramref name="t"/> must have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVRotateLeft(int i, BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkRotateLeft(nCtx(), i, t.NativeObject()));
	}

	/**
	 * Rotate Right. <remarks> Rotate bits of \c t to the right \c i times. The
	 * argument <paramref name="t"/> must have a bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVRotateRight(int i, BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkRotateRight(nCtx(), i, t.NativeObject()));
	}

	/**
	 * Rotate Left. <remarks> Rotate bits of <paramref name="t1"/> to the left
	 * <paramref name="t2"/> times. The arguments must have the same bit-vector
	 * sort. </remarks>
	 **/
	public BitVecExpr MkBVRotateLeft(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkExtRotateLeft(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Rotate Right. <remarks> Rotate bits of <paramref name="t1"/> to the
	 * right<paramref name="t2"/> times. The arguments must have the same
	 * bit-vector sort. </remarks>
	 **/
	public BitVecExpr MkBVRotateRight(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BitVecExpr(this, Native.mkExtRotateRight(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an <paramref name="n"/> bit bit-vector from the integer argument
	 * <paramref name="t"/>. <remarks> NB. This function is essentially treated
	 * as uninterpreted. So you cannot expect Z3 to precisely reflect the
	 * semantics of this function when solving constraints with this function.
	 * 
	 * The argument must be of integer sort. </remarks>
	 **/
	public BitVecExpr MkInt2BV(int n, IntExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BitVecExpr(this, Native.mkInt2bv(nCtx(), n, t.NativeObject()));
	}

	/**
	 * Create an integer from the bit-vector argument <paramref name="t"/>.
	 * <remarks> If \c is_signed is false, then the bit-vector \c t1 is treated
	 * as unsigned. So the result is non-negative and in the range
	 * <code>[0..2^N-1]</code>, where N are the number of bits in <paramref
	 * name="t"/>. If \c is_signed is true, \c t1 is treated as a signed
	 * bit-vector.
	 * 
	 * NB. This function is essentially treated as uninterpreted. So you cannot
	 * expect Z3 to precisely reflect the semantics of this function when
	 * solving constraints with this function.
	 * 
	 * The argument must be of bit-vector sort. </remarks>
	 **/
	public IntExpr MkBV2Int(BitVecExpr t, boolean signed) throws Z3Exception {

		CheckContextMatch(t);
		return new IntExpr(this, Native.mkBv2int(nCtx(), t.NativeObject(), (signed) ? true : false));
	}

	/**
	 * Create a predicate that checks that the bit-wise addition does not
	 * overflow. <remarks> The arguments must be of bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVAddNoOverflow(BitVecExpr t1, BitVecExpr t2, boolean isSigned) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvaddNoOverflow(nCtx(), t1.NativeObject(), t2.NativeObject(), (isSigned) ? true : false));
	}

	/**
	 * Create a predicate that checks that the bit-wise addition does not
	 * underflow. <remarks> The arguments must be of bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVAddNoUnderflow(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvaddNoUnderflow(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create a predicate that checks that the bit-wise subtraction does not
	 * overflow. <remarks> The arguments must be of bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVSubNoOverflow(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvsubNoOverflow(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create a predicate that checks that the bit-wise subtraction does not
	 * underflow. <remarks> The arguments must be of bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVSubNoUnderflow(BitVecExpr t1, BitVecExpr t2, boolean isSigned) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvsubNoUnderflow(nCtx(), t1.NativeObject(), t2.NativeObject(), (isSigned) ? true : false));
	}

	/**
	 * Create a predicate that checks that the bit-wise signed division does not
	 * overflow. <remarks> The arguments must be of bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVSDivNoOverflow(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvsdivNoOverflow(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create a predicate that checks that the bit-wise negation does not
	 * overflow. <remarks> The arguments must be of bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVNegNoOverflow(BitVecExpr t) throws Z3Exception {

		CheckContextMatch(t);
		return new BoolExpr(this, Native.mkBvnegNoOverflow(nCtx(), t.NativeObject()));
	}

	/**
	 * Create a predicate that checks that the bit-wise multiplication does not
	 * overflow. <remarks> The arguments must be of bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVMulNoOverflow(BitVecExpr t1, BitVecExpr t2, boolean isSigned) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvmulNoOverflow(nCtx(), t1.NativeObject(), t2.NativeObject(), (isSigned) ? true : false));
	}

	/**
	 * Create a predicate that checks that the bit-wise multiplication does not
	 * underflow. <remarks> The arguments must be of bit-vector sort. </remarks>
	 **/
	public BoolExpr MkBVMulNoUnderflow(BitVecExpr t1, BitVecExpr t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new BoolExpr(this, Native.mkBvmulNoUnderflow(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create an array constant.
	 **/
	public ArrayExpr MkArrayConst(Symbol name, Sort domain, Sort range) throws Z3Exception {

		return (ArrayExpr) MkConst(name, MkArraySort(domain, range));
	}

	/**
	 * Create an array constant.
	 **/
	public ArrayExpr MkArrayConst(String name, Sort domain, Sort range) throws Z3Exception {

		return (ArrayExpr) MkConst(MkSymbol(name), MkArraySort(domain, range));
	}

	/**
	 * Array read. <remarks> The argument <code>a</code> is the array and
	 * <code>i</code> is the index of the array that gets read.
	 * 
	 * The node <code>a</code> must have an array sort
	 * <code>[domain -> range]</code>, and <code>i</code> must have the sort
	 * <code>domain</code>. The sort of the result is <code>range</code>.
	 * <seealso cref="MkArraySort"/> <seealso cref="MkStore"/> </remarks>
	 **/
	public Expr MkSelect(ArrayExpr a, Expr i) throws Z3Exception {

		CheckContextMatch(a);
		CheckContextMatch(i);
		return Expr.Create(this, Native.mkSelect(nCtx(), a.NativeObject(), i.NativeObject()));
	}

	/**
	 * Array update. <remarks> The node <code>a</code> must have an array sort
	 * <code>[domain -> range]</code>, <code>i</code> must have sort
	 * <code>domain</code>, <code>v</code> must have sort range. The sort of the
	 * result is <code>[domain -> range]</code>. The semantics of this function
	 * is given by the theory of arrays described in the SMT-LIB standard. See
	 * http://smtlib.org for more details. The result of this function is an
	 * array that is equal to <code>a</code> (with respect to
	 * <code>select</code>) on all indices except for <code>i</code>, where it
	 * maps to <code>v</code> (and the <code>select</code> of <code>a</code>
	 * with respect to <code>i</code> may be a different value). <seealso
	 * cref="MkArraySort"/> <seealso cref="MkSelect"/> </remarks>
	 **/
	public ArrayExpr MkStore(ArrayExpr a, Expr i, Expr v) throws Z3Exception {

		CheckContextMatch(a);
		CheckContextMatch(i);
		CheckContextMatch(v);
		return new ArrayExpr(this, Native.mkStore(nCtx(), a.NativeObject(), i.NativeObject(), v.NativeObject()));
	}

	/**
	 * Create a constant array. <remarks> The resulting term is an array, such
	 * that a <code>select</code>on an arbitrary index produces the value
	 * <code>v</code>. <seealso cref="MkArraySort"/> <seealso cref="MkSelect"/>
	 * </remarks>
	 **/
	public ArrayExpr MkConstArray(Sort domain, Expr v) throws Z3Exception {

		CheckContextMatch(domain);
		CheckContextMatch(v);
		return new ArrayExpr(this, Native.mkConstArray(nCtx(), domain.NativeObject(), v.NativeObject()));
	}

	/**
	 * Maps f on the argument arrays. <remarks> Eeach element of
	 * <code>args</code> must be of an array sort
	 * <code>[domain_i -> range_i]</code>. The function declaration
	 * <code>f</code> must have type <code> range_1 .. range_n -> range</code>.
	 * <code>v</code> must have sort range. The sort of the result is
	 * <code>[domain_i -> range]</code>. <seealso cref="MkArraySort"/> <seealso
	 * cref="MkSelect"/> <seealso cref="MkStore"/> </remarks>
	 **/
	public ArrayExpr MkMap(FuncDecl f, ArrayExpr[] args) throws Z3Exception {

		CheckContextMatch(f);
		CheckContextMatch(args);
		return (ArrayExpr) Expr.Create(this, Native.mkMap(nCtx(), f.NativeObject(), AST.ArrayLength(args), AST.ArrayToNative(args)));
	}

	/**
	 * Access the array default value. <remarks> Produces the default range
	 * value, for arrays that can be represented as finite maps with a default
	 * range value. </remarks>
	 **/
	public Expr MkTermArray(ArrayExpr array) throws Z3Exception {

		CheckContextMatch(array);
		return Expr.Create(this, Native.mkArrayDefault(nCtx(), array.NativeObject()));
	}

	/**
	 * Create a set type.
	 **/
	public SetSort MkSetSort(Sort ty) throws Z3Exception {

		CheckContextMatch(ty);
		return new SetSort(this, ty);
	}

	/**
	 * Create an empty set.
	 **/
	public Expr MkEmptySet(Sort domain) throws Z3Exception {

		CheckContextMatch(domain);
		return Expr.Create(this, Native.mkEmptySet(nCtx(), domain.NativeObject()));
	}

	/**
	 * Create the full set.
	 **/
	public Expr MkFullSet(Sort domain) throws Z3Exception {

		CheckContextMatch(domain);
		return Expr.Create(this, Native.mkFullSet(nCtx(), domain.NativeObject()));
	}

	/**
	 * Add an element to the set.
	 **/
	public Expr MkSetAdd(Expr set, Expr element) throws Z3Exception {

		CheckContextMatch(set);
		CheckContextMatch(element);
		return Expr.Create(this, Native.mkSetAdd(nCtx(), set.NativeObject(), element.NativeObject()));
	}

	/**
	 * Remove an element from a set.
	 **/
	public Expr MkSetDel(Expr set, Expr element) throws Z3Exception {

		CheckContextMatch(set);
		CheckContextMatch(element);
		return Expr.Create(this, Native.mkSetDel(nCtx(), set.NativeObject(), element.NativeObject()));
	}

	/**
	 * Take the union of a list of sets.
	 **/
	public Expr MkSetUnion(Expr[] args) throws Z3Exception {

		CheckContextMatch(args);
		return Expr.Create(this, Native.mkSetUnion(nCtx(), (int) args.length, AST.ArrayToNative(args)));
	}

	/**
	 * Take the intersection of a list of sets.
	 **/
	public Expr MkSetIntersection(Expr[] args) throws Z3Exception {

		CheckContextMatch(args);
		return Expr.Create(this, Native.mkSetIntersect(nCtx(), (int) args.length, AST.ArrayToNative(args)));
	}

	/**
	 * Take the difference between two sets.
	 **/
	public Expr MkSetDifference(Expr arg1, Expr arg2) throws Z3Exception {

		CheckContextMatch(arg1);
		CheckContextMatch(arg2);
		return Expr.Create(this, Native.mkSetDifference(nCtx(), arg1.NativeObject(), arg2.NativeObject()));
	}

	/**
	 * Take the complement of a set.
	 **/
	public Expr MkSetComplement(Expr arg) throws Z3Exception {

		CheckContextMatch(arg);
		return Expr.Create(this, Native.mkSetComplement(nCtx(), arg.NativeObject()));
	}

	/**
	 * Check for set membership.
	 **/
	public Expr MkSetMembership(Expr elem, Expr set) throws Z3Exception {

		CheckContextMatch(elem);
		CheckContextMatch(set);
		return Expr.Create(this, Native.mkSetMember(nCtx(), elem.NativeObject(), set.NativeObject()));
	}

	/**
	 * Check for subsetness of sets.
	 **/
	public Expr MkSetSubset(Expr arg1, Expr arg2) throws Z3Exception {

		CheckContextMatch(arg1);
		CheckContextMatch(arg2);
		return Expr.Create(this, Native.mkSetSubset(nCtx(), arg1.NativeObject(), arg2.NativeObject()));
	}

	/**
	 * Create a Term of a given sort. <param name="v">A string representing the
	 * Term value in decimal notation. If the given sort is a real, then the
	 * Term can be a rational, that is, a string of the form
	 * <code>[num]* / [num]*</code>.</param> <param name="ty">The sort of the
	 * numeral. In the current implementation, the given sort can be an int,
	 * real, or bit-vectors of arbitrary size. </param>
	 * 
	 * @return A Term with value <paramref name="v"/> and sort <paramref
	 *         name="ty"/>
	 **/
	public Expr MkNumeral(String v, Sort ty) throws Z3Exception {

		CheckContextMatch(ty);
		return Expr.Create(this, Native.mkNumeral(nCtx(), v, ty.NativeObject()));
	}

	/**
	 * Create a Term of a given sort. This function can be use to create
	 * numerals that fit in a machine integer. It is slightly faster than
	 * <code>MakeNumeral</code> since it is not necessary to parse a string.
	 * <param name="v">Value of the numeral</param> <param name="ty">Sort of the
	 * numeral</param>
	 * 
	 * @return A Term with value <paramref name="v"/> and type <paramref
	 *         name="ty"/>
	 **/
	public Expr MkNumeral(int v, Sort ty) throws Z3Exception {

		CheckContextMatch(ty);
		return Expr.Create(this, Native.mkInt(nCtx(), v, ty.NativeObject()));
	}

	/**
	 * Create a Term of a given sort. This function can be use to create
	 * numerals that fit in a machine integer. It is slightly faster than
	 * <code>MakeNumeral</code> since it is not necessary to parse a string.
	 * <param name="v">Value of the numeral</param> <param name="ty">Sort of the
	 * numeral</param>
	 * 
	 * @return A Term with value <paramref name="v"/> and type <paramref
	 *         name="ty"/>
	 **/
	public Expr MkNumeral(long v, Sort ty) throws Z3Exception {

		CheckContextMatch(ty);
		return Expr.Create(this, Native.mkInt64(nCtx(), v, ty.NativeObject()));
	}

	/**
	 * Create a real from a fraction. <param name="num">numerator of
	 * rational.</param> <param name="den">denominator of rational.</param>
	 * 
	 * @return A Term with value <paramref name="num"/>/<paramref name="den"/>
	 *         and sort Real <seealso cref="MkNumeral(string, Sort)"/>
	 **/
	public RatNum MkReal(int num, int den) throws Z3Exception {
		if (den == 0)
			throw new Z3Exception("Denominator is zero");

		return new RatNum(this, Native.mkReal(nCtx(), num, den));
	}

	/**
	 * Create a real numeral. <param name="v">A string representing the Term
	 * value in decimal notation.</param>
	 * 
	 * @return A Term with value <paramref name="v"/> and sort Real
	 **/
	public RatNum MkReal(String v) throws Z3Exception {

		return new RatNum(this, Native.mkNumeral(nCtx(), v, RealSort().NativeObject()));
	}

	/**
	 * Create a real numeral. <param name="v">value of the numeral.</param>
	 * 
	 * @return A Term with value <paramref name="v"/> and sort Real
	 **/
	public RatNum MkReal(int v) throws Z3Exception {

		return new RatNum(this, Native.mkInt(nCtx(), v, RealSort().NativeObject()));
	}

	/**
	 * Create a real numeral. <param name="v">value of the numeral.</param>
	 * 
	 * @return A Term with value <paramref name="v"/> and sort Real
	 **/
	public RatNum MkReal(long v) throws Z3Exception {

		return new RatNum(this, Native.mkInt64(nCtx(), v, RealSort().NativeObject()));
	}

	/**
	 * Create an integer numeral. <param name="v">A string representing the Term
	 * value in decimal notation.</param>
	 **/
	public IntNum MkInt(String v) throws Z3Exception {

		return new IntNum(this, Native.mkNumeral(nCtx(), v, IntSort().NativeObject()));
	}

	/**
	 * Create an integer numeral. <param name="v">value of the numeral.</param>
	 * 
	 * @return A Term with value <paramref name="v"/> and sort Integer
	 **/
	public IntNum MkInt(int v) throws Z3Exception {

		return new IntNum(this, Native.mkInt(nCtx(), v, IntSort().NativeObject()));
	}

	/**
	 * Create an integer numeral. <param name="v">value of the numeral.</param>
	 * 
	 * @return A Term with value <paramref name="v"/> and sort Integer
	 **/
	public IntNum MkInt(long v) throws Z3Exception {

		return new IntNum(this, Native.mkInt64(nCtx(), v, IntSort().NativeObject()));
	}

	/**
	 * Create a bit-vector numeral. <param name="v">A string representing the
	 * value in decimal notation.</param> <param name="size">the size of the
	 * bit-vector</param>
	 **/
	public BitVecNum MkBV(String v, int size) throws Z3Exception {

		return (BitVecNum) MkNumeral(v, MkBitVecSort(size));
	}

	/**
	 * Create a bit-vector numeral. <param name="v">value of the
	 * numeral.</param> <param name="size">the size of the bit-vector</param>
	 **/
	public BitVecNum MkBV(int v, int size) throws Z3Exception {

		return (BitVecNum) MkNumeral(v, MkBitVecSort(size));
	}

	/**
	 * Create a bit-vector numeral. <param name="v">value of the
	 * numeral.</param> * <param name="size">the size of the bit-vector</param>
	 **/
	public BitVecNum MkBV(long v, int size) throws Z3Exception {

		return (BitVecNum) MkNumeral(v, MkBitVecSort(size));
	}

	/**
	 * Create a universal Quantifier. <remarks> Creates a forall formula, where
	 * <paramref name="weight"/> is the weight, <paramref name="patterns"/> is
	 * an array of patterns, <paramref name="sorts"/> is an array with the sorts
	 * of the bound variables, <paramref name="names"/> is an array with the
	 * 'names' of the bound variables, and <paramref name="body"/> is the body
	 * of the quantifier. Quantifiers are associated with weights indicating the
	 * importance of using the quantifier during instantiation. </remarks>
	 * <param name="sorts">the sorts of the bound variables.</param> <param
	 * name="names">names of the bound variables</param> <param name="body">the
	 * body of the quantifier.</param> <param name="weight">quantifiers are
	 * associated with weights indicating the importance of using the quantifier
	 * during instantiation. By default, pass the weight 0.</param> <param
	 * name="patterns">array containing the patterns created using
	 * <code>MkPattern</code>.</param> <param name="noPatterns">array containing
	 * the anti-patterns created using <code>MkPattern</code>.</param> <param
	 * name="quantifierID">optional symbol to track quantifier.</param> <param
	 * name="skolemID">optional symbol to track skolem constants.</param>
	 **/
	public Quantifier MkForall(Sort[] sorts, Symbol[] names, Expr body, int weight, Pattern[] patterns, Expr[] noPatterns, Symbol quantifierID, Symbol skolemID) throws Z3Exception {

		return new Quantifier(this, true, sorts, names, body, weight, patterns, noPatterns, quantifierID, skolemID);
	}

	/**
	 * Create a universal Quantifier.
	 **/
	public Quantifier MkForall(Expr[] boundConstants, Expr body, int weight, Pattern[] patterns, Expr[] noPatterns, Symbol quantifierID, Symbol skolemID) throws Z3Exception {

		return new Quantifier(this, true, boundConstants, body, weight, patterns, noPatterns, quantifierID, skolemID);
	}

	/**
	 * Create an existential Quantifier. <seealso cref=
	 * "MkForall(Sort[],Symbol[],Expr,uint,Pattern[],Expr[],Symbol,Symbol)"/>
	 **/
	public Quantifier MkExists(Sort[] sorts, Symbol[] names, Expr body, int weight, Pattern[] patterns, Expr[] noPatterns, Symbol quantifierID, Symbol skolemID) throws Z3Exception {

		return new Quantifier(this, false, sorts, names, body, weight, patterns, noPatterns, quantifierID, skolemID);
	}

	/**
	 * Create an existential Quantifier.
	 **/
	public Quantifier MkExists(Expr[] boundConstants, Expr body, int weight, Pattern[] patterns, Expr[] noPatterns, Symbol quantifierID, Symbol skolemID) throws Z3Exception {

		return new Quantifier(this, false, boundConstants, body, weight, patterns, noPatterns, quantifierID, skolemID);
	}

	/**
	 * Create a Quantifier.
	 **/
	public Quantifier MkQuantifier(boolean universal, Sort[] sorts, Symbol[] names, Expr body, int weight, Pattern[] patterns, Expr[] noPatterns, Symbol quantifierID, Symbol skolemID) throws Z3Exception {

		if (universal)
			return MkForall(sorts, names, body, weight, patterns, noPatterns, quantifierID, skolemID);
		else
			return MkExists(sorts, names, body, weight, patterns, noPatterns, quantifierID, skolemID);
	}

	/**
	 * Create a Quantifier.
	 **/
	public Quantifier MkQuantifier(boolean universal, Expr[] boundConstants, Expr body, int weight, Pattern[] patterns, Expr[] noPatterns, Symbol quantifierID, Symbol skolemID) throws Z3Exception {

		if (universal)
			return MkForall(boundConstants, body, weight, patterns, noPatterns, quantifierID, skolemID);
		else
			return MkExists(boundConstants, body, weight, patterns, noPatterns, quantifierID, skolemID);
	}

	/**
	 * Selects the format used for pretty-printing expressions. <remarks> The
	 * default mode for pretty printing expressions is to produce SMT-LIB style
	 * output where common subexpressions are printed at each occurrence. The
	 * mode is called Z3_PRINT_SMTLIB_FULL. To print shared common
	 * subexpressions only once, use the Z3_PRINT_LOW_LEVEL mode. To print in
	 * way that conforms to SMT-LIB standards and uses let expressions to share
	 * common sub-expressions use Z3_PRINT_SMTLIB_COMPLIANT. </remarks> <seealso
	 * cref="AST.ToString()"/> <seealso cref="Pattern.ToString()"/> <seealso
	 * cref="FuncDecl.ToString()"/> <seealso cref="Sort.ToString()"/>
	 **/
	public void setPrintMode(Z3_ast_print_mode value) throws Z3Exception {
		Native.setAstPrintMode(nCtx(), value.toInt());
	}

	/**
	 * Convert a benchmark into an SMT-LIB formatted string. <param
	 * name="name">Name of the benchmark. The argument is optional.</param>
	 * <param name="logic">The benchmark logic. </param> <param
	 * name="status">The status string (sat, unsat, or unknown)</param> <param
	 * name="attributes">Other attributes, such as source, difficulty or
	 * category.</param> <param name="assumptions">Auxiliary
	 * assumptions.</param> <param name="formula">Formula to be checked for
	 * consistency in conjunction with assumptions.</param>
	 * 
	 * @return A string representation of the benchmark.
	 **/
	public String BenchmarkToSMTString(String name, String logic, String status, String attributes, BoolExpr[] assumptions, BoolExpr formula) throws Z3Exception {

		return Native.benchmarkToSmtlibString(nCtx(), name, logic, status, attributes, (int) assumptions.length, AST.ArrayToNative(assumptions), formula.NativeObject());
	}

	/**
	 * Parse the given string using the SMT-LIB parser. <remarks> The symbol
	 * table of the parser can be initialized using the given sorts and
	 * declarations. The symbols in the arrays <paramref name="sortNames"/> and
	 * <paramref name="declNames"/> don't need to match the names of the sorts
	 * and declarations in the arrays <paramref name="sorts"/> and <paramref
	 * name="decls"/>. This is a useful feature since we can use arbitrary names
	 * to reference sorts and declarations. </remarks>
	 **/
	public void ParseSMTLIBString(String str, Symbol[] sortNames, Sort[] sorts, Symbol[] declNames, FuncDecl[] decls) throws Z3Exception {
		int csn = Symbol.ArrayLength(sortNames);
		int cs = Sort.ArrayLength(sorts);
		int cdn = Symbol.ArrayLength(declNames);
		int cd = AST.ArrayLength(decls);
		if (csn != cs || cdn != cd)
			throw new Z3Exception("Argument size mismatch");
		Native.parseSmtlibString(nCtx(), str, AST.ArrayLength(sorts), Symbol.ArrayToNative(sortNames), AST.ArrayToNative(sorts), AST.ArrayLength(decls), Symbol.ArrayToNative(declNames), AST.ArrayToNative(decls));
	}

	/**
	 * Parse the given file using the SMT-LIB parser. <seealso
	 * cref="ParseSMTLIBString"/>
	 **/
	public void ParseSMTLIBFile(String fileName, Symbol[] sortNames, Sort[] sorts, Symbol[] declNames, FuncDecl[] decls) throws Z3Exception {
		int csn = Symbol.ArrayLength(sortNames);
		int cs = Sort.ArrayLength(sorts);
		int cdn = Symbol.ArrayLength(declNames);
		int cd = AST.ArrayLength(decls);
		if (csn != cs || cdn != cd)
			throw new Z3Exception("Argument size mismatch");
		Native.parseSmtlibFile(nCtx(), fileName, AST.ArrayLength(sorts), Symbol.ArrayToNative(sortNames), AST.ArrayToNative(sorts), AST.ArrayLength(decls), Symbol.ArrayToNative(declNames), AST.ArrayToNative(decls));
	}

	/**
	 * The number of SMTLIB formulas parsed by the last call to
	 * <code>ParseSMTLIBString</code> or <code>ParseSMTLIBFile</code>.
	 **/
	public int NumSMTLIBFormulas() throws Z3Exception {
		return Native.getSmtlibNumFormulas(nCtx());
	}

	/**
	 * The formulas parsed by the last call to <code>ParseSMTLIBString</code> or
	 * <code>ParseSMTLIBFile</code>.
	 **/
	public BoolExpr[] SMTLIBFormulas() throws Z3Exception {

		int n = NumSMTLIBFormulas();
		BoolExpr[] res = new BoolExpr[n];
		for (int i = 0; i < n; i++)
			res[i] = (BoolExpr) Expr.Create(this, Native.getSmtlibFormula(nCtx(), i));
		return res;
	}

	/**
	 * The number of SMTLIB assumptions parsed by the last call to
	 * <code>ParseSMTLIBString</code> or <code>ParseSMTLIBFile</code>.
	 **/
	public int NumSMTLIBAssumptions() throws Z3Exception {
		return Native.getSmtlibNumAssumptions(nCtx());
	}

	/**
	 * The assumptions parsed by the last call to <code>ParseSMTLIBString</code>
	 * or <code>ParseSMTLIBFile</code>.
	 **/
	public BoolExpr[] SMTLIBAssumptions() throws Z3Exception {

		int n = NumSMTLIBAssumptions();
		BoolExpr[] res = new BoolExpr[n];
		for (int i = 0; i < n; i++)
			res[i] = (BoolExpr) Expr.Create(this, Native.getSmtlibAssumption(nCtx(), i));
		return res;
	}

	/**
	 * The number of SMTLIB declarations parsed by the last call to
	 * <code>ParseSMTLIBString</code> or <code>ParseSMTLIBFile</code>.
	 **/
	public int NumSMTLIBDecls() throws Z3Exception {
		return Native.getSmtlibNumDecls(nCtx());
	}

	/**
	 * The declarations parsed by the last call to
	 * <code>ParseSMTLIBString</code> or <code>ParseSMTLIBFile</code>.
	 **/
	public FuncDecl[] SMTLIBDecls() throws Z3Exception {

		int n = NumSMTLIBDecls();
		FuncDecl[] res = new FuncDecl[n];
		for (int i = 0; i < n; i++)
			res[i] = new FuncDecl(this, Native.getSmtlibDecl(nCtx(), i));
		return res;
	}

	/**
	 * The number of SMTLIB sorts parsed by the last call to
	 * <code>ParseSMTLIBString</code> or <code>ParseSMTLIBFile</code>.
	 **/
	public int NumSMTLIBSorts() throws Z3Exception {
		return Native.getSmtlibNumSorts(nCtx());
	}

	/**
	 * The declarations parsed by the last call to
	 * <code>ParseSMTLIBString</code> or <code>ParseSMTLIBFile</code>.
	 **/
	public Sort[] SMTLIBSorts() throws Z3Exception {

		int n = NumSMTLIBSorts();
		Sort[] res = new Sort[n];
		for (int i = 0; i < n; i++)
			res[i] = Sort.Create(this, Native.getSmtlibSort(nCtx(), i));
		return res;
	}

	/**
	 * Parse the given string using the SMT-LIB2 parser. <seealso
	 * cref="ParseSMTLIBString"/>
	 * 
	 * @return A conjunction of assertions in the scope (up to push/pop) at the
	 *         end of the string.
	 **/
	public BoolExpr ParseSMTLIB2String(String str, Symbol[] sortNames, Sort[] sorts, Symbol[] declNames, FuncDecl[] decls) throws Z3Exception {

		int csn = Symbol.ArrayLength(sortNames);
		int cs = Sort.ArrayLength(sorts);
		int cdn = Symbol.ArrayLength(declNames);
		int cd = AST.ArrayLength(decls);
		if (csn != cs || cdn != cd)
			throw new Z3Exception("Argument size mismatch");
		return (BoolExpr) Expr.Create(this,
				Native.parseSmtlib2String(nCtx(), str, AST.ArrayLength(sorts), Symbol.ArrayToNative(sortNames), AST.ArrayToNative(sorts), AST.ArrayLength(decls), Symbol.ArrayToNative(declNames), AST.ArrayToNative(decls)));
	}

	public BoolExpr ParseSMTLIB2String(String str) throws Z3Exception {

		Symbol[] sortNames = new Symbol[0];
		Sort[] sorts = new Sort[0];
		Symbol[] declNames = new Symbol[0];
		FuncDecl[] decls = new FuncDecl[0];

		int csn = Symbol.ArrayLength(sortNames);
		int cs = Sort.ArrayLength(sorts);
		int cdn = Symbol.ArrayLength(declNames);
		int cd = AST.ArrayLength(decls);
		if (csn != cs || cdn != cd)
			throw new Z3Exception("Argument size mismatch");
		return (BoolExpr) Expr.Create(this,
				Native.parseSmtlib2String(nCtx(), str, AST.ArrayLength(sorts), Symbol.ArrayToNative(sortNames), AST.ArrayToNative(sorts), AST.ArrayLength(decls), Symbol.ArrayToNative(declNames), AST.ArrayToNative(decls)));
	}

	/**
	 * Parse the given file using the SMT-LIB2 parser. <seealso
	 * cref="ParseSMTLIB2String"/>
	 **/
	public BoolExpr ParseSMTLIB2File(String fileName, Symbol[] sortNames, Sort[] sorts, Symbol[] declNames, FuncDecl[] decls) throws Z3Exception {

		int csn = Symbol.ArrayLength(sortNames);
		int cs = Sort.ArrayLength(sorts);
		int cdn = Symbol.ArrayLength(declNames);
		int cd = AST.ArrayLength(decls);
		if (csn != cs || cdn != cd)
			throw new Z3Exception("Argument size mismatch");
		return (BoolExpr) Expr.Create(this,
				Native.parseSmtlib2File(nCtx(), fileName, AST.ArrayLength(sorts), Symbol.ArrayToNative(sortNames), AST.ArrayToNative(sorts), AST.ArrayLength(decls), Symbol.ArrayToNative(declNames), AST.ArrayToNative(decls)));
	}

	/**
	 * Creates a new Goal. <remarks> Note that the Context must have been
	 * created with proof generation support if <paramref name="proofs"/> is set
	 * to true here. </remarks> <param name="models">Indicates whether model
	 * generation should be enabled.</param> <param name="unsatCores">Indicates
	 * whether unsat core generation should be enabled.</param> <param
	 * name="proofs">Indicates whether proof generation should be
	 * enabled.</param>
	 **/
	public Goal MkGoal(boolean models, boolean unsatCores, boolean proofs) throws Z3Exception {

		return new Goal(this, models, unsatCores, proofs);
	}

	/**
	 * Creates a new ParameterSet.
	 **/
	public Params MkParams() throws Z3Exception {

		return new Params(this);
	}

	/**
	 * The number of supported tactics.
	 **/
	public int NumTactics() throws Z3Exception {
		return Native.getNumTactics(nCtx());
	}

	/**
	 * The names of all supported tactics.
	 **/
	public String[] TacticNames() throws Z3Exception {

		int n = NumTactics();
		String[] res = new String[n];
		for (int i = 0; i < n; i++)
			res[i] = Native.getTacticName(nCtx(), i);
		return res;
	}

	/**
	 * Returns a string containing a description of the tactic with the given
	 * name.
	 **/
	public String TacticDescription(String name) throws Z3Exception {

		return Native.tacticGetDescr(nCtx(), name);
	}

	/**
	 * Creates a new Tactic.
	 **/
	public Tactic MkTactic(String name) throws Z3Exception {

		return new Tactic(this, name);
	}

	/**
	 * Create a tactic that applies <paramref name="t1"/> to a Goal and then
	 * <paramref name="t2"/> to every subgoal produced by <paramref name="t1"/>.
	 **/
	public Tactic AndThen(Tactic t1, Tactic t2, Tactic[] ts) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		CheckContextMatch(ts);

		long last = 0;
		if (ts != null && ts.length > 0) {
			last = ts[ts.length - 1].NativeObject();
			for (int i = ts.length - 2; i >= 0; i--)
				last = Native.tacticAndThen(nCtx(), ts[i].NativeObject(), last);
		}
		if (last != 0) {
			last = Native.tacticAndThen(nCtx(), t2.NativeObject(), last);
			return new Tactic(this, Native.tacticAndThen(nCtx(), t1.NativeObject(), last));
		} else
			return new Tactic(this, Native.tacticAndThen(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create a tactic that applies <paramref name="t1"/> to a Goal and then
	 * <paramref name="t2"/> to every subgoal produced by <paramref name="t1"/>.
	 * <remarks> Shorthand for <code>AndThen</code>. </remarks>
	 **/
	public Tactic Then(Tactic t1, Tactic t2, Tactic[] ts) throws Z3Exception {

		return AndThen(t1, t2, ts);
	}

	/**
	 * Create a tactic that first applies <paramref name="t1"/> to a Goal and if
	 * it fails then returns the result of <paramref name="t2"/> applied to the
	 * Goal.
	 **/
	public Tactic OrElse(Tactic t1, Tactic t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new Tactic(this, Native.tacticOrElse(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create a tactic that applies <paramref name="t"/> to a goal for <paramref
	 * name="ms"/> milliseconds. <remarks> If <paramref name="t"/> does not
	 * terminate within <paramref name="ms"/> milliseconds, then it fails.
	 * </remarks>
	 **/
	public Tactic TryFor(Tactic t, int ms) throws Z3Exception {

		CheckContextMatch(t);
		return new Tactic(this, Native.tacticTryFor(nCtx(), t.NativeObject(), ms));
	}

	/**
	 * Create a tactic that applies <paramref name="t"/> to a given goal if the
	 * probe <paramref name="p"/> evaluates to true. <remarks> If <paramref
	 * name="p"/> evaluates to false, then the new tactic behaves like the
	 * <code>skip</code> tactic. </remarks>
	 **/
	public Tactic When(Probe p, Tactic t) throws Z3Exception {

		CheckContextMatch(t);
		CheckContextMatch(p);
		return new Tactic(this, Native.tacticWhen(nCtx(), p.NativeObject(), t.NativeObject()));
	}

	/**
	 * Create a tactic that applies <paramref name="t1"/> to a given goal if the
	 * probe <paramref name="p"/> evaluates to true and <paramref name="t2"/>
	 * otherwise.
	 **/
	public Tactic Cond(Probe p, Tactic t1, Tactic t2) throws Z3Exception {

		CheckContextMatch(p);
		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new Tactic(this, Native.tacticCond(nCtx(), p.NativeObject(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Create a tactic that keeps applying <paramref name="t"/> until the goal
	 * is not modified anymore or the maximum number of iterations <paramref
	 * name="max"/> is reached.
	 **/
	public Tactic Repeat(Tactic t, int max) throws Z3Exception {

		CheckContextMatch(t);
		return new Tactic(this, Native.tacticRepeat(nCtx(), t.NativeObject(), max));
	}

	/**
	 * Create a tactic that just returns the given goal.
	 **/
	public Tactic Skip() throws Z3Exception {

		return new Tactic(this, Native.tacticSkip(nCtx()));
	}

	/**
	 * Create a tactic always fails.
	 **/
	public Tactic Fail() throws Z3Exception {

		return new Tactic(this, Native.tacticFail(nCtx()));
	}

	/**
	 * Create a tactic that fails if the probe <paramref name="p"/> evaluates to
	 * false.
	 **/
	public Tactic FailIf(Probe p) throws Z3Exception {

		CheckContextMatch(p);
		return new Tactic(this, Native.tacticFailIf(nCtx(), p.NativeObject()));
	}

	/**
	 * Create a tactic that fails if the goal is not triviall satisfiable (i.e.,
	 * empty) or trivially unsatisfiable (i.e., contains `false').
	 **/
	public Tactic FailIfNotDecided() throws Z3Exception {

		return new Tactic(this, Native.tacticFailIfNotDecided(nCtx()));
	}

	/**
	 * Create a tactic that applies <paramref name="t"/> using the given set of
	 * parameters <paramref name="p"/>.
	 **/
	public Tactic UsingParams(Tactic t, Params p) throws Z3Exception {

		CheckContextMatch(t);
		CheckContextMatch(p);
		return new Tactic(this, Native.tacticUsingParams(nCtx(), t.NativeObject(), p.NativeObject()));
	}

	/**
	 * Create a tactic that applies <paramref name="t"/> using the given set of
	 * parameters <paramref name="p"/>. <remarks>Alias for
	 * <code>UsingParams</code></remarks>
	 **/
	public Tactic With(Tactic t, Params p) throws Z3Exception {

		return UsingParams(t, p);
	}

	/**
	 * Create a tactic that applies the given tactics in parallel.
	 **/
	public Tactic ParOr(Tactic[] t) throws Z3Exception {

		CheckContextMatch(t);
		return new Tactic(this, Native.tacticParOr(nCtx(), Tactic.ArrayLength(t), Tactic.ArrayToNative(t)));
	}

	/**
	 * Create a tactic that applies <paramref name="t1"/> to a given goal and
	 * then <paramref name="t2"/> to every subgoal produced by <paramref
	 * name="t1"/>. The subgoals are processed in parallel.
	 **/
	public Tactic ParAndThen(Tactic t1, Tactic t2) throws Z3Exception {

		CheckContextMatch(t1);
		CheckContextMatch(t2);
		return new Tactic(this, Native.tacticParAndThen(nCtx(), t1.NativeObject(), t2.NativeObject()));
	}

	/**
	 * Interrupt the execution of a Z3 procedure. <remarks>This procedure can be
	 * used to interrupt: solvers, simplifiers and tactics.</remarks>
	 **/
	public void Interrupt() throws Z3Exception {
		Native.interrupt(nCtx());
	}

	/**
	 * The number of supported Probes.
	 **/
	public int NumProbes() throws Z3Exception {
		return Native.getNumProbes(nCtx());
	}

	/**
	 * The names of all supported Probes.
	 **/
	public String[] ProbeNames() throws Z3Exception {

		int n = NumProbes();
		String[] res = new String[n];
		for (int i = 0; i < n; i++)
			res[i] = Native.getProbeName(nCtx(), i);
		return res;
	}

	/**
	 * Returns a string containing a description of the probe with the given
	 * name.
	 **/
	public String ProbeDescription(String name) throws Z3Exception {

		return Native.probeGetDescr(nCtx(), name);
	}

	/**
	 * Creates a new Probe.
	 **/
	public Probe MkProbe(String name) throws Z3Exception {

		return new Probe(this, name);
	}

	/**
	 * Create a probe that always evaluates to <paramref name="val"/>.
	 **/
	public Probe Const(double val) throws Z3Exception {

		return new Probe(this, Native.probeConst(nCtx(), val));
	}

	/**
	 * Create a probe that evaluates to "true" when the value returned by
	 * <paramref name="p1"/> is less than the value returned by <paramref
	 * name="p2"/>
	 **/
	public Probe Lt(Probe p1, Probe p2) throws Z3Exception {

		CheckContextMatch(p1);
		CheckContextMatch(p2);
		return new Probe(this, Native.probeLt(nCtx(), p1.NativeObject(), p2.NativeObject()));
	}

	/**
	 * Create a probe that evaluates to "true" when the value returned by
	 * <paramref name="p1"/> is greater than the value returned by <paramref
	 * name="p2"/>
	 **/
	public Probe Gt(Probe p1, Probe p2) throws Z3Exception {

		CheckContextMatch(p1);
		CheckContextMatch(p2);
		return new Probe(this, Native.probeGt(nCtx(), p1.NativeObject(), p2.NativeObject()));
	}

	/**
	 * Create a probe that evaluates to "true" when the value returned by
	 * <paramref name="p1"/> is less than or equal the value returned by
	 * <paramref name="p2"/>
	 **/
	public Probe Le(Probe p1, Probe p2) throws Z3Exception {

		CheckContextMatch(p1);
		CheckContextMatch(p2);
		return new Probe(this, Native.probeLe(nCtx(), p1.NativeObject(), p2.NativeObject()));
	}

	/**
	 * Create a probe that evaluates to "true" when the value returned by
	 * <paramref name="p1"/> is greater than or equal the value returned by
	 * <paramref name="p2"/>
	 **/
	public Probe Ge(Probe p1, Probe p2) throws Z3Exception {

		CheckContextMatch(p1);
		CheckContextMatch(p2);
		return new Probe(this, Native.probeGe(nCtx(), p1.NativeObject(), p2.NativeObject()));
	}

	/**
	 * Create a probe that evaluates to "true" when the value returned by
	 * <paramref name="p1"/> is equal to the value returned by <paramref
	 * name="p2"/>
	 **/
	public Probe Eq(Probe p1, Probe p2) throws Z3Exception {

		CheckContextMatch(p1);
		CheckContextMatch(p2);
		return new Probe(this, Native.probeEq(nCtx(), p1.NativeObject(), p2.NativeObject()));
	}

	/**
	 * Create a probe that evaluates to "true" when the value <paramref
	 * name="p1"/> and <paramref name="p2"/> evaluate to "true".
	 **/
	public Probe And(Probe p1, Probe p2) throws Z3Exception {

		CheckContextMatch(p1);
		CheckContextMatch(p2);
		return new Probe(this, Native.probeAnd(nCtx(), p1.NativeObject(), p2.NativeObject()));
	}

	/**
	 * Create a probe that evaluates to "true" when the value <paramref
	 * name="p1"/> or <paramref name="p2"/> evaluate to "true".
	 **/
	public Probe Or(Probe p1, Probe p2) throws Z3Exception {

		CheckContextMatch(p1);
		CheckContextMatch(p2);
		return new Probe(this, Native.probeOr(nCtx(), p1.NativeObject(), p2.NativeObject()));
	}

	/**
	 * Create a probe that evaluates to "true" when the value <paramref
	 * name="p"/> does not evaluate to "true".
	 **/
	public Probe Not(Probe p) throws Z3Exception {

		CheckContextMatch(p);
		return new Probe(this, Native.probeNot(nCtx(), p.NativeObject()));
	}

	/**
	 * Creates a new (incremental) solver. <remarks> This solver also uses a set
	 * of builtin tactics for handling the first check-sat command, and
	 * check-sat commands that take more than a given number of milliseconds to
	 * be solved. </remarks>
	 **/
	public Solver MkSolver() throws Z3Exception {
		return MkSolver((Symbol) null);
	}

	/**
	 * Creates a new (incremental) solver. <remarks> This solver also uses a set
	 * of builtin tactics for handling the first check-sat command, and
	 * check-sat commands that take more than a given number of milliseconds to
	 * be solved. </remarks>
	 **/
	public Solver MkSolver(Symbol logic) throws Z3Exception {

		if (logic == null)
			return new Solver(this, Native.mkSolver(nCtx()));
		else
			return new Solver(this, Native.mkSolverForLogic(nCtx(), logic.NativeObject()));
	}

	/**
	 * Creates a new (incremental) solver. <seealso cref="MkSolver(Symbol)"/>
	 **/
	public Solver MkSolver(String logic) throws Z3Exception {

		return MkSolver(MkSymbol(logic));
	}

	/**
	 * Creates a new (incremental) solver.
	 **/
	public Solver MkSimpleSolver() throws Z3Exception {

		return new Solver(this, Native.mkSimpleSolver(nCtx()));
	}

	/**
	 * Creates a solver that is implemented using the given tactic. <remarks>
	 * The solver supports the commands <code>Push</code> and <code>Pop</code>,
	 * but it will always solve each check from scratch. </remarks>
	 **/
	public Solver MkSolver(Tactic t) throws Z3Exception {

		return new Solver(this, Native.mkSolverFromTactic(nCtx(), t.NativeObject()));
	}

	/**
	 * Create a Fixedpoint context.
	 **/
	public Fixedpoint MkFixedpoint() throws Z3Exception {

		return new Fixedpoint(this);
	}

	/**
	 * Wraps an AST. <remarks>This function is used for transitions between
	 * native and managed objects. Note that <paramref name="nativeObject"/>
	 * must be a native object obtained from Z3 (e.g., through <seealso
	 * cref="UnwrapAST"/>) and that it must have a correct reference count (see
	 * e.g., <seealso cref="Native.Z3_inc_ref"/>.</remarks> <seealso
	 * cref="UnwrapAST"/> <param name="nativeObject">The native pointer to
	 * wrap.</param>
	 **/
	public AST WrapAST(long nativeObject) throws Z3Exception {

		return AST.Create(this, nativeObject);
	}

	/**
	 * Unwraps an AST. <remarks>This function is used for transitions between
	 * native and managed objects. It returns the native pointer to the AST.
	 * Note that AST objects are reference counted and unwrapping an AST
	 * disables automatic reference counting, i.e., all references to the IntPtr
	 * that is returned must be handled externally and through native calls (see
	 * e.g., <seealso cref="Native.Z3_inc_ref"/>).</remarks> <seealso
	 * cref="WrapAST"/> <param name="a">The AST to unwrap.</param>
	 **/
	public long UnwrapAST(AST a) {
		return a.NativeObject();
	}

	/**
	 * Return a string describing all available parameters to
	 * <code>Expr.Simplify</code>.
	 **/
	public String SimplifyHelp() throws Z3Exception {

		return Native.simplifyGetHelp(nCtx());
	}

	/**
	 * Retrieves parameter descriptions for simplifier.
	 **/
	public ParamDescrs SimplifyParameterDescriptions() throws Z3Exception {
		return new ParamDescrs(this, Native.simplifyGetParamDescrs(nCtx()));
	}

	/**
	 * Enable/disable printing of warning messages to the console. <remarks>Note
	 * that this function is static and effects the behaviour of all contexts
	 * globally.</remarks>
	 **/
	public static void ToggleWarningMessages(boolean enabled) throws Z3Exception {
		Native.toggleWarningMessages((enabled) ? true : false);
	}

	/**
	 * Update a mutable configuration parameter. <remarks> The list of all
	 * configuration parameters can be obtained using the Z3 executable:
	 * <code>z3.exe -ini?</code> Only a few configuration parameters are mutable
	 * once the context is created. An exception is thrown when trying to modify
	 * an immutable parameter. </remarks> <seealso cref="GetParamValue"/>
	 **/
	public void UpdateParamValue(String id, String value) throws Z3Exception {
		Native.updateParamValue(nCtx(), id, value);
	}

	/**
	 * Get a configuration parameter. <remarks> Returns null if the parameter
	 * value does not exist. </remarks> <seealso cref="UpdateParamValue"/>
	 **/
	public String GetParamValue(String id) throws Z3Exception {
		Native.StringPtr res = new Native.StringPtr();
		boolean r = Native.getParamValue(nCtx(), id, res);
		if (!r)
			return null;
		else
			return res.value;
	}

	long m_ctx = 0;

	long nCtx() {
		return m_ctx;
	}

	void InitContext() throws Z3Exception {
		setPrintMode(Z3_ast_print_mode.Z3_PRINT_SMTLIB2_COMPLIANT);
		Native.setInternalErrorHandler(nCtx());
	}

	void CheckContextMatch(Z3Object other) throws Z3Exception {
		if (this != other.Context())
			throw new Z3Exception("Context mismatch");
	}

	void CheckContextMatch(Z3Object[] arr) throws Z3Exception {
		if (arr != null)
			for (Z3Object a : arr)
				CheckContextMatch(a);
	}

	private ASTDecRefQueue m_AST_DRQ = new ASTDecRefQueue();
	private ASTMapDecRefQueue m_ASTMap_DRQ = new ASTMapDecRefQueue();
	private ASTVectorDecRefQueue m_ASTVector_DRQ = new ASTVectorDecRefQueue();
	private ApplyResultDecRefQueue m_ApplyResult_DRQ = new ApplyResultDecRefQueue();
	private FuncInterpEntryDecRefQueue m_FuncEntry_DRQ = new FuncInterpEntryDecRefQueue();
	private FuncInterpDecRefQueue m_FuncInterp_DRQ = new FuncInterpDecRefQueue();
	private GoalDecRefQueue m_Goal_DRQ = new GoalDecRefQueue();
	private ModelDecRefQueue m_Model_DRQ = new ModelDecRefQueue();
	private ParamsDecRefQueue m_Params_DRQ = new ParamsDecRefQueue();
	private ParamDescrsDecRefQueue m_ParamDescrs_DRQ = new ParamDescrsDecRefQueue();
	private ProbeDecRefQueue m_Probe_DRQ = new ProbeDecRefQueue();
	private SolverDecRefQueue m_Solver_DRQ = new SolverDecRefQueue();
	private StatisticsDecRefQueue m_Statistics_DRQ = new StatisticsDecRefQueue();
	private TacticDecRefQueue m_Tactic_DRQ = new TacticDecRefQueue();
	private FixedpointDecRefQueue m_Fixedpoint_DRQ = new FixedpointDecRefQueue();

	ASTDecRefQueue AST_DRQ() {
		return m_AST_DRQ;
	}

	ASTMapDecRefQueue ASTMap_DRQ() {
		return m_ASTMap_DRQ;
	}

	ASTVectorDecRefQueue ASTVector_DRQ() {
		return m_ASTVector_DRQ;
	}

	ApplyResultDecRefQueue ApplyResult_DRQ() {
		return m_ApplyResult_DRQ;
	}

	FuncInterpEntryDecRefQueue FuncEntry_DRQ() {
		return m_FuncEntry_DRQ;
	}

	FuncInterpDecRefQueue FuncInterp_DRQ() {
		return m_FuncInterp_DRQ;
	}

	GoalDecRefQueue Goal_DRQ() {
		return m_Goal_DRQ;
	}

	ModelDecRefQueue Model_DRQ() {
		return m_Model_DRQ;
	}

	ParamsDecRefQueue Params_DRQ() {
		return m_Params_DRQ;
	}

	ParamDescrsDecRefQueue ParamDescrs_DRQ() {
		return m_ParamDescrs_DRQ;
	}

	ProbeDecRefQueue Probe_DRQ() {
		return m_Probe_DRQ;
	}

	SolverDecRefQueue Solver_DRQ() {
		return m_Solver_DRQ;
	}

	StatisticsDecRefQueue Statistics_DRQ() {
		return m_Statistics_DRQ;
	}

	TacticDecRefQueue Tactic_DRQ() {
		return m_Tactic_DRQ;
	}

	FixedpointDecRefQueue Fixedpoint_DRQ() {
		return m_Fixedpoint_DRQ;
	}

	protected long m_refCount = 0;

	/**
	 * Finalizer.
	 **/
	protected void finalize() {
		Dispose();

		if (m_refCount == 0) {
			try {
				Native.delContext(m_ctx);
			} catch (Z3Exception e) {
				// OK.
			}
			m_ctx = 0;
		} else
			/* re-queue the finalizer */
			/* BUG: DRQ's need to be taken over too! */
			new Context(m_ctx, m_refCount);
	}

	/**
	 * Disposes of the context.
	 **/
	public void Dispose() {
		m_AST_DRQ.Clear(this);
		m_ASTMap_DRQ.Clear(this);
		m_ASTVector_DRQ.Clear(this);
		m_ApplyResult_DRQ.Clear(this);
		m_FuncEntry_DRQ.Clear(this);
		m_FuncInterp_DRQ.Clear(this);
		m_Goal_DRQ.Clear(this);
		m_Model_DRQ.Clear(this);
		m_Params_DRQ.Clear(this);
		m_Probe_DRQ.Clear(this);
		m_Solver_DRQ.Clear(this);
		m_Statistics_DRQ.Clear(this);
		m_Tactic_DRQ.Clear(this);
		m_Fixedpoint_DRQ.Clear(this);

		m_boolSort = null;
		m_intSort = null;
		m_realSort = null;
	}
}
