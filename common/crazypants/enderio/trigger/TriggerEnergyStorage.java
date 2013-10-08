package crazypants.enderio.trigger;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.power.TileCapacitorBank;

public class TriggerEnergyStorage implements ITrigger {

  public static Icon[] triggerIcons = new Icon[3];

  private int id;

  private int iconIndex;

  private String uniqueTag;

  public TriggerEnergyStorage(int iconIndex, String uniqueTag) {
    this.iconIndex = iconIndex;
    this.uniqueTag = uniqueTag;
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
    if(uniqueTag == "enderIO.trigger.noEnergy")
      return "Capacitor Bank has no energy stored";
    if(uniqueTag == "enderIO.trigger.hasEnergy")
      return "Capacitor Bank has energy stored";
    if(uniqueTag == "enderIO.trigger.fullEnergy")
      return "Capacitor Bank is full with energy";
    return "";
  }

  @Override
  public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter) {

    if(tile instanceof TileCapacitorBank) {
      TileCapacitorBank capacitorBank = (TileCapacitorBank) tile;
      if(uniqueTag == "enderIO.trigger.noEnergy")
        return capacitorBank.getEnergyStored() == 0;
      if(uniqueTag == "enderIO.trigger.hasEnergy")
        return capacitorBank.getEnergyStored() != 0;
      if(uniqueTag == "enderIO.trigger.fullEnergy")
        return capacitorBank.getEnergyStored() == capacitorBank.getMaxEnergyStored();
    }
    return false;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public int getIconIndex() {
    return iconIndex;
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
