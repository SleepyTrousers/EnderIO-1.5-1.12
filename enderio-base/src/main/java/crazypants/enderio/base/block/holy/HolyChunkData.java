package crazypants.enderio.base.block.holy;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.util.FuncUtil;
import crazypants.enderio.util.SparseArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class HolyChunkData {

  private static Map<Chunk, SparseArray> DATA = new WeakHashMap<>();

  @SubscribeEvent
  public static void onSave(ChunkDataEvent.Save event) {
    FuncUtil.doIf(FuncUtil.runIf(DATA.get(event.getChunk()), SparseArray::toNBT), nbt -> event.getData().setTag(EnderIO.DOMAIN + ":hcd", nbt));
  }

  @SubscribeEvent
  public static void onLoad(ChunkDataEvent.Load event) {
    if (event.getData().hasKey(EnderIO.DOMAIN + ":hcd")) {
      DATA.put(event.getChunk(), new SparseArray(event.getData().getCompoundTag(EnderIO.DOMAIN + ":hcd")));
    }
  }

  public static void put(@Nonnull Chunk chunk, @Nonnull BlockPos pos, int value) {
    DATA.computeIfAbsent(chunk, unused -> new SparseArray()).put(makeKey(pos), value);
  }

  public static void del(@Nonnull Chunk chunk, @Nonnull BlockPos pos) {
    DATA.computeIfAbsent(chunk, unused -> new SparseArray()).delete(makeKey(pos));
  }

  public static int get(@Nonnull Chunk chunk, @Nonnull BlockPos pos) {
    SparseArray data = DATA.get(chunk);
    return data == null ? 0 : data.get(makeKey(pos));
  }

  public static int get(@Nonnull Chunk chunk, @Nonnull BlockPos pos, Supplier<Integer> defaultValue) {
    int value = get(chunk, pos);
    if (value == 0) {
      value = defaultValue.get();
      put(chunk, pos, value);
    }
    return value;
  }

  protected static int makeKey(@Nonnull BlockPos pos) {
    return (pos.getX() & 0b1111) | ((pos.getZ() & 0b1111) << 4) | (pos.getY() << 8);
  }

}
