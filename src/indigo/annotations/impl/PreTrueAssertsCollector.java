package indigo.annotations.impl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import indigo.annotations.PreTrue;

@Retention(RetentionPolicy.RUNTIME)
public @interface PreTrueAssertsCollector {
	PreTrue[] value();
}
