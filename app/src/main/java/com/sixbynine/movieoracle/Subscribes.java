package com.sixbynine.movieoracle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark that a Fragment or Activity should be registered to the main bus during onResume and
 * unregistered during onPause.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Subscribes {}
