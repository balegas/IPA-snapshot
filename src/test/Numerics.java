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

import indigo.annotations.Numeric;
import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;

@Invariant("forall( Arg : x ) :- Counter_A(x) > 0")
@Invariant("forall( Arg : x ) :- Counter_B(x) <= 20")
@Invariant("forall( Arg : x ) :- Counter_C(x) >= 0 and Counter_C(x) <= 30")
public interface Numerics {

	@Numeric
	final String Counter_A = "Counter_A( Arg : x )";

	@Numeric
	final String Counter_B = "Counter_B( Arg : x )";

	@Numeric
	final String Counter_C = "Counter_C( Arg : x )";

	@Increments(Counter_A)
	void incA(Arg x);

	@Decrements(Counter_A)
	void decA(Arg x);

	@Increments(Counter_B)
	void incB(Arg x);

	@Decrements(Counter_B)
	void decB(Arg x);

	@Increments(Counter_C)
	void incC(Arg x);

	@Decrements(Counter_C)
	void decC(Arg x);

	class Arg {
	}

}
