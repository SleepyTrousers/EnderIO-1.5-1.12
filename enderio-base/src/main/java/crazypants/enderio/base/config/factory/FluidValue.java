package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public class FluidValue extends AbstractValue<Fluid> {

  private final static @Nonnull Fluid defaultFluidPlaceholder = new Fluid("", null, null);

  private final @Nonnull String defaultValueName;
  private Fluid defaultFluid = null;

  protected FluidValue(@Nonnull ValueFactory owner, @Nonnull String section, @Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    super(owner, section, keyname, defaultFluidPlaceholder, text);
    defaultValueName = defaultValue;
  }

  @Nonnull
  @Override
  public Fluid get() {
    if (!Loader.instance().hasReachedState(LoaderState.INITIALIZATION)) {
      return defaultValue;
    }
    if (defaultFluid == null) {
      FluidRegistry.getFluid(defaultValueName);
    }
    if (value == null || valueGeneration != owner.getGeneration()) {
      if (owner.getServerConfig() != null && owner.getServerConfig().containsKey(keyname)) {
        value = FluidRegistry.getFluid((String) owner.getServerConfig().get(keyname));
      } else {
        value = FluidRegistry.getFluid(owner.getConfig().getString(keyname, section, defaultValueName, getText()));
        if (!owner.isInInit() && owner.getConfig().hasChanged()) {
          owner.getConfig().save();
        }
      }
      valueGeneration = owner.getGeneration();
    }
    return NullHelper.first(value, defaultFluid, defaultValue);
  }

  @Override
  protected @Nullable Fluid makeValue() {
    return null;
  }

  @Override
  protected ByteBufHelper getDataType() {
    return ByteBufHelper.STRING127;
  }

  @Override
  public void save(final ByteBuf buf) {
    ByteBufHelper.STRING127.saveValue(buf, keyname);
    ByteBufHelper dataType = getDataType();
    buf.writeByte(dataType.ordinal());
    dataType.saveValue(buf, NullHelper.first(FluidRegistry.getFluidName(get()), defaultValueName));
  }

}