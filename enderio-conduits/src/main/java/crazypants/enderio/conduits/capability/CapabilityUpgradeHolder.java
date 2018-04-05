package crazypants.enderio.conduits.capability;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityUpgradeHolder {

  @CapabilityInject(IUpgradeHolder.class)
  @Nonnull
  public static Capability<IUpgradeHolder> UPGRADE_HOLDER_CAPABILITY = null;

  public static void register() {
    CapabilityManager.INSTANCE.register(IUpgradeHolder.class, new Storage(), new Factory());

    NullHelper.notnullJ(UPGRADE_HOLDER_CAPABILITY, "Filter Holder Capability is not registered");
  }

  private static class Storage implements Capability.IStorage<IUpgradeHolder> {

    @Override
    @Nullable
    public NBTBase writeNBT(Capability<IUpgradeHolder> capability, IUpgradeHolder instance, EnumFacing side) {
      return null;
    }

    @Override
    public void readNBT(Capability<IUpgradeHolder> capability, IUpgradeHolder instance, EnumFacing side, NBTBase nbt) {
    }

  }

  private static class Factory implements Callable<IUpgradeHolder> {

    @Override
    public IUpgradeHolder call() throws Exception {
      return null;
    }

  }

}
