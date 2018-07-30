package crazypants.enderio.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public interface IModTileEntity {

  @Nonnull
  String getUnlocalisedName();

  @Nonnull
  ResourceLocation getRegistryName();

  @Nonnull
  Class<? extends TileEntity> getTileEntityClass();

  @Nullable
  TileEntity getTileEntity();

  @Nonnull
  TileEntity getTileEntityNN();

}
