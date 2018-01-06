package crazypants.enderio.base.gui.handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIODummy;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.IModObject.Registerable;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.init.ModObjectRegistry;
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
    NNList<Registerable> objects = ModObjectRegistry.getObjects();
    for (int i = 0; i < objects.size(); i++) {
      IModObject mo = objects.get(i);
      if (mo.getBlock() instanceof IEioGuiHandler || mo.getItem() instanceof IEioGuiHandler) {
        Log.info("Registered permission ", PermissionAPI.registerNode(getPermission(mo), DefaultPermissionLevel.ALL,
            "Permission to open the GUI(s) of Ender IO's " + mo.getUnlocalisedName()));
      }
    }
    NetworkRegistry.INSTANCE.registerGuiHandler(EnderIODummy.getInstance(), new GuiHandler()); // TODO: switch to real mod
  }

  public static boolean openGui(@Nonnull IModObject mo, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer,
      @Nullable EnumFacing side, int param) {
    if (!world.isRemote) {
      if (PermissionAPI.hasPermission(entityPlayer.getGameProfile(), getPermission(mo),
          new BlockPosContext(entityPlayer, pos, world.getBlockState(pos), side))) {
        return openGui(world, entityPlayer, getID(mo), pos, side, param, 0, 0);
      } else {
        entityPlayer.sendMessage(Lang.GUI_PERMISSION_DENIED.toChat());
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
        entityPlayer.sendMessage(Lang.GUI_PERMISSION_DENIED.toChat());
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

    entityPlayer.openGui(EnderIODummy.getInstance(), a, world, b, c, d); // TODO
    return true;
  }

  // TODO: This ID depends on the order the modObjects were registered, which depends on the order the RegistryEvent.Register<Block> was executed and the
  // content of the IModObject enums. Those should be the same on server and client side for the same builds, but there's still a small risk they are not. Can
  // we find a better ID that is more stable (and fits into 24 bits) without hardcoding it? (Note: Hardcoding is bad because there could be conflicts between
  // third-party submods.)
  protected static int getID(@Nonnull IModObject mo) {
    final int id = ModObjectRegistry.getObjects().indexOf(mo);
    if (id < 0) {
      throw new RuntimeException("Cannot open GUI for object " + mo + " because it is not registered.");
    }
    return id & 0x00FFFFFF;
  }

  protected static Registerable getFromID(int id) {
    final NNList<Registerable> objects = ModObjectRegistry.getObjects();
    if (id < 0 || id >= objects.size()) {
      throw new RuntimeException("Failed to open GUI " + id + "---not a valid ID");
    }
    return objects.get(id);
  }

  protected static @Nonnull String getPermission(@Nonnull IModObject mo) {
    if (mo.getBlock() instanceof IEioGuiHandler || mo.getItem() instanceof IEioGuiHandler) {
      return EnderIO.DOMAIN + ".gui." + mo.getUnlocalisedName();
    } else {
      throw new RuntimeException("Cannot open GUI for object " + mo + " because it has no GUI handler.");
    }
  }

}
