package crazypants.enderio.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IModObject;
import crazypants.enderio.Log;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.config.Config.leversEnabled;

public class BlockSelfResettingLever extends BlockLever implements IHaveRenderers {

  private static List<Integer> delays = new ArrayList<Integer>();
  private static NNList<BlockSelfResettingLever> blocks = new NNList<BlockSelfResettingLever>();;

  private final int delay;
  private final @Nonnull String name;

  public @Nonnull String getName() {
    return name;
  }

  public BlockSelfResettingLever(@Nonnull String name, int delay) {
    super();
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.5F);
    setSoundType(SoundType.WOOD);
    this.name = name;
    this.delay = delay;
    setUnlocalizedName(name);
    setRegistryName(name);
  }

  public static Block create(@Nonnull IModObject modObject) {
    getLevers();
    blocks.clear();
    for (Integer value : delays) {
      final String name = modObject.getUnlocalisedName() + value;
      final BlockSelfResettingLever lever = new BlockSelfResettingLever(name, value * 20);
      GameRegistry.register(lever);
      GameRegistry.register(new ItemBlock(lever).setRegistryName(name));
      blocks.add(lever);
    }
    return blocks.isEmpty() ? null : blocks.get(0);
  }

  public static List<Integer> getLevers() {
    if (delays == null || delays.isEmpty()) {
      delays.clear();
      String s = leversEnabled;
      s = s.replaceAll("[^0-9,]", "");
      String[] split = s.split(",");
      for (String string : split) {
        if (string != null && !string.isEmpty()) {
          try {
            final Integer value = Integer.valueOf(string);
            if (value > 0 && value <= 60 * 60 * 24) { // max 1 day
              delays.add(value);
            }
          } catch (NumberFormatException e) {
            Log.error("Could not parse lever time setting '" + string + "'");
          }
        }
      }
    }
    return delays;
  }

  public static List<BlockSelfResettingLever> getBlocks() {
    return blocks;
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

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    blocks.apply(new Callback<BlockSelfResettingLever>() {
      @Override
      public void apply(@Nonnull BlockSelfResettingLever b) {
        ClientUtil.registerRenderer(Item.getItemFromBlock(b), b.getName());
      }
    });
  }

}
