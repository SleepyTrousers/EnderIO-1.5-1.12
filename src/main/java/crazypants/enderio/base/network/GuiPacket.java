package crazypants.enderio.base.network;

import crazypants.enderio.base.Log;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * A generic network packet that allows a client-side GUI (or container) to talk to its server-side container.
 * <p>
 * Usage:
 * <ul>
 * <li>On the GUI implement IRemoteExec.IGui (already done on GuiContainerBaseEIO).
 * <li>On the container implement IRemoteExec.IContainer.
 * <li>If needed, add a new send() method and PATTERN to GuiPacket for the correct number and type of parameters.
 * <li>If needed, add a new DataType value and handling code to GuiPacket (for things that cannot be transmitted as int or long).
 * <li>In the GUI, call the appropriate send() method. Chose a unique (per container) message ID.
 * <li>In the container, implement networkExec() and switch on the message ID. You can get the parameters with the same index numbers as the send() method put
 * them in. Be careful to match the data types used or you'll get either 0 values.
 * <li>Done.
 * </ul>
 * 
 * The GuiPacket's handler will make sure that calls are only delivered to the correct container. If that container isn't open, the message will be silently
 * ignored.
 * <p>
 * The GUI and container are responsible to agree on the data transferred.
 *
 */
public class GuiPacket implements IMessage {

  public static void send(IRemoteExec gui, int msgID) {
    GuiPacket p = new GuiPacket(gui, msgID, 0, null, null, null);
    p.send();
  }

  public static void send(IRemoteExec gui, int msgID, int data) {
    GuiPacket p = new GuiPacket(gui, msgID, 1, new int[] { data }, null, null);
    p.send();
  }

  public static void send(IRemoteExec gui, int msgID, Enum<?> data) {
    GuiPacket p = new GuiPacket(gui, msgID, 1, new int[] { data.ordinal() }, null, null);
    p.send();
  }

  public static void send(IRemoteExec gui, int msgID, boolean data) {
    GuiPacket p = new GuiPacket(gui, msgID, 1, new int[] { data ? 1 : 0 }, null, null);
    p.send();
  }

  public static void send(IRemoteExec gui, int msgID, String data) {
    GuiPacket p = new GuiPacket(gui, msgID, 2, null, null, new String[] { data });
    p.send();
  }

  // ------------------------------------------------------------------

  private static enum DataType {
    INT,
    LONG,
    STRING;
  }

  private static final DataType[][] PATTERN = { {}, { DataType.INT }, { DataType.STRING } };

  private int guiID, msgID, pattern;
  private int[] ints;
  private long[] longs;
  private String[] strings;

  private GuiPacket(IRemoteExec gui, int msgID, int pattern, int[] ints, long[] longs, String[] strings) {
    this.guiID = gui.getGuiID();
    this.msgID = msgID;
    this.pattern = pattern;
    this.ints = ints;
    this.longs = longs;
    this.strings = strings;
  }

  public GuiPacket() {
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeShort(guiID);
    buf.writeShort(msgID);
    buf.writeShort(pattern);
    int idx = 0;
    for (DataType dt : PATTERN[pattern]) {
      switch (dt) {
      case INT:
        buf.writeInt(ints[idx++]);
        break;
      case LONG:
        buf.writeLong(longs[idx++]);
        break;
      case STRING:
        buf.writeBoolean(strings[idx] != null);
        if (strings[idx] != null) {
          ByteBufUtils.writeUTF8String(buf, strings[idx]);
        }
        idx++;
        break;
      }
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    guiID = buf.readShort();
    msgID = buf.readShort();
    pattern = buf.readShort();
    if (pattern < 0 || pattern >= PATTERN.length) {
      Log.warn("Invalid network packet received (" + guiID + "/" + msgID + "/" + pattern + "): p invalid");
      return;
    }
    int idx = 0;
    for (DataType dt : PATTERN[pattern]) {
      switch (dt) {
      case INT:
        if (ints == null)
          ints = new int[PATTERN[pattern].length];
        ints[idx++] = buf.readInt();
        break;
      case LONG:
        if (longs == null)
          longs = new long[PATTERN[pattern].length];
        longs[idx++] = buf.readLong();
        break;
      case STRING:
        if (strings == null)
          strings = new String[PATTERN[pattern].length];
        if (buf.readBoolean()) {
          strings[idx] = ByteBufUtils.readUTF8String(buf);
        }
        idx++;
        break;
      }
    }
  }

  private boolean checkAccess(int idx, DataType type) {
    if (idx >= PATTERN[pattern].length) {
      Log.warn("Invalid network packet received (" + guiID + "/" + msgID + "/" + pattern + "): idx" + idx + " high");
      return false;
    }
    if (PATTERN[pattern][idx] != type) {
      Log.warn("Invalid network packet received (" + guiID + "/" + msgID + "/" + pattern + "): idx" + idx + " not " + type);
      return false;
    }
    if ((type == DataType.INT && ints == null) || (type == DataType.LONG && longs == null) || (type == DataType.STRING && strings == null)) {
      Log.warn(
          "Invalid network packet received (" + guiID + "/" + msgID + "/" + pattern + "): idx" + idx + " no " + type + " data");
      return false;
    }
    return true;
  }

  private void send() {
    PacketHandler.INSTANCE.sendToServer(this);
  }

  private EntityPlayerMP player;

  public EntityPlayerMP getPlayer() {
    return player;
  }

  public static class Handler implements IMessageHandler<GuiPacket, IMessage> {

    @Override
    public IMessage onMessage(GuiPacket message, MessageContext ctx) {
      message.player = ctx.getServerHandler().player;
      final Container openContainer = message.player.openContainer;
      if (openContainer instanceof IRemoteExec.IContainer && ((IRemoteExec.IContainer) openContainer).getGuiID() == message.guiID && message.guiID >= 0) {
        Log.debug("Exec (" + message.guiID + "/" + message.msgID + "/" + message.pattern + "): cont=" + openContainer);
        return ((IRemoteExec.IContainer) openContainer).networkExec(message.msgID, message);
      } else {
        Log.debug("Invalid network packet received (" + message.guiID + "/" + message.msgID + "/" + message.pattern + "): cont=" + openContainer + " id="
            + ((openContainer instanceof IRemoteExec.IContainer) ? ((IRemoteExec.IContainer) openContainer).getGuiID() : "n/a"));
        return null;
      }
    }

  }

  // GETTERS

  public int getInt(int idx) {
    return checkAccess(idx, DataType.INT) ? ints[idx] : 0;
  }

  public <E extends Enum<?>> E getEnum(int idx, Class<E> clazz) {
    E[] enumConstants = clazz.getEnumConstants();
    return enumConstants[MathHelper.clamp(getInt(idx), 0, enumConstants.length - 1)];
  }

  public boolean getBoolean(int idx) {
    return getInt(idx) != 0;
  }

  public long getLong(int idx) {
    return checkAccess(idx, DataType.LONG) ? longs[idx] : 0L;
  }

  public BlockPos getBlockPos(int idx) {
    return BlockPos.fromLong(getLong(idx));
  }

  public String getString(int idx) {
    return checkAccess(idx, DataType.STRING) ? strings[idx] : null;
  }

}
