package crazypants.enderio.machine.painter.paint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketWorldPaintUpdate implements IMessage {

  protected static final ObjectIntIdentityMap<IBlockState> BLOCK_STATE_IDS = net.minecraftforge.fml.common.registry.GameData.getBlockStateIDMap();

  public static List<PacketWorldPaintUpdate> create(int dimension, CopyOnWriteHashMap<BlockPos, IBlockState> data, Set<BlockPos> updates) {
    List<PacketWorldPaintUpdate> result = new ArrayList<PacketWorldPaintUpdate>();
    boolean reset = false;

    if (updates == null) {
      updates = new HashSet<BlockPos>(data.keySet());
      reset = true;
    }

    if (updates.size() <= 1000) {
      result.add(new PacketWorldPaintUpdate(dimension, data, updates, reset));
    } else {
      // segment updates into blocks of 1000
      while (!updates.isEmpty()) {
        HashSet<BlockPos> set = new HashSet<BlockPos>();
        Iterator<BlockPos> iterator = updates.iterator();
        while (iterator.hasNext() && set.size() < 1000) {
          set.add(iterator.next());
          iterator.remove();
        }
        result.add(new PacketWorldPaintUpdate(dimension, data, set, reset));
        reset = false;
      }
    }

    return result;
  }

  private boolean reset;
  int dimension;
  private int[] blockstates;
  private long[] poses;

  public PacketWorldPaintUpdate() {
    // called when a packet is received
  }

  public PacketWorldPaintUpdate(int dimension, CopyOnWriteHashMap<BlockPos, IBlockState> data, Set<BlockPos> updates, boolean reset) {
    this.reset = reset;
    this.dimension = dimension;
    blockstates = new int[updates.size()];
    poses = new long[updates.size()];

    int i = 0;
    Iterator<BlockPos> iterator = updates.iterator();
    while (iterator.hasNext()) {
      BlockPos next = iterator.next();
      IBlockState iBlockState = data.get(next);
      poses[i] = next.toLong();
      if (iBlockState == null) {
        blockstates[i] = -1;
      } else {
        blockstates[i] = BLOCK_STATE_IDS.get(iBlockState);
      }
      i++;
    }
    System.out.println("Making packet with reset=" + reset + " for dim=" + dimension + " with " + updates.size() + " updates");
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeBoolean(reset);
    buf.writeInt(dimension);
    buf.writeInt(poses.length);
    for (int i = 0; i < poses.length; i++) {
      buf.writeLong(poses[i]);
      buf.writeInt(blockstates[i]);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    reset = buf.readBoolean();
    dimension = buf.readInt();
    int size = buf.readInt();
    blockstates = new int[size];
    poses = new long[size];
    for (int i = 0; i < size; i++) {
      poses[i] = buf.readLong();
      blockstates[i] = buf.readInt();
    }

  }

  public static class Handler implements IMessageHandler<PacketWorldPaintUpdate, IMessage> {

    @Override
    public IMessage onMessage(PacketWorldPaintUpdate message, MessageContext ctx) {
      World world = Minecraft.getMinecraft().theWorld;
      System.out.println("Got an update for dim=" + message.dimension + ", we have " + world.provider.getDimension());
      if (world != null && world.provider.getDimension() == message.dimension) {
        IPaintRegister capability = world.getCapability(PaintRegister.CAP, null);
        if (capability != null) {
          if (message.reset) {
            capability.resetClient();
          }
          for (int i = 0; i < message.poses.length; i++) {
            BlockPos pos = BlockPos.fromLong(message.poses[i]);
            IBlockState paintState = message.blockstates[i] == -1 ? null : BLOCK_STATE_IDS.getByValue(message.blockstates[i]);
            capability.setPaintSource(pos, paintState);
            IBlockState realState = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, realState, realState, 1);
            System.out.println("Got an update for " + pos + " with paint=" + paintState);
          }
        }
      }
      return null;
    }

  }
}