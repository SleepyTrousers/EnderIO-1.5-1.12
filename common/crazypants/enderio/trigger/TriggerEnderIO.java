package crazypants.enderio.trigger;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import thermalexpansion.api.item.IChargeableItem;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.power.TileCapacitorBank;

public class TriggerEnderIO implements ITrigger {

  public static Icon[] triggerIcons = new Icon[5];
  public static String[] descriptions = new String[] { "Capacitor Bank has no energy stored", "Capacitor Bank has energy stored",
      "Capacitor Bank is full with energy", "Capacitor Bank is charging items", "Capacitor Bank finished charging items" };

  public String uniqueTag;

  public int triggerIndex;
  private int id;

  public TriggerEnderIO(String uniqueTag, int triggerID) {
    this.uniqueTag = uniqueTag;
    this.triggerIndex = triggerID;

    for (int i = 0; i < ActionManager.triggers.length; i++) {
      if(ActionManager.triggers[i] == null) {
        ActionManager.triggers[i] = this;
        this.id = i;
        return;
      }
    }

  }

  @Override
  public String getDescription() {
    return descriptions[triggerIndex];
  }

  @Override
  public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter) {
    if(tile instanceof TileCapacitorBank) {
      TileCapacitorBank capacitorBank = (TileCapacitorBank) tile;

      if(triggerIndex == 0) {
        return capacitorBank.getEnergyStored() == 0;
      }
      if(triggerIndex == 1) {
        return capacitorBank.getEnergyStored() != 0;
      }
      if(triggerIndex == 2) {
        return capacitorBank.getEnergyStored() == capacitorBank.getMaxEnergyStored();
      }
      if(triggerIndex == 3 || triggerIndex == 4) {
        ItemStack[] items = new ItemStack[capacitorBank.getSizeInventory()];
        for (int i = 0; i < items.length; i++) {
          items[i] = capacitorBank.getStackInSlot(i);
        }
        boolean capacitorBankEmpty = true;
        for (int i = 0; i < items.length; i++) {
          if(items[i] != null && items[i].getItem() instanceof IChargeableItem) {
            capacitorBankEmpty = false;
          }
        }
        if(capacitorBankEmpty) {
          return false;
        }
        boolean hasUnchargedItems = false;
        for (int i = 0; i < items.length; i++) {
          if(items[i] != null && items[i].getItem() instanceof IChargeableItem) {
            IChargeableItem item = (IChargeableItem) items[i].getItem();
            if(item.getEnergyStored(items[i]) < item.getMaxEnergyStored(items[i])) {
              hasUnchargedItems = true;
            }
          }
        }
        if(hasUnchargedItems && triggerIndex == 3) {
          return true;
        }
        if(!hasUnchargedItems && triggerIndex == 4) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getIconIndex() {
    return triggerIndex;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIconProvider getIconProvider() {
    return TriggerIconProvider.instance;
  }

  @Override
  public boolean hasParameter() {
    return false;
  }

  @Override
  public ITriggerParameter createParameter() {
    return null;
  }

}
