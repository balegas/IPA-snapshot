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

import indigo.interfaces.logic.Value;
import indigo.interfaces.logic.enums.PREDICATE_TYPE;

public class BooleanValue extends JavaPredicateValue {

	BooleanValue(String value) {
		this.value = value;
	}

	public BooleanValue(boolean value) {
		this.value = value + "";
	}

	@Override
	public PREDICATE_TYPE getType() {
		return PREDICATE_TYPE.bool;
	}

	public static BooleanValue fromString(String value) {
		if (value.equals("true")) {
			return new BooleanValue("true");
		}
		return new BooleanValue("false");
	}

	public static BooleanValue TrueValue() {
		return new BooleanValue("true");
	}

	public static BooleanValue FalseValue() {
		return new BooleanValue("false");
	}

	public static Value newFromBool(boolean b) {
		return new BooleanValue(b);
	}

	@Override
	public Value copyOf() {
		return new BooleanValue((String) value);
	}

}