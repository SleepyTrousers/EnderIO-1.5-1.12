package crazypants.enderio.trigger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.machine.power.TileCapacitorBank;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;

public class TriggerEnergyStorage implements ITrigger
{
	public static Icon[] triggerIcons = new Icon[3];
	
	public String uniqueTag;
	
	public TriggerEnergyStorage(String uniqueTag) 
	{
		this.uniqueTag = uniqueTag;
		ActionManager.registerTrigger(this);
	}
	
	@Override
	public String getDescription() 
	{
		if (uniqueTag == "enderIO.trigger.noEnergy") return "Capacitor Bank has no energy stored";
		if (uniqueTag == "enderIO.trigger.hasEnergy") return "Capacitor Bank has energy stored";
		if (uniqueTag == "enderIO.trigger.fullEnergy") return "Capacitor Bank is full with energy";
		return "";
	}
	

	@Override
	public boolean isTriggerActive(ForgeDirection side, TileEntity tile, ITriggerParameter parameter) 
	{
		if (tile instanceof TileCapacitorBank)
		{
			TileCapacitorBank capacitorBank = (TileCapacitorBank) tile;
			
			if (uniqueTag == "enderIO.trigger.noEnergy") return capacitorBank.getEnergyStored() == 0;
			if (uniqueTag == "enderIO.trigger.hasEnergy") return capacitorBank.getEnergyStored() != 0;
			if (uniqueTag == "enderIO.trigger.fullEnergy") return capacitorBank.getEnergyStored() == capacitorBank.getMaxEnergyStored();
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon() 
	{
		if (uniqueTag == "enderIO.trigger.noEnergy") return triggerIcons[0];
		if (uniqueTag == "enderIO.trigger.hasEnergy") return triggerIcons[1];
		if (uniqueTag == "enderIO.trigger.fullEnergy") return triggerIcons[2];
		return triggerIcons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegistry) 
	{
		triggerIcons[0] = iconRegistry.registerIcon("enderio:triggers/noEnergy");
		triggerIcons[1] = iconRegistry.registerIcon("enderio:triggers/hasEnergy");
		triggerIcons[2] = iconRegistry.registerIcon("enderio:triggers/fullEnergy");
	}

	@Override
	public int getLegacyId() 
	{
		return 0;
	}

	@Override
	public String getUniqueTag() 
	{
		return this.uniqueTag;
	}

	@Override
	public boolean hasParameter() 
	{
		return false;
	}

	@Override
	public ITriggerParameter createParameter() 
	{
		return null;
	}
}
