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
package test;

import indigo.annotations.False;
import indigo.annotations.Invariant;

@Invariant("forall( X : x) :- A(x) or B(x) or C(x)")
public interface Disjunction {

	@False("A($0)")
	void a(X x);

	@False("B($0)")
	void b(X x);

	@False("C($0)")
	void c(X x);

	class X {
	}
}
