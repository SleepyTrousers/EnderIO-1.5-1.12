package crazypants.enderio.base.item.darksteel;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IElytraFlyingProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNMap;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IHasPlayerRenderer;
import crazypants.enderio.api.upgrades.IRenderUpgrade;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.PacketUpgradeState;
import crazypants.enderio.base.handler.darksteel.PacketUpgradeState.Type;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.integration.thaumcraft.GogglesOfRevealingUpgrade;
import crazypants.enderio.base.integration.thaumcraft.ThaumaturgeRobesUpgrade;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.elytra.ElytraUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.item.darksteel.upgrade.glider.GliderUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.nightvision.NightVisionUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.sound.SoundDetectorUpgrade;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.paint.PaintUtil.IWithPaintName;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.HelmetPainterTemplate;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.Prep;
import forestry.api.apiculture.IArmorApiarist;
import forestry.api.core.IArmorNaturalist;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IVisDiscountGear;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@InterfaceList({
    // @Interface(iface = "thaumcraft.api.items.IGoggles", modid = "thaumcraft"),
    @Interface(iface = "thaumcraft.api.items.IVisDiscountGear", modid = "thaumcraft"),
    // @Interface(iface = "thaumcraft.api.items.IRevealer", modid = "thaumcraft"),
    @Interface(iface = "forestry.api.apiculture.IArmorApiarist", modid = "forestry"),
    @Interface(iface = "forestry.api.core.IArmorNaturalist", modid = "forestry") })
public class ItemDarkSteelArmor extends ItemArmor implements ISpecialArmor, IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware, IHasPlayerRenderer,
    IWithPaintName, IElytraFlyingProvider, IArmorApiarist, IArmorNaturalist, IVisDiscountGear {
  // IGoggles, IRevealer, IVisDiscountGear {

  // ============================================================================================================
  // Item creation
  // ============================================================================================================

  public static ItemDarkSteelArmor createDarkSteelBoots(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.DARK_STEEL, modObject, EntityEquipmentSlot.FEET, 1);
  }

  public static ItemDarkSteelArmor createDarkSteelLeggings(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.DARK_STEEL, modObject, EntityEquipmentSlot.LEGS, 1);
  }

  public static ItemDarkSteelArmor createDarkSteelChestplate(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.DARK_STEEL, modObject, EntityEquipmentSlot.CHEST, 1);
  }

  public static ItemDarkSteelArmor createDarkSteelHelmet(@Nonnull IModObject modObject) {
    final ItemDarkSteelArmor helmet = new ItemDarkSteelArmor(EquipmentData.DARK_STEEL, modObject, EntityEquipmentSlot.HEAD, 1);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new HelmetPainterTemplate(helmet));
    return helmet;
  }

  public static ItemDarkSteelArmor createEndSteelBoots(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.END_STEEL, modObject, EntityEquipmentSlot.FEET, 2);
  }

  public static ItemDarkSteelArmor createEndSteelLeggings(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.END_STEEL, modObject, EntityEquipmentSlot.LEGS, 2);
  }

  public static ItemDarkSteelArmor createEndSteelChestplate(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.END_STEEL, modObject, EntityEquipmentSlot.CHEST, 2);
  }

  public static ItemDarkSteelArmor createEndSteelHelmet(@Nonnull IModObject modObject) {
    final ItemDarkSteelArmor helmet = new ItemDarkSteelArmor(EquipmentData.END_STEEL, modObject, EntityEquipmentSlot.HEAD, 2);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new HelmetPainterTemplate(helmet));
    return helmet;
  }

  // ============================================================================================================
  // Fields
  // ============================================================================================================

  /**
   * The amount of energy that is needed to mitigate one point of armor damage
   */
  private final int powerPerDamagePoint;
  private final @Nonnull EquipmentData data;


  // ============================================================================================================
  // Constructor
  // ============================================================================================================

  protected ItemDarkSteelArmor(@Nonnull EquipmentData data, @Nonnull IModObject modObject, @Nonnull EntityEquipmentSlot armorType, @Nonnull Integer tier) {
    super(data.getArmorMaterial(), 0, armorType);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    powerPerDamagePoint = Config.darkSteelPowerStorageBase / data.getArmorMaterial().getDurability(armorType);
    this.data = data;
  }

  // ============================================================================================================
  // Additional armor value calculation
  // ============================================================================================================

  protected @Nonnull ArmorMaterial getMaterial(@Nonnull ItemStack stack) {
    return EnergyUpgradeManager.getEnergyStored(stack) > 0 ? data.getArmorMaterialEmpowered() : getArmorMaterial();
  }

  @Override
  public int getItemEnchantability(@Nonnull ItemStack stack) {
    return getMaterial(stack).getEnchantability();
  }

  // ============================================================================================================
  // Creative menu
  // ============================================================================================================

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> par3List) {
    if (isInCreativeTab(tab)) {
      @Nonnull
      ItemStack is = new ItemStack(this);
      par3List.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.EMPOWERED_FOUR.addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);

      Iterator<IDarkSteelUpgrade> iter = DarkSteelRecipeManager.recipeIterator();
      while (iter.hasNext()) {
        IDarkSteelUpgrade upgrade = iter.next();
        if (!(upgrade instanceof EnergyUpgrade || upgrade instanceof GliderUpgrade || upgrade instanceof ElytraUpgrade) && upgrade.canAddToItem(is, this)) {
          upgrade.addToItem(is, this);
        }
      }

      if (GliderUpgrade.INSTANCE.canAddToItem(is, this)) {
        ItemStack is2 = is.copy();
        GliderUpgrade.INSTANCE.addToItem(is2, this);
        par3List.add(is2);
        if (ElytraUpgrade.INSTANCE.canAddToItem(is, this)) {
          ItemStack is3 = is.copy();
          ElytraUpgrade.INSTANCE.addToItem(is3, this);
          par3List.add(is3);
        }
        return;
      }

      par3List.add(is);
    }
  }

  // ============================================================================================================
  // Repairing
  // ============================================================================================================

  /**
   * Don't allow vanilla repairing
   */
  @Override
  public boolean getIsRepairable(@Nonnull ItemStack i1, @Nonnull ItemStack i2) {
    return false;
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    switch (armorType) {
    case HEAD:
      return 5;
    case CHEST:
      return 8;
    case LEGS:
      return 7;
    case FEET:
    default:
      return 4;
    }
  }

  @Override
  public boolean isItemForRepair(@Nonnull ItemStack right) {
    return OreDictionaryHelper.hasName(right, data.getRepairIngotOredict());
  }

  // ============================================================================================================
  // Tooltips
  // ============================================================================================================

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
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
    if (EnergyUpgradeManager.itemHasAnyPowerUpgrade(itemstack)) {
      list.addAll(Lang.DARK_STEEL_POWERED.getLines(TextFormatting.WHITE));
      if (armorType == EntityEquipmentSlot.FEET) {
        list.addAll(Lang.DARK_BOOTS_POWERED.getLines(TextFormatting.WHITE));
      }
    }
    DarkSteelRecipeManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public String getPaintName(@Nonnull ItemStack itemStack) {
    final NBTTagCompound subCompound = itemStack.getSubCompound("DSPAINT");
    if (subCompound != null) {
      ItemStack paintSource = new ItemStack(subCompound);
      if (Prep.isValid(paintSource)) {
        return paintSource.getDisplayName();
      }
    }
    return null;
  }

  // ============================================================================================================
  // Rendering
  // ============================================================================================================

  @Override
  public String getArmorTexture(@Nonnull ItemStack itemStack, @Nonnull Entity entity, @Nonnull EntityEquipmentSlot slot, @Nonnull String layer) {
    if (armorType == EntityEquipmentSlot.LEGS || (armorType == EntityEquipmentSlot.HEAD && !NightVisionUpgrade.INSTANCE.hasUpgrade(itemStack)
        && !SoundDetectorUpgrade.INSTANCE.hasUpgrade(itemStack))) {
      // LEGS and HELMET without faceplate
      return data.getTexture2();
    }
    // BOOTS, HELMET with faceplate, CHEST
    return data.getTexture1();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public ModelBiped getArmorModel(@Nonnull EntityLivingBase entityLiving, @Nonnull ItemStack itemStack, @Nonnull EntityEquipmentSlot armorSlot,
      @Nonnull ModelBiped _default) {
    if (armorType == EntityEquipmentSlot.HEAD && itemStack.getSubCompound("DSPAINT") != null) {
      // Don't render the armor model of the helmet if it is painted. The paint will be rendered by the PaintedHelmetLayer.
      return PaintedHelmetLayer.no_render;
    }
    return null;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IRenderUpgrade getRender() {
    return armorType == EntityEquipmentSlot.HEAD ? PaintedHelmetLayer.instance : PaintedHelmetLayer.not_an_helmet;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

  // ============================================================================================================
  // Applying armor
  // ============================================================================================================

  private static final NNMap<EntityEquipmentSlot, UUID> ARMOR_MODIFIERS = new NNMap.Brutal<>();
  static {
    ARMOR_MODIFIERS.put(EntityEquipmentSlot.FEET, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
    ARMOR_MODIFIERS.put(EntityEquipmentSlot.LEGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
    ARMOR_MODIFIERS.put(EntityEquipmentSlot.CHEST, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
    ARMOR_MODIFIERS.put(EntityEquipmentSlot.HEAD, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
  }

  @Override
  public @Nonnull Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot, @Nonnull ItemStack stack) {
    Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier> create();

    if (equipmentSlot == this.armorType) {
      ArmorMaterial armorMaterial = getMaterial(stack);
      multimap.put(SharedMonsterAttributes.ARMOR.getName(),
          new AttributeModifier(ARMOR_MODIFIERS.get(equipmentSlot), "Armor modifier", armorMaterial.getDamageReductionAmount(equipmentSlot), 0));
      multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(),
          new AttributeModifier(ARMOR_MODIFIERS.get(equipmentSlot), "Armor toughness", armorMaterial.getToughness(), 0));
    }

    return multimap;
  }

  // ============================================================================================================
  // ISpecialArmor
  // ============================================================================================================

  @Override
  public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
    double ratio = 0d; // percentage of damage that is removed before normal armor calculations are done. This is actually bad for armor with a high
                       // toughness...
    if (!source.isUnblockable()) {
      ArmorMaterial armorMaterial = getMaterial(armor);
      int damageReductionAmount = armorMaterial.getDamageReductionAmount(armorType) - ArmorMaterial.DIAMOND.getDamageReductionAmount(armorType);
      if (damageReductionAmount > 0) {
        // Reduce the damage by 5% for each point of armor we have more than diamond
        ratio = damageReductionAmount * .05d;
        // This is just to counter the maximum effective armor (80%) vanilla enforces for normal armor calculations
      }
    }
    return new ArmorProperties(0, ratio, Integer.MAX_VALUE);
  }

  @Override
  public int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armor, int slot) {
    // This is added to the UI display in addition to the normal value from SharedMonsterAttributes.ARMOR
    return 0;
  }

  @Override
  public void damageArmor(EntityLivingBase entity, @Nonnull ItemStack stack, DamageSource source, int damage, int slot) {
    if (entity != null) {
      stack.damageItem(damage, entity);
    }
  }

  // ============================================================================================================
  // Item damage mitigation when powered
  // ============================================================================================================

  @Override
  public void setDamage(@Nonnull ItemStack stack, int damageNew) {
    int damage = damageNew - getDamage(stack);

    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null && eu.getUpgrade().isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(damage * powerPerDamagePoint, false);
      eu.writeToItem(stack, this);
    } else {
      super.setDamage(stack, damageNew);
    }
  }

  // ============================================================================================================
  // Elytra flight upgrade
  // ============================================================================================================

  @Override
  public boolean isElytraFlying(@Nonnull EntityLivingBase entity, @Nonnull ItemStack itemstack, boolean shouldStop) {
    if (entity instanceof EntityPlayer && DarkSteelController.isElytraUpgradeEquipped(itemstack) && DarkSteelController.isElytraActive((EntityPlayer) entity)) {
      if (shouldStop && !entity.world.isRemote) {
        DarkSteelController.setActive((EntityPlayer) entity, Type.ELYTRA, false);
        PacketHandler.INSTANCE.sendToDimension(new PacketUpgradeState(Type.ELYTRA, false, entity.getEntityId()), entity.world.provider.getDimension());
      }
      return true;
    } else {
      return false;
    }
  }

  // ============================================================================================================
  // Dark Steel Upgrades
  // ============================================================================================================

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == armorType;
  }

  @Override
  public boolean hasUpgradeCallbacks(@Nonnull IDarkSteelUpgrade upgrade) {
    return upgrade == FORESTRY_FEET || upgrade == FORESTRY_LEGS || upgrade == FORESTRY_CHEST || upgrade == FORESTRY_HEAD || upgrade == FORESTRY_EYES
        || upgrade == ElytraUpgrade.INSTANCE || upgrade == GogglesOfRevealingUpgrade.INSTANCE || upgrade == ThaumaturgeRobesUpgrade.BOOTS
        || upgrade == ThaumaturgeRobesUpgrade.LEGS || upgrade == ThaumaturgeRobesUpgrade.CHEST;
  }

  // ============================================================================================================
  // THAUMCRAFT
  // ============================================================================================================

  boolean gogglesUgradeActive = true;

  // TODO: Mod Thaumcraft - Should we re add goggles upgrade?
  //
  // @Override
  // @Method(modid = "thaumcraft")
  // public boolean showNodes(ItemStack stack, EntityLivingBase player) {
  // if (stack.isEmpty() || !gogglesUgradeActive) {
  // return false;
  // }
  // return GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack);
  //
  // }
  //
  // @Override
  // @Method(modid = "thaumcraft")
  // public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
  // if (stack.isEmpty() || !gogglesUgradeActive) {
  // return false;
  // }
  // return GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack);
  // }
  //
  @Override
  @Method(modid = "thaumcraft")
  public int getVisDiscount(ItemStack stack, EntityPlayer player) {
    if (!stack.isEmpty()) {
      if (stack.getItem() == ModObject.itemDarkSteelBoots.getItemNN()) {
        return ThaumaturgeRobesUpgrade.BOOTS.hasUpgrade(stack) ? 2 : 0;
      }
      if (stack.getItem() == ModObject.itemDarkSteelLeggings.getItemNN()) {
        return ThaumaturgeRobesUpgrade.LEGS.hasUpgrade(stack) ? 3 : 0;
      }
      if (stack.getItem() == ModObject.itemDarkSteelChestplate.getItemNN()) {
        return ThaumaturgeRobesUpgrade.CHEST.hasUpgrade(stack) ? 3 : 0;
      }
      // if (stack.getItem() == ModObject.itemDarkSteelHelmet.getItemNN()) {
      // return GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack) ? 5 : 0;
      // }
    }
    return 0;
  }

  public boolean isGogglesUgradeActive() {
    return gogglesUgradeActive;
  }

  public void setGogglesUgradeActive(boolean gogglesUgradeActive) {
    this.gogglesUgradeActive = gogglesUgradeActive;
  }

  // ============================================================================================================
  // FORESTRY
  // ============================================================================================================

  @ObjectHolder("enderiointegrationforestry:apiarist_armor_feet")
  public static final IDarkSteelUpgrade FORESTRY_FEET = null;
  @ObjectHolder("enderiointegrationforestry:apiarist_armor_legs")
  public static final IDarkSteelUpgrade FORESTRY_LEGS = null;
  @ObjectHolder("enderiointegrationforestry:apiarist_armor_chest")
  public static final IDarkSteelUpgrade FORESTRY_CHEST = null;
  @ObjectHolder("enderiointegrationforestry:apiarist_armor_head")
  public static final IDarkSteelUpgrade FORESTRY_HEAD = null;
  @ObjectHolder("enderiointegrationforestry:naturalist_eye")
  public static final IDarkSteelUpgrade FORESTRY_EYES = null;

  @Override
  @Method(modid = "forestry")
  public boolean canSeePollination(@Nonnull EntityPlayer player, @Nonnull ItemStack armor, boolean doSee) {
    if (armor.getItem() != ModObject.itemDarkSteelHelmet.getItemNN()) {
      return false;
    }
    return FORESTRY_EYES != null && FORESTRY_EYES.hasUpgrade(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
  }

  @Override
  @Method(modid = "forestry")
  public boolean protectEntity(@Nonnull EntityLivingBase entity, @Nonnull ItemStack armor, @Nullable String cause, boolean doProtect) {
    return (FORESTRY_HEAD != null && FORESTRY_HEAD.hasUpgrade(armor)) || (FORESTRY_CHEST != null && FORESTRY_CHEST.hasUpgrade(armor))
        || (FORESTRY_FEET != null && FORESTRY_FEET.hasUpgrade(armor)) || (FORESTRY_LEGS != null && FORESTRY_LEGS.hasUpgrade(armor));
  }

  @Override
  public int getTier(){
    return data.getTier();
  }
}
