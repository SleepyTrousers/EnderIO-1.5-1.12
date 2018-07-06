package crazypants.enderio.base.gui.handler;

import javax.annotation.Nullable;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.network.IRemoteExec;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

class GuiHandler implements IGuiHandler {

  @Override
  @Nullable
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return getGuiElement(true, ID, player, world, x, y, z);
  }

  @Override
  @Nullable
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (!world.isRemote) {
      throw new RuntimeException("Cannot show GUI on the server---no screen, no user, no coffee...");
    }
    return getGuiElement(false, ID, player, world, x, y, z);
  }

  @Nullable
  protected Object getGuiElement(boolean server, int a, EntityPlayer player, World world, int b, int c, int d) {
    if (player == null || world == null) {
      return null;
    }
    int facingI = a & 0xFF;
    EnumFacing facing = facingI == 0xFF ? null : EnumFacing.values()[facingI];
    int id = a >>> 8;
    long posl = ((long) c << 32) | (b & 0xffffffffL);
    BlockPos pos = BlockPos.fromLong(posl);
    int param2 = b, param3 = c, param1 = d;

    IModObject mo = GuiHelper.getFromID(id);
    IEioGuiHandler handler = mo.getBlock() instanceof IEioGuiHandler ? (IEioGuiHandler) mo.getBlock()
        : mo.getItem() instanceof IEioGuiHandler ? (IEioGuiHandler) mo.getItem() : null;
    if (handler != null) {
      Log.debug("Opening GUI for ", mo, ": isServer=", server, " id=", id, " player=", player, " world=", world, " pos=", pos, " facing=", facing, " param1=",
          param1, " param2=", param2, " param3=", param3);
      final Object guiElement = handler.getGuiElement(server, player, world, pos, facing, param1, param2, param3);
      if (guiElement instanceof IRemoteExec) {
        ((IRemoteExec) guiElement).setGuiID(id);
      }
      return guiElement;
    } else {
      Log.error("Failed to open GUI ", id, " because ", mo, " is no GUI handler");
      player.closeScreen();
      return null;
    }
  }
}