package indigo.annotations.impl;

import indigo.annotations.False;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FalseAssertsCollector {
	False[] value();
}
