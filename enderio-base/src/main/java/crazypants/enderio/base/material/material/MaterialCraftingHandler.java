package crazypants.enderio.base.material.material;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.InfinityConfig;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.NeighborNotifyEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class MaterialCraftingHandler {

  private static final Random RANDOM = new Random();
  private static final Map<BlockPos, Long> fires = new HashMap<>();

  @SubscribeEvent
  public static void on(NeighborNotifyEvent event) {
    if (InfinityConfig.inWorldCraftingEnabled.get()) {
      final World world = event.getWorld();
      BlockPos posIdx = event.getPos();
      if (world.provider.getDimension() != 0) {
        if (InfinityConfig.enableInAllDimensions.get()) {
          posIdx = posIdx.up(world.provider.getDimension() * 256);
        } else {
          return;
        }
      }
      BlockPos pos = event.getPos();
      final long worldTime = world.getTotalWorldTime();
      if (fires.containsKey(posIdx)) {
        if (world.isAirBlock(pos) && world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && worldTime > fires.get(posIdx)
            && RANDOM.nextFloat() <= InfinityConfig.dropChance.get()) {
          double d0 = RANDOM.nextFloat() * 0.5F + 0.25D;
          double d1 = RANDOM.nextFloat() * 0.5F + 0.25D;
          double d2 = RANDOM.nextFloat() * 0.5F + 0.25D;
          EntityItem entityitem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2,
              Material.POWDER_INFINITY.getStack(InfinityConfig.dropStackSize.get()));
          entityitem.setDefaultPickupDelay();
          // This gives the item enough health to survive for a while...
          entityitem.attackEntityFrom(DamageSource.IN_FIRE, -100);
          // while being on fire
          entityitem.setFire(10);
          world.spawnEntity(entityitem);
          if (InfinityConfig.makesSound.get()) {
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
        fires.put(posIdx, worldTime + InfinityConfig.fireMinAge.get());
      }
    }
  }

  @SuppressWarnings("null")
  @SubscribeEvent
  public static void onWorldTick(@Nonnull TickEvent.WorldTickEvent event) {
    if (!fires.isEmpty() && !event.world.getGameRules().getBoolean("doFireTick") && InfinityConfig.inWorldCraftingEnabled.get()
        && (InfinityConfig.enableInAllDimensions.get() || event.world.provider.getDimension() == 0)) {
      final int yOffset = event.world.provider.getDimension() * 256;
      final long worldTime = event.world.getTotalWorldTime();
      for (Entry<BlockPos, Long> fire : fires.entrySet()) {
        final BlockPos posIdx = fire.getKey();
        final BlockPos pos = posIdx.down(yOffset);
        if (pos.getY() >= 0 && pos.getY() <= 255 && worldTime > fire.getValue()) {
          if (event.world.getBlockState(pos).getBlock() instanceof BlockFire) {
            event.world.setBlockToAir(pos);
          } else {
            fires.remove(posIdx);
          }
          return; // else CME
        }
      }
    }
  }
}
