package crazypants.enderio.invpanel.server;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import crazypants.enderio.base.invpanel.capability.InventoryDatabaseSource;
import crazypants.enderio.base.invpanel.database.AbstractInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public abstract class InventoryFactory {

  private static final ArrayList<InventoryFactory> factories;

  static {
    factories = new ArrayList<InventoryFactory>();
    // factories.add(new DSUFactory());
  }

  // TODO: Remove networked inventory
  static AbstractInventory createInventory(InventoryDatabaseSource source) {
    for (InventoryFactory f : factories) {
      AbstractInventory ai = f.create(source.getSource(), source.getPos());
      if (ai != null) {
        return ai;
      }
    }
    return new NormalInventory(source.getSource(), source.getPos());
  }

  abstract AbstractInventory create(@Nonnull IItemHandler inv, @Nonnull BlockPos pos);

  // static class DSUFactory extends InventoryFactory {
  // @Override
  // AbstractInventory create(NetworkedInventory ni) {
  // ISidedInventory inv = ni.getInventory();
  // if (inv instanceof IDeepStorageUnit) {
  // return new DSUInventory((IDeepStorageUnit) inv);
  // }
  // return null;
  // }
  // }

}
