package crazypants.enderio.machines.machine.mapomatic;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.Filters;
import com.enderio.core.common.inventory.Filters.PredicateItemStack;
import com.enderio.core.common.inventory.InventorySlot;

import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredTaskEntity;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.mapomatic.MapOMaticRecipe;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.MapData;

public class TileMapOMatic extends AbstractCapabilityPoweredTaskEntity {

  private class PredicatePaperStack extends PredicateItemStack {

    public PredicatePaperStack() {
    }

    @Override
    public boolean doApply(@Nonnull ItemStack input) {
      return input.isItemEqual(new ItemStack(Items.PAPER));
    }
  }

  public TileMapOMatic() {
    super(CapacitorKey.MAP_O_MATIC_POWER_INTAKE, CapacitorKey.MAP_O_MATIC_POWER_BUFFER, CapacitorKey.MAP_O_MATIC_POWER_USE);

    getInventory().add(Type.INPUT, "PAPER", new InventorySlot(new PredicatePaperStack(), Filters.ALWAYS_TRUE));
    getInventory().add(Type.INPUT, "INPUT", new InventorySlot());
    getInventory().add(Type.OUTPUT, "OUTPUT", new InventorySlot(Filters.ALWAYS_FALSE, Filters.ALWAYS_TRUE));
  }

  @Nonnull
  @Override
  public String getMachineName() {
    return MachineRecipeRegistry.MAPOMATIC;
  }

  @Override
  protected void mergeResults(@Nonnull IMachineRecipe.ResultStack[] results) {
    EnderInventory.View outputSlots = getInventory().getView(EnderInventory.Type.OUTPUT);
    if (outputSlots.getStackInSlot(0).isEmpty()) {
      if (cachedNextRecipe instanceof MapOMaticRecipe) {
        MapOMaticRecipe recipe = (MapOMaticRecipe) cachedNextRecipe;
        BlockPos structurePos = getWorld().findNearestStructure(recipe.getStructure(), getPos(), true);
        setupNewMap(results[0].item, structurePos, (byte) 2);
        outputSlots.getSlot(0).set(results[0].item);
      }
    }
  }

  private void setupNewMap(@Nonnull ItemStack mapIn, @Nonnull BlockPos pos, byte scale) {
    if (mapIn.getItem() instanceof ItemMap) {
      String s = "map_" + mapIn.getMetadata();

      // Initialise map
      MapData mapdata = new MapData(s);
      world.setData(s, mapdata);
      mapdata.scale = scale;
      mapdata.calculateMapCenter(pos.getX(), pos.getZ(), mapdata.scale);
      mapdata.dimension = world.provider.getDimension();
      mapdata.trackingPosition = true;
      mapdata.unlimitedTracking = true;
      mapdata.markDirty();
    }
  }

}
