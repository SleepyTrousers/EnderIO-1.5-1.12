package crazypants.enderio.base.tool;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

public class ToolUtil {

  public static boolean isToolEquipped(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    return getInstance().isToolEquippedImpl(player, hand);
  }

  public static ITool getEquippedTool(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    return getInstance().getEquippedToolImpl(player, hand);
  }

  public static boolean breakBlockWithTool(@Nonnull Block block, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing side,
      @Nonnull EntityPlayer entityPlayer, @Nonnull EnumHand hand, @Nonnull String permissionNode) {
    ItemStack heldItem = entityPlayer.getHeldItem(hand);
    ITool tool = ToolUtil.getToolFromStack(heldItem);
    if (tool != null && entityPlayer.isSneaking() && tool.canUse(hand, entityPlayer, pos)) {
      IBlockState bs = world.getBlockState(pos);
      if (!PermissionAPI.hasPermission(entityPlayer.getGameProfile(), permissionNode, new BlockPosContext(entityPlayer, pos, bs, side))) {
        entityPlayer.sendMessage(Lang.WRENCH_DENIED.toChatServer());
        return false;
      }
      BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, bs, entityPlayer);
      event.setExpToDrop(0);
      if (MinecraftForge.EVENT_BUS.post(event)) {
        return false;
      }
      if (block.removedByPlayer(bs, world, pos, entityPlayer, true)) {
        block.harvestBlock(world, entityPlayer, pos, world.getBlockState(pos), world.getTileEntity(pos), heldItem);
      }
      tool.used(hand, entityPlayer, pos);
      return true;
    }
    return false;
  }

  private static final @Nonnull ToolUtil instance = new ToolUtil();

  public static @Nonnull ToolUtil getInstance() {
    return instance;
  }

  private final @Nonnull List<IToolProvider> toolProviders = new ArrayList<IToolProvider>();

  private ToolUtil() {
  }

  public void registerToolProvider(@Nonnull IToolProvider toolProvider) {
    toolProviders.add(toolProvider);
  }

  private boolean isToolEquippedImpl(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    return getEquippedToolImpl(player, hand) != null;
  }

  private @Nullable ITool getEquippedToolImpl(@Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack equipped = player.getHeldItem(hand);
    return getToolFromStack(equipped);
  }

  public static @Nullable ITool getToolFromStack(@Nonnull ItemStack equipped) {
    if (Prep.isInvalid(equipped)) {
      return null;
    }
    if (equipped.getItem() instanceof ITool) {
      return (ITool) equipped.getItem();
    }
    return getInstance().getToolImpl(equipped);
  }

  private ITool getToolImpl(@Nonnull ItemStack equipped) {
    for (IToolProvider provider : toolProviders) {
      ITool result = provider.getTool(equipped);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

}
