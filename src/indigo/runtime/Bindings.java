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

import java.util.HashMap;

import indigo.runtime.Parser.Expression;

@SuppressWarnings("serial")
public class Bindings extends HashMap<Expression, Expression> {

	Bindings() {
	}

	Bindings(Expression a, Expression b) {
		put(a, b);
	}

	Bindings(Bindings a, Bindings b) {
		if (a != null)
			super.putAll(a);
		if (b != null)
			super.putAll(b);
	}
}
