package crazypants.enderio.base.config.factory;

import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.Fluid;

public interface IValueFactory extends IRootFactory {

  boolean isServerOverrideInPlace();

  @Nonnull
  IValue<Integer> make(@Nonnull String keyname, int defaultValue, @Nonnull String text);

  @Nonnull
  IValue<Double> make(@Nonnull String keyname, double defaultValue, @Nonnull String text);

  @Nonnull
  IValue<Float> make(@Nonnull String keyname, float defaultValue, @Nonnull String text);

  @Nonnull
  IValue<String> make(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text);

  @Nonnull
  IValue<String> make(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String[] limit, @Nonnull String text);

  @Nonnull
  IValue<Boolean> make(@Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text);

  @Nonnull
  IValue<Things> make(@Nonnull String keyname, @Nonnull Things defaultValue, @Nonnull String text);

  @Nonnull
  <E extends Enum<E>> IValue<E> make(@Nonnull String keyname, @Nonnull E defaultValue, @Nonnull String text);

  /**
   * Please note that fluids won't work in or before preinit!
   */
  @Nonnull
  IValue<Fluid> makeFluid(@Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text);

  void endServerOverride();

  void addSyncValue(@Nonnull AbstractValue<?> value);

  Map<String, Object> getServerConfig();

  boolean needsSyncing();

  void save(final ByteBuf buf);

  int read(final ByteBuf buf);

}