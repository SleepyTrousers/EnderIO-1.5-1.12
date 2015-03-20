package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.util.BlockCoord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

abstract public class Fertilizer {

  private static boolean initialized = false;
  private static final Fertilizer NONE = new None();
  private static List<Fertilizer> instances = new ArrayList<Fertilizer>();

  /**
   * Returns the singleton instance for the fertilizer that was given as
   * parameter. If the given item is no fertilizer, it will return an instance
   * of Fertilizer.None.
   * 
   */
  public static Fertilizer getInstance(ItemStack stack) {
    if (instances.isEmpty()) {
      instances.add(new Bonemeal());
      instances.add(new ForestryFertilizerCompound());
    }
    for (Fertilizer fertilizer : instances) {
      if (fertilizer.matches(stack)) {
        return fertilizer;
      }
    }
    return NONE;
  }

  /**
   * Returns true if the given item can be used as fertilizer.
   */
  public static boolean isFertilizer(ItemStack stack) {
    return getInstance(stack) != NONE;
  }

  protected abstract boolean matches(ItemStack stack);

  /**
   * Tries to apply the given item on the given block using the type-specific
   * method. SFX is played on success.
   * 
   * If the item was successfully applied, the stacksize will be decreased if
   * appropriate. The caller will need to check for stacksize 0 and null the
   * inventory slot if needed.
   * 
   * @param stack
   * @param player
   * @param world
   * @param bc
   * @return true if the fertilizer was applied
   */
  public abstract boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc);

  /**
   * Not a fertilizer. Using this handler class any item can be "used" as a
   * fertilizer. Meaning, fertilizing will always fail.
   *
   */
  private static class None extends Fertilizer {

    @Override
    protected boolean matches(ItemStack stack) {
      return true;
    }

    @Override
    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
      return false;
    }

  }

  private static class Bonemeal extends Fertilizer {

    @Override
    protected boolean matches(ItemStack stack) {
      return stack.getItem() == Items.dye && stack.getItemDamage() == 15;
    }

    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
      return stack.getItem().onItemUse(stack, player, world, bc.x, bc.y, bc.z, 1, 0.5f, 0.5f, 0.5f);
    }

  }

  private static class ForestryFertilizerCompound extends Bonemeal {

    private final Item forestryFertilizerCompound;

    protected ForestryFertilizerCompound() {
      forestryFertilizerCompound = GameRegistry.findItem("Forestry", "fertilizerCompound");
    }

    @Override
    protected boolean matches(ItemStack stack) {
      return forestryFertilizerCompound != null && stack.getItem() == forestryFertilizerCompound;
    }

  }

}
