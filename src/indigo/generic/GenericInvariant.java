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
package indigo.generic;

import indigo.interfaces.logic.Clause;
import indigo.interfaces.logic.Invariant;
import indigo.invariants.LogicExpression;

public class GenericInvariant implements Invariant {

	Clause<Invariant> invariant;

	public GenericInvariant(Clause<Invariant> invariant) {
		this.invariant = invariant;
	}

	@Override
	public Invariant mergeClause(Invariant next) {
		return new GenericInvariant(invariant.mergeClause(next));
	}

	@Override
	public LogicExpression toLogicExpression() {
		return invariant.toLogicExpression();
	}

	@Override
	public boolean affectedBy(String predicateName) {
		return !invariant.toLogicExpression().matches(predicateName).isEmpty();
	}

	@Override
	public String toString() {
		return invariant.toString();
	}
}
