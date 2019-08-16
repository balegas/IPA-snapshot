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
package app;

import indigo.annotations.Invariant;
import indigo.annotations.True;

// We need a new data type like state-machine that accepts a list of values with a specific order of precedence. E.g. start || end, end wins.

@Invariant("forall( Tournament : t) :- not (active(t)  and finished(t))")

public interface PAPOC5 {

	// @PreFalse("active($0)")
	// @PreFalse("finished($0)")
	@True("active($0)")
	public void begin(Tournament t);

	// @PreTrue("active($0)")
	// @False("active($0)")
	@True("finished($0)")
	public void end(Tournament t);

	class Player {
	}

	class Tournament {
	}
}
