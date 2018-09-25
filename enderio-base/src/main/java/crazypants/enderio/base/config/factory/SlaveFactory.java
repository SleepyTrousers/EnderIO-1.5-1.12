package crazypants.enderio.base.config.factory;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;

public class SlaveFactory implements IValueFactory {

  private final @Nonnull String section;
  private final @Nonnull IRootFactory parent;
  private Map<String, Object> serverConfig = null;
  private int generation = 0;
  final private @Nonnull NNList<AbstractValue<?>> syncValues = new NNList<>();

  public SlaveFactory(@Nonnull IRootFactory valueFactory, @Nonnull String section) {
    this.parent = valueFactory;
    this.section = section;
    FactoryManager.registerFactory(this);
  }

  @Override
  public @Nonnull IValueFactory section(@SuppressWarnings("hiding") @Nonnull String section) {
    if (section.startsWith(".")) {
      return new SlaveFactory(this, this.section + section);
    } else {
      return new SlaveFactory(this, section);
    }
  }

  @Override
  public boolean isServerOverrideInPlace() {
    return serverConfig != null;
  }

  @Override
  public @Nonnull String getModid() {
    return parent.getModid();
  }

  @Override
  public @Nonnull String getSection() {
    return section;
  }

  @Override
  public @Nonnull IValue<Integer> make(@Nonnull String keyname, int defaultValue, @Nonnull String text) {
    return new IntValue(this, section, keyname, defaultValue, text).preload();
  }

  @Override
  public @Nonnull IValue<Double> make(@Nonnull String keyname, double defaultValue, @Nonnull String text) {
    return new DoubleValue(this, section, keyname, defaultValue, text).preload();
  }

  @Override
  public @Nonnull IValue<Float> make(@Nonnull String keyname, float defaultValue, @Nonnull String text) {
    return new FloatValue(this, section, keyname, defaultValue, text).preload();
  }

  @Override
  public @Nonnull IValue<String> make(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    return new StringValue(this, section, keyname, defaultValue, text).preload();
  }

  @Override
  public @Nonnull IValue<String> make(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String[] limit, @Nonnull String text) {
    return new LimitedStringValue(this, section, keyname, defaultValue, limit, text).preload();
  }

  @Override
  public @Nonnull IValue<Boolean> make(@Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
    return new BooleanValue(this, section, keyname, defaultValue, text).preload();
  }

  @Override
  public @Nonnull IValue<Things> make(@Nonnull String keyname, @Nonnull Things defaultValue, @Nonnull String text) {
    return new ThingsValue(this, section, keyname, defaultValue, text).preload();
  }

  @Override
  public @Nonnull <E extends Enum<E>> IValue<E> make(@Nonnull String keyname, @Nonnull E defaultValue, @Nonnull String text) {
    return new EnumValue<E>(this, section, keyname, defaultValue, text);
  }

  /**
   * Please note that fluids won't work in or before preinit!
   */
  @Override
  public @Nonnull IValue<Fluid> makeFluid(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    return new FluidValue(this, section, keyname, defaultValue, text).preload();
  }

  @Override
  public synchronized int read(final ByteBuf buf) {
    Map<String, Object> result = new HashMap<>();
    while (ByteBufHelper.ENDMARKER.readValue(buf) == null) {
      String keyName = (String) ByteBufHelper.STRING127.readValue(buf);
      byte dataTypeByte = buf.readByte();
      ByteBufHelper dataType = ByteBufHelper.values()[dataTypeByte];
      Object readValue = dataType.readValue(buf);
      result.put(keyName, readValue);
    }
    for (AbstractValue<?> abstractValue : syncValues) {
      abstractValue.onServerSync(result);
    }
    serverConfig = result;
    generation++;
    return result.size();
  }

  @Override
  public boolean needsSyncing() {
    return !syncValues.isEmpty();
  }

  @Override
  public void save(final ByteBuf buf) {
    for (AbstractValue<?> value : syncValues) {
      value.save(buf);
    }
    ByteBufHelper.ENDMARKER.saveValue(buf, this);
  }

  @Override
  public void endServerOverride() {
    serverConfig = null;
    generation++;
  }

  @Override
  public Configuration getConfig() {
    return parent.getConfig();
  }

  @Override
  public boolean isInInit() {
    return parent.isInInit();
  }

  @Override
  public Map<String, Object> getServerConfig() {
    return serverConfig;
  }

  @Override
  public int getGeneration() {
    return 31 * (31 + parent.getGeneration()) + generation;
  }

  @Override
  public void addSyncValue(@Nonnull AbstractValue<?> value) {
    syncValues.add(value);
  }

  @Override
  public void addPreloadValue(@Nonnull AbstractValue<?> value) {
    parent.addPreloadValue(value);
  }

}
