package indigo.annotations.impl;

import indigo.annotations.Invariant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Invariants {
	Invariant[] value();
}
