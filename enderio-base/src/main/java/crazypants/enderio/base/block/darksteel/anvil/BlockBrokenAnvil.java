package crazypants.enderio.base.block.darksteel.anvil;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.BaseConfig;
import crazypants.enderio.base.config.config.BlockConfig;
import crazypants.enderio.base.material.alloy.Alloy;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = EnderIO.MODID)
public class BlockBrokenAnvil extends BlockFalling {

  public static BlockBrokenAnvil create(@Nonnull IModObject modObject) {
    return new BlockBrokenAnvil(modObject);
  }

  private static final double px = 1 / 16d;
  protected static final @Nonnull AxisAlignedBB SHAPE = new AxisAlignedBB(1 * px, 0 * px, 1 * px, 15 * px, 4.5 * px, 15 * px);

  BlockBrokenAnvil(@Nonnull IModObject modObject) {
    super(Material.ANVIL);
    setHardness(5.0F);
    setSoundType(SoundType.ANVIL);
    setResistance(BaseConfig.explosionResistantBlockHardness.get());
    setDefaultState(this.blockState.getBaseState().withProperty(BlockAnvil.FACING, EnumFacing.NORTH));
    setLightOpacity(0);
    modObject.apply(this);
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public @Nonnull BlockFaceShape getBlockFaceShape(@Nonnull IBlockAccess worldIn, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing face) {
    return face == EnumFacing.DOWN ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState state) {
    return false;
  }

  @Override
  public @Nonnull AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos) {
    return SHAPE;
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.getHorizontal(meta & 3));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    int i = 0;
    i = i | state.getValue(BlockAnvil.FACING).getHorizontalIndex();
    return i;
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, BlockAnvil.FACING);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState stateIn, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }

  @Override
  public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
    int totalValue = BlockConfig.dsaMaterialWorth.get();
    float min = MathHelper.clamp(Math.min(BlockConfig.dsaMinDrop.get(), BlockConfig.dsaMaxDrop.get()), 0, 1); // don't trust users to
    float max = MathHelper.clamp(Math.max(BlockConfig.dsaMinDrop.get(), BlockConfig.dsaMaxDrop.get()), 0, 1); // give us valid values
    if (totalValue <= 0 || max <= 0) {
      return;
    }
    totalValue = (int) (totalValue * (max - min) * RANDOM.nextFloat() + totalValue * min);
    int blocks = 0, ingots = 0, nuggets = 0;
    while (totalValue > 0) {
      if (RANDOM.nextBoolean()) {
        nuggets++;
        totalValue--;
      } else if (totalValue >= 9 && RANDOM.nextBoolean()) {
        ingots++;
        totalValue -= 9;
      } else if (totalValue >= 81 && RANDOM.nextBoolean()) {
        blocks++;
        totalValue -= 81;
      } else {
        nuggets++;
        totalValue--;
      }
    }
    while (blocks > 0) {
      drops.add(Alloy.DARK_STEEL.getStackBlock(Math.min(blocks, Alloy.DARK_STEEL.getStackBlock().getMaxStackSize())));
      blocks -= Alloy.DARK_STEEL.getStackBlock().getMaxStackSize();
    }
    while (ingots > 0) {
      drops.add(Alloy.DARK_STEEL.getStackIngot(Math.min(ingots, Alloy.DARK_STEEL.getStackIngot().getMaxStackSize())));
      ingots -= Alloy.DARK_STEEL.getStackIngot().getMaxStackSize();
    }
    while (nuggets > 0) {
      drops.add(Alloy.DARK_STEEL.getStackNugget(Math.min(nuggets, Alloy.DARK_STEEL.getStackNugget().getMaxStackSize())));
      nuggets -= Alloy.DARK_STEEL.getStackNugget().getMaxStackSize();
    }
  }

}
