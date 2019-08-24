package crazypants.enderio.base.item.darksteel;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.interfaces.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.handler.darksteel.DarkSteelTooltipManager;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.anvil.AnvilUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDarkSteelShield extends ItemShield
    implements IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware, IHaveRenderers, AnvilUpgrade.INoAnvilUpgrade {

  private static final String DOT_COM = ".name";

  public static ItemDarkSteelShield createDarkSteel(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemDarkSteelShield(EquipmentData.DARK_STEEL, modObject);
  }

  public static ItemDarkSteelShield createEndSteel(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemDarkSteelShield(EquipmentData.END_STEEL, modObject);
  }

  public static ItemDarkSteelShield createStelar(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemDarkSteelShield(EquipmentData.STELLAR_ALLOY, modObject);
  }

  private final @Nonnull IEquipmentData data;

  protected ItemDarkSteelShield(@Nonnull IEquipmentData data, @Nonnull IModObject modobject) {
    this.setCreativeTab(EnderIOTab.tabEnderIOItems);
    this.setMaxDamage(data.getShieldDurability());
    this.data = data;
    modobject.apply(this);
  }

  @Override
  public @Nonnull String getItemStackDisplayName(@Nonnull ItemStack stack) {
    if (stack.getSubCompound("BlockEntityTag") != null) {
      EnumDyeColor enumdyecolor = TileEntityBanner.getColor(stack);
      return EnderIO.lang.localizeExact(getUnlocalizedName(stack) + "." + enumdyecolor.getUnlocalizedName() + DOT_COM);
    } else {
      return EnderIO.lang.localizeExact(getUnlocalizedName(stack) + DOT_COM);
    }
  }

  @Override
  public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
    return false;
  }

  @Override
  @Nonnull
  public IEquipmentData getEquipmentData() {
    return data;
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 6;
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, data.getRepairIngotOredict());
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);
      list.add(is);
    }
  }

  @Override
  public void setDamage(@Nonnull ItemStack stack, int damageNew) {
    int damage = damageNew - getDamage(stack);

    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (damage > 0 && eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(damage * getPowerPerDamagePoint(stack), false);
      eu.writeToItem();
    } else {
      super.setDamage(stack, damageNew);
    }
  }

  protected int getPowerPerDamagePoint(@Nonnull ItemStack stack) {
    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null) {
      return eu.getCapacity() / data.getShieldDurability();
    } else {
      return 1;
    }
  }

  protected @Nonnull ArmorMaterial getMaterial(@Nonnull ItemStack stack) {
    return EnergyUpgradeManager.getEnergyStored(stack) > 0 ? data.getArmorMaterialEmpowered() : data.getArmorMaterial();
  }

  @Override
  public int getItemEnchantability(@Nonnull ItemStack stack) {
    return getMaterial(stack).getEnchantability();
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
    list.add(ItemUtil.getDurabilityString(itemstack));
    if (!SpecialTooltipHandler.showDurability(flag)) {
      // doesn't do durability for this kind of item
    }
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    DarkSteelTooltipManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.OFFHAND;
  }

  @Override
  @Nonnull
  public ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SHIELD_ENERGY_BUFFER;
  }

  @Override
  @Nonnull
  public ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SHIELD_ENERGY_INPUT;
  }

  @Override
  @Nonnull
  public ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SHIELD_ENERGY_USE;
  }

  @Override
  @Nonnull
  public ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_SHIELD_ABSORPTION_RATIO;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    ClientUtil.registerDefaultItemRenderer(modObject);
    setTileEntityItemStackRenderer(DarkShieldRenderer.INSTANCE);
  }

  @Override
  public boolean isShield(@Nonnull ItemStack stack, @Nullable EntityLivingBase entity) {
    return true;
  }

  private static final @Nonnull UUID uuid1 = UUID.nameUUIDFromBytes("ItemDarkSteelShield".getBytes()),
      uuid2 = UUID.nameUUIDFromBytes("ItemDarkSteelShield2".getBytes());

  @Override
  public @Nonnull Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack stack) {
    Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier> create();

    if (equipmentSlot == EntityEquipmentSlot.MAINHAND || equipmentSlot == EntityEquipmentSlot.OFFHAND) {
      ArmorMaterial armorMaterial = getMaterial(stack);
      multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(uuid1, "Shield toughness", armorMaterial.getToughness(), 0));
      multimap.put(EntityLivingBase.SWIM_SPEED.getName(), new AttributeModifier(uuid2, "Shield swimming", -0.5, Constants.AttributeModifierOperation.MULTIPLY));

      // see crazypants.enderio.base.item.darksteel.upgrade.DarkSteelUpgradeMixin.getAttributeModifiers(EntityEquipmentSlot, ItemStack)
      for (IDarkSteelUpgrade upgrade : UpgradeRegistry.getUpgrades()) {
        if (upgrade.hasUpgrade(stack)) {
          upgrade.addAttributeModifiers(equipmentSlot, stack, multimap);
        }
      }
    }

    return multimap;
  }

}
