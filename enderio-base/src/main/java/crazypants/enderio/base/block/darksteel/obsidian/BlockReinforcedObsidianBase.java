package crazypants.enderio.base.block.darksteel.obsidian;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.BlockEio;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.TileEntityEio;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockReinforcedObsidianBase extends BlockEio<TileEntityEio> implements IResourceTooltipProvider {

  protected BlockReinforcedObsidianBase(@Nonnull IModObject modObject) {
    super(modObject, Material.ROCK);
    setHardness(50.0F);
    setResistance(2000.0F);
    setSoundType(SoundType.STONE);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setShape(mkShape(BlockFaceShape.SOLID));
  }

  @Override
  public @Nonnull MapColor getMapColor(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
    return MapColor.OBSIDIAN;
  }

  private static final int[] COLS = { 0x3c3056, 0x241e31, 0x1e182b, 0x0e0e15, 0x07070b };

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(@Nonnull IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Random rand) {
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
  public boolean canEntityDestroy(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull Entity entity) {
    return !(entity instanceof EntityWither) && !(entity instanceof EntityWitherSkull);
  }

  @Override
  public void onBlockExploded(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
  }

  @Override
  public boolean canDropFromExplosion(@Nonnull Explosion p_149659_1_) {
    return false;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean canBeWrenched() {
    return false;
  }

  @Override
  public @Nonnull EnumPushReaction getMobilityFlag(@Nonnull IBlockState state) {
    // moving reinforced obsidian with a mere piston? nope, I don't think so...
    return EnumPushReaction.BLOCK;
  }

}
