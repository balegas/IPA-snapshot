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
package indigo.interfaces.logic;

import java.util.List;

import indigo.interfaces.logic.enums.PREDICATE_TYPE;
import indigo.interfaces.operations.Parameter;
import indigo.invariants.LogicExpression;
import indigo.runtime.Parser.Expression;

public interface PredicateAssignment {

	public Expression expression();

	public PREDICATE_TYPE getType();

	public boolean isType(PREDICATE_TYPE type);

	public PredicateAssignment copyOf();

	public String getOperationName();

	public String getPredicateName();

	public Value getAssignedValue();

	public List<Parameter> getParams();

	public void applyEffect(LogicExpression e, int iteration);

	boolean affects(Invariant otherClause);

	public void updateParamTypes(List<Expression> params);

	public Value negateValue();

}
