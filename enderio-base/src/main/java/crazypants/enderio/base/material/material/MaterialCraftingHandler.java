package crazypants.enderio.base.material.material;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.InfinityConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class MaterialCraftingHandler {

  private static final Random RANDOM = new Random();
  private static final Map<BlockPos, Long> fires = new HashMap<>();

  @SubscribeEvent
  public static void on(NeighborNotifyEvent event) {
    if (InfinityConfig.infinityCraftingEnabled.get()) {
      final World world = event.getWorld();
      BlockPos posIdx = event.getPos();
      if (world.provider.getDimension() != 0) {
        if (InfinityConfig.infinityInAllDimensions.get()) {
          posIdx = posIdx.up(world.provider.getDimension() * 256);
        } else {
          return;
        }
      }
      BlockPos pos = event.getPos();
      final long worldTime = world.getTotalWorldTime();
      if (fires.containsKey(posIdx)) {
        if (world.isAirBlock(pos) && world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && worldTime > fires.get(posIdx)
            && RANDOM.nextFloat() <= InfinityConfig.infinityDropChance.get()) {
          Block.spawnAsEntity(world, pos, Material.POWDER_INFINITY.getStack(InfinityConfig.infinityStackSize.get()));
          if (InfinityConfig.infinityMakesSound.get()) {
            world.playSound(null, pos, SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
          }
        }
        fires.remove(posIdx);
      } else if (event.getState().getBlock() instanceof BlockFire && world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
        if (fires.size() > 100) {
          Iterator<Long> iterator = fires.values().iterator();
          while (iterator.hasNext()) {
            if (iterator.next() < worldTime || fires.size() > 500) {
              iterator.remove();
            }
          }
        }
        fires.put(posIdx, worldTime + InfinityConfig.infinityMinAge.get());
      }
    }
  }

}
