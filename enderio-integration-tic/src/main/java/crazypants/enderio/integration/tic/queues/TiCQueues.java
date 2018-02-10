package crazypants.enderio.integration.tic.queues;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

public class TiCQueues {

  private static final @Nonnull List<CastQueue> castQueue = new ArrayList<>();
  private static final @Nonnull List<BasinQueue> basinQueue = new ArrayList<>();
  private static final @Nonnull List<SmeltQueue> smeltQueue = new ArrayList<>();
  private static final @Nonnull List<Pair<Things, NNList<Things>>> alloyQueue = new ArrayList<>();

  public static @Nonnull List<CastQueue> getCastQueue() {
    return castQueue;
  }

  public static @Nonnull List<BasinQueue> getBasinQueue() {
    return basinQueue;
  }

  public static @Nonnull List<SmeltQueue> getSmeltQueue() {
    return smeltQueue;
  }

  public static @Nonnull List<Pair<Things, NNList<Things>>> getAlloyQueue() {
    return alloyQueue;
  }

}
