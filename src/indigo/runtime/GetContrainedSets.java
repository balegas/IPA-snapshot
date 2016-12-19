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
package indigo.runtime;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class GetContrainedSets implements Parser.ASTVisitor {

	static final Pattern bool = Pattern.compile("true|false");
	static final Pattern number = Pattern.compile("\\d+");

	Set<String> constrainedSets;

	public GetContrainedSets(Set<String> constrainedSets) {
		this.constrainedSets = constrainedSets;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T> T evalConstant(P parent, String val) {
		return (T) ImmutableSet.of();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalBinaryOperation(P parent, String op, E left, E right) {
		Set<String> predicateNames = Sets.newTreeSet();
		switch (op) {
		// case "=":
		// return (T) z3.ctx.mkEq((Expr) left, (Expr) right);
		// case "==":
		// return (T) z3.ctx.mkEq((Expr) left, (Expr) right);
		// case "<>":
		// return (T) z3.Not(z3.ctx.mkEq((Expr) left, (Expr) right));
		case "<":
		case "<=":
		case ">":
		case ">=":
		case "and":
		case "/\\":
		case "or":
		case "\\/":
			predicateNames.addAll((Collection<String>) left);
			predicateNames.addAll((Collection<String>) right);
			constrainedSets.addAll(predicateNames);
			break;
		default:
		}

		return (T) ImmutableSet.copyOf(predicateNames);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P, T, E> T evalUnaryOperation(P parent, String op, E right) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalFunction(P parent, String name, String ret, List<E> params) {
		if (name.charAt(0) == '#') {
			return (T) ImmutableSet.of(name.substring(1));
		}
		return (T) ImmutableSet.of();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, E> T evalDeclaration(P parent, String type, String var) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P, T, B, E> T evalQuantiedExpression(P parent, String type, B body, List<E> bounds) {
		if (body instanceof BinaryExpression) {
			BinaryExpression bexp = (BinaryExpression) body;
			evalBinaryOperation(this, bexp.type, bexp.myLeft, bexp.myRight);
		}
		return null;
	}
}
