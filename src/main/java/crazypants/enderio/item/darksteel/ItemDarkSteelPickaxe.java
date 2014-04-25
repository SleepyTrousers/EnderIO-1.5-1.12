package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.material.Alloy;
import crazypants.util.ItemUtil;
import crazypants.util.Lang;

public class ItemDarkSteelPickaxe extends ItemPickaxe implements IEnergyContainerItem, IAdvancedTooltipProvider, IDarkSteelItem {

  public static boolean isEquipped(EntityPlayer player) {
    if(player == null) {
      return false;
    }
    ItemStack equipped = player.getCurrentEquippedItem();
    if(equipped == null) {
      return false;
    }
    return equipped.getItem() == EnderIO.itemDarkSteelPickaxe;
  }

  public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
    if(!isEquipped(player)) {
      return false;
    }
    return EnderIO.itemDarkSteelPickaxe.getEnergyStored(player.getCurrentEquippedItem()) >= requiredPower;
  }

  public static ItemDarkSteelPickaxe create() {
    ItemDarkSteelPickaxe res = new ItemDarkSteelPickaxe();
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  public ItemDarkSteelPickaxe() {
    super(ItemDarkSteelSword.MATERIAL);
    setCreativeTab(EnderIOTab.tabEnderIO);
    String str = "darkSteel_pickaxe";
    setUnlocalizedName(str);
    setTextureName("enderIO:" + str);
  }

  @Override
  public boolean isDamaged(ItemStack stack) {
    return false;
  }

  @Override
  public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase par2EntityLivingBase, EntityLivingBase par3EntityLivingBase) {
    applyDamage(par3EntityLivingBase, par1ItemStack, 2);
    return true;
  }

  @Override
  public boolean onBlockDestroyed(ItemStack item, World world, Block block, int x, int y, int z, EntityLivingBase entLiving) {
    if(block.getBlockHardness(world, x, y, z) != 0.0D) {
      if(block == Blocks.obsidian) {
        extractEnergy(item, Config.darkSteelPickPowerUseObsidian, false);
      }
      applyDamage(entLiving, item, 1);
    }
    return true;
  }

  @Override
  public boolean onItemUse(ItemStack item, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {

    int slot = player.inventory.currentItem + 1;
    if(slot < 9 && player.inventory.mainInventory[slot] != null) {
      return player.inventory.mainInventory[slot].getItem().onItemUse(player.inventory.mainInventory[slot], player, par3World, par4, par5, par6, par7, par8,
          par9, par10);
    }

    return super.onItemUse(item, player, par3World, par4, par5, par6, par7, par8, par9, par10);
  }

  private void applyDamage(EntityLivingBase entity, ItemStack item, int damage) {

    EnergyUpgrade eu = EnergyUpgrade.loadFromItem(item);
    if(eu != null && eu.isAbsorbDamageWithPower() && eu.getEnergy() > 0) {
      eu.extractEnergy(damage * Config.darkSteelPickPowerUsePerDamagePoint, false);

    } else {
      damage = item.getItemDamage() + damage;
      if(damage >= getMaxDamage()) {
        item.stackSize = 0;
      }
      item.setItemDamage(damage);
    }
    if(eu != null) {
      eu.setAbsorbDamageWithPower(!eu.isAbsorbDamageWithPower());
      eu.writeToItem(item);
    }

  }

  @Override
  public float func_150893_a(ItemStack item, Block block) {
    if(block == Blocks.obsidian && EnergyContainer.getEnergyStored(item) > 0) {
      return Config.darkSteelPickEffeciencyObsidian;
    }
    return super.func_150893_a(item, block);
  }

  @Override
  public float getDigSpeed(ItemStack stack, Block block, int meta) {
    if(ForgeHooks.isToolEffective(stack, block, meta)) {
      if(Config.darkSteelPickPowerUsePerDamagePoint > 0 && getEnergyStored(stack) > 0) {
        return ItemDarkSteelSword.MATERIAL.getEfficiencyOnProperMaterial() + Config.darkSteelPickEffeciencyBoostWhenPowered;
      }
      return ItemDarkSteelSword.MATERIAL.getEfficiencyOnProperMaterial();
    }
    return super.getDigSpeed(stack, block, meta);
  }

  protected void init() {
    GameRegistry.registerItem(this, getUnlocalizedName());
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

  @Override
  public boolean getIsRepairable(ItemStack i1, ItemStack i2) {
    return i2 != null && i2.getItem() == EnderIO.itemAlloy && i2.getItemDamage() == Alloy.DARK_STEEL.ordinal();
  }

  @Override
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    AnvilRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    AnvilRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addAdvancedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {

    list.add(ItemUtil.getDurabilityString(itemstack));
    if(EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
      EnergyUpgrade.addVibrantTooltip(list, itemstack);
      list.add(EnumChatFormatting.ITALIC + "+" + Config.darkSteelPickEffeciencyBoostWhenPowered + " "
          + Lang.localize("item.darkSteel_pickaxe.tooltip.effPowered"));
      list.add(EnumChatFormatting.ITALIC + "+" + Config.darkSteelPickEffeciencyObsidian + " "
          + Lang.localize("item.darkSteel_pickaxe.tooltip.effObs") + " ");
      list.add(EnumChatFormatting.ITALIC + "     (cost "
          + PowerDisplayUtil.formatPower(Config.darkSteelPickPowerUseObsidian / 10) + " "
          + PowerDisplayUtil.abrevation() + ")");
    }
    EnergyUpgrade.addNextUpgradeTooltip(itemstack, entityplayer, list, flag);
    AnvilRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  public ItemStack createItemStack() {
    return new ItemStack(this);
  }

}
