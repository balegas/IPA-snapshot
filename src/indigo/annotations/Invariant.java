package indigo.annotations;

import indigo.annotations.impl.Invariants;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(Invariants.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Invariant {
	String value();
}
