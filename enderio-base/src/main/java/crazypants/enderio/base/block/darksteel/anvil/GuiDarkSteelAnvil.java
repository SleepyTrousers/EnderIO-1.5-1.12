package crazypants.enderio.base.block.darksteel.anvil;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

//This class is almost a complete copy / paste of GuiRepair with the container changed to ContainerDarkSteelAnvil and the 
//hard coded check for a max anvil level of 40 replaced to a call to EnderCoreMethods
public class GuiDarkSteelAnvil extends GuiRepair {

  public GuiDarkSteelAnvil(InventoryPlayer inventoryIn, World worldIn) {
    super(inventoryIn, worldIn);
  }

}
