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
import indigo.annotations.True;

@Invariant("forall( T:x, s1, e1, s2, e2 ) :- reserva(s1, e1) and reserva(s2, e2)) => ((e2 < s1) or (s2 > e1)) and exists(x)")
public interface Reserva {

	@True("reserva($0, $1)")
	void makeReservation1(int s1, int e2);

	@True("reserva($0, $1)")
	void makeReservation2(int s1, int e2);

	@False("exists($0)")
	void xxx(T x);

	class T {
	}
}
