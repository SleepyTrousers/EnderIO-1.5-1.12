package crazypants.enderio.base.block.infinity;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInfinity extends BlockEio<TileEntityEio> implements IResourceTooltipProvider, IHaveRenderers {

  public static BlockInfinity create(@Nonnull IModObject modObject) {
    BlockInfinity result = new BlockInfinity(modObject);
    return result;
  }

  public static final @Nonnull PropertyInteger LEVEL = PropertyInteger.create("level", 0, 2);

  protected BlockInfinity(@Nonnull IModObject modObject) {
    super(modObject, Material.ROCK);
    setHardness(50.0F);
    setResistance(2000.0F);
    setSoundType(SoundType.GROUND); // SoundType.GROUND should be SoundType.GRAVEL
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    setShape(mkShape(BlockFaceShape.SOLID));
    initDefaultState();
    for (int i = 0; i <= 2; i++) {
      setHarvestLevel("shovel", i + 1, getDefaultState().withProperty(LEVEL, i));
    }
  }

  @Override
  public Item createBlockItem(@Nonnull IModObject modObject) {
    return modObject.apply(new ItemBlockInfinity(this) {
      @Override
      public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" + stack.getMetadata();
      }
    });
  }

  protected void initDefaultState() {
    setDefaultState(getBlockState().getBaseState());
  }

  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { LEVEL });
  }

  @Override
  public @Nonnull IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(LEVEL, MathHelper.clamp(meta, 0, 2));
  }

  @Override
  public int getMetaFromState(@Nonnull IBlockState state) {
    return state.getValue(LEVEL);
  }

  @Override
  public int damageDropped(@Nonnull IBlockState state) {
    return state.getValue(LEVEL);
  }

  @Override
  public @Nonnull MapColor getMapColor(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return MapColor.OBSIDIAN;
  }

  private static final int[] COLS = { 0x010101, 0x111111, 0x222222, 0x333333, 0xFFFFFF }; // red==0 is forced to 255 by the particle

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
    if (rand.nextFloat() < (state.getValue(LEVEL) + 1) * .25f) {
      EnumFacing face = EnumFacing.values()[rand.nextInt(EnumFacing.values().length)];
      double xd = face.getFrontOffsetX() == 0 ? rand.nextDouble() : face.getFrontOffsetX() < 0 ? -0.05 : 1.05;
      double yd = face.getFrontOffsetY() == 0 ? rand.nextDouble() : face.getFrontOffsetY() < 0 ? -0.05 : 1.05;
      double zd = face.getFrontOffsetZ() == 0 ? rand.nextDouble() : face.getFrontOffsetZ() < 0 ? -0.05 : 1.05;

      double x = pos.getX() + xd;
      double y = pos.getY() + yd;
      double z = pos.getZ() + zd;

      int col = COLS[rand.nextInt(COLS.length)];

      worldIn.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, (col >> 16 & 255) / 255d, (col >> 8 & 255) / 255d, (col & 255) / 255d);
    }
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return itemStack.getItem().getUnlocalizedName(itemStack);
  }

  @Override
  public boolean canBeWrenched() {
    return false;
  }

  private boolean breakup(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, float baseChance) {
    if (!worldIn.isRemote && RANDOM.nextFloat() < (state.getValue(LEVEL) + 1) * baseChance) {
      int count;
      switch (state.getValue(LEVEL)) {
      case 2:
        count = 9 * 9 * 9;
        break;
      case 1:
        count = 9 * 9;
        break;
      default:
        count = 9;
      }
      worldIn.setBlockToAir(pos);
      worldIn.playEvent(2001, pos, Block.getStateId(state));
      while (count > 0) {
        int num = MathHelper.clamp(RANDOM.nextInt(Math.max(count / 5, 1)), 1, Math.min(count, 64));
        count -= num;
        spawnAsEntity(worldIn, pos, crazypants.enderio.base.material.material.Material.POWDER_INFINITY.getStack(num));
      }
      if (RANDOM.nextFloat() < (state.getValue(LEVEL) + 1) * .3f) {
        CapturedMob capturedMob = CapturedMob.create(new ResourceLocation("minecraft", "endermite"));
        if (capturedMob != null) {
          capturedMob.doSpawn(worldIn, pos, EnumFacing.DOWN, false);
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public void fillWithRain(@Nonnull World worldIn, @Nonnull BlockPos pos) {
    breakup(worldIn.getBlockState(pos), worldIn, pos, .025f);
  }

  @Override
  public void onFallenUpon(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Entity entityIn, float fallDistance) {
    if (breakup(worldIn.getBlockState(pos), worldIn, pos, fallDistance / 10f)) {
      super.onFallenUpon(worldIn, pos, entityIn, fallDistance - 3f);
    } else {
      super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (int i = 0; i <= 2; i++) {
      ClientUtil.regRenderer(BlockInfinity.this, i, LEVEL.getName() + "=" + LEVEL.getName(i));
    }
  }

  @Override
  public void getSubBlocks(@Nonnull CreativeTabs itemIn, @Nonnull NonNullList<ItemStack> items) {
    for (int i = 0; i <= 2; i++) {
      items.add(new ItemStack(this, 1, i));
    }
  }

}
