package crazypants.enderio.conduit.power;

import net.minecraftforge.common.util.ForgeDirection;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class PowerConduitPeripheral implements IPeripheral {

	private final IPowerConduit pc;

	public PowerConduitPeripheral(IPowerConduit pc) {
		this.pc = pc;
	}

	@Override
	public String getType() {
		return "command"; // provide CC-Commands
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { "getMaxEnergyStored", "getEnergyStored",
				"getMaxEnergyRecieved", "getMaxEnergyExtracted" };
	}

	private boolean checkArgumentForDirection(Object[] arguments) {
		if (arguments == null || arguments.length == 0) {
			return false;
		}
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if (dir.name().equals(arguments[0].toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context,
			int method, Object[] arguments) throws LuaException,
			InterruptedException {
		switch (method) {
		case 0: { // getMaxEnergyStored
			return new Object[] { pc.getMaxEnergyStored() };
		}
		case 1: { // getEnergyStored
			return new Object[] { pc.getEnergyStored() };
		}
		case 2: { // getMaxEnergyRecieved
			return new Object[] { pc.getCapacitor().getMaxEnergyReceived() };
		}
		case 3: { // getMaxEnergyExtracted
			if (!checkArgumentForDirection(arguments)) {
				return new Object[] { pc.getCapacitor().getMaxEnergyExtracted() };
			}
			ForgeDirection dir = ForgeDirection
					.valueOf(arguments[0].toString());
			return new Object[] { pc.getMaxEnergyExtracted(dir) };
		}
		}
		return null;
	}

	@Override
	public void attach(IComputerAccess computer) {
	}

	@Override
	public void detach(IComputerAccess computer) {
	}

	@Override
	public boolean equals(IPeripheral other) {
		return (other != null) && (other.getClass() == getClass());
	}
	
}
