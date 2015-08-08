package indigo.annotations;

import indigo.annotations.impl.TrueAssertsCollector;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(TrueAssertsCollector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface True {
	String value();
}
