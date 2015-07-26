package info.loenwind.waterhooks;

import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class WaterFormEvent extends FluidEvent {

	public WaterFormEvent(World world, int x, int y, int z) {
		super(new FluidStack(FluidRegistry.WATER, 1000), world, x, y, z);
	}
	
}