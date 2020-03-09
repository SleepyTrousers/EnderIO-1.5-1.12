package crazypants.enderio.machines.machine.fracker;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.machines.EnderIOMachines;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODID)
public class FrackingAction {

  public static final @Nonnull NNList<WeightedOre> ORES = new NNList<>();

  static {
    ORES.add(new WeightedOre(100, 10, 10, Integer.MAX_VALUE, new Things("dustBedrock")));
    ORES.add(new WeightedOre(10, 1000, 50, -1, new Things("POWDER_QUARTZ")));
    ORES.add(new WeightedOre(10, 2500, 50, 0, new Things("minecraft:obsidian")));
    ORES.add(new WeightedOre(10, 1000, 25, 0, new Things("minecraft:coal:0")));
    ORES.add(new WeightedOre(5, 5000, 50, 0, new Things("oreIron")));
  }

  @SubscribeEvent
  public static void onTick(@Nonnull WorldTickEvent event) {
    if (event.phase == Phase.END) {
      final World world = event.world;
      if (world != null) {
        final FrackingData data = FrackingData.getIfExists(world);
        if (data != null) {
          long serverTickCount = EnderIO.proxy.getServerTickCount();
          if (data.getNextTick() <= serverTickCount) {
            data.setNextTick(serverTickCount + 20);
            for (NNIterator<Deposit> iterator = data.getData().iterator(); iterator.hasNext();) {
              Deposit deposit = iterator.next();
              spawn(world, deposit);
              if (deposit.getSize() < 10L) {
                iterator.remove();
              }
            }
          }
        }
      }
    }
  }

  static void spawn(@Nonnull World world, @Nonnull Deposit deposit) {
    if (world.isBlockLoaded(deposit.getPos())) {
      int count = 0;
      while (count < deposit.getRadius() && deposit.getSize() >= 10) {
        count++;
        BlockPos pos = makePos(world, deposit);
        if (world.getBlockState(pos).getBlock() == Blocks.BEDROCK) {
          spawn(world, deposit, pos);
        } else {
          spill(world, deposit, pos);
        }
      }
    }
  }

  private static void spill(@Nonnull World world, @Nonnull Deposit deposit, @Nonnull BlockPos pos) {
    if (deposit.getSize() >= 1000) {
      for (int i = 0; i < 256; i++) {
        BlockPos p2 = BlockCoord.withY(pos, i);
        if (world.isAirBlock(p2)) {
          world.setBlockState(pos, Fluids.VAPOR_OF_LEVITY.getBlockNN().getDefaultState(), 3);
          deposit.remove(1000);
          return;
        } else if (world.getBlockState(p2).isOpaqueCube()) {
          return;
        }
      }
    }
  }

  private static void spawn(@Nonnull World world, @Nonnull Deposit deposit, @Nonnull BlockPos pos) {
    @SuppressWarnings("null")
    final @Nonnull List<WeightedOre> list = ORES.stream()
        .filter(ore -> ore.getDimension() == Integer.MAX_VALUE || ore.getDimension() == world.provider.getDimension())
        .filter(ore -> ore.getMinPressure() < deposit.getSize()).collect(Collectors.toList());
    if (!list.isEmpty()) {
      final WeightedOre randomOre = WeightedRandom.getRandomItem(world.rand, list);
      Block.spawnAsEntity(world, pos, randomOre.getOre());
      deposit.remove(randomOre.getCost());
    }
  }

  private static @Nonnull BlockPos makePos(@Nonnull World world, @Nonnull Deposit deposit) {
    for (int i = 0; i < 5; i++) {
      BlockPos candidate = deposit.getPos().add(world.rand.nextGaussian() * deposit.getRadius(), -deposit.getPos().getY(),
          world.rand.nextGaussian() * deposit.getRadius());
      if (deposit.inRange(candidate) && world.isBlockLoaded(candidate)) {
        return candidate;
      }
    }
    return BlockCoord.withY(deposit.getPos(), 0);
  }

}
