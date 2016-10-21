package crazypants.enderio.block;

import java.util.List;
import java.util.Random;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.config.Config;
import crazypants.enderio.integration.waila.IWailaInfoProvider;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedObsidian extends BlockEio<TileEntityEio> implements IResourceTooltipProvider, IWailaInfoProvider, IHaveRenderers {

  public static BlockReinforcedObsidian create() {
    BlockReinforcedObsidian result = new BlockReinforcedObsidian();
    result.init();
    return result;
  }

  private BlockReinforcedObsidian() {
    super(ModObject.blockReinforcedObsidian.getUnlocalisedName(), null, Material.ROCK);
    setHardness(50.0F);
    setResistance(2000.0F);
    setSoundType(SoundType.STONE);

    if (!Config.reinforcedObsidianEnabled) {
      setCreativeTab(null);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public MapColor getMapColor(IBlockState state) {
    return MapColor.OBSIDIAN;
  }

  private static final int[] COLS = { 0x3c3056, 0x241e31, 0x1e182b, 0x0e0e15, 0x07070b };

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
    if (rand.nextFloat() < .25f) {
      EnumFacing face = EnumFacing.values()[rand.nextInt(EnumFacing.values().length)];
      double xd = face.getFrontOffsetX() == 0 ? rand.nextDouble() : face.getFrontOffsetX() < 0 ? -0.05 : 1.05;
      double yd = face.getFrontOffsetY() == 0 ? rand.nextDouble() : face.getFrontOffsetY() < 0 ? -0.05 : 1.05;
      double zd = face.getFrontOffsetZ() == 0 ? rand.nextDouble() : face.getFrontOffsetZ() < 0 ? -0.05 : 1.05;

      double x = pos.getX() + xd;
      double y = pos.getY() + yd;
      double z = pos.getZ() + zd;

      int col = COLS[rand.nextInt(COLS.length)];

      worldIn.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, (col >> 16 & 255) / 255d, (col >> 8 & 255) / 255d, (col & 255) / 255d, new int[0]);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
    if (tab != null) {
      super.getSubBlocks(itemIn, tab, list);
    }
  }

  @Override
  public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
    return !(entity instanceof EntityWither);
  }

  @Override
  public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
    this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
    return true;
  }

  @Override
  public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
  }

  @Override
  public boolean canDropFromExplosion(Explosion p_149659_1_) {
    return false;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean shouldWrench(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    return false;
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return IWailaInfoProvider.BIT_BASIC;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    ClientUtil.registerDefaultItemRenderer(ModObject.blockReinforcedObsidian);
  }

}
