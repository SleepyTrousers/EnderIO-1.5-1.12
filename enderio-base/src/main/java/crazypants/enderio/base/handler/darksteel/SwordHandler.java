package crazypants.enderio.base.handler.darksteel;

import java.lang.reflect.InvocationTargetException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.Util;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.integration.tic.TicProxy;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelSword;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.util.Prep;
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
import net.minecraft.nbt.NBTTagCompound;
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

    // Handle TiC weapons with beheading differently
    if (handleBeheadingWeapons(player, evt)) {
      return;
    }

    double skullDropChance = getSkullDropChance(player, evt);
    if (player instanceof FakePlayer) {
      skullDropChance *= Config.fakePlayerSkullChance;
    }
    if (killedMob.world.rand.nextFloat() < skullDropChance) {
      dropSkull(evt, player);
    }

    // Special handling for ender pearl drops
    if (isEquipped(player)) {
      ResourceLocation name = EntityList.getKey(killedMob);
      final boolean isEnderman = killedMob instanceof EntityEnderman;
      final boolean isEnderminy = !isEnderman && (ENDERZOO_ENDERMINY.equals(name) || ENDERIOZOO_ENDERMINY.equals(name));
      if (isEnderman || isEnderminy) {
        ItemStack dropItem = isEnderminy ? Material.SHARD_ENDER.getStack() : new ItemStack(Items.ENDER_PEARL);
        int numPearls = 0;
        double chance = Config.darkSteelSwordEnderPearlDropChance;
        while (chance >= 1) {
          numPearls++;
          chance--;
        }
        if (chance > 0 && Math.random() <= chance) {
          numPearls++;
        }
        for (int i = 0; i < evt.getLootingLevel(); i++) {
          chance = Config.darkSteelSwordEnderPearlDropChancePerLooting;
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

  private static boolean handleBeheadingWeapons(EntityPlayer player, LivingDropsEvent evt) {
    if (player == null) {
      return false;
    }
    ItemStack equipped = player.getHeldItemMainhand();
    NBTTagCompound tagCompound = equipped.getTagCompound();
    if (tagCompound == null) {
      return false;
    }

    int beheading = TicProxy.getBehadingLevel(equipped);

    if (beheading == 0) {
      // Use default behavior if it is not a cleaver and doesn't have beheading
      return false;
    }

    if (!(evt.getEntityLiving() instanceof EntityEnderman)) {
      // If its not an enderman just let TiC do its thing
      // We wont modify head drops at all
      return true;
    }

    float chance = Math.max(Config.vanillaSwordSkullChance, Config.ticBeheadingSkullModifier * beheading);
    if (player instanceof FakePlayer) {
      chance *= Config.fakePlayerSkullChance;
    }
    while (chance >= 1) {
      dropSkull(evt, player);
      chance--;
    }
    if (chance > 0 && Math.random() <= chance) {
      dropSkull(evt, player);
    }
    return true;
  }

  private static double getSkullDropChance(@Nonnull EntityPlayer player, LivingDropsEvent evt) {
    if (evt.getEntityLiving() instanceof EntityWitherSkeleton) {
      if (isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
        return Config.darkSteelSwordWitherSkullChance + Config.darkSteelSwordWitherSkullLootingModifier * evt.getLootingLevel();
      } else {
        return 0.01;
      }
    } else {
      if (isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
        return Config.darkSteelSwordSkullChance + Config.darkSteelSwordSkullLootingModifier * evt.getLootingLevel();
      } else {
        return Config.vanillaSwordSkullChance + Config.vanillaSwordSkullLootingModifier * evt.getLootingLevel();
      }
    }
  }

  private static void dropSkull(LivingDropsEvent evt, EntityPlayer player) {
    ItemStack skull = getSkullForEntity(evt.getEntityLiving());
    if (skull != null && !skull.isEmpty() && !containsDrop(evt, skull)) {
      evt.getDrops().add(Util.createEntityItem(player.world, skull, evt.getEntityLiving().posX, evt.getEntityLiving().posY, evt.getEntityLiving().posZ));
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

  public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
    return isEquipped(player) && getStoredPower(player) >= requiredPower;
  }

  private static int getStoredPower(EntityPlayer player) {
    return EnergyUpgradeManager.getEnergyStored(player.getHeldItemMainhand());
  }

}
