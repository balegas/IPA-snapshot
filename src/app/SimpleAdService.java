package app;

import indigo.annotations.Decrements;
import indigo.annotations.Invariant;

//Non-negative number of prints.
@Invariant("forall( Ad : ad ) :- availPrints(ad) >= 0")
public interface SimpleAdService {

	@Decrements("availPrints($0)")
	public void printAd(Ad ad);

	class Ad {
	}

}
