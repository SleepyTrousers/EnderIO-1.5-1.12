package crazypants.enderio.machine.capbank;

import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import net.minecraftforge.common.util.ForgeDirection;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

public class CapBankPeripheral implements IPeripheral {

	private final TileCapBank capBank;

	public CapBankPeripheral(TileCapBank capBank) {
		this.capBank = capBank;
	}

	@Override
	public String getType() {
		return "command"; // provide CC-Commands
	}

	@Override
	public String[] getMethodNames() {
		return new String[] { 
				"getAverageChangePerTick", 
				"getEnergyStoredL",
				"getMaxEnergyStoredL", 
				"getMaxInput",
				"getMaxIO",
				"getMaxOutput",
				"getMembers",
				"getOutputControlMode",
				"setOutputControlMode",
				"setMaxInput",
				"setMaxOutput"
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
		ICapBankNetwork net = capBank.getNetwork();
		switch (method) {
		case 0: { //getAverageChangePerTick
			return new Object[] {net.getAverageChangePerTick()};
		}
		case 1:	{ //getEnergyStoredL
			return new Object[] {net.getEnergyStoredL()};
		}
		case 2: { //getMaxEnergyStoredL
			return new Object[] {net.getMaxEnergyStoredL()};
		}
		case 3: { //getMaxInput
			return new Object[] {net.getMaxInput()};
		}
		case 4: { //getMaxIO
			return new Object[] {net.getMaxIO()};
		}
		case 5: { //getMaxOutput
			return new Object[] {net.getMaxOutput()};
		}
		case 6: { //getMembers
			return new Object[] {net.getMembers().size()};
		}
		case 7: { //getOutputControlMode
			return new Object[] {net.getOutputControlMode().toString()};
		}
		
		}
		if(arguments.length < 1 || arguments[0] == null) return null;
		String sArg = arguments[0].toString();
		
		switch(method) {
		case 8: { //setOutputControlMode
			try {
				RedstoneControlMode rMode = RedstoneControlMode.valueOf(sArg);
				net.setOutputControlMode(rMode);
			} catch(Exception ex) {
			}
		}
		case 9: { //setMaxInput
			try {
				int max = Integer.parseInt(sArg);
				net.setMaxInput(max);
			} catch(NumberFormatException ex) {
			}
		}
		case 10: { //setMaxOutput
			try {
				int max = Integer.parseInt(sArg);
				net.setMaxOutput(max);
			} catch(NumberFormatException ex) {
			}
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
