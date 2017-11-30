package crazypants.enderio.base.config;

import javax.annotation.Nonnull;

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

  public IValue<Integer> make(Section section, String keyname, int defaultValue, String text) {
    return new IntValue(section.name, keyname, defaultValue, text);
  }

  public IValue<Double> make(Section section, String keyname, double defaultValue, String text) {
    return new DoubleValue(section.name, keyname, defaultValue, text);
  }

  public IValue<Float> make(Section section, String keyname, float defaultValue, String text) {
    return new FloatValue(section.name, keyname, defaultValue, text);
  }

  public IValue<String> make(Section section, String keyname, String defaultValue, String text) {
    return new StringValue(section.name, keyname, defaultValue, text);
  }

  public interface IValue<T> {
    @Nonnull
    T get();
  }

  public abstract class AbstractValue<T> implements IValue<T> {

    protected int valueGeneration = 0;
    protected @Nonnull final String section, keyname, text;
    protected final T defaultValue;
    protected T value = null;

    protected AbstractValue(String section, String keyname, T defaultValue, String text) {
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
      return value;
    }

    protected abstract @Nonnull T makeValue();

  }

  public class IntValue extends AbstractValue<Integer> {

    protected IntValue(String section, String keyname, Integer defaultValue, String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nonnull Integer makeValue() {
      return config.get(section, keyname, defaultValue, text).getInt(defaultValue);
    }

  }

  public class DoubleValue extends AbstractValue<Double> {

    protected DoubleValue(String section, String keyname, Double defaultValue, String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nonnull Double makeValue() {
      return config.get(section, keyname, defaultValue, text).getDouble(defaultValue);
    }

  }

  public class FloatValue extends AbstractValue<Float> {

    protected FloatValue(String section, String keyname, Float defaultValue, String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nonnull Float makeValue() {
      return (float) config.get(section, keyname, defaultValue, text).getDouble(defaultValue);
    }

  }

  public class StringValue extends AbstractValue<String> {

    protected StringValue(String section, String keyname, String defaultValue, String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nonnull String makeValue() {
      return config.get(section, keyname, defaultValue, text).getString();
    }

  }

}
