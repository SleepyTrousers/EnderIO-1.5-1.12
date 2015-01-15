package crazypants.enderio.machine.capbank;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CapBankPeripheralProvider implements IPeripheralProvider {

	@Override
	public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
		TileEntity tile = world.getTileEntity(x, y, z);
	    if ((tile != null) && ((tile instanceof TileCapBank)))
	    {
	    	TileCapBank capBank = (TileCapBank)tile;
	    	return new CapBankPeripheral(capBank);
	    }
	    return null;
	}
	
	public static void register() {
		ComputerCraftAPI.registerPeripheralProvider(new CapBankPeripheralProvider());
	}
}
