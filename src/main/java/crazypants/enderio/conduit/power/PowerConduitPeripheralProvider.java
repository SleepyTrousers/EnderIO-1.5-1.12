package crazypants.enderio.conduit.power;

import crazypants.enderio.conduit.TileConduitBundle;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.world.World;

public class PowerConduitPeripheralProvider implements IPeripheralProvider {

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
	    if ((tile != null) && ((tile instanceof TileConduitBundle)))
	    {
	    	TileConduitBundle conduit = (TileConduitBundle)tile;
	    	IPowerConduit pc = conduit.getConduit(IPowerConduit.class);
	    	if(pc != null) {
	    		return new PowerConduitPeripheral(pc);
	    	}
	    }
	    return null;
	}
	
	public static void register() {
		ComputerCraftAPI.registerPeripheralProvider(new PowerConduitPeripheralProvider());
	}
}
