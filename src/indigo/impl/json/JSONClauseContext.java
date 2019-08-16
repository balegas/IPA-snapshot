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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import indigo.interfaces.operations.Parameter;

public class JSONClauseContext {

	private final Map<String, String> nameToType;

	public JSONClauseContext() {
		nameToType = new HashMap<>();
	}

	public JSONClauseContext(Collection<Parameter> vars) {
		nameToType = new HashMap<>();
		for (Parameter var : vars) {
			nameToType.put(var.getName(), var.getType());
		}
	}

	String getVarType(String varName) {
		return nameToType.getOrDefault(varName, "_");
	}

	public static JSONClauseContext emptyContext() {
		return new JSONClauseContext();
	}
}
