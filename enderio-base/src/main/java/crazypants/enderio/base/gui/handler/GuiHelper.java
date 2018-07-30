package crazypants.enderio.base.gui.handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.PlayerContext;

public class GuiHelper {

  public static void init(@Nonnull FMLInitializationEvent event) {
    for (IModObject mo : ModObjectRegistry.getRegistry()) {
      if (mo.getBlock() instanceof IEioGuiHandler.WithServerComponent || mo.getItem() instanceof IEioGuiHandler.WithServerComponent) {
        Log.info("Registered permission ", PermissionAPI.registerNode(getPermission(mo), DefaultPermissionLevel.ALL,
            "Permission to open the GUI(s) of Ender IO's " + mo.getUnlocalisedName()));
      }
    }
    NetworkRegistry.INSTANCE.registerGuiHandler(EnderIO.getInstance(), new GuiHandler());
  }

  public static boolean openGui(@Nonnull IModObject mo, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer,
      @Nullable EnumFacing side, int param) {
    if (!world.isRemote) {
      if (PermissionAPI.hasPermission(entityPlayer.getGameProfile(), getPermission(mo),
          new BlockPosContext(entityPlayer, pos, world.getBlockState(pos), side))) {
        return openGui(world, entityPlayer, getID(mo), pos, side, param, 0, 0);
      } else {
        entityPlayer.sendStatusMessage(Lang.GUI_PERMISSION_DENIED.toChatServer(), true);
        entityPlayer.closeScreen();
        return false;
      }
    } else {
      return true;
    }
  }

  public static boolean openGui(@Nonnull IModObject mo, @Nonnull World world, @Nonnull EntityPlayer entityPlayer, int a, int b, int c) {
    if (!world.isRemote) {
      if (PermissionAPI.hasPermission(entityPlayer.getGameProfile(), getPermission(mo), new PlayerContext(entityPlayer))) {
        return openGui(world, entityPlayer, getID(mo), null, null, a, b, c);
      } else {
        entityPlayer.sendStatusMessage(Lang.GUI_PERMISSION_DENIED.toChatServer(), true);
        entityPlayer.closeScreen();
        return false;
      }
    } else {
      return true;
    }
  }

  public static boolean openClientGui(@Nonnull IModObject mo, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer,
      @Nullable EnumFacing side, int param) {
    if (world.isRemote) {
      return openGui(world, entityPlayer, getID(mo), pos, side, param, 0, 0);
    }
    return false;
  }

  public static boolean openClientGui(@Nonnull IModObject mo, @Nonnull World world, @Nonnull EntityPlayer entityPlayer, int a, int b, int c) {
    if (world.isRemote) {
      return openGui(world, entityPlayer, getID(mo), null, null, a, b, c);
    }
    return false;
  }

  protected static boolean openGui(@Nonnull World world, @Nonnull EntityPlayer entityPlayer, int id, @Nullable BlockPos pos, @Nullable EnumFacing facing,
      int param1, int param2, int param3) {
    int a, b, c, d;
    a = (facing == null ? 0xFF : facing.ordinal()) | id << 8;
    if (pos != null) {
      long posl = pos.toLong();
      b = (int) posl;
      c = (int) (posl >>> 32);
      if (param2 != 0 | param3 != 0) {
        Log.warn("Invalid parameters 2+3 together with pos for gui " + id);
      }
    } else {
      b = param2;
      c = param3;
    }
    d = param1;

    entityPlayer.openGui(EnderIO.getInstance(), a, world, b, c, d);
    return true;
  }

  protected static int getID(@Nonnull IModObject mo) {
    int id = ModObjectRegistry.getRegistry().getID(mo);
    if (id < 0) {
      throw new RuntimeException("Cannot open GUI for object " + mo + " because it is not registered.");
    }
    return id & 0x00FFFFFF;
  }

  protected static @Nonnull IModObject getFromID(int id) {
    IModObject modObject = ModObjectRegistry.getRegistry().getValue(id);
    if (modObject == null) {
      throw new RuntimeException("Failed to open GUI " + id + "---not a valid ID");
    }
    return modObject;
  }

  protected static @Nonnull String getPermission(@Nonnull IModObject mo) {
    if (mo.getBlock() instanceof IEioGuiHandler.WithServerComponent || mo.getItem() instanceof IEioGuiHandler.WithServerComponent) {
      return EnderIO.DOMAIN + ".gui." + mo.getUnlocalisedName();
    } else {
      throw new RuntimeException("Cannot open GUI for object " + mo + " because it has no server-side GUI handler.");
    }
  }

}
