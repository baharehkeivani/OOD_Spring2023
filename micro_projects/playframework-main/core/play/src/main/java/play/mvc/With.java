/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.mvc;

import java.lang.annotation.*;

/**
 * Decorates an <code>Action</code> or a <code>Controller</code> with another <code>Action</code>.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface With {
  Class<? extends Action<?>>[] value();
}
