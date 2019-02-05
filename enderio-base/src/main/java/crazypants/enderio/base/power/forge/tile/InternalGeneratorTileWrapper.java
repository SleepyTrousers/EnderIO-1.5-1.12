package crazypants.enderio.base.power.forge.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;

public class InternalGeneratorTileWrapper extends InternalPoweredTileWrapper {

  public static @Nullable IEnergyStorage get(@Nonnull ILegacyPoweredTile.Generator tile, @Nullable EnumFacing facing) {
    if (facing != null && tile.canConnectEnergy(facing)) {
      return new InternalGeneratorTileWrapper(tile, facing);
    }
    return null;
  }

  public InternalGeneratorTileWrapper(@Nonnull ILegacyPoweredTile.Generator tile, @Nonnull EnumFacing from) {
    super(tile, from);
  }

}
