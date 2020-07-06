package com.mattc.autotyper.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FXParseable {

    public static final String NO_VAL = "%%NO_VAL_SELECTED%%";

    String value() default NO_VAL;

    boolean hidden() default true;

}
