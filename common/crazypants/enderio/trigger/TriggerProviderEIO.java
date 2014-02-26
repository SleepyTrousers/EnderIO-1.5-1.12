package crazypants.enderio.trigger;

import buildcraft.api.gates.IOverrideDefaultTriggers;
import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerProvider;
import buildcraft.api.transport.IPipeTile;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.power.TileCapacitorBank;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.LinkedList;

public class TriggerProviderEIO implements ITriggerProvider {
	
	@Override
	public LinkedList<ITrigger> getNeighborTriggers(Block block, TileEntity tile) {
		if (tile instanceof IOverrideDefaultTriggers) {
			return ((IOverrideDefaultTriggers) tile).getTriggers();
		}
		
		LinkedList<ITrigger> triggers = new LinkedList<ITrigger>();
				
		if (tile instanceof TileCapacitorBank) {
			triggers.add(EnderIO.triggerNoEnergy);
			triggers.add(EnderIO.triggerHasEnergy);
			triggers.add(EnderIO.triggerFullEnergy);
			triggers.add(EnderIO.triggerIsCharging);
			triggers.add(EnderIO.triggerFinishedCharging);
		}
		
		return triggers;
	}

  @Override
  public LinkedList<ITrigger> getPipeTriggers(IPipeTile pipe) {
    return null;
  }
}
