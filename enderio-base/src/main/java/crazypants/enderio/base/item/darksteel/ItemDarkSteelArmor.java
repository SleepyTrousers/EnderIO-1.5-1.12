package crazypants.enderio.base.item.darksteel;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.transform.EnderCoreMethods.IElytraFlyingProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNMap;
import com.enderio.core.common.util.OreDictionaryHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.api.upgrades.IHasPlayerRenderer;
import crazypants.enderio.api.upgrades.IRenderUpgrade;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.handler.darksteel.DarkSteelController;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.PacketUpgradeState;
import crazypants.enderio.base.handler.darksteel.PacketUpgradeState.Type;
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
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.base.paint.PaintUtil.IWithPaintName;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.painter.HelmetPainterTemplate;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.Prep;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;

@InterfaceList({ @Interface(iface = "thaumcraft.api.items.IGoggles", modid = "thaumcraft"),
    @Interface(iface = "thaumcraft.api.items.IVisDiscountGear", modid = "thaumcraft"),
    @Interface(iface = "thaumcraft.api.items.IRevealer", modid = "thaumcraft") })
public class ItemDarkSteelArmor extends ItemArmor implements ISpecialArmor, IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware, IHasPlayerRenderer,
    IWithPaintName, IElytraFlyingProvider, IVisDiscountGear, IGoggles, IRevealer {

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

  public static ItemDarkSteelArmor createStellarAlloyBoots(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.STELLAR_ALLOY, modObject, EntityEquipmentSlot.FEET, 2);
  }

  public static ItemDarkSteelArmor createStellarAlloyLeggings(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.STELLAR_ALLOY, modObject, EntityEquipmentSlot.LEGS, 2);
  }

  public static ItemDarkSteelArmor createStellarAlloyChestplate(@Nonnull IModObject modObject) {
    return new ItemDarkSteelArmor(EquipmentData.STELLAR_ALLOY, modObject, EntityEquipmentSlot.CHEST, 2);
  }

  public static ItemDarkSteelArmor createStellarAlloyHelmet(@Nonnull IModObject modObject) {
    final ItemDarkSteelArmor helmet = new ItemDarkSteelArmor(EquipmentData.STELLAR_ALLOY, modObject, EntityEquipmentSlot.HEAD, 2);
    MachineRecipeRegistry.instance.registerRecipe(MachineRecipeRegistry.PAINTER, new HelmetPainterTemplate(helmet));
    return helmet;
  }

  // ============================================================================================================
  // Fields
  // ============================================================================================================

  /**
   * The amount of energy that is needed to mitigate one point of armor damage
   */
  private final @Nonnull IEquipmentData data;

  // ============================================================================================================
  // Constructor
  // ============================================================================================================

  protected ItemDarkSteelArmor(@Nonnull IEquipmentData data, @Nonnull IModObject modObject, @Nonnull EntityEquipmentSlot armorType, @Nonnull Integer tier) {
    super(data.getArmorMaterial(), 0, armorType);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    this.data = data;
  }

  // ============================================================================================================
  // Additional armor value calculation
  // ============================================================================================================

  protected int getPowerPerDamagePoint(@Nonnull ItemStack stack) {
    EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
    if (eu != null) {
      return eu.getCapacity() / data.getArmorMaterial().getDurability(armorType);
    } else {
      return 1;
    }
  }

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
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> par3List) {
    if (isInCreativeTab(tab)) {
      @Nonnull
      ItemStack is = new ItemStack(this);
      par3List.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(getMaxEmpoweredLevel(is)).addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);

      for (int i = 0; i < 2; i++) { // upgrades may not be in the best order, so try adding them a couple of times
        Iterator<IDarkSteelUpgrade> iter = DarkSteelRecipeManager.recipeIterator();
        while (iter.hasNext()) {
          IDarkSteelUpgrade upgrade = iter.next();
          if (!(upgrade instanceof EnergyUpgrade || upgrade instanceof GliderUpgrade || upgrade instanceof ElytraUpgrade) && upgrade.canAddToItem(is, this)) {
            upgrade.addToItem(is, this);
          }
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
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    if (!SpecialTooltipHandler.showDurability(flag)) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    NNList.addIf(list, EnergyUpgradeManager.getStoredEnergyString(itemstack));
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
    ItemStack paintSource = PaintUtil.getPaintSource(itemStack);
    if (Prep.isValid(paintSource)) {
      return paintSource.getDisplayName();
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
    if (armorType == EntityEquipmentSlot.HEAD && (PaintUtil.hasPaintSource(itemStack) || itemStack.getSubCompound("DSPAINT") != null)) { // TODO 1.13 remove
                                                                                                                                         // DSPAINT
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
    if (damage > 0 && eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(damage * getPowerPerDamagePoint(stack), false);
      eu.writeToItem();
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
    return upgrade == ElytraUpgrade.INSTANCE || upgrade == GogglesOfRevealingUpgrade.INSTANCE || upgrade == ThaumaturgeRobesUpgrade.BOOTS
        || upgrade == ThaumaturgeRobesUpgrade.LEGS || upgrade == ThaumaturgeRobesUpgrade.CHEST;
  }

  // ============================================================================================================
  // THAUMCRAFT
  // ============================================================================================================

  @Override
  @Method(modid = "thaumcraft")
  public boolean showNodes(ItemStack stack, EntityLivingBase player) {
    if (stack.isEmpty() || !(player instanceof EntityPlayer) || DarkSteelController.isActive((EntityPlayer) player, Type.GOGGLES)) {
      return false;
    }
    return GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack);

  }

  @Override
  @Method(modid = "thaumcraft")
  public boolean showIngamePopups(ItemStack stack, EntityLivingBase player) {
    if (stack.isEmpty() || !(player instanceof EntityPlayer) || DarkSteelController.isActive((EntityPlayer) player, Type.GOGGLES)) {
      return false;
    }
    return GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack);
  }

  @Override
  @Method(modid = "thaumcraft")
  public int getVisDiscount(ItemStack stack, EntityPlayer player) {
    if (stack == null) {
      return -100; // Garbage in, garbage out
    }
    return ThaumaturgeRobesUpgrade.BOOTS.hasUpgrade(stack) ? 2
        : ThaumaturgeRobesUpgrade.LEGS.hasUpgrade(stack) ? 3
            : ThaumaturgeRobesUpgrade.CHEST.hasUpgrade(stack) ? 3 : GogglesOfRevealingUpgrade.INSTANCE.hasUpgrade(stack) ? 5 : 0;
  }

  // ============================================================================================================
  // FORESTRY
  // ============================================================================================================

  // ============================================================================================================

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return data;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_ARMOR_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_ARMOR_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_ARMOR_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_ARMOR_ABSORPTION_RATIO;
  }

}
