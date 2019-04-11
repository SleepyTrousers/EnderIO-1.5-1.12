package crazypants.enderio.base.item.darksteel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.interfaces.IOverlayRenderAware;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IEquipmentData;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capacitor.CapacitorKey;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.item.darksteel.attributes.EquipmentData;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgrade.EnergyUpgradeHolder;
import crazypants.enderio.base.item.darksteel.upgrade.energy.EnergyUpgradeManager;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.util.NbtValue;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIO.MODID)
public class ItemInventoryCharger extends Item implements IAdvancedTooltipProvider, IDarkSteelItem, IOverlayRenderAware {

  public static ItemInventoryCharger createSimple(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemInventoryCharger res = new ItemInventoryCharger(modObject, CapacitorKey.DARK_STEEL_CHARGER_SIMPLE_ENERGY_BUFFER,
        CapacitorKey.DARK_STEEL_CHARGER_SIMPLE_ENERGY_INPUT, CapacitorKey.DARK_STEEL_CHARGER_SIMPLE_ENERGY_USE, true);
    return res;
  }

  public static ItemInventoryCharger createBasic(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemInventoryCharger res = new ItemInventoryCharger(modObject, CapacitorKey.DARK_STEEL_CHARGER_BASIC_ENERGY_BUFFER,
        CapacitorKey.DARK_STEEL_CHARGER_BASIC_ENERGY_INPUT, CapacitorKey.DARK_STEEL_CHARGER_BASIC_ENERGY_USE, false);
    return res;
  }

  public static ItemInventoryCharger create(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemInventoryCharger res = new ItemInventoryCharger(modObject, CapacitorKey.DARK_STEEL_CHARGER_ENERGY_BUFFER, CapacitorKey.DARK_STEEL_CHARGER_ENERGY_INPUT,
        CapacitorKey.DARK_STEEL_CHARGER_ENERGY_USE, false);
    return res;
  }

  public static ItemInventoryCharger createVibrant(@Nonnull IModObject modObject, @Nullable Block block) {
    ItemInventoryCharger res = new ItemInventoryCharger(modObject, CapacitorKey.DARK_STEEL_CHARGER_VIBRANT_ENERGY_BUFFER,
        CapacitorKey.DARK_STEEL_CHARGER_VIBRANT_ENERGY_INPUT, CapacitorKey.DARK_STEEL_CHARGER_VIBRANT_ENERGY_USE, false);
    return res;
  }

  private final @Nonnull ICapacitorKey energyStorageKey, energyInputKey, energyUseKey;
  private final boolean rangeLimited;

  protected ItemInventoryCharger(@Nonnull IModObject modObject, @Nonnull ICapacitorKey energyStorageKey, @Nonnull ICapacitorKey energyInputKey,
      @Nonnull ICapacitorKey energyUseKey, boolean rangeLimited) {
    this.energyStorageKey = energyStorageKey;
    this.energyInputKey = energyInputKey;
    this.energyUseKey = energyUseKey;
    this.rangeLimited = rangeLimited;
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIOItems);
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack is = new ItemStack(this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(0).addToItem(is, this);
      list.add(is);

      is = new ItemStack(this);
      EnergyUpgrade.UPGRADES.get(3).addToItem(is, this);
      EnergyUpgradeManager.setPowerFull(is, this);
      list.add(is);
    }
  }

  @SubscribeEvent
  public static void onTick(PlayerTickEvent event) {
    if (event.side == Side.SERVER && event.phase == Phase.END && EnderIO.proxy.getServerTickCount() % (20 * 1) == 0) {
      final EntityPlayer player = event.player;
      if (player != null) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
          ItemStack stackInSlot = player.inventory.getStackInSlot(i);
          if (stackInSlot.getItem() instanceof ItemInventoryCharger) {
            ((ItemInventoryCharger) stackInSlot.getItem()).charge(player, stackInSlot, i);
          }
        }
      }
    }
  }

  public void charge(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, int slot) {
    if (NbtValue.ENABLED.getBoolean(stack)) {
      EnergyUpgradeHolder eu = EnergyUpgradeManager.loadFromItem(stack);
      if (eu != null && eu.getEnergy() > 0) {
        for (int i : rangeLimited ? SlotNeighborHelper.getSlotNeighors(slot) : SlotNeighborHelper.getAllSlots()) {
          // for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
          ItemStack stackInSlot = player.inventory.getStackInSlot(i);
          if (!(stackInSlot.getItem() instanceof ItemInventoryCharger) && Prep.isValid(stackInSlot)) {
            IEnergyStorage cap = PowerHandlerUtil.getCapability(stackInSlot, null);
            if (cap != null && cap.canReceive() && cap.getEnergyStored() < cap.getMaxEnergyStored()) {
              int added = cap.receiveEnergy(eu.extractEnergy(eu.getEnergy(), true), false);
              if (added > 0) {
                eu.extractEnergy(added, false);
                eu.writeToItem();
                if (eu.getEnergy() <= 0) {
                  return;
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return NbtValue.ENABLED.getBoolean(stack) || super.hasEffect(stack);
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
    final ItemStack stack = playerIn.getHeldItem(handIn);
    if (EnergyUpgradeManager.loadFromItem(stack) != null) {
      if (!worldIn.isRemote) {
        NbtValue.ENABLED.setBoolean(stack, !NbtValue.ENABLED.getBoolean(stack));
        playerIn.sendStatusMessage((NbtValue.ENABLED.getBoolean(stack) ? Lang.INVCHARGER_ENABLED : Lang.INVCHARGER_DISABLED).toChatServer(), true);
      }
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
    return super.onItemRightClick(worldIn, playerIn, handIn);
  }

  @Override
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    if (EnergyUpgradeManager.loadFromItem(itemstack) == null) {
      list.add(Lang.INVCHARGER_NEEDUPGRADE.get());
    }
    DarkSteelRecipeManager.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    DarkSteelRecipeManager.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    String str = EnergyUpgradeManager.getStoredEnergyString(itemstack);
    if (str != null) {
      list.add(str);
    }
    DarkSteelRecipeManager.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    PowerBarOverlayRenderHelper.instance_upgradeable_vert.render(stack, xPosition, yPosition);
  }

  @Override
  public boolean isForSlot(@Nonnull EntityEquipmentSlot slot) {
    return slot == EntityEquipmentSlot.OFFHAND;
  }

  @Override
  public @Nonnull IEquipmentData getEquipmentData() {
    return EquipmentData.DARK_STEEL;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyStorageKey(@Nonnull ItemStack stack) {
    return energyStorageKey;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyInputKey(@Nonnull ItemStack stack) {
    return energyInputKey;
  }

  @Override
  public @Nonnull ICapacitorKey getEnergyUseKey(@Nonnull ItemStack stack) {
    return energyUseKey;
  }

  @Override
  public @Nonnull ICapacitorKey getAbsorptionRatioKey(@Nonnull ItemStack stack) {
    return CapacitorKey.NO_POWER;
  }

  @Override
  public boolean allowExtractEnergy() {
    return true;
  }
}
