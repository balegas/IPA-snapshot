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

import indigo.annotations.Decrements;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.True;

@Invariant("forall( User : u, Currency : c) :- available_currency(u, c) >= 0")
@Invariant("forall( Voucher : v ) :- nr_owners_voucher(v) <= 1")
@Invariant("forall( User : u ) :- available_user_vouchers(u) >= 0")
@Invariant("forall( User : u , Voucher : v) :- consumed(u, v) => owns_voucher(u, v)")
public interface Wallet {

	@Increments("nr_owners_voucher($1)")
	@Increments("available_user_vouchers($0)")
	@True("owns_voucher($0, $1)")
	public void purchaseVoucher(User u, Voucher v);

	@True("consumed($0, $1)")
	@Decrements("available_user_vouchers($0)")
	public void consumeVoucher(User u, Voucher v);

	@Increments("available_currency($0, $1)")
	public void purchaseCurrency(User u, Currency c, int amount);

	@Decrements("available_currency($0, $1)")
	public void consumeCurrency(User u, Currency c, int amount);

	class Currency {
	}

	class Voucher {
	}

	class User {
	}
}
