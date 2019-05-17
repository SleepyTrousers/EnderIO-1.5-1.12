package crazypants.enderio.base.filter.capability;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityFilterHolder {

  @SuppressWarnings("null")
  @CapabilityInject(IFilterHolder.class)
  @Nonnull
  public static Capability<IFilterHolder> FILTER_HOLDER_CAPABILITY = null;

  public static void register() {
    CapabilityManager.INSTANCE.register(IFilterHolder.class, new Storage(), new Factory());

    NullHelper.notnullJ(FILTER_HOLDER_CAPABILITY, "Filter Holder Capability is not registered");
  }

  private static class Storage implements Capability.IStorage<IFilterHolder> {

    @Override
    @Nullable
    public NBTBase writeNBT(Capability<IFilterHolder> capability, IFilterHolder instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<IFilterHolder> capability, IFilterHolder instance, EnumFacing side, NBTBase nbt) {
    }

  }

  private static class Factory implements Callable<IFilterHolder> {

    @Override
    public IFilterHolder call() throws Exception {
      return null;
    }

  }

}
