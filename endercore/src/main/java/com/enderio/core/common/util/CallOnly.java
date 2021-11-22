package com.enderio.core.common.util;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks a method to only be called on a side without using the {@link net.minecraftforge.api.distmarker.OnlyIn} Annotation
 * Todo: Maybe do some bytecode magic to add a check before each invocation of this method to {@link EffectiveSide#get()} and log a warning and print a stacktrace
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CallOnly {
    LogicalSide value();
}
