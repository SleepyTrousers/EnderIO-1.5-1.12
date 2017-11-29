package crazypants.enderio.base.material.material;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MaterialCraftingHandler {

  private static final Random RANDOM = new Random();

  public static void init(@Nonnull FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(MaterialCraftingHandler.class);
  }

  private MaterialCraftingHandler() {
  }

  private static Map<BlockPos, Long> fires = new HashMap<>();

  // TODO: config values for spawn chance, stack size, fire time

  @SubscribeEvent
  public static void on(NeighborNotifyEvent event) {
    final World world = event.getWorld();
    if (world.provider.getDimension() != 0) {
      return;
    }
    final BlockPos pos = event.getPos();
    final long worldTime = world.getTotalWorldTime();
    if (fires.containsKey(pos)) {
      if (world.isAirBlock(pos) && world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && worldTime > fires.get(pos) && RANDOM.nextFloat() <= .5f) {
        Block.spawnAsEntity(world, pos, Material.POWDER_INFINITY.getStack());
        world.playSound(null, pos, SoundEvents.ENTITY_FIREWORK_LARGE_BLAST, SoundCategory.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      }
      fires.remove(pos);
    } else if (event.getState().getBlock() instanceof BlockFire && world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
      if (fires.size() > 100) {
        Iterator<Long> iterator = fires.values().iterator();
        while (iterator.hasNext()) {
          Long entry = iterator.next();
          if (entry < worldTime - 30 * 10) {
            iterator.remove();
          }
        }
      }
      fires.put(pos, worldTime + 260); // 13s. avg is 230 (11.5s)
    }
  }

}
