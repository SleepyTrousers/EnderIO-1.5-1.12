package crazypants.enderio.base.item.darksteel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.interfaces.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.config.config.TeleportConfig;
import crazypants.enderio.base.handler.darksteel.DarkSteelTooltipManager;
import crazypants.enderio.base.handler.darksteel.SwordHandler;
import crazypants.enderio.base.item.darksteel.attributes.DarkSteelAttributeModifiers;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.direct.DirectUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.item.darksteel.upgrade.travel.TravelUpgrade;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.base.teleport.TravelController;
import info.loenwind.autoconfig.factory.IValue;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemDarkSteelSword extends ItemSword implements IAdvancedTooltipProvider, IDarkSteelItem, IItemOfTravel, IOverlayRenderAware {

  public static ItemDarkSteelSword createEndSteel(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemDarkSteelSword res = new ItemDarkSteelSword(modObject, EquipmentData.END_STEEL);
    return res;
  }

  public static ItemDarkSteelSword createDarkSteel(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemDarkSteelSword res = new ItemDarkSteelSword(modObject, EquipmentData.DARK_STEEL);
    return res;
  }

  public static ItemDarkSteelSword createStellarAlloy(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemDarkSteelSword res = new ItemDarkSteelSword(modObject, EquipmentData.STELLAR_ALLOY);
    return res;
  }

  private long lastBlickTick = -1;
  private final @Nonnull IEquipmentData data;

  public ItemDarkSteelSword(@Nonnull IModObject modObject, @Nonnull IEquipmentData data) {
    super(data.getToolMaterial());
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    this.data = data;
  }

  protected int getPowerPerDamagePoint(@Nonnull ItemStack stack) {
    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null) {
      return eu.getCapacity() / data.getToolMaterial().getMaxUses();
    } else {
      return 1;
    }
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      if (EnergyUpgrade.UPGRADES.get(4).canAddToItem(is, this)) {
        EnergyUpgrade.UPGRADES.get(4).addToItem(is, this);
      }
      EnergyUpgradeManager.setPowerFull(is, this);
      TravelUpgrade.INSTANCE.addToItem(is, this);
      DirectUpgrade.INSTANCE.addToItem(is, this);
      list.add(is);
    }
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 3;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, data.getRepairIngotOredict());
  }

  @Override
  @Nonnull
  public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack stack) {
    Multimap<String, AttributeModifier> res = super.getItemAttributeModifiers(equipmentSlot);
    if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
      if (DarkSteelConfig.darkSteelSwordPowerUsePerHit.get() <= 0
          || EnergyUpgradeManager.getEnergyStored(stack) >= DarkSteelConfig.darkSteelSwordPowerUsePerHit.get()) {
        EnergyUpgrade energyUpgrade = EnergyUpgrade.loadAnyFromItem(stack);
        int level = energyUpgrade.getLevel();
        res.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), DarkSteelAttributeModifiers.getAttackDamage(level));
        res.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), DarkSteelAttributeModifiers.getAttackSpeed(level));
      }
    }
    return res;
  }

  @Override
  public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull EntityLivingBase entity, @Nonnull EntityLivingBase playerEntity) {

    if (playerEntity instanceof EntityPlayer) {

      EntityPlayer player = (EntityPlayer) playerEntity;

      // Durability damage
      EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
      if (eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
        eu.extractEnergy(getPowerPerDamagePoint(stack), false);

      } else {
        super.hitEntity(stack, entity, playerEntity);
      }

      // sword hit
      if (eu != null) {
        eu.writeToItem();

        if (eu.getEnergy() >= DarkSteelConfig.darkSteelSwordPowerUsePerHit.get()) {
          extractInternal(player.getHeldItemMainhand(), DarkSteelConfig.darkSteelSwordPowerUsePerHit);
          entity.getEntityData().setBoolean(SwordHandler.HIT_BY_DARK_STEEL_SWORD, true);
        }

      }

    }
    return true;
  }

  @Override
  public int getEnergyStored(@Nonnull ItemStack container) {
    return EnergyUpgradeManager.getEnergyStored(container);
  }

  @Override
  public boolean getIsRepairable(@Nonnull ItemStack i1, @Nonnull ItemStack i2) {
    return false;
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelTooltipManager.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    if (!SpecialTooltipHandler.showDurability(flag)) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    DarkSteelTooltipManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public boolean isActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
    return isTravelUpgradeActive(ep, equipped);
  }

  @Override
  public void extractInternal(@Nonnull ItemStack equipped, int power) {
    EnergyUpgradeManager.extractEnergy(equipped, this, power, false);
  }

  private boolean isTravelUpgradeActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
    return ep.isSneaking() && TravelUpgrade.INSTANCE.hasUpgrade(equipped);
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    if (hand == EnumHand.MAIN_HAND) {
      ItemStack stack = player.getHeldItem(hand);
      if (isTravelUpgradeActive(player, stack)) {
        if (world.isRemote) {
          if (TravelController.activateTravelAccessable(stack, hand, world, player, TravelSource.STAFF)) {
            player.swingArm(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
          }
        }

        long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
        if (ticksSinceBlink < 0) {
          lastBlickTick = -1;
        }
        if (TeleportConfig.enableBlink.get() && world.isRemote && ticksSinceBlink >= TeleportConfig.blinkDelay.get()) {
          if (TravelController.doBlink(stack, hand, player)) {
            player.swingArm(hand);
            lastBlickTick = EnderIO.proxy.getTickCount();
          }
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
      }
    }
    if (player.isSneaking()) {
      if (!world.isRemote) {
        openUpgradeGui(player, hand);
      }
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean shouldCauseReequipAnimation(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged) {
    return slotChanged || oldStack.getItem() != newStack.getItem();
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.MAINHAND;
  }

  @Override
  public boolean hasUpgradeCallbacks(@Nonnull IDarkSteelUpgrade upgrade) {
    return upgrade == TravelUpgrade.INSTANCE;
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return data;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SWORD_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SWORD_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SWORD_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SWORD_ABSORPTION_RATIO;
  }

  @Override
  public void extractInternal(@Nonnull ItemStack equipped, IValue<Integer> power) {
    EnergyUpgradeManager.extractEnergy(equipped, this, power, false);
  }

}
