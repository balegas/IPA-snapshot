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

public abstract class JavaPredicateValue implements Value {

	protected String value;

	public static JavaPredicateValue newFromString(String value) {
		if (value.matches("true|false")) {
			return new BooleanValue(value);
		} else if (value.matches("\\$*\\d*|\\-\\d*\\$*")) {
			return new IntegerValue(Integer.parseInt(value));
		} else {
			System.out.println("DOES NOT MATCH VALUE TYPE");
			System.exit(1);
			return null;
		}
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Value) {
			Object otherValue = ((Value) other).getValue();
			return value.equals(otherValue);
		} else
			return false;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public abstract PREDICATE_TYPE getType();

	@Override
	public String toString() {
		return value.toString();
	}

}
