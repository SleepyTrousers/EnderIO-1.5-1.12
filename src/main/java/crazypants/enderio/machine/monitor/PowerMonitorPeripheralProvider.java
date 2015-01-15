package crazypants.enderio.machine.monitor;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PowerMonitorPeripheralProvider implements IPeripheralProvider {

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
	    if ((tile != null) && ((tile instanceof TilePowerMonitor)))
	    {
	    	TilePowerMonitor Monitor = (TilePowerMonitor)tile;
	    	return new PowerMonitorPeripheral(Monitor);
	    }
	    return null;
	}
	
	public static void register() {
		ComputerCraftAPI.registerPeripheralProvider(new PowerMonitorPeripheralProvider());
	}
}
