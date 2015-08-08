package app;

import indigo.annotations.Decrements;
import indigo.annotations.False;
import indigo.annotations.Increments;
import indigo.annotations.Invariant;
import indigo.annotations.True;

/** A product in a cart implies the product exists and the cart exists. **/
@Invariant("forall( Product : u, Cart : c) :- inCart(p, c)  => product(p) and cart(c)")
/** A product in a finished cart is ordered**/
@Invariant("forall( Product : p, Cart : c ) :- ( finished(c) and inCart(p, c) ) => order(p, c)")
/**Stock of items are always greater or equalt than zero**/
@Invariant("forall( Product : p) :- nrProduct(p) >= 0")
/**Unique user identifiers**/
@Invariant("forall( User : u) :- nrUser(u) <= 1")
/**A cart is bought only once**/
@Invariant("forall( Cart : c) :- nrFinished(c) <= 1")
public interface ITPCW {

	@True("inCart($0, $1)")
	public void ShoppingCartInteractionPUT(Product p, Cart c, User u);

	@False("inCart($0, $1)")
	public void ShoppingCartInteractionREMOVE(Product p, Cart c, User u);

	@Increments("nrUser($0)")
	public void customerRegistration(User u);

	@True("order(Product : _,  $0)")
	@True("finished($0)")
	@Increments("nrFinished($0)")
	@Decrements("nrProduct(X)")
	public void buyConfirm(Cart c, User u);

	/*
	 * public void _buyRequest(Cart c, User u);
	 * 
	 * public void _home();
	 * 
	 * public void _orderInquiry();
	 * 
	 * public void _orderDisplay();
	 * 
	 * public void _searchRequest();
	 * 
	 * public void _searchResult();
	 * 
	 * public void _newProducts();
	 * 
	 * public void _bestSellers();
	 * 
	 * public void _productDetail();
	 * 
	 * public void __adminRequest();
	 * 
	 * public void __adminConfirm();
	 */
	class User {
	}

	class Product {
	}

	class Cart {
	}

	class Order {
	}

	class Payment {
	}
}