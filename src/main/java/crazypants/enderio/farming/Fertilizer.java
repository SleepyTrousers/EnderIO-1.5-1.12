package crazypants.enderio.farming;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

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
  NONE(Prep.getEmpty()) {

    @Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      return new Result(stack, false);
    }
  },

  BONEMEAL(new ItemStack(Items.DYE, 1, 15)) {

    @Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      ItemStack before = player.getHeldItem(EnumHand.MAIN_HAND);
      player.setHeldItem(EnumHand.MAIN_HAND, stack);
      EnumActionResult res = stack.getItem().onItemUse(player, world, bc, EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 0.5f, 0.5f);
      ItemStack after = player.getHeldItem(EnumHand.MAIN_HAND);
      player.setHeldItem(EnumHand.MAIN_HAND, before);
      return new Result(after, res != EnumActionResult.PASS);
    }
  },

  FORESTRY_FERTILIZER_COMPOUND(Item.REGISTRY.getObject(new ResourceLocation("Forestry", "fertilizerCompound"))) {

    @Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      return BONEMEAL.apply(stack, player, world, bc);
    }
  },

  BOTANIA_FLORAL_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("Botania", "fertilizer"))) {

    @Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      BlockPos below = bc.down();
      Block belowBlock = world.getBlockState(below).getBlock();
      if (belowBlock == Blocks.DIRT || belowBlock == Blocks.GRASS) {
        return BONEMEAL.apply(stack, player, world, below);
      }
      return new Result(stack, false);
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
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      return BONEMEAL.apply(stack, player, world, bc);
    }
  },

  GARDEN_CORE_COMPOST(Item.REGISTRY.getObject(new ResourceLocation("GardenCore", "compost_pile"))) {

    @Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      return BONEMEAL.apply(stack, player, world, bc);
    }
  },

  MAGICALCROPS_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("magicalcrops", "magicalcrops_MagicalCropFertilizer"))) {

    @Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
      return BONEMEAL.apply(stack, player, world, bc);
    }
  },
  
  ACTUALLY_ADDITIONS_FERTILIZER(Item.REGISTRY.getObject(new ResourceLocation("actuallyadditions", "itemFertilizer"))) {
	  
	@Override
    public Result apply(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc) {
	  return BONEMEAL.apply(stack, player, world, bc);
	}
  };

  private @Nonnull ItemStack stack;

  private Fertilizer(@Nullable Item item) {
    this(item == null ? Prep.getEmpty() : new ItemStack(item));
  }

  private Fertilizer(@Nullable Block block) {
    this(block == null ? Prep.getEmpty() : new ItemStack(block));
  }

  private Fertilizer(@Nonnull ItemStack stack) {
    this.stack = Prep.isInvalid(stack) ? Prep.getEmpty() : stack;
    if (Prep.isValid(this.stack)) {
      FarmersRegistry.slotItemsFertilizer.add(this.stack);
    }
  }

  private static final NNList<Fertilizer> validFertilizers = new NNList<>();

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
  public static Fertilizer getInstance(@Nonnull ItemStack stack) {
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
  public static boolean isFertilizer(@Nonnull ItemStack stack) {
    return getInstance(stack) != NONE;
  }

  protected boolean matches(@Nonnull ItemStack stackIn) {
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
  public abstract Result apply(@Nonnull ItemStack stackIn, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos bc);

  public boolean applyOnAir() {
    return false;
  }

  public boolean applyOnPlant() {
    return true;
  }

  public static class Result {
    private final @Nonnull ItemStack stack;
    private final boolean wasApplied;

    Result(@Nonnull ItemStack stack, boolean wasApplied) {
      super();
      this.stack = stack;
      this.wasApplied = wasApplied;
    }

    public @Nonnull ItemStack getStack() {
      return stack;
    }

    public boolean isWasApplied() {
      return wasApplied;
    }
  }
}
