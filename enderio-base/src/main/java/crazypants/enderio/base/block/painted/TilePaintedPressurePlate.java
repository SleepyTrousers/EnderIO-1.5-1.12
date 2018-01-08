package crazypants.enderio.base.block.painted;

import javax.annotation.Nonnull;

import crazypants.enderio.util.CapturedMob;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.EnumFacing;

@Storable
public class TilePaintedPressurePlate extends TileEntityPaintedBlock {

  @Store
  private @Nonnull EnumPressurePlateType type = EnumPressurePlateType.WOOD;
  @Store
  private boolean silent = false;
  @Store
  private @Nonnull EnumFacing rotation = EnumFacing.NORTH;
  @Store
  private CapturedMob capturedMob = null;

  protected @Nonnull EnumPressurePlateType getType() {
    return type;
  }

  protected void setType(@Nonnull EnumPressurePlateType type) {
    this.type = type;
    markDirty();
  }

  protected boolean isSilent() {
    return silent;
  }

  protected void setSilent(boolean silent) {
    this.silent = silent;
    markDirty();
  }

  protected @Nonnull EnumFacing getRotation() {
    return rotation;
  }

  protected void setRotation(@Nonnull EnumFacing rotation) {
    if (rotation != EnumFacing.DOWN && rotation != EnumFacing.UP) {
      this.rotation = rotation;
      markDirty();
      updateBlock();
    }
  }

  protected CapturedMob getMobType() {
    return capturedMob;
  }

  protected void setMobType(CapturedMob capturedMob) {
    this.capturedMob = capturedMob;
  }

}