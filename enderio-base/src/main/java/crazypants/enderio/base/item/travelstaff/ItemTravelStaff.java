package crazypants.enderio.base.item.travelstaff;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.MappedCapabilityProvider;
import com.enderio.core.common.interfaces.IOverlayRenderAware;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.config.config.TeleportConfig;
import crazypants.enderio.base.handler.darksteel.DarkSteelTooltipManager;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.base.teleport.TravelController;
import info.loenwind.autoconfig.factory.IValue;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTravelStaff extends Item implements IItemOfTravel, IAdvancedTooltipProvider, IOverlayRenderAware, IDarkSteelItem {

  private long lastBlickTick = 0;

  public static ItemTravelStaff create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemTravelStaff(modObject);
  }

  protected ItemTravelStaff(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
    setHasSubtypes(true);
  }

  @Override
  @Nonnull
  public MappedCapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt, @Nonnull MappedCapabilityProvider capProv) {
    if (!EnergyUpgrade.UPGRADES.get(0).hasAnyUpgradeVariant(stack)) {
      EnergyUpgrade.UPGRADES.get(0).addToItem(stack, this);
    }
    return IDarkSteelItem.super.initCapabilities(stack, nbt, capProv);
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack equipped = player.getHeldItem(hand);
    if (player.isSneaking()) {
      long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
      if (ticksSinceBlink < 0) {
        lastBlickTick = -1;
      }
      if (TeleportConfig.enableBlink.get() && world.isRemote && ticksSinceBlink >= TeleportConfig.blinkDelay.get()) {
        if (TravelController.doBlink(equipped, hand, player)) {
          player.swingArm(hand);
          lastBlickTick = EnderIO.proxy.getTickCount();
        }
      }
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
    }

    if (world.isRemote) {
      TravelController.activateTravelAccessable(equipped, hand, world, player, TravelSource.STAFF);
    }
    player.swingArm(hand);
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, equipped);
  }

  @Override
  public boolean canDestroyBlockInCreative(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
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
    list.add(EnergyUpgradeManager.getStoredEnergyString(itemstack));
    DarkSteelTooltipManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void extractInternal(@Nonnull ItemStack item, int powerUse) {
    EnergyUpgradeManager.extractEnergy(item, this, powerUse, false);
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
  public boolean isActive(@Nonnull EntityPlayer ep, @Nonnull ItemStack equipped) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean isFull3D() {
    return true;
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
  }

  @Override
  public int getEnergyStored(@Nonnull ItemStack item) {
    return EnergyUpgradeManager.getEnergyStored(item);
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.MAINHAND;
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return EquipmentData.IRON;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_TRAVEL_ENERGY_BUFFER;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_TRAVEL_ENERGY_INPUT;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return CapacitorKey.DARK_STEEL_TRAVEL_ENERGY_USE;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.NO_POWER;
  }

  @Override
  public void extractInternal(@Nonnull ItemStack item, IValue<Integer> powerUse) {
    EnergyUpgradeManager.extractEnergy(item, this, powerUse, false);
  }

}
