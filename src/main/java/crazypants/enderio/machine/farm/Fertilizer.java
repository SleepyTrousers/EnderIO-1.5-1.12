package crazypants.enderio.machine.farm;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.util.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

abstract public class Fertilizer {

  private static boolean initialized = false;
  private static final Fertilizer NONE = new None();
  protected static List<Fertilizer> instances = new ArrayList<Fertilizer>();

  /**
   * Returns the singleton instance for the fertilizer that was given as
   * parameter. If the given item is no fertilizer, it will return an instance
   * of Fertilizer.None.
   * 
   */
  public static Fertilizer getInstance(ItemStack stack) {
    if (instances.isEmpty()) {
      new Bonemeal().addInstance();
      new ForestryFertilizerCompound().addInstance();
      new BotaniaFloralFertilizer().addInstance();
      new MetallurgyFertilizer().addInstance();
      new GardenCoreCompost_pile().addInstance();
    }
    for (Fertilizer fertilizer : instances) {
      if (fertilizer.matches(stack)) {
        return fertilizer;
      }
    }
    return NONE;
  }

  protected abstract void addInstance();
  
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

  public boolean applyOnAir() { return false; }
  public boolean applyOnPlant() { return true; }

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

    @Override
    protected void addInstance() {
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

    @Override
    protected void addInstance() {
      instances.add(this);
    }

  }

  private static class ForestryFertilizerCompound extends Bonemeal {

    private Item forestryFertilizerCompound;

    @Override
    protected void addInstance() {
      forestryFertilizerCompound = GameRegistry.findItem("Forestry", "fertilizerCompound");
      if (forestryFertilizerCompound!= null) {
        instances.add(this);
      }
    }

    @Override
    protected boolean matches(ItemStack stack) {
      return forestryFertilizerCompound != null && stack.getItem() == forestryFertilizerCompound;
    }

  }

  private static class BotaniaFloralFertilizer extends Fertilizer {

    private Item floralFertilizer;

    @Override
    protected void addInstance() {
      floralFertilizer = GameRegistry.findItem("Botania", "fertilizer");
      if (floralFertilizer!= null) {
        instances.add(this);
      }
    }

    @Override
    protected boolean matches(ItemStack stack) {
      return floralFertilizer != null && stack.getItem() == floralFertilizer;
    }

    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockCoord bc) {
      BlockCoord below = bc.getLocation(ForgeDirection.DOWN);
      Block belowBlock = below.getBlock(world);
      if (belowBlock == Blocks.dirt || belowBlock == Blocks.grass) {
        return stack.getItem().onItemUse(stack, player, world, below.x, below.y, below.z, 1, 0.5f, 0.5f, 0.5f);
      }
      return false;
    }

    public boolean applyOnAir() { return true; }
    public boolean applyOnPlant() { return false; }


  }

  private static class MetallurgyFertilizer extends Bonemeal {

    private Item metallurgyFertilizer;

    @Override
    protected void addInstance() {
      metallurgyFertilizer = GameRegistry.findItem("Metallurgy", "fertilizer");
      if (metallurgyFertilizer!= null) {
        instances.add(this);
      }
    }

    @Override
    protected boolean matches(ItemStack stack) {
      return metallurgyFertilizer != null && stack.getItem() == metallurgyFertilizer;
    }

  }

  private static class GardenCoreCompost_pile extends Bonemeal {

    private Item compost_pile;

    @Override
    protected void addInstance() {
      compost_pile = GameRegistry.findItem("GardenCore", "compost_pile");
      if (compost_pile!= null) {
        instances.add(this);
      }
    }

    @Override
    protected boolean matches(ItemStack stack) {
      return compost_pile != null && stack.getItem() == compost_pile;
    }

  }


}
