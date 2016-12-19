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
package indigo.impl.javaclass;

import indigo.interfaces.logic.Invariant;
import indigo.invariants.LogicExpression;
import indigo.runtime.Bindings;
import indigo.runtime.Parser.Expression;

public class JavaInvariantClause implements Invariant {

	LogicExpression invariant;

	public JavaInvariantClause(String invString) {
		this.invariant = new LogicExpression(invString);
	}

	public JavaInvariantClause() {
		this.invariant = new LogicExpression("");
	}

	JavaInvariantClause(LogicExpression invExp) {
		this.invariant = invExp;
	}

	// @Override
	public boolean affectedBy(String predicateName) {
		Bindings result = invariant.matches(predicateName);
		return result != null && !result.isEmpty();
	}

	@Override
	public Invariant mergeClause(Invariant next) {
		if (next instanceof JavaInvariantClause) {
			Expression mergedExp = Expression.merge(invariant.expression(),
					(((JavaInvariantClause) next).invariant.expression()));
			return new JavaInvariantClause(new LogicExpression(mergedExp));
		}
		assert (false);
		System.exit(0);
		return null;
	}

	@Override
	public LogicExpression toLogicExpression() {
		return invariant.copyOf();
	}

	// @Override
	// public JavaInvariantClause copyOf() {
	// return new JavaInvariantClause(invariant.copyOf());
	// }

	@Override
	public String toString() {
		return invariant.toString();
	}

	@Override
	public int hashCode() {
		return this.invariant.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof JavaInvariantClause) {
			return this.invariant.equals(((JavaInvariantClause) other).invariant);
		} else {
			return false;
		}
	}

}
