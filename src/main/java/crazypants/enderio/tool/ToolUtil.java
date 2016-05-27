package crazypants.enderio.tool;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.EnderIO;
import crazypants.enderio.api.tool.ITool;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolUtil {

  public static boolean isToolEquipped(EntityPlayer player, EnumHand hand) {
    return getInstance().isToolEquippedImpl(player, hand);
  }

  public static ITool getEquippedTool(EntityPlayer player, EnumHand hand) {
    return getInstance().getEquippedToolImpl(player, hand);
  }

  public static boolean breakBlockWithTool(Block block, World world, int x, int y, int z, EntityPlayer entityPlayer, EnumHand hand) {
    return breakBlockWithTool(block, world, new BlockPos(x,y,z), entityPlayer, hand);
  }
  
  public static boolean breakBlockWithTool(Block block, World world, BlockPos pos, EntityPlayer entityPlayer, EnumHand hand) {
    return breakBlockWithTool(block, world, pos, entityPlayer, entityPlayer.getHeldItem(hand));
  }

  public static boolean breakBlockWithTool(Block block, World world, BlockPos pos, EntityPlayer entityPlayer, ItemStack heldItem) {
    ITool tool = ToolUtil.getToolFromStack(heldItem);
    if (tool != null && entityPlayer.isSneaking() && tool.canUse(heldItem, entityPlayer, pos)) {
      IBlockState bs = world.getBlockState(pos);;
      if(block.removedByPlayer(bs, world, pos, entityPlayer, true)) {
        block.harvestBlock(world, entityPlayer, pos, world.getBlockState(pos), world.getTileEntity(pos), heldItem);
      }
      tool.used(heldItem, entityPlayer, pos);
      return true;
    }
    return false;
  }

  private static ToolUtil instance;

  public static ToolUtil getInstance() {
    if(instance == null) {
      instance = new ToolUtil();
    }
    return instance;
  }

  private final List<IToolProvider> toolProviders = new ArrayList<IToolProvider>();

  private ToolUtil() {
  }

  public void registerToolProvider(IToolProvider toolProvider) {
    toolProviders.add(toolProvider);
  }

  private boolean isToolEquippedImpl(EntityPlayer player, EnumHand hand) {
    return getEquippedToolImpl(player, hand) != null;
  }

  private ITool getEquippedToolImpl(EntityPlayer player, EnumHand hand) {
    player = player == null ? EnderIO.proxy.getClientPlayer() : player;
    if(player == null) {
      return null;
    }
    ItemStack equipped = player.getHeldItem(hand);
    return getToolFromStack(equipped);
  }

  public static ITool getToolFromStack(ItemStack equipped) {
    if(equipped == null) {
      return null;
    }
    if(equipped.getItem() instanceof ITool) {
      return (ITool) equipped.getItem();
    }
    return getInstance().getToolImpl(equipped);
  }

  private ITool getToolImpl(ItemStack equipped) {
    for (IToolProvider provider : toolProviders) {
      ITool result = provider.getTool(equipped);
      if(result != null) {
        return result;
      }
    }
    return null;
  }
 
}
