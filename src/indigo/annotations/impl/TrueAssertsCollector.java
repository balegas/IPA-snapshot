package indigo.annotations.impl;

import indigo.annotations.True;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TrueAssertsCollector {
	True[] value();
}
