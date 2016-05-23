package crazypants.enderio.item.darksteel;

import java.util.Iterator;
import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;
import com.enderio.core.common.util.ItemUtil;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.IDarkSteelUpgrade;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@InterfaceList({
    @Interface(iface = "thaumcraft.api.items.IGoggles", modid = "Thaumcraft"),
    @Interface(iface = "thaumcraft.api.items.IVisDiscountGear", modid = "Thaumcraft"),
    @Interface(iface = "thaumcraft.api.items.IRevealer", modid = "Thaumcraft")
})
public class ItemDarkSteelArmor extends ItemArmor implements IEnergyContainerItem, ISpecialArmor, IAdvancedTooltipProvider, IDarkSteelItem, 
    IOverlayRenderAware { //, IGoggles, IRevealer, IVisDiscountGear, //TODO: 1.9 Thaumcraft


  public static final ArmorMaterial MATERIAL = createMaterial();

  private static ArmorMaterial createMaterial() {
    //TODO: 1.9, there is currently a PR to fix EnumHelper.addArmorMaterial, once done can go back to using that    
    Class<?>[] params = new Class<?>[] {String.class, int.class, int[].class, int.class, SoundEvent.class, float.class};
    return EnumHelper.addEnum(ArmorMaterial.class, "darkSteel", params, "darkSteel", 35,new int[] { 2, 6, 5, 2 }, 15, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f); 
  }
                                                                                      
  public static final int[] CAPACITY = new int[] { Config.darkSteelPowerStorageBase, Config.darkSteelPowerStorageBase, Config.darkSteelPowerStorageBase * 2,
      Config.darkSteelPowerStorageBase * 2 };

  public static final int[] RF_PER_DAMAGE_POINT = new int[] { Config.darkSteelPowerStorageBase, Config.darkSteelPowerStorageBase,
      Config.darkSteelPowerStorageBase * 2,
      Config.darkSteelPowerStorageBase * 2 };

  public static final String[] NAMES = new String[] { "boots", "leggings", "chestplate", "helmet"};

  boolean gogglesUgradeActive = true;

  static {    
    MinecraftForge.EVENT_BUS.register(DarkSteelController.instance);
    MinecraftForge.EVENT_BUS.register(DarkSteelRecipeManager.instance);
  }

  //TODO: 1.9 I think this is borked
  public static ItemDarkSteelArmor forArmorType(int armorType) {
    switch (armorType) {
    case 0:
      return DarkSteelItems.itemDarkSteelBoots;
    case 1:
      return DarkSteelItems.itemDarkSteelLeggings;
    case 2:
      return DarkSteelItems.itemDarkSteelChestplate;
    case 3:
      return DarkSteelItems.itemDarkSteelHelmet;
    }
    return null;
  }
  
  public static ItemDarkSteelArmor forArmorType(EntityEquipmentSlot armorType) {
    switch (armorType) {
    case HEAD:
      return DarkSteelItems.itemDarkSteelHelmet;
    case CHEST:
      return DarkSteelItems.itemDarkSteelChestplate;
    case LEGS:
      return DarkSteelItems.itemDarkSteelLeggings;
    case FEET:
      return DarkSteelItems.itemDarkSteelBoots;
    default:
      break;
    }
    return null;
  }

  public static int getPoweredProtectionIncrease(int armorType) {
    switch (armorType) {
    case 0:
      return 1;
    case 1:
      return 2;
    case 2:
    case 3:
      return 1;
    }
    return 0;
  }

  public static ItemDarkSteelArmor create(EntityEquipmentSlot armorType) {
    ItemDarkSteelArmor res = new ItemDarkSteelArmor(armorType);
    res.init();
    return res;
  }

  private final int powerPerDamagePoint;

  protected ItemDarkSteelArmor(EntityEquipmentSlot armorType) {
    super(MATERIAL, 0, armorType);
    setCreativeTab(EnderIOTab.tabEnderIO);

    String str = "darkSteel_" + NAMES[armorType.getIndex()];
    setUnlocalizedName(str);
    setRegistryName(str);

    powerPerDamagePoint = Config.darkSteelPowerStorageBase / MATERIAL.getDurability(armorType);
  }

  protected void init() {    
    GameRegistry.register(this);
  }
  
  @Override
  public String getItemName() {
    String regName = getUnlocalizedName();
    regName = regName.substring(5, regName.length());
    return regName;
  }
  
  @Override
  @SideOnly(Side.CLIENT)  
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
    EnergyUpgrade.setPowerFull(is);
    
    Iterator<IDarkSteelUpgrade> iter = DarkSteelRecipeManager.instance.recipeIterator();
    while (iter.hasNext()) {
      IDarkSteelUpgrade upgrade = iter.next();
      if (!(upgrade instanceof EnergyUpgrade) && upgrade.canAddToItem(is)) {
        upgrade.writeToItem(is);
      }
    }
    
    par3List.add(is);
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
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    DarkSteelRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    DarkSteelRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
    if(!Config.addDurabilityTootip) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgrade.getStoredEnergyString(itemstack);
    if(str != null) {
      list.add(str);
    }
    if(EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(TextFormatting.WHITE + EnderIO.lang.localize("item.darkSteel_armor.tooltip.line1"));
      list.add(TextFormatting.WHITE + EnderIO.lang.localize("item.darkSteel_armor.tooltip.line2"));
      if(itemstack.getItem() == DarkSteelItems.itemDarkSteelBoots) {
        list.add(TextFormatting.WHITE + EnderIO.lang.localize("item.darkSteel_boots.tooltip.line1"));
        list.add(TextFormatting.WHITE + EnderIO.lang.localize("item.darkSteel_boots.tooltip.line2"));
      }
    }
    DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
    
  }

  @Override
  public String getArmorTexture(ItemStack itemStack, Entity entity, EntityEquipmentSlot slot, String layer) {
    if(armorType == EntityEquipmentSlot.LEGS) {
      return "enderio:textures/models/armor/darkSteel_layer_2.png";
    }
    return "enderio:textures/models/armor/darkSteel_layer_1.png";
  }

  public ItemStack createItemStack() {
    return new ItemStack(this);
  }

  @Override
  public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
    if(source.isUnblockable()) {
      return new ArmorProperties(0, 0, armor.getMaxDamage() + 1 - armor.getItemDamage());
    }
    double damageRatio = damageReduceAmount + (getEnergyStored(armor) > 0 ? getPoweredProtectionIncrease(3 - slot) : 0);
    damageRatio /= 25D;
    ArmorProperties ap = new ArmorProperties(0, damageRatio, armor.getMaxDamage() + 1 - armor.getItemDamage());
    return ap;
  }

  @Override
  public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
    int powerBonus = getEnergyStored(armor) > 0 ? getPoweredProtectionIncrease(3 - slot) : 0;    
    return getArmorMaterial().getDamageReductionAmount(armorType) + powerBonus;
  }

  @Override
  public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {

    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(stack);
    if(eu != null && eu.isAbsorbDamageWithPower(stack) && eu.getEnergy() > 0) {
      eu.extractEnergy(damage * powerPerDamagePoint, false);

    } else {
      stack.damageItem(damage, entity);
    }
    if(eu != null) {
      eu.writeToItem(stack);
    }
  }

  @Override
  public boolean getIsRepairable(ItemStack i1, ItemStack i2) {
    return false;
  }

  @Override
  public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    return EnergyUpgrade.receiveEnergy(container, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    return EnergyUpgrade.extractEnergy(container, maxExtract, simulate);
  }

  @Override
  public int getEnergyStored(ItemStack container) {
    return EnergyUpgrade.getEnergyStored(container);
  }

  @Override
  public int getMaxEnergyStored(ItemStack container) {
    return EnergyUpgrade.getMaxEnergyStored(container);
  }

  //TODO: 1.9 Thaumcraft

//  @Override
//  @Method(modid = "Thaumcraft")
//  public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
//    if(itemstack == null || itemstack.getItem() == null || !gogglesUgradeActive) {
//      return false;
//    }
//    return GogglesOfRevealingUpgrade.loadFromItem(itemstack) != null;
//
//  }
//
//  @Override
//  @Method(modid = "Thaumcraft")
//  public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
//    if(itemstack == null || itemstack.getItem() == null || !gogglesUgradeActive) {
//      return false;
//    }
//    return GogglesOfRevealingUpgrade.loadFromItem(itemstack) != null;
//  }
//
//  @Override
//  @Method(modid = "Thaumcraft")
//  public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
//    if(stack == null || stack.getItem() != DarkSteelItems.itemDarkSteelHelmet) {
//      return 0;
//    }
//    return GogglesOfRevealingUpgrade.isUpgradeEquipped(player) ? 5 : 0;
//  }

  public boolean isGogglesUgradeActive() {
    return gogglesUgradeActive;
  }

  public void setGogglesUgradeActive(boolean gogglesUgradeActive) {
    this.gogglesUgradeActive = gogglesUgradeActive;
  }
  
  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable.render(stack, xPosition, yPosition);
  }

}
