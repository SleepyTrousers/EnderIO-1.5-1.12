package crazypants.enderio.base.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.Config.Section;
import net.minecraftforge.common.config.Configuration;

public class ValueFactory {

  protected Configuration config = null;
  protected int generation = 0;

  public ValueFactory() {
  }

  public void setConfig(Configuration config) {
    this.config = config;
    generation++;
  }

  public @Nonnull IValue<Integer> make(@Nonnull Section section, @Nonnull String keyname, int defaultValue, @Nonnull String text) {
    return new IntValue(section.name, keyname, defaultValue, text);
  }

  public @Nonnull IValue<Double> make(@Nonnull Section section, @Nonnull String keyname, double defaultValue, @Nonnull String text) {
    return new DoubleValue(section.name, keyname, defaultValue, text);
  }

  public @Nonnull IValue<Float> make(@Nonnull Section section, @Nonnull String keyname, float defaultValue, @Nonnull String text) {
    return new FloatValue(section.name, keyname, defaultValue, text);
  }

  public @Nonnull IValue<String> make(@Nonnull Section section, @Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    return new StringValue(section.name, keyname, defaultValue, text);
  }

  public @Nonnull IValue<Boolean> make(@Nonnull Section section, @Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
    return new BooleanValue(section.name, keyname, defaultValue, text);
  }

  public interface IValue<T> {
    @Nonnull
    T get();
  }

  public abstract class AbstractValue<T> implements IValue<T> {

    protected int valueGeneration = 0;
    protected final @Nonnull String section, keyname, text;
    protected final @Nonnull T defaultValue;
    protected @Nullable T value = null;

    protected AbstractValue(@Nonnull String section, @Nonnull String keyname, @Nonnull T defaultValue, @Nonnull String text) {
      this.section = section;
      this.keyname = keyname;
      this.text = text;
      this.defaultValue = defaultValue;
    }

    @Nonnull
    @Override
    public T get() {
      if (value == null || valueGeneration != generation) {
        value = makeValue();
        if (config.hasChanged()) {
          config.save();
        }
        valueGeneration = generation;
      }
      return NullHelper.first(value, defaultValue);
    }

    protected abstract @Nullable T makeValue();

  }

  public class IntValue extends AbstractValue<Integer> {

    protected IntValue(@Nonnull String section, @Nonnull String keyname, @Nonnull Integer defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Integer makeValue() {
      return config.get(section, keyname, defaultValue, text).getInt(defaultValue);
    }

  }

  public class DoubleValue extends AbstractValue<Double> {

    protected DoubleValue(@Nonnull String section, @Nonnull String keyname, @Nonnull Double defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Double makeValue() {
      return config.get(section, keyname, defaultValue, text).getDouble(defaultValue);
    }

  }

  public class FloatValue extends AbstractValue<Float> {

    protected FloatValue(@Nonnull String section, @Nonnull String keyname, @Nonnull Float defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Float makeValue() {
      return (float) config.get(section, keyname, defaultValue, text).getDouble(defaultValue);
    }

  }

  public class StringValue extends AbstractValue<String> {

    protected StringValue(@Nonnull String section, @Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable String makeValue() {
      return config.get(section, keyname, defaultValue, text).getString();
    }

  }

  public class BooleanValue extends AbstractValue<Boolean> {

    protected BooleanValue(@Nonnull String section, @Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Boolean makeValue() {
      return config.get(section, keyname, defaultValue, text).getBoolean(defaultValue);
    }

  }

}
