package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.material.Alloy;
import crazypants.util.ItemUtil;
import crazypants.util.Lang;

public class ItemDarkSteelArmor extends ItemArmor implements IEnergyContainerItem, ISpecialArmor, IAdvancedTooltipProvider, IDarkSteelItem {

  public static final ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("darkSteel", 35, new int[] { 2, 6, 5, 2 }, 15);

  public static final int[] CAPACITY = new int[] { Config.darkSteelPowerStorageBase, Config.darkSteelPowerStorageBase, Config.darkSteelPowerStorageBase * 2,
      Config.darkSteelPowerStorageBase * 2 };

  public static final int[] RF_PER_DAMAGE_POINT = new int[] { Config.darkSteelPowerStorageBase, Config.darkSteelPowerStorageBase,
      Config.darkSteelPowerStorageBase * 2,
      Config.darkSteelPowerStorageBase * 2 };

  public static final String[] NAMES = new String[] { "helmet", "chestplate", "leggings", "boots" };

  static {
    FMLCommonHandler.instance().bus().register(DarkSteelController.instance);
    MinecraftForge.EVENT_BUS.register(DarkSteelRecipeManager.instance);
  }

  public static ItemDarkSteelArmor forArmorType(int armorType) {
    switch (armorType) {
    case 0:
      return EnderIO.itemDarkSteelHelmet;
    case 1:
      return EnderIO.itemDarkSteelChestplate;
    case 2:
      return EnderIO.itemDarkSteelLeggings;
    case 3:
      return EnderIO.itemDarkSteelBoots;
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

  public static ItemDarkSteelArmor create(int armorType) {
    ItemDarkSteelArmor res = new ItemDarkSteelArmor(armorType);
    res.init();
    return res;
  }

  private int powerPerDamagePoint;

  protected ItemDarkSteelArmor(int armorType) {
    super(MATERIAL, 0, armorType);
    setCreativeTab(EnderIOTab.tabEnderIO);

    String str = "darkSteel_" + NAMES[armorType];
    setUnlocalizedName(str);
    setTextureName("enderIO:" + str);

    powerPerDamagePoint = Config.darkSteelPowerStorageBase / MATERIAL.getDurability(armorType);
  }

  protected void init() {
    GameRegistry.registerItem(this, getUnlocalizedName());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
    EnergyUpgrade.setPowerFull(is);
    if(armorType == 2) {
      SpeedUpgrade.SPEED_THREE.writeToItem(is);
    } else if(armorType == 3) {
      JumpUpgrade.JUMP_THREE.writeToItem(is);
    }
    
    par3List.add(is);
  }

  @Override
  public int getIngotsRequiredForFullRepair() {
    switch (armorType) {
    case 0:
      return 5;//EnderIO.itemDarkSteelHelmet;
    case 1:
      return 8;//EnderIO.itemDarkSteelChestplate;
    case 2:
      return 7;//EnderIO.itemDarkSteelLeggings;
    case 3:
      return 4;//EnderIO.itemDarkSteelBoots;
    }
    return 4;
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    DarkSteelRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    DarkSteelRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    list.add(ItemUtil.getDurabilityString(itemstack));
    String str = EnergyUpgrade.getStoredEnergyString(itemstack);
    if(str != null) {
      list.add(str);
    }
    if(EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(EnumChatFormatting.WHITE + Lang.localize("item.darkSteel_armor.tooltip.line1"));
      list.add(EnumChatFormatting.WHITE + Lang.localize("item.darkSteel_armor.tooltip.line2")); 
    }
    DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public boolean isDamaged(ItemStack stack) {
    return false;
  }

  @Override
  public String getArmorTexture(ItemStack itemStack, Entity entity, int slot, String layer) {
    if(armorType == 2) {
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
    ItemDarkSteelArmor arm = forArmorType(3 - slot);
    int powerBonus = getEnergyStored(armor) > 0 ? getPoweredProtectionIncrease(3 - slot) : 0;
    return arm.getArmorMaterial().getDamageReductionAmount(3 - slot) + powerBonus;
  }

  @Override
  public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {

    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(stack);
    if(eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(damage * powerPerDamagePoint, false);

    } else {
      damage = stack.getItemDamage() + damage;
      if(damage >= getMaxDamage()) {
        stack.stackSize = 0;
      }
      stack.setItemDamage(damage);
    }
    if(eu != null) {
      eu.setAbsorbDamageWithPower(!eu.isAbsorbDamageWithPower());
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

  //Idea from Mekanism
  //  @ForgeSubscribe
  //  public void onLivingSpecialSpawn(LivingSpawnEvent event)
  //  {
  //    int chance = event.world.rand.nextInt(100);
  //    int armorType = event.world.rand.nextInt(4);
  //
  //    if(chance < 3)
  //    {
  //      if(event.entityLiving instanceof EntityZombie || event.entityLiving instanceof EntitySkeleton)
  //      {
  //        int sword = event.world.rand.nextInt(100);
  //        int helmet = event.world.rand.nextInt(100);
  //        int chestplate = event.world.rand.nextInt(100);
  //        int leggings = event.world.rand.nextInt(100);
  //        int boots = event.world.rand.nextInt(100);
  //
  //        if(armorType == 0)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(GlowstoneSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(GlowstoneHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(GlowstoneChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(GlowstoneLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(GlowstoneBoots));
  //        }
  //        else if(armorType == 1)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(LazuliSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(LazuliHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(LazuliChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(LazuliLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(LazuliBoots));
  //        }
  //        else if(armorType == 2)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(OsmiumSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(OsmiumHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(OsmiumChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(OsmiumLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(OsmiumBoots));
  //        }
  //        else if(armorType == 3)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(SteelSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(SteelHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(SteelChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(SteelLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(SteelBoots));
  //        }
  //        else if(armorType == 4)
  //        {
  //          if(event.entityLiving instanceof EntityZombie && sword < 50) event.entityLiving.setCurrentItemOrArmor(0, new ItemStack(BronzeSword));
  //          if(helmet < 50) event.entityLiving.setCurrentItemOrArmor(1, new ItemStack(BronzeHelmet));
  //          if(chestplate < 50) event.entityLiving.setCurrentItemOrArmor(2, new ItemStack(BronzeChestplate));
  //          if(leggings < 50) event.entityLiving.setCurrentItemOrArmor(3, new ItemStack(BronzeLeggings));
  //          if(boots < 50) event.entityLiving.setCurrentItemOrArmor(4, new ItemStack(BronzeBoots));
  //        }
  //      }
  //    }
  //  }

}
