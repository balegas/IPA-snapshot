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

public class JSONBinaryClause extends JSONClause {

	private final String operator;
	private final JSONClause left;
	private final JSONClause right;

	public JSONBinaryClause(String operator, JSONObject left, JSONObject right, JSONClauseContext context) {
		this.operator = operator;
		this.left = objectToClause(left, context);
		this.right = objectToClause(right, context);
	}

	protected JSONBinaryClause(String operator, JSONClause left, JSONClause right) {
		this.operator = operator;
		this.left = left.copyOf();
		this.right = right.copyOf();
	}

	@Override
	public String toString() {
		return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
	}

	@Override
	public JSONClause copyOf() {
		return new JSONBinaryClause(operator, left, right);
	}

	@Override
	public void instantiateVariables(int i) {
		left.instantiateVariables(i);
		right.instantiateVariables(i);
	}

	public JSONClause getLeftClause() {
		return left.copyOf();
	}

	public static JSONBinaryClause newFrom(String operator, JSONClause leftClause, JSONClause rightClause) {
		return new JSONBinaryClause(operator, leftClause, rightClause);
	}

}
