package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import io.netty.buffer.ByteBuf;

public abstract class AbstractValue<T> implements IValue<T> {

  /**
   * 
   */
  protected final @Nonnull ValueFactory owner;
  protected int valueGeneration = 0;
  protected final @Nonnull String section, keyname;
  private final @Nonnull String text;
  protected final @Nonnull T defaultValue;
  protected @Nullable T value = null;
  protected Double minValue, maxValue;
  private boolean isSynced = false;

  protected AbstractValue(@Nonnull ValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull T defaultValue, @Nonnull String text) {
    this.owner = owner;
    this.section = section;
    this.keyname = keyname;
    this.text = text;
    this.defaultValue = defaultValue;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public T get() {
    if (value == null || valueGeneration != owner.getGeneration()) {
      if (owner.getServerConfig() != null && owner.getServerConfig().containsKey(keyname)) {
        try {
          value = (T) owner.getServerConfig().get(keyname);
        } catch (java.lang.ClassCastException e) {
          // I'm quite sure this will not happen here but when the caller gets the value. I'm not sure how to catch it at all, but it actually should not be
          // possible to happen as the server config is generated in code, and that code should be the same on server and client.
          Log.error("Server config value " + keyname + " is invalid");
          value = null;
        }
      } else {
        value = makeValue();
        if (!owner.isInInit() && owner.getConfig().hasChanged()) {
          owner.getConfig().save();
        }
      }
      valueGeneration = owner.getGeneration();
    }
    return NullHelper.first(value, defaultValue);
  }

  protected abstract @Nullable T makeValue();

  @Override
  @Nonnull
  public IValue<T> setMin(double min) {
    this.minValue = min;
    return this;
  }

  @Override
  @Nonnull
  public IValue<T> setMax(double max) {
    this.maxValue = max;
    return this;
  }

  @Override
  @Nonnull
  public IValue<T> sync() {
    isSynced = true;
    owner.addSyncValue(this);
    return this;
  };

  @Nonnull
  public IValue<T> preload() {
    owner.addPreloadValue(this);
    return this;
  };

  public void save(final ByteBuf buf) {
    ByteBufHelper.STRING127.saveValue(buf, keyname);
    ByteBufHelper dataType = getDataType();
    buf.writeByte(dataType.ordinal());
    dataType.saveValue(buf, get());
  }

  protected abstract ByteBufHelper getDataType();

  protected String getText() {
    return text + (isSynced ? FactoryManager.SERVER_OVERRIDE : "");
  }
}