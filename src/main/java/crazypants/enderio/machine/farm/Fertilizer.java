package crazypants.enderio.machine.farm;

import java.util.List;

import com.google.common.collect.Lists;

import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public enum Fertilizer {

  /**
   * Not a fertilizer. Using this handler class any item can be "used" as a fertilizer. Meaning, fertilizing will always fail.
   */
  NONE((ItemStack) null) {

    @Override
    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) {
      return false;
    }
  },

  BONEMEAL(new ItemStack(Items.DYE, 1, 15)) {

    @Override
    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) {
      EnumActionResult res = stack.getItem().onItemUse(stack, player, world, bc, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
      return res != null && res != EnumActionResult.PASS;
    }
  },

  FORESTRY_FERTILIZER_COMPOUND(Item.REGISTRY.getObject(new ResourceLocation("Forestry", "fertilizerCompound"))) {

    @Override
    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) {
      return BONEMEAL.apply(stack, player, world, bc);
    }
  },

  BOTANIA_FLORAL_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("Botania", "fertilizer"))) {

    @Override
    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) {
      BlockPos below = bc.down();
      Block belowBlock = world.getBlockState(below).getBlock();
      if (belowBlock == Blocks.DIRT || belowBlock == Blocks.GRASS) {
        EnumActionResult res = stack.getItem().onItemUse(stack, player, world, below, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
        return res != null && res != EnumActionResult.PASS;
      }
      return false;
    }

    @Override
    public boolean applyOnAir() {
      return true;
    }

    @Override
    public boolean applyOnPlant() {
      return false;
    }
  },

  METALLURGY_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("Metallurgy", "fertilizer"))) {

    @Override
    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) {
      return BONEMEAL.apply(stack, player, world, bc);
    }
  },

  GARDEN_CORE_COMPOST(Item.REGISTRY.getObject(new ResourceLocation("GardenCore", "compost_pile"))) {

    @Override
    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) {
      return BONEMEAL.apply(stack, player, world, bc);
    }
  },

  MAGICALCROPS_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("magicalcrops", "magicalcrops_MagicalCropFertilizer"))) {

    @Override
    public boolean apply(ItemStack stack, EntityPlayer player, World world, BlockPos bc) {
      return BONEMEAL.apply(stack, player, world, bc);
    }
  };

  private ItemStack stack;

  private Fertilizer(Item item) {
    this(new ItemStack(item));
  }

  private Fertilizer(Block block) {
    this(new ItemStack(block));
  }

  private Fertilizer(ItemStack stack) {
    this.stack = Prep.isInvalid(stack) ? Prep.getEmpty() : stack;
    if (Prep.isValid(this.stack)) {
      FarmStationContainer.slotItemsFertilizer.add(this.stack);
    }
  }

  private static final List<Fertilizer> validFertilizers = Lists.newArrayList();

  static {
    for (Fertilizer f : values()) {
      if (Prep.isValid(f.stack)) {
        validFertilizers.add(f);
      }
    }
  }

  /**
   * Returns the singleton instance for the fertilizer that was given as parameter. If the given item is no fertilizer, it will return an instance of
   * Fertilizer.None.
   * 
   */
  public static Fertilizer getInstance(ItemStack stack) {
    for (Fertilizer fertilizer : validFertilizers) {
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

  protected boolean matches(ItemStack stackIn) {
    return OreDictionary.itemMatches(this.stack, stackIn, false);
  }

  /**
   * Tries to apply the given item on the given block using the type-specific method. SFX is played on success.
   * 
   * If the item was successfully applied, the stacksize will be decreased if appropriate. The caller will need to check for stacksize 0 and null the inventory
   * slot if needed.
   * 
   * @param stack
   * @param player
   * @param world
   * @param bc
   * @return true if the fertilizer was applied
   */
  public abstract boolean apply(ItemStack stackIn, EntityPlayer player, World world, BlockPos bc);

  public boolean applyOnAir() {
    return false;
  }

  public boolean applyOnPlant() {
    return true;
  }
}
