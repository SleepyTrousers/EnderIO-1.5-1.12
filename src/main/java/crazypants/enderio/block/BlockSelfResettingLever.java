package crazypants.enderio.block;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IModObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockSelfResettingLever extends BlockLever {

  public static Block create10(@Nonnull IModObject modObject) {
    return create(modObject, 10);
  }

  public static Block create30(@Nonnull IModObject modObject) {
    return create(modObject, 30);
  }

  public static Block create60(@Nonnull IModObject modObject) {
    return create(modObject, 60);
  }

  public static Block create300(@Nonnull IModObject modObject) {
    return create(modObject, 300);
  }

  public static Block create(@Nonnull IModObject modObject, int seconds) {
    final String name = modObject.getUnlocalisedName() + seconds;
    final BlockSelfResettingLever lever = new BlockSelfResettingLever(name, seconds * 20);
    GameRegistry.register(lever);
    GameRegistry.register(new ItemBlock(lever).setRegistryName(name));
    return lever;
  }

  private final int delay;

  public BlockSelfResettingLever(@Nonnull String name, int delay) {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.5F);
    setSoundType(SoundType.WOOD);
    this.delay = delay;
    setUnlocalizedName(name);
    setRegistryName(name);
  }


  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX,
      float hitY, float hitZ) {
    if (world.isRemote) {
      return true;
    } else {
      if (!state.getValue(POWERED)) {
        world.scheduleBlockUpdate(pos, this, delay, 0);
      }
      return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }
  }

  @SuppressWarnings("null")
  @Override
  public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
    if (!world.isRemote && state.getValue(POWERED)) {
      super.onBlockActivated(world, pos, state, null, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0f, 0f, 0f);
    }
  }

}
