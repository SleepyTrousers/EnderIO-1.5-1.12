package crazypants.enderio;

import java.security.InvalidParameterException;
import java.util.Locale;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.PlayerContext;

public enum GuiID {

  GUI_ID_ENDERFACE,
  GUI_ID_PAINTER,
  GUI_ID_STIRLING_GEN,
  GUI_ID_ALLOY_SMELTER,
  GUI_ID_CRUSHER,
  GUI_ID_POWER_MONITOR,
  GUI_ID_POWER_MONITOR_ADVANCED,

  GUI_ID_EXTERNAL_CONNECTION(true),
  GUI_ID_EXTERNAL_CONNECTION_UP(GUI_ID_EXTERNAL_CONNECTION),
  GUI_ID_EXTERNAL_CONNECTION_DOWN(GUI_ID_EXTERNAL_CONNECTION),
  GUI_ID_EXTERNAL_CONNECTION_NORTH(GUI_ID_EXTERNAL_CONNECTION),
  GUI_ID_EXTERNAL_CONNECTION_SOUTH(GUI_ID_EXTERNAL_CONNECTION),
  GUI_ID_EXTERNAL_CONNECTION_EAST(GUI_ID_EXTERNAL_CONNECTION),
  GUI_ID_EXTERNAL_CONNECTION_WEST(GUI_ID_EXTERNAL_CONNECTION),
  GUI_ID_EXTERNAL_CONNECTION_SELECTOR(GUI_ID_EXTERNAL_CONNECTION),

  GUI_ID_TRAVEL_ACCESSABLE,
  GUI_ID_TRAVEL_AUTH,
  GUI_ID_VAT,
  GUI_ID_COMBUSTION_GEN,
  GUI_ID_FARM_STATATION,
  GUI_ID_TANK,
  GUI_ID_CRAFTER,
  GUI_ID_ZOMBIE_GEN,
  GUI_ID_POWERED_SPAWNER,
  GUI_ID_VACUUM_CHEST,
  GUI_ID_ENCHANTER,
  GUI_ID_KILLER_JOE,
  GUI_ID_SOUL_BINDER,
  GUI_ID_SLICE_N_SPLICE,
  GUI_ID_ATTRACTOR,
  GUI_ID_SPAWN_GUARD,
  GUI_ID_TRANSCEIVER,
  GUI_ID_XP_OBELISK,
  GUI_ID_ANVIL,
  GUI_ID_BUFFER,
  GUI_ID_WEATHER_OBELISK,
  GUI_ID_TELEPAD,
  GUI_ID_TELEPAD_TRAVEL,
  GUI_ID_TELEPAD_DIALING_DEVICE,
  GUI_ID_INHIBITOR,
  GUI_ID_INVENTORY_PANEL,
  GUI_ID_SPAWN_RELOCATOR,
  GUI_ID_INVENTORY_PANEL_SENSOR,
  GUI_ID_INVENTORY_PANEL_REMOTE,
  GUI_ID_LOCATION_PRINTOUT {
    @Override
    protected void registerNode() {
      // client-only GUI
    }
  },
  GUI_ID_LOCATION_PRINTOUT_CREATE(GUI_ID_LOCATION_PRINTOUT),

  GUI_ID_CAP_BANK,
  GUI_ID_CAP_BANK_WITH_BAUBLES4(GUI_ID_CAP_BANK),
  GUI_ID_CAP_BANK_WITH_BAUBLES7(GUI_ID_CAP_BANK);

  private final GuiID basePermission;
  private final boolean synthetic;
  private IGuiHandler handler = null;

  private GuiID() {
    this(null, false);
  }

  private GuiID(GuiID basePermission) {
    this(basePermission, false);
  }

  private GuiID(boolean synthetic) {
    this(null, synthetic);
  }

  private GuiID(GuiID basePermission, boolean synthetic) {
    this.basePermission = basePermission;
    this.synthetic = synthetic;
  }

  public boolean is(int id) {
    return ordinal() == id;
  }

  public static GuiID byID(int id) {
    if (id >= 0 && id < values().length) {
      return values()[id];
    }
    return null;
  }

  public String getPermission() {
    return basePermission != null ? basePermission.getPermission() : EnderIO.DOMAIN + ".gui." + name().toLowerCase(Locale.ENGLISH);
  }

  public void openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if (!world.isRemote) {
      if (PermissionAPI.hasPermission(entityPlayer.getGameProfile(), getPermission(),
          new BlockPosContext(entityPlayer, pos, world.getBlockState(pos), side))) {
        entityPlayer.openGui(EnderIO.instance, ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
      } else {
        entityPlayer.addChatMessage(new TextComponentString(EnderIO.lang.localize("gui.permission.denied")));
      }
    }
  }

  public void openGui(World world, EntityPlayer entityPlayer, int a, int b, int c) {
    if (!world.isRemote) {
      if (PermissionAPI.hasPermission(entityPlayer.getGameProfile(), getPermission(), new PlayerContext(entityPlayer))) {
        entityPlayer.openGui(EnderIO.instance, ordinal(), world, a, b, c);
      } else {
        entityPlayer.addChatMessage(new TextComponentString(EnderIO.lang.localize("gui.permission.denied")));
      }
    }
  }

  public void openClientGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if (world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
    }
  }

  public void openClientGui(World world, EntityPlayer entityPlayer, int a, int b, int c) {
    if (world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, ordinal(), world, a, b, c);
    }
  }

  static @Nonnull
  public EnumFacing guiid2facing(GuiID id) {
    switch (id) {
    case GUI_ID_EXTERNAL_CONNECTION_DOWN:
      return EnumFacing.DOWN;
    case GUI_ID_EXTERNAL_CONNECTION_EAST:
      return EnumFacing.EAST;
    case GUI_ID_EXTERNAL_CONNECTION_NORTH:
      return EnumFacing.NORTH;
    case GUI_ID_EXTERNAL_CONNECTION_SOUTH:
      return EnumFacing.SOUTH;
    case GUI_ID_EXTERNAL_CONNECTION_UP:
      return EnumFacing.UP;
    case GUI_ID_EXTERNAL_CONNECTION_WEST:
      return EnumFacing.WEST;
    default:
      return EnumFacing.DOWN;
    }
  }

  static @Nonnull
  public GuiID facing2guiid(EnumFacing facing) {
    switch (facing) {
    case DOWN:
      return GUI_ID_EXTERNAL_CONNECTION_DOWN;
    case EAST:
      return GUI_ID_EXTERNAL_CONNECTION_EAST;
    case NORTH:
      return GUI_ID_EXTERNAL_CONNECTION_NORTH;
    case SOUTH:
      return GUI_ID_EXTERNAL_CONNECTION_SOUTH;
    case UP:
      return GUI_ID_EXTERNAL_CONNECTION_UP;
    case WEST:
      return GUI_ID_EXTERNAL_CONNECTION_WEST;
    default:
      return GUI_ID_EXTERNAL_CONNECTION_DOWN;
    }
  }

  public static void registerGuiHandler(GuiID id, IGuiHandler handler) {
    if (id != null && handler != null) {
      if (id.handler != null) {
        throw new InvalidParameterException("Handler for " + id + " already set to " + id.handler);
      }
      id.handler = handler;
    }
  }

  protected void registerNode() {
    if (basePermission == null) {
      PermissionAPI.registerNode(getPermission(), DefaultPermissionLevel.ALL, "Permission to open the " + name() + " GUI of Ender IO");
    }
    if (!synthetic && handler == null) {
      Log.warn("Unused GUI ID " + name());
    }
  }

  public static void init() {
    for (GuiID id : values()) {
      id.registerNode();
    }

    NetworkRegistry.INSTANCE.registerGuiHandler(EnderIO.instance, new IGuiHandler() {
      @Override
      public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        IGuiHandler handler = byID(id).handler;
        if (handler != null) {
          return handler.getServerGuiElement(id, player, world, x, y, z);
        }
        return null;
      }

      @Override
      public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        IGuiHandler handler = byID(id).handler;
        if (handler != null) {
          return handler.getClientGuiElement(id, player, world, x, y, z);
        }
        return null;
      }
    });
  }

}
