package crazypants.enderio.base.block.darksteel.anvil;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.material.material.Material;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBlackPaperAnvil extends BlockDarkSteelAnvil {

  public BlockBlackPaperAnvil(@Nonnull IModObject modObject) {
    super(modObject);
    setResistance(0F); // will be corrected by setHardness()
    setHardness(0.1F);
    setSoundType(SoundType.PLANT);
    setHarvestLevel("pickaxe", 0);
  }

  @Override
  public float getDamageChance() {
    return 99999f;
  }

  @Override
  protected void onStartFalling(@Nonnull EntityFallingBlock fallingEntity) {
    fallingEntity.setHurtEntities(false);
  }

  @Override
  public void onEndFalling(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState p_176502_3_, @Nonnull IBlockState p_176502_4_) {
  }

  @Override
  public void onBroken(@Nonnull World worldIn, @Nonnull BlockPos pos) {
  }

  @Override
  public int getUseEvent() {
    return -1;
  }

  @Override
  public int getBreakEvent() {
    return -1;
  }

  @Override
  public void getDrops(@Nonnull NonNullList<ItemStack> drops, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int fortune) {
    int count = quantityDropped(state, fortune, world instanceof World ? ((World) world).rand : RANDOM);
    for (int i = 0; i < count; i++) {
      drops.add(Material.BLACK_PAPER.getStack());
    }
  }

  @Override
  protected boolean canSilkHarvest() {
    return true;
  }

  @Override
  public int quantityDropped(@Nonnull IBlockState state, int fortune, @Nonnull Random random) {
    return Math.max(1, quantityDroppedWithBonus(fortune, random) - state.getValue(BlockAnvil.DAMAGE));
  }

  @Override
  public int quantityDropped(@Nonnull Random random) {
    return random.nextInt(6) + 1;
  }

  @Override
  public @Nonnull Item getItemDropped(@Nonnull IBlockState state, @Nonnull Random rand, int fortune) {
    return Items.PAPER; // not right, but better than 1-6 anvils when someone doesn't use getDrops()
  }

}
