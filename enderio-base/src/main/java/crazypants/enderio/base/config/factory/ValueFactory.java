package crazypants.enderio.base.config.factory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.Log;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;

public class ValueFactory {

  private final @Nonnull String modid;
  private final @Nonnull String section;
  private Configuration config = null;
  private ValueFactory parent = null;
  private boolean inInit = false;
  private Map<String, Object> serverConfig = null;
  private int generation = 0;
  final private @Nonnull NNList<AbstractValue<?>> syncValues = new NNList<>();
  final private @Nonnull NNList<AbstractValue<?>> preloadValues = new NNList<>();

  public ValueFactory(@Nonnull String modid) {
    this(modid, "");
  }

  public ValueFactory(@Nonnull String modid, @Nonnull String section) {
    this.modid = modid;
    this.section = section;
    FactoryManager.registerFactory(this);
  }

  public ValueFactory(@Nonnull ValueFactory parent, @Nonnull String section) {
    this.parent = parent;
    this.modid = parent.getModid();
    this.section = section;
    FactoryManager.registerFactory(this);
  }

  public @Nonnull ValueFactory section(@SuppressWarnings("hiding") @Nonnull String section) {
    if (section.startsWith(".")) {
      return new ValueFactory(this, this.section + section);
    } else {
      return new ValueFactory(this, section);
    }
  }

  public boolean isServerOverrideInPlace() {
    return serverConfig != null;
  }

  public @Nonnull String getModid() {
    return modid;
  }

  public @Nonnull String getSection() {
    return section;
  }

  public void setConfig(Configuration config) {
    this.config = config;
    generation++;
    inInit = true;
    for (AbstractValue<?> value : preloadValues) {
      value.get();
    }
    inInit = false;
    if (config.hasChanged()) {
      config.save();
    }
    // Note: Forge trashes the config when loading it from disk, so we need to re-configure all values every time that happens
    // preloadValues.clear();
  }

  public @Nonnull IValue<Integer> make(@Nonnull String keyname, int defaultValue, @Nonnull String text) {
    return new IntValue(this, section, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<Double> make(@Nonnull String keyname, double defaultValue, @Nonnull String text) {
    return new DoubleValue(this, section, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<Float> make(@Nonnull String keyname, float defaultValue, @Nonnull String text) {
    return new FloatValue(this, section, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<String> make(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    return new StringValue(this, section, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<Boolean> make(@Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
    return new BooleanValue(this, section, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<Things> make(@Nonnull String keyname, @Nonnull Things defaultValue, @Nonnull String text) {
    return new ThingsValue(this, section, keyname, defaultValue, text).preload();
  }

  /**
   * Please note that fluids won't work in or before preinit!
   */
  public @Nonnull IValue<Fluid> makeFluid(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    return new FluidValue(this, section, keyname, defaultValue, text).preload();
  }

  final synchronized void read(final ByteBuf buf) {
    Map<String, Object> result = new HashMap<>();
    while (ByteBufHelper.ENDMARKER.readValue(buf) == null) {
      String keyName = (String) ByteBufHelper.STRING127.readValue(buf);
      byte dataTypeByte = buf.readByte();
      ByteBufHelper dataType = ByteBufHelper.values()[dataTypeByte];
      Object readValue = dataType.readValue(buf);
      result.put(keyName, readValue);
    }
    Log.info("Read " + result.size() + " config values from server packet for mod " + modid);
    serverConfig = result;
    generation++;
  }

  protected final void save(final ByteBuf buf) {
    for (AbstractValue<?> value : syncValues) {
      value.save(buf);
    }
    ByteBufHelper.ENDMARKER.saveValue(buf, this);
  }

  public void endServerOverride() {
    serverConfig = null;
    generation++;
  }

  protected Configuration getConfig() {
    return config != null ? config : parent != null ? parent.getConfig() : null;
  }

  public boolean isInInit() {
    return inInit;
  }

  protected Map<String, Object> getServerConfig() {
    return serverConfig;
  }

  protected int getGeneration() {
    return generation;
  }

  protected void addSyncValue(@Nonnull AbstractValue<?> value) {
    syncValues.add(value);
  }

  protected void addPreloadValue(@Nonnull AbstractValue<?> value) {
    preloadValues.add(value);
  }

}
