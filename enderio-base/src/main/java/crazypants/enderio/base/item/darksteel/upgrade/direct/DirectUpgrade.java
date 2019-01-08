package crazypants.enderio.base.item.darksteel.upgrade.direct;

import java.util.Iterator;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.util.Prep;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class DirectUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "direct";
  private static final Random random = new Random();

  public static final @Nonnull DirectUpgrade INSTANCE = new DirectUpgrade();

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
  }

  public DirectUpgrade() {
    super(UPGRADE_NAME, 0, "enderio.darksteel.upgrade." + UPGRADE_NAME, Alloy.VIBRANT_ALLOY.getStackBlock(), DarkSteelConfig.directCost);
  }

  @Override
  public boolean canAddToItem(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
    return item.isForSlot(EntityEquipmentSlot.MAINHAND) && item.isBlockBreakingTool() && EnergyUpgradeManager.itemHasAnyPowerUpgrade(stack)
        && !hasAnyUpgradeVariant(stack);
  }

  @Override
  public boolean canOtherBeRemoved(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item, @Nonnull IDarkSteelUpgrade other) {
    return !EnergyUpgradeManager.isLowestPowerUpgrade(other);
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void blockDropEvent(BlockEvent.HarvestDropsEvent event) {
    if (event.getHarvester() == null) {
      return;
    }

    for (EnumHand hand : EnumHand.values()) {
      ItemStack stack = event.getHarvester().getHeldItem(NullHelper.notnullJ(hand, "EnumHand.values()"));

      if (INSTANCE.hasAnyUpgradeVariant(stack) && EnergyUpgradeManager.getEnergyStored(stack) > 0) {
        EnergyUpgradeManager.extractEnergy(stack, doDirect(event) * DarkSteelConfig.directEnergyCost.get(), false);
        return;
      }
    }
  }

  // also used by TraitPickup
  public static int doDirect(BlockEvent.HarvestDropsEvent event) {
    int count = 0;
    final EntityPlayer player = NullHelper.notnullF(event.getHarvester(), "BlockEvent.HarvestDropsEvent.getHarvester()");
    final NNList<ItemStack> remainsList = new NNList<>();
    for (Iterator<ItemStack> iterator = event.getDrops().iterator(); iterator.hasNext();) {
      ItemStack next = NullHelper.notnullF(iterator.next(), "null stack in HarvestDropsEvent");
      if (random.nextFloat() < event.getDropChance()) {
        ItemStack remains = fakeItemPickup(player, next.copy()); // TODO use ItemUtil version after we update endercore dep
        if (remains.getCount() < next.getCount()) {
          count++;
          iterator.remove();
          if (Prep.isValid(remains)) {
            remainsList.add(remains);
          }
        }
      } else {
        iterator.remove();
      }
    }
    event.getDrops().addAll(remainsList);
    event.setDropChance(1); // we already implemented the drop chance
    return count;
  }

  // TODO use ItemUtil version after we update endercore dep
  public static @Nonnull ItemStack fakeItemPickup(@Nonnull EntityPlayer player, @Nonnull ItemStack itemstack) {
    if (!player.world.isRemote) {
      EntityItem entityItem = new EntityItem(player.world, player.posX, player.posY, player.posZ, itemstack);
      entityItem.onCollideWithPlayer(player);
      if (entityItem.isDead) {
        return Prep.getEmpty();
      } else {
        entityItem.setDead();
        return entityItem.getItem();
      }
    }
    return itemstack;
  }

}
