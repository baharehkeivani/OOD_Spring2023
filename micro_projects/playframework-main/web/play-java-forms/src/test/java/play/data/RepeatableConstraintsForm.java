/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.data;

import play.data.validation.Constraints.Pattern;
import play.data.validation.Constraints.ValidateWith;

public class RepeatableConstraintsForm {

  @ValidateWith(BlueValidator.class)
  @ValidateWith(GreenValidator.class)
  @Pattern(value = "[a-c]", message = "Should be a - c")
  @Pattern(value = "[c-h]", message = "Should be c - h")
  private String name;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
