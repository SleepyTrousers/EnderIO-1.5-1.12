package crazypants.enderio.zoo.spawn;

import javax.annotation.Nonnull;

import crazypants.enderio.base.Log;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import crazypants.enderio.zoo.entity.EntityDireSlime;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class MobSpawnEventHandler {

  @SubscribeEvent
  public static void onBlockHarvest(HarvestDropsEvent event) {

    if ((!ZooConfig.direSlimeEnabled.get() && !ZooConfig.direSlimeEnabledHand.get()) || event.isCanceled() || event.getWorld() == null
        || event.getWorld().isRemote || event.getHarvester() == null || event.getHarvester().capabilities.isCreativeMode
        || !(event.getState().getBlock() instanceof BlockDirt || event.getState().getBlock() instanceof BlockGrass)) {
      return;
    }

    if (!isToolEffective(event.getState(), event.getHarvester().getHeldItemMainhand())
        && event.getWorld().rand.nextFloat() < ZooConfig.direSlime1Chance.get()) {
      EntityDireSlime direSlime = new EntityDireSlime(event.getWorld());
      direSlime.setPosition(event.getPos().getX() + 0.5, event.getPos().getY() + 0.0, event.getPos().getZ() + 0.5);
      event.getWorld().spawnEntity(direSlime);
      direSlime.playLivingSound();
      for (ItemStack drop : event.getDrops()) {
        if (drop != null && drop.getItem() == Item.getItemFromBlock(Blocks.DIRT)) {
          if (drop.getCount() > 1) {
            drop.shrink(1);
          } else if (event.getDrops().size() == 1) {
            event.getDrops().clear();
          } else {
            event.getDrops().remove(drop);
          }
          return;
        }
      }
    }
  }

  public static boolean isToolEffective(IBlockState state, ItemStack stack) {
    if (stack.isEmpty()) { // don't spawn them with an empty hand, helps newly spawned players
      return !ZooConfig.direSlimeEnabledHand.get();
    }
    if (!ZooConfig.direSlimeEnabled.get()) {
      return true;
    }
    for (String type : stack.getItem().getToolClasses(stack)) {
      if (type != null) {
        if (state.getBlock().isToolEffective(type, state) || "shovel".equals(type)) {
          // the "shovel" check is needed for modded blocks that extend dirt/grass but refuse to acknowledge any tool as effective, e.g. TiC
          return true;
        }
      }
    }
    return false;
  }

  @SubscribeEvent
  public static void onCheckSpawn(@Nonnull CheckSpawn evt) {
    if (evt.isSpawner()) {
      return;
    }
    final EntityLivingBase entityLiving = evt.getEntityLiving();
    if (entityLiving == null) {
      return;
    }
    ResourceLocation resourceLocation = EntityList.getKey(entityLiving);
    if (resourceLocation == null) {
      return;
    }
    for (ISpawnEntry ent : MobSpawns.instance.getEntries()) {
      if (resourceLocation.toString().equals(ent.getMobName())) {
        if (!ent.canSpawnInDimension(evt.getWorld())) {
          Log.debug(resourceLocation, " may not spawn in dimension ", evt.getWorld().provider.getDimensionType().getName());
          evt.setResult(Result.DENY);
          return;
        } else {
          Log.debug(resourceLocation, " is allowed to spawn in dimension ", evt.getWorld().provider.getDimensionType().getName());
        }
      }
    }

  }

}
