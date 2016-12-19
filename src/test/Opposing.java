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
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.True;

//TODO: Need some extra stuff to work. We want "test.Opposing" to run.

@Invariant("forall( Var : x ) :- A(x)")
@Invariant("forall( Var : x ) :- B(x) > 0")
public interface Opposing {

	@True("A(x)")
	public void doIt();

	@False("A(x)")
	@Increments("B(x)")
	public void doNotDoIt();

	class Var {

	}

}
