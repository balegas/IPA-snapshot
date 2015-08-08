package indigo.annotations;

import indigo.annotations.impl.AssignsCollector;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(AssignsCollector.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Assigns {
	String value();
}
