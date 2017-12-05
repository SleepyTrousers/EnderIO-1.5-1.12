package crazypants.enderio.base.config;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.RebuildableThings;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.ValueFactory.IValue;

public class SectionedValueFactory {

  private final @Nonnull ValueFactory parent;
  private final @Nonnull Section section;

  public SectionedValueFactory(@Nonnull ValueFactory parent, @Nonnull Section section) {
    this.parent = parent;
    this.section = section;
  }

  public @Nonnull IValue<Integer> make(@Nonnull String keyname, int defaultValue, @Nonnull String text) {
    return parent.make(section, keyname, defaultValue, text);
  }

  public @Nonnull IValue<Double> make(@Nonnull String keyname, double defaultValue, @Nonnull String text) {
    return parent.make(section, keyname, defaultValue, text);
  }

  public @Nonnull IValue<Float> make(@Nonnull String keyname, float defaultValue, @Nonnull String text) {
    return parent.make(section, keyname, defaultValue, text);
  }

  public @Nonnull IValue<String> make(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    return parent.make(section, keyname, defaultValue, text);
  }

  public @Nonnull IValue<Boolean> make(@Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
    return parent.make(section, keyname, defaultValue, text);
  }

  public @Nonnull IValue<RebuildableThings> make(@Nonnull String keyname, @Nonnull RebuildableThings defaultValue, @Nonnull String text) {
    return parent.make(section, keyname, defaultValue, text);
  }

}
