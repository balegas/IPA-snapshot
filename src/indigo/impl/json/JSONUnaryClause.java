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

public class JSONUnaryClause extends JSONClause {

	private final String operator;
	private final JSONClause unaryClause;

	public JSONUnaryClause(String operator, JSONObject jsonObject, JSONClauseContext context) {
		super();
		this.operator = operator;
		this.unaryClause = objectToClause(jsonObject, context);
	}

	private JSONUnaryClause(String operator, JSONClause clause) {
		super();
		this.operator = operator;
		this.unaryClause = clause.copyOf();
	}

	@Override
	public JSONClause copyOf() {
		return new JSONUnaryClause(operator, unaryClause);
	}

	@Override
	public String toString() {
		return operator + " ( " + unaryClause.toString() + " ) ";
	}

	// @Override
	// public boolean isNumeric() {
	// return unaryClause.isNumeric();
	// }

	@Override
	public void instantiateVariables(int i) {
		unaryClause.instantiateVariables(i);
	}

}
