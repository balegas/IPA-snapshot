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
