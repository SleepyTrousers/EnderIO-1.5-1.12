package crazypants.enderio.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.Log;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.config.Config.leversEnabled;

public class BlockSelfResettingLever extends BlockLever implements IHaveRenderers {

  private static List<Integer> delays = null;
  private static List<BlockSelfResettingLever> blocks = null;

  private final int delay;
  private final String name;

  public String getName() {
    return name;
  }

  public BlockSelfResettingLever(String name, int delay) {
    super();
    setCreativeTab(EnderIOTab.tabEnderIO);
    setHardness(0.5F);
    setSoundType(SoundType.WOOD);
    this.name = name;
    this.delay = delay;
    setUnlocalizedName(name);
    setRegistryName(name);
  }

  public static Block create() {
    getLevers();
    blocks = new ArrayList<BlockSelfResettingLever>();
    for (Integer value : delays) {
      final String name = ModObject.blockSelfResettingLever.getUnlocalisedName() + value;
      final BlockSelfResettingLever lever = new BlockSelfResettingLever(name, value * 20);
      GameRegistry.register(new ItemBlock(GameRegistry.register(lever)).setRegistryName(name));
      blocks.add(lever);
    }
    return blocks.isEmpty() ? null : blocks.get(0);
  }

  public static List<Integer> getLevers() {
    if (delays == null) {
      delays = new ArrayList<Integer>();
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
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem,
      EnumFacing side, float hitX, float hitY, float hitZ) {
    if (world.isRemote) {
      return true;
    } else {
      if (!state.getValue(POWERED)) {
        world.scheduleBlockUpdate(pos, this, delay, 0);
      }
      return super.onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
    }
  }

  @Override
  public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
    if (!world.isRemote && state.getValue(POWERED)) {
      super.onBlockActivated(world, pos, state, null, EnumHand.MAIN_HAND, null, EnumFacing.DOWN, 0f, 0f, 0f);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    for (BlockSelfResettingLever b : blocks) {
      ClientUtil.registerRenderer(Item.getItemFromBlock(b), b.getName());
    }
  }

}
