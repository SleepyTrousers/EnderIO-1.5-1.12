package crazypants.enderio.base.handler.darksteel;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.Util;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.util.Prep;
import info.loenwind.autoconfig.factory.IValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import static crazypants.enderio.base.init.ModObject.blockEndermanSkull;

@EventBusSubscriber(modid = EnderIO.MODID)
public class SwordHandler {

  public static final @Nonnull String HIT_BY_DARK_STEEL_SWORD = "hitByDarkSteelSword";
  private static final @Nonnull ResourceLocation ENDERZOO_ENDERMINY = new ResourceLocation("enderzoo", "enderminy"); // TODO 1.13 remove
  private static final @Nonnull ResourceLocation ENDERIOZOO_ENDERMINY = new ResourceLocation("enderiozoo", "enderminy");

  @SubscribeEvent
  public static void onEnderTeleport(EnderTeleportEvent evt) {
    if (evt.getEntityLiving().getEntityData().getBoolean(HIT_BY_DARK_STEEL_SWORD)) {
      evt.setCanceled(true);
    }
  }

  // Set priority to lowest in the hope any other mod adding head drops will have already added them
  // by the time this is called to prevent multiple head drops
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void onEntityDrop(LivingDropsEvent evt) {

    final Entity attacker = evt.getSource().getTrueSource();
    final EntityLivingBase killedMob = evt.getEntityLiving();
    if (!(attacker instanceof EntityPlayer) || killedMob == null) {
      return;
    }

    EntityPlayer player = (EntityPlayer) attacker;

    double skullDropChance = getSkullDropChance(player, evt);
    dropSkull(evt, player, ((int) skullDropChance) + (killedMob.world.rand.nextDouble() < (skullDropChance % 1) ? 1 : 0));

    // Special handling for ender pearl drops
    if (isEquipped(player)) {
      ResourceLocation name = EntityList.getKey(killedMob);
      final boolean isEnderman = killedMob instanceof EntityEnderman;
      final boolean isEnderminy = !isEnderman && (ENDERZOO_ENDERMINY.equals(name) || ENDERIOZOO_ENDERMINY.equals(name));
      if (isEnderman || isEnderminy) {
        ItemStack dropItem = isEnderminy ? Material.SHARD_ENDER.getStack() : new ItemStack(Items.ENDER_PEARL);
        int numPearls = 0;
        double chance = DarkSteelConfig.darkSteelSwordEnderPearlDropChance.get();
        while (chance >= 1) {
          numPearls++;
          chance--;
        }
        if (chance > 0 && Math.random() <= chance) {
          numPearls++;
        }
        for (int i = 0; i < evt.getLootingLevel(); i++) {
          chance = DarkSteelConfig.darkSteelSwordEnderPearlDropChancePerLooting.get();
          while (chance >= 1) {
            numPearls++;
            chance--;
          }
          if (chance > 0 && Math.random() <= chance) {
            numPearls++;
          }
        }

        int existing = 0;
        for (EntityItem stack : evt.getDrops()) {
          if (stack.getItem().getItem() == dropItem.getItem() && stack.getItem().getItemDamage() == dropItem.getItemDamage()) {
            existing += stack.getItem().getCount();
          }
        }
        int toDrop = numPearls - existing;
        if (toDrop > 0) {
          dropItem.setCount(toDrop);
          evt.getDrops().add(Util.createDrop(player.world, dropItem, killedMob.posX, killedMob.posY, killedMob.posZ, false));
        }

      }
    }

  }

  private static double getSkullDropChance(@Nonnull EntityPlayer player, LivingDropsEvent evt) {
    ItemStack equipped = player.getHeldItemMainhand();
    boolean isWitherSkeleton = evt.getEntityLiving() instanceof EntityWitherSkeleton;
    boolean isEnderman = !isWitherSkeleton && evt.getEntityLiving() instanceof EntityEnderman;

    if (isEquipped(player)) {
      return (iee(isWitherSkeleton, isEnderman, CapacitorKey.HEAD_CHANCE, CapacitorKey.HEAD_CHANCE_WITHER, CapacitorKey.HEAD_CHANCE_ENDERMAN)
          .get(evt.getLootingLevel())
          + iee(isWitherSkeleton, isEnderman, CapacitorKey.HEAD_EMPOWERED_CHANCE, CapacitorKey.HEAD_EMPOWERED_CHANCE_WITHER,
              CapacitorKey.HEAD_EMPOWERED_CHANCE_ENDERMAN).get(
                  isEquippedAndPowered(player, DarkSteelConfig.darkSteelSwordPowerUsePerHit) ? EnergyUpgradeManager.getPowerUpgradeLevel(equipped) + 1 : 0))
          * CapacitorKey.HEAD_TIER_CHANCE.get(((ItemDarkSteelSword) equipped.getItem()).getEquipmentData().getTier())
          * (double) iee(isWitherSkeleton, isEnderman, CapacitorKey.HEAD_FAKEPLAYER_CHANCE, CapacitorKey.HEAD_FAKEPLAYER_CHANCE_WITHER,
              CapacitorKey.HEAD_FAKEPLAYER_CHANCE_ENDERMAN).get(player instanceof FakePlayer ? 0 : 1);
    } else {
      return (CapacitorKey.HEAD_BEHEADING_CHANCE_ENDERMAN.get(TicProxy.getBehadingLevel(equipped)) + iee(isWitherSkeleton, isEnderman,
          CapacitorKey.HEAD_VANILLA_CHANCE, CapacitorKey.HEAD_VANILLA_CHANCE_WITHER, CapacitorKey.HEAD_VANILLA_CHANCE_ENDERMAN).get(evt.getLootingLevel()))
          * (double) iee(isWitherSkeleton, isEnderman, CapacitorKey.HEAD_FAKEPLAYER_CHANCE, CapacitorKey.HEAD_FAKEPLAYER_CHANCE_WITHER,
              CapacitorKey.HEAD_FAKEPLAYER_CHANCE_ENDERMAN).get(player instanceof FakePlayer ? 0 : 1);
    }
  }

  private static <X> @Nonnull X iee(boolean a, boolean b, X neither, X A, X B) {
    return a ? A : b ? B : neither;
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, IValue<Integer> requiredPower) {
    return isEquipped(player) && getStoredPower(player) >= requiredPower.get();
  }

  private static void dropSkull(LivingDropsEvent evt, EntityPlayer player, int amount) {
    if (amount > 0) {
      ItemStack skull = getSkullForEntity(evt.getEntityLiving());
      if (skull != null && !skull.isEmpty() && !containsDrop(evt, skull)) {
        skull.setCount(amount);
        evt.getDrops().add(Util.createEntityItem(player.world, skull, evt.getEntityLiving().posX, evt.getEntityLiving().posY, evt.getEntityLiving().posZ));
      }
    }
  }

  private static boolean containsDrop(LivingDropsEvent evt, @Nonnull ItemStack skull) {
    for (EntityItem ei : evt.getDrops()) {
      if (ei != null && ei.getItem().getItem() == skull.getItem() && ei.getItem().getItemDamage() == skull.getItemDamage()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Find the skull type that matches the given entity.
   * 
   * @param entityLiving
   *          An entity that may have a head.
   * @return A skull, and empty itemStack or <code>null</code>
   */
  private static @Nullable ItemStack getSkullForEntity(EntityLivingBase entityLiving) {
    // ItemSkull: {"skeleton", "wither", "zombie", "char", "creeper", "dragon"}
    if (entityLiving instanceof EntitySkeleton) {
      return new ItemStack(Items.SKULL, 1, 0);
    } else if (entityLiving instanceof EntityWitherSkeleton) {
      return new ItemStack(Items.SKULL, 1, 1);
    } else if (entityLiving instanceof EntityZombie) {
      try {
        // ask the entity to do the right thing for modded zombies (e.g. Love Child)
        return (ItemStack) ReflectionHelper.findMethod(EntityZombie.class, "getSkullDrop", "func_190732_dj").invoke(entityLiving);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        if (entityLiving.getClass() == EntityZombie.class) { // sic! not PigZombie, ZombieVillager or Husk
          return new ItemStack(Items.SKULL, 1, 2);
        }
      }
    } else if (entityLiving instanceof EntityCreeper) {
      return new ItemStack(Items.SKULL, 1, 4);
    } else if (entityLiving instanceof EntityEnderman) {
      return new ItemStack(blockEndermanSkull.getBlockNN());
    }

    return Prep.getEmpty();
  }

  private static boolean isEquipped(EntityPlayer player) {
    return player != null && player.getHeldItemMainhand().getItem() instanceof ItemDarkSteelSword;
  }

  private static int getStoredPower(EntityPlayer player) {
    return EnergyUpgradeManager.getEnergyStored(player.getHeldItemMainhand());
  }

}
