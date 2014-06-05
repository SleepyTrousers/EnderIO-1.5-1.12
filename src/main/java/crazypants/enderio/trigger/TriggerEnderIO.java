package crazypants.enderio.trigger;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.power.TileCapacitorBank;

public class TriggerEnderIO implements ITrigger {

  public static IIcon[] triggerIcons = new IIcon[5];
  public static String[] descriptions = new String[] { "Capacitor Bank has no energy stored", "Capacitor Bank has energy stored",
      "Capacitor Bank is full with energy", "Capacitor Bank is charging items", "Capacitor Bank finished charging items" };

  public String uniqueTag;

  public int triggerID;

  public TriggerEnderIO(String uniqueTag, int triggerID) {
    this.uniqueTag = uniqueTag;
    this.triggerID = triggerID;
    ActionManager.registerTrigger(this);
  }

  @Override
  public String getDescription() {
    return descriptions[triggerID];
  }

  //@Override
  public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter) {
    if(tile instanceof TileCapacitorBank) {
      TileCapacitorBank capacitorBank = (TileCapacitorBank) tile;

      if(triggerID == 0) {
        return capacitorBank.getEnergyStored() == 0;
      }
      if(triggerID == 1) {
        return capacitorBank.getEnergyStored() != 0;
      }
      if(triggerID == 2) {
        return capacitorBank.getMaxEnergyStored() - capacitorBank.getEnergyStored() < 5;
      }
      if(triggerID == 3 || triggerID == 4) {
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
        if(hasUnchargedItems && triggerID == 3) {
          return true;
        }
        if(!hasUnchargedItems && triggerID == 4) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon() {
    return triggerIcons[triggerID];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister iconRegistry) {
    triggerIcons[0] = iconRegistry.registerIcon("enderio:triggers/noEnergy");
    triggerIcons[1] = iconRegistry.registerIcon("enderio:triggers/hasEnergy");
    triggerIcons[2] = iconRegistry.registerIcon("enderio:triggers/fullEnergy");
    triggerIcons[3] = iconRegistry.registerIcon("enderio:triggers/charging");
    triggerIcons[4] = iconRegistry.registerIcon("enderio:triggers/chargingDone");
  }

//  @Override
//  public int getLegacyId() {
//    return 0;
//  }

  @Override
  public String getUniqueTag() {
    return this.uniqueTag;
  }

  @Override
  public boolean hasParameter() {
    return false;
  }

  @Override
  public ITriggerParameter createParameter() {
    return null;
  }

  @Override
  public boolean requiresParameter() {
    return false;
  }
}
