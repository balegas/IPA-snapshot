package indigo.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import indigo.annotations.impl.PreTrueAssertsCollector;

@Repeatable(PreTrueAssertsCollector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreTrue {
	String value();
}
