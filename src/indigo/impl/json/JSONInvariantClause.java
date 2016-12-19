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
package indigo.impl.json;

import org.json.simple.JSONObject;

import indigo.interfaces.logic.Invariant;

public class JSONInvariantClause extends JSONClause implements Invariant {

	private final JSONClause invariantClause;

	public JSONInvariantClause(JSONObject obj) {
		this.invariantClause = objectToClause(obj, JSONClauseContext.emptyContext());
	}

	private JSONInvariantClause(JSONInvariantClause clause) {
		this.invariantClause = clause.invariantClause.copyOf();
	}

	JSONInvariantClause(JSONClause clause) {
		this.invariantClause = clause.copyOf();
	}

	@Override
	public int hashCode() {
		return invariantClause.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this.invariantClause.equals(((JSONInvariantClause) other).invariantClause);
	}

	@Override
	public JSONInvariantClause copyOf() {
		return new JSONInvariantClause(this);
	}

	@Override
	public String toString() {
		return invariantClause.toString();
	}

	@Override
	public void instantiateVariables(int i) {
		System.out.println("INV - copyWithSubstituteVariables - NOT IMPLEMENTED");
		System.exit(-1);
	}

	@Override
	public Invariant mergeClause(Invariant other) {
		if (other instanceof JSONInvariantClause) {
			JSONInvariantClause otherIC = (JSONInvariantClause) other;
			if (!(this.invariantClause instanceof JSONQuantifiedClause)
					&& (otherIC).invariantClause instanceof JSONQuantifiedClause) {
				return new JSONInvariantClause(otherIC.invariantClause.mergeClause(invariantClause));
			}
			return new JSONInvariantClause(invariantClause.mergeClause(((JSONInvariantClause) other).invariantClause));
		} else {
			System.out.println("MERGE NOT EXPECTED");
			System.exit(-1);
		}
		return null;
	}

	// @Override
	public boolean affectedBy(String predicateName) {
		return !invariantClause.toLogicExpression().matches(predicateName).isEmpty();
	}

}
