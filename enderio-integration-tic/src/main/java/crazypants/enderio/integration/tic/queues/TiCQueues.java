package crazypants.enderio.integration.tic.queues;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.ItemStack;

public class TiCQueues {

  private static final @Nonnull List<CastQueue> castQueue = new ArrayList<CastQueue>();
  private static final @Nonnull List<BasinQueue> basinQueue = new ArrayList<BasinQueue>();
  private static final @Nonnull List<SmeltQueue> smeltQueue = new ArrayList<SmeltQueue>();
  private static final @Nonnull List<Pair<ItemStack, ItemStack[]>> alloyQueue = new ArrayList<Pair<ItemStack, ItemStack[]>>();

  public static @Nonnull List<CastQueue> getCastQueue() {
    return castQueue;
  }

  public static @Nonnull List<BasinQueue> getBasinQueue() {
    return basinQueue;
  }

  public static @Nonnull List<SmeltQueue> getSmeltQueue() {
    return smeltQueue;
  }

  public static @Nonnull List<Pair<ItemStack, ItemStack[]>> getAlloyQueue() {
    return alloyQueue;
  }

}
