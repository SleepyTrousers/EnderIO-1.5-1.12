package crazypants.enderio.base.paint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPiston;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fluids.FluidStack;

import static crazypants.enderio.util.NbtValue.BLOCKSTATE;

public class PaintUtil {

  public static boolean isValid(@Nonnull ItemStack paintSource, Block target) {
    boolean solidPaint = false;
    boolean textureOnly = false;
    if (Prep.isValid(paintSource)) {
      if (!PaintSourceValidator.instance.isValidSourceDefault(paintSource)) {
        return false;
      }
      Block block = getBlockFromItem(paintSource);
      if (block == null || block == Blocks.AIR) {
        return false;
      }
      IBlockState bs = Block$getBlockFromItem_stack$getItem___$getStateFromMeta_stack$getMetadata___(paintSource, block);
      solidPaint = bs.isOpaqueCube();
      if (!shouldHaveModel(block)) {
        if (shouldHaveTexture(block)) {
          textureOnly = true;
        } else {
          return false;
        }
      } else if (block instanceof IPaintable) {
        bs = ((IPaintable) block).getPaintSource(block, paintSource);
        if (bs != null) {
          return false;
        }
      }
    }

    if (target == null) {
      return Prep.isValid(paintSource);
    } else if (Prep.isInvalid(paintSource)) {
      return target instanceof IPaintable;
    } else if (target instanceof IPaintable.ITexturePaintableBlock) {
      return true;
    } else if (target instanceof IPaintable.ISolidBlockPaintableBlock) {
      return solidPaint && !textureOnly;
    } else if (target instanceof IPaintable.INonSolidBlockPaintableBlock) {
      return !solidPaint && !textureOnly;
    } else if (target instanceof IPaintable.IBlockPaintableBlock) {
      return !textureOnly;
    } else {
      return false;
    }
  }

  public static IBlockState rotate(@Nonnull IBlockState paintSource) {
    // TODO: Need to handle cases like stairs that have 'upper' and 'lower' so they are included in the rotation
    // cycle
    for (IProperty<?> prop : paintSource.getPropertyKeys()) {
      if (prop instanceof PropertyDirection) {
        return paintSource.cycleProperty(prop);
      } else if (prop == BlockSlab.HALF) {
        return paintSource.cycleProperty(prop);
      }
    }
    if (paintSource.getBlock() instanceof BlockLog) {
      return paintSource.cycleProperty(BlockLog.LOG_AXIS);
    }
    return paintSource;
  }

  public static void writeNbt(NBTTagCompound nbtRoot, IBlockState paintSource) {
    if (nbtRoot == null) {
      return;
    }
    if (paintSource == null) {
      BLOCKSTATE.removeTag(nbtRoot);
    } else {
      BLOCKSTATE.setTag(nbtRoot, NBTUtil.writeBlockState(new NBTTagCompound(), paintSource));
    }
  }

  public static IBlockState readNbt(NBTTagCompound nbtRoot) {
    if (BLOCKSTATE.hasTag(nbtRoot)) {
      final NBTTagCompound tag = BLOCKSTATE.getTag(nbtRoot);
      if (tag != null) {
        final IBlockState paint = NBTUtil.readBlockState(tag);
        if (paint != Blocks.AIR.getDefaultState()) {
          return paint;
        }
      }
    }
    return null;
  }

  public static boolean isPainted(@Nonnull ItemStack itemStack) {
    return BLOCKSTATE.hasTag(itemStack);
  }

  public static IBlockState getSourceBlock(@Nonnull ItemStack itemStack) {
    return readNbt(itemStack.getTagCompound());
  }

  public static void setPaintSource(@Nonnull ItemStack itemStack, @Nonnull ItemStack paintSource) {
    paintSource = paintSource.copy();
    paintSource.setCount(1);
    NbtValue.PAINT_SOURCE.setStack(itemStack, paintSource);
  }

  public static @Nonnull ItemStack getPaintSource(@Nonnull ItemStack itemStack) {
    return NbtValue.PAINT_SOURCE.getStack(itemStack);
  }

  public static void setOriginalStack(@Nonnull ItemStack itemStack, @Nonnull ItemStack originalStack) {
    originalStack = originalStack.copy();
    originalStack.setCount(1);
    NbtValue.ORIGINAL_STACK.setStack(itemStack, originalStack);
  }

  public static @Nonnull ItemStack getOriginalStack(@Nonnull ItemStack itemStack) {
    if (NbtValue.ORIGINAL_STACK.hasTag(itemStack)) {
      return NbtValue.ORIGINAL_STACK.getStack(itemStack);
    } else if (hasPaintSource(itemStack)) {
      return NbtValue.PAINT_SOURCE.removeTag(BLOCKSTATE.removeTagCopy(itemStack));
    }
    return Prep.getEmpty();
  }

  public static boolean hasPaintSource(@Nonnull ItemStack itemStack) {
    return NbtValue.PAINT_SOURCE.hasTag(itemStack);
  }

  public static void setSourceBlock(@Nonnull ItemStack itemStack, IBlockState paintSource) {
    if (Prep.isInvalid(itemStack)) {
      return;
    }
    if (paintSource == null || paintSource.getBlock() == Blocks.AIR) {
      BLOCKSTATE.removeTag(itemStack);
      return;
    } else {
      try {
        BLOCKSTATE.setTag(itemStack, NBTUtil.writeBlockState(new NBTTagCompound(), paintSource));
      } catch (Exception e) {
        String s;
        try {
          s = "" + Block.REGISTRY.getNameForObject(paintSource.getBlock());
        } catch (Exception e1) {
          s = e1.getMessage();
        }
        throw new RuntimeException("Failed to write blockstate to nbt. blockstate=" + paintSource + " registry name=" + s + " block=" + paintSource.getBlock(),
            e);
      }
    }
  }

  public static String getTooltTipText(@Nonnull ItemStack itemStack) {
    String sourceName = null;
    if (itemStack.getItem() instanceof IWithPaintName) {
      sourceName = ((IWithPaintName) itemStack.getItem()).getPaintName(itemStack);
    } else {
      IBlockState state = getSourceBlock(itemStack);
      if (state != null) {
        Block block = state.getBlock();
        if (block != Blocks.AIR) {
          Item itemFromBlock = Item.getItemFromBlock(block);
          if (itemFromBlock != Items.AIR) {
            ItemStack is = new ItemStack(itemFromBlock, 1, block.getMetaFromState(state));
            sourceName = is.getDisplayName();
          } else {
            sourceName = block.getLocalizedName();
          }
        }
      }
    }
    if (sourceName == null || sourceName.isEmpty()) {
      return Lang.PAINTED_NOT.get();
    } else {
      return Lang.PAINTED_WITH.get(sourceName);
    }
  }

  public static @Nonnull ItemStack getPaintAsStack(IBlockState state) {
    if (state != null) {
      Block block = state.getBlock();
      Item itemFromBlock = Item.getItemFromBlock(block);
      if (itemFromBlock != Items.AIR) {
        return new ItemStack(itemFromBlock, 1, block.getMetaFromState(state));
      }
    }
    return Prep.getEmpty();
  }

  public interface IWithPaintName {
    @Nullable
    String getPaintName(@Nonnull ItemStack stack);
  }

  // Note: Config-based white-/blacklisting is done by PaintSourceValidator and checked as part of the input slot validation of the Painter
  public static boolean shouldHaveModel(Block block) {
    if (block == null) {
      return false;
    }

    return block.getDefaultState().getRenderType() == EnumBlockRenderType.MODEL;
  }

  public static boolean shouldHaveTexture(Block block) {
    if (block == null) {
      return false;
    }
    EnumBlockRenderType rt = block.getDefaultState().getRenderType();
    return rt != EnumBlockRenderType.INVISIBLE;
  }

  public static Block getBlockFromItem(Item itemIn) {
    if (itemIn != null) {
      if (itemIn instanceof ItemBlock) {
        return ((ItemBlock) itemIn).getBlock();
      }
      FluidStack fluidStack = FluidUtil.getFluidTypeFromItem(new ItemStack(itemIn));
      if (fluidStack != null) {
        return fluidStack.getFluid().getBlock();
      }
    }
    return null;
  }

  public static Block getBlockFromItem(@Nonnull ItemStack itemStack) {
    if (Prep.isValid(itemStack)) {
      if (itemStack.getItem() instanceof ItemBlock) {
        return ((ItemBlock) itemStack.getItem()).getBlock();
      }
      FluidStack fluidStack = FluidUtil.getFluidTypeFromItem(itemStack);
      if (fluidStack != null && fluidStack.getFluid() != null) {
        return fluidStack.getFluid().getBlock();
      }
    }
    return null;
  }

  private static final BlockRenderLayer BREAKING = null;

  public static boolean canRenderInLayer(@Nullable IBlockState paintSource, @Nonnull BlockRenderLayer blockLayer) {
    if (blockLayer == BREAKING) {
      return true;
    } else if (paintSource != null) {
      return paintSource.getBlock().canRenderInLayer(paintSource, blockLayer);
    } else {
      return blockLayer == BlockRenderLayer.SOLID;
    }
  }

  // This line is in this excessively named method to show up nicely in a stack trace
  public static IBlockState Block$getBlockFromItem_stack$getItem___$getStateFromMeta_stack$getMetadata___(ItemStack paintSource, Block paintBlock) {
    if (paintSource.getItem().getClass() == ItemPiston.class) {
      // Vanilla bug. ItemPiston returns an invalid block meta.
      return paintBlock.getDefaultState();
    }
    final IBlockState stateFromMeta = paintBlock.getStateFromMeta(paintSource.getItem().getMetadata(paintSource.getMetadata()));
    if (NullHelper.untrust(stateFromMeta) == null) {
      throw new RuntimeException("Block " + paintBlock + " returned null from getStateFromMeta(). This is a major bug in the mod that block belongs to.");
    } else if (NullHelper.untrust(stateFromMeta.getBlock()) == null) {
      throw new RuntimeException("Block " + paintBlock + " returned a blockstate (" + stateFromMeta
          + ") without block from getStateFromMeta(). This is a major bug in the mod that block belongs to.");
    } else if (NullHelper.untrust(Block.REGISTRY.getNameForObject(stateFromMeta.getBlock())) == null) {
      throw new RuntimeException("Block " + paintBlock + " returned a blockstate (" + stateFromMeta + ") that belongs to an unregistered block "
          + stateFromMeta.getBlock() + " from getStateFromMeta(). This is a major bug in the mod that block belongs to.");
    }
    return stateFromMeta;
  }

  /**
   * Registers blocks that can be painted but do not implement IPaintable themselves. Used for blocks that are not Ender IO blocks, e.g. vanilla fences.
   */
  public static void registerPaintable(Block... blocks) {
    for (Block block : blocks) {
      if (!(block instanceof IPaintable)) {
        PaintUtil.paintables.add(block);
      }
    }
  }

  /**
   * Registers items that can be painted but are not items for blocks implement IPaintable themselves. Used for items that do not have a block , e.g. Dark Steel
   * Helmets and Facades.
   */
  public static void registerPaintable(Item... items) {
    for (Item item : items) {
      if (!(Block.getBlockFromItem(item) instanceof IPaintable)) {
        PaintUtil.paintables.add(item);
      }
    }
  }

  /**
   * Checks if an item is paintable.
   * <p>
   * An item can be painted if
   * <ul>
   * <li>it already is painted,
   * <li>it represents a block that implements IPaintable,
   * <li>it represents a block that was registered as being paintable, or
   * <li>it was registered as being paintable.
   * </ul>
   */
  public static boolean isPaintable(@Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    return isPainted(stack) || Block.getBlockFromItem(stack.getItem()) instanceof IPaintable || PaintUtil.paintables.contains(stack);
  }

  private static Things paintables = new Things();

}
