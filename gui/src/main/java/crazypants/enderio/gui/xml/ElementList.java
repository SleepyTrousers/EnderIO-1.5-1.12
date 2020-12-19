package crazypants.enderio.gui.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

public class ElementList {

  private final @Nonnull List<IRecipeConfigElement> º = new ArrayList<>();

  public @Nonnull List<IRecipeConfigElement> getElements() {
    return º;
  }

  public final @Nonnull ElementList add(@Nonnull IRecipeConfigElement... elements) {
    for (IRecipeConfigElement element : elements) {
      if (element != null) {
        º.add(element);
      }
    }
    return this;
  }

  @SafeVarargs
  public final @Nonnull ElementList add(@Nonnull List<? extends IRecipeConfigElement>... elements) {
    for (List<? extends IRecipeConfigElement> element : elements) {
      if (element != null) {
        º.addAll(element);
      }
    }
    return this;
  }

  @SafeVarargs
  public final @Nonnull ElementList add(@Nonnull Optional<? extends IRecipeConfigElement>... elements) {
    for (Optional<? extends IRecipeConfigElement> element : elements) {
      if (element != null && element.isPresent()) {
        º.add(element.get());
      }
    }
    return this;
  }

  public static @Nonnull ElementList of() {
    return new ElementList();
  }

  public static @Nonnull ElementList of(@Nonnull IRecipeConfigElement... elements) {
    return new ElementList().add(elements);
  }

  @SafeVarargs
  public static @Nonnull ElementList of(@Nonnull List<? extends IRecipeConfigElement>... elements) {
    return new ElementList().add(elements);
  }

  @SafeVarargs
  public static @Nonnull ElementList of(@Nonnull Optional<? extends IRecipeConfigElement>... element) {
    return new ElementList().add(element);
  }

}
