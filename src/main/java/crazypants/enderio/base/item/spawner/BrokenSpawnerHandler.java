package crazypants.enderio.base.item.spawner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNMap;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.SpawnerConfig;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.recipe.poweredspawner.PoweredSpawnerRecipeRegistry;
import crazypants.enderio.util.CapturedMob;
import crazypants.enderio.util.Prep;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BrokenSpawnerHandler {

  public static void init(@Nonnull FMLPreInitializationEvent event) {
    try {
      getEntityIdMethod = ReflectionHelper.findMethod(MobSpawnerBaseLogic.class, null, new String[] { "getEntityId", "func_190895_g" }, new Class<?>[0]);
    } catch (Exception e) {
      Log.error("Broken Spawner: Could not find method: getEntityId/func_190895_g");
    }
    try {
      spawnDelayField = ReflectionHelper.findField(MobSpawnerBaseLogic.class, "spawnDelay", "field_98286_b");
    } catch (Exception e) {
      Log.error("Broken Spawner: Could not find field: spawnDelay/field_98286_b");
    }

    MinecraftForge.EVENT_BUS.register(BrokenSpawnerHandler.class);
  }

  private static Method getEntityIdMethod;
  private static Field spawnDelayField;

  private BrokenSpawnerHandler() {
  }

  private static final @Nonnull NNMap<BlockPos, ItemStack> dropCache = new NNMap.Brutal<BlockPos, ItemStack>();

  @SubscribeEvent
  public static void onBreakEvent(BlockEvent.BreakEvent evt) {
    if (evt.getState().getBlock() instanceof BlockMobSpawner) {
      if (evt.getPlayer() != null && !evt.getPlayer().capabilities.isCreativeMode && !evt.getPlayer().world.isRemote && !evt.isCanceled()) {
        TileEntity tile = evt.getPlayer().world.getTileEntity(NullHelper.notnullF(evt.getPos(), "BlockEvent.BreakEvent.getPos()"));
        if (tile instanceof TileEntityMobSpawner) {

          if (Math.random() > SpawnerConfig.brokenSpawnerDropChance.get()) {
            return;
          }

          ItemStack equipped = evt.getPlayer().getHeldItemMainhand();
          if (Prep.isValid(equipped) && SpawnerConfig.brokenSpawnerToolBlacklist.get().contains(equipped)) {
            return;
          }

          TileEntityMobSpawner spawner = (TileEntityMobSpawner) tile;
          MobSpawnerBaseLogic logic = spawner.getSpawnerBaseLogic();
          ResourceLocation entityName = getEntityName(logic);
          if (entityName != null && !isBlackListed(entityName)) {
            final CapturedMob capturedMob = CapturedMob.create(entityName);
            if (capturedMob != null) {
              ItemStack drop = capturedMob.toStack(ModObject.itemBrokenSpawner.getItemNN(), 0, 1);
              dropCache.put(evt.getPos().toImmutable(), drop);

              for (int i = (int) (Math.random() * 7); i > 0; i--) {
                setSpawnDelay(logic);
                logic.updateSpawner();
              }
            } else {
              dropCache.put(evt.getPos().toImmutable(), Prep.getEmpty());
            }
          }
        }
      } else {
        dropCache.put(evt.getPos().toImmutable(), Prep.getEmpty());
      }
    }
  }

  @SubscribeEvent
  public static void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent evt) {
    if (!evt.isCanceled() && evt.getState().getBlock() instanceof BlockMobSpawner) {
      if (dropCache.containsKey(evt.getPos())) {
        ItemStack stack = dropCache.get(evt.getPos());
        if (Prep.isValid(stack)) {
          evt.getDrops().add(stack);
          dropCache.put(evt.getPos().toImmutable(), Prep.getEmpty());
        }
      } else {
        // A spawner was broken---but not by a player. The TE has been
        // invalidated already, but we might be able to recover it.
        try {
          for (Object object : evt.getWorld().loadedTileEntityList) {
            if (object instanceof TileEntityMobSpawner) {
              TileEntityMobSpawner spawner = (TileEntityMobSpawner) object;
              BlockPos p = spawner.getPos();
              if (spawner.getWorld() == evt.getWorld() && p.equals(evt.getPos())) {
                // Bingo!
                MobSpawnerBaseLogic logic = spawner.getSpawnerBaseLogic();
                ResourceLocation entityName = getEntityName(logic);
                if (entityName != null && !isBlackListed(entityName)) {
                  final CapturedMob capturedMob = CapturedMob.create(entityName);
                  if (capturedMob != null) {
                    evt.getDrops().add(capturedMob.toStack(ModObject.itemBrokenSpawner.getItemNN(), 0, 1));
                  }
                }
              }
            }
          }
        } catch (Exception e) {
          // Risky recovery failed. Happens.
        }
      }
    }
  }

  private static ResourceLocation getEntityName(MobSpawnerBaseLogic logic) {
    if (getEntityIdMethod != null) {
      try {
        return (ResourceLocation) getEntityIdMethod.invoke(logic, new Object[0]);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private static void setSpawnDelay(MobSpawnerBaseLogic logic) {
    if (spawnDelayField != null) {
      try {
        spawnDelayField.set(logic, 0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @SubscribeEvent
  public static void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END) {
      dropCache.clear();
    }
  }

  public static boolean isBlackListed(ResourceLocation entityId) {
    return PoweredSpawnerRecipeRegistry.getInstance().isBlackListed(entityId);
  }

}
