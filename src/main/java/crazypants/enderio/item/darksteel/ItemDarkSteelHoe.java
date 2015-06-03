package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.tool.SpiralPattern;
import crazypants.util.BlockCoord;
import crazypants.util.ItemUtil;
import crazypants.util.Lang;

public class ItemDarkSteelHoe extends ItemHoe implements IEnergyContainerItem, IAdvancedTooltipProvider, IDarkSteelItem {

  /* VANILLA */

  public ItemDarkSteelHoe() {
    super(ItemDarkSteelSword.MATERIAL);
    setCreativeTab(EnderIOTab.tabEnderIO);
    String str = "darkSteel_hoe";
    setUnlocalizedName(str);
    setTextureName("enderIO:" + str);
  }

  @Override
  public boolean isDamaged(ItemStack stack) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
    ItemStack is = new ItemStack(this);
    par3List.add(is);

    is = new ItemStack(this);
    EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
    EnergyUpgrade.setPowerFull(is);
    par3List.add(is);
  }

  /* Ender IO */

  public static ItemDarkSteelHoe create() {
    ItemDarkSteelHoe res = new ItemDarkSteelHoe();
    MinecraftForge.EVENT_BUS.register(res);
    res.init();
    return res;
  }

  public ItemStack createItemStack() {
    return new ItemStack(this);
  }

  protected void init() {
    GameRegistry.registerItem(this, getUnlocalizedName());
  }

  /* Item Logic */

  @Override
  public void setDamage(ItemStack stack, int newDamage) {
    int oldDamage = getDamage(stack);
    if (newDamage <= oldDamage) {
      super.setDamage(stack, newDamage);
    } else {
      int damage = newDamage - oldDamage;
      if (!absorbDamageWithEnergy(stack, damage * Config.darkSteelHoePowerUsePerDamagePoint)) {
        super.setDamage(stack, newDamage);
      }
    }
  }

  private boolean absorbDamageWithEnergy(ItemStack stack, int amount) {
    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(stack);
    if (eu != null && eu.isAbsorbDamageWithPower(stack) && eu.getEnergy() > 0) {
      eu.extractEnergy(amount, false);
      eu.writeToItem(stack);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float par8, float par9,
      float par10) {
    boolean result = super.onItemUse(item, player, world, x, y, z, side, par8, par9, par10);
    if (result && !world.isRemote && EnergyUpgrade.itemHasAnyPowerUpgrade(item)) {
      SpiralPattern pattern = new SpiralPattern(x, z, EnergyUpgrade.getEmpoweredLevel(item));
      BlockCoord next = pattern.next();
      while (next != null && EnergyUpgrade.getEnergyStored(item) > Config.darkSteelHoePowerUsePerDamagePoint) {
        super.onItemUse(item, player, world, next.x, y, next.z, side, par8, par9, par10);
        next = pattern.next();
      }
    }
    return result;
  }

  /* IDarkSteelItem */

  @Override
  public int getIngotsRequiredForFullRepair() {
    return 2;
  }

  /* IAdvancedTooltipProvider */

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
    if (!Config.addDurabilityTootip) {
      list.add(ItemUtil.getDurabilityString(itemstack));
    }
    String str = EnergyUpgrade.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    if (EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
      list.add(Lang.localize("item.darkSteel_hoe.tooltip.multiHarvest"));
    }
    DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  /* IEnergyContainerItem */

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

}
