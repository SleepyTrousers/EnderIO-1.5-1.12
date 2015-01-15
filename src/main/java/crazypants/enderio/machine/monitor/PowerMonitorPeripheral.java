package crazypants.enderio.machine.monitor;

import net.minecraftforge.common.util.ForgeDirection;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class PowerMonitorPeripheral implements IPeripheral {

	private final TilePowerMonitor monitor;

	public PowerMonitorPeripheral(TilePowerMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public String getType() {
		return "command"; // provide CC-Commands
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { 
				"getMaxPowerInCapBanks", 
				"getMaxPowerInConduits",
				"getMaxPowerInMachines", 
				"getAveRfReceived",
				"getAveRfSent",
				"getEnergyPerTick",
				"getPowerInCapBanks",
				"getPowerInConduits",
				"getPowerInMachines",
				"getStartLevel",
				"getStopLevel",
				"getEngineControlEnabled",
				"getEnergyStored",
				"setEngineControlEnabled",
				"setStartLevel",
				"setStopLevel"
		};
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
		case 0: { //getMaxPowerInCapBanks
			return new Object[] {monitor.getMaxPowerInCapBanks()};
		}
		case 1:	{ //getMaxPowerInConduits
			return new Object[] {monitor.getMaxPowerInConduits()};
		}
		case 2: { //getMaxPowerInMachines
			return new Object[] {monitor.getMaxPowerInMachines()};
		}
		case 3: { //getAveRfReceived
			return new Object[] {monitor.getAveRfReceived()};
		}
		case 4: { //getAveRfSent
			return new Object[] {monitor.getAveRfSent()};
		}
		case 5: { //getEnergyPerTick
			return new Object[] {monitor.getEnergyPerTick()};
		}
		case 6: { //getPowerInCapBanks
			return new Object[] {monitor.getPowerInCapBanks()};
		}
		case 7: { //getPowerInConduits
			return new Object[] {monitor.getPowerInConduits()};
		}
		case 8: { //getPowerInMachines
			return new Object[] {monitor.getPowerInMachines()};
		}
		case 9: { //getStartLevel
			return new Object[] {monitor.getStartLevel()};
		}
		case 10: { //getStopLevel
			return new Object[] {monitor.getStopLevel()};
		}
		case 11: { //getEngineControlEnabled
			return new Object[] {monitor.isEngineControlEnabled()};
		}
		case 12: { //getEnergyStored
			return new Object[] {monitor.getEnergyStored()};
		}
		}
		if(arguments.length < 1 || arguments[0] == null) return null;
		String sArg = arguments[0].toString();
		switch(method) {
			case 13: { //setEngineControlEnabled
				boolean ctrl = Boolean.parseBoolean(sArg);
				monitor.setEngineControlEnabled(ctrl);
				break;
			}
			case 14: { //setStartLevel
				try {
					float fVal = Float.parseFloat(sArg);
					if(fVal >= 0.0 && fVal <= 1.0) {
						monitor.setStartLevel(fVal);
					}
				} catch (NumberFormatException ex) {
				}
				break;
			}
			case 15: { //setStopLevel
				try {
					float fVal = Float.parseFloat(sArg);
					if(fVal >= 0.0 && fVal <= 1.0) {
						monitor.setStopLevel(fVal);
					}
				} catch (NumberFormatException ex) {
				}
				break;
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
