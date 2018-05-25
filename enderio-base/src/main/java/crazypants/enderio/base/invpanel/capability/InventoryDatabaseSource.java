package crazypants.enderio.base.invpanel.capability;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public class InventoryDatabaseSource implements IDatabaseSource<IItemHandler> {

  private final @Nonnull BlockPos pos;
  private final @Nonnull IItemHandler inv;

  public InventoryDatabaseSource(@Nonnull BlockPos pos, @Nonnull IItemHandler inv) {
    this.pos = pos;
    this.inv = inv;
  }

  @Override
  @Nonnull
  public BlockPos getPos() {
    return pos;
  }

  @Override
  @Nonnull
  public IItemHandler getSource() {
    return inv;
  }

}
