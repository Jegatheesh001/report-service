package com.medas.rewamp.reportservice.configuration.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate <b>@Loggable</b> in method to log method related info
 * 
 * @author Jegatheesh <br>
 *         <b>Created</b> On Jan 11, 2020
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {

}
