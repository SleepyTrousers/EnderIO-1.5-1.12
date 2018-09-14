package crazypants.enderio.base.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.EnderIO;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public interface IModTileEntityBase extends IModTileEntity {

  @Override
  default @Nonnull ResourceLocation getRegistryName() {
    return new ResourceLocation(EnderIO.DOMAIN, getUnlocalisedName());
  }

  @Override
  default @Nullable TileEntity getTileEntity() {
    try {
      return getTileEntityClass().newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  default @Nonnull TileEntity getTileEntityNN() {
    return NullHelper.notnull(getTileEntity(), "TileEntity ", this, " is unexpectedly missing");
  }

}
