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

import indigo.interfaces.operations.Parameter;

public class GenericVariable implements Parameter {

	private String type;
	private final String name;

	public GenericVariable(JSONObject obj) {
		this.name = (String) obj.get("var_name");
		this.type = (String) obj.get("type");
	}

	public GenericVariable(String varName, String varType) {
		this.name = varName;
		this.type = varType;
	}

	@Override
	public String toString() {
		return type + " : " + name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return (name + type).hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof GenericVariable) {
			return this.name.equals(((GenericVariable) other).name) && this.type.equals(((GenericVariable) other).type);
		} else {
			return false;
		}
	}

	@Override
	public GenericVariable copyOf() {
		return new GenericVariable(name, type);
	}

	@Override
	public void setType(String type) {
		this.type = type;

	}

}
