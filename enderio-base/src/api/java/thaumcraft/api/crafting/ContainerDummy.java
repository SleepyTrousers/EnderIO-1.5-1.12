package thaumcraft.api.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerDummy extends Container{

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return false;
	}

}
