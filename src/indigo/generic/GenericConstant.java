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

import org.json.simple.JSONObject;

import indigo.impl.json.JSONClause;
import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;

public class GenericConstant extends JSONClause implements Value {

	private final PREDICATE_TYPE type;
	private final String value;

	public GenericConstant(JSONObject obj) {
		JSONObject value = (JSONObject) obj.get("value");
		// TODO: must change spec reserved value "int"
		if (value.get("type").equals("int")) {
			this.type = PREDICATE_TYPE.valueOf("numeric");
		} else {
			this.type = PREDICATE_TYPE.valueOf((String) value.get("type"));
		}
		this.value = "" + value.get("value");
	}

	public GenericConstant(PREDICATE_TYPE type, String valueAsString) {
		this.type = type;
		this.value = valueAsString;
	}

	/*
	 * @Override public int hashCode() { return (type.name() +
	 * value).hashCode(); }
	 *
	 * @Override public boolean equals(Object otherConstant) { JSONConstant
	 * other = (JSONConstant) otherConstant; return this.type.equals(other.type)
	 * && this.value.equals(other.value); }
	 */

	@Override
	public GenericConstant copyOf() {
		return new GenericConstant(type, value);
	}

	@Override
	public String toString() {
		// return value + " : " + type;
		return value;
	}

	@Override
	public void instantiateVariables(int i) {
	}

	public String getValueAsString() {
		return value;
	}

	@Override
	public PREDICATE_TYPE getType() {
		return type;
	}

	@Override
	public String getValue() {
		return value;
	}

	/*
	 * @Override public Value negatedValue() { if
	 * (type.equals(PREDICATE_TYPE.bool)) { if (value.equals("true")) { return
	 * new JSONConstant(type, "false"); } else { return new JSONConstant(type,
	 * "true"); } } System.out.println("NOT IMPLEMENTED - negated Value");
	 * System.exit(0); return null; }
	 */
}
