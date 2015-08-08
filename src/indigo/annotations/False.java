package indigo.annotations;

import indigo.annotations.impl.FalseAssertsCollector;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(FalseAssertsCollector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface False {
	String value();
}
