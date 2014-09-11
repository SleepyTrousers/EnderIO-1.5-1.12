package crazypants.enderio.machine.attractor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;

public class BlockAttractor extends AbstractMachineBlock<TileAttractor> {

  public static BlockAttractor create() {
    BlockAttractor res = new BlockAttractor();
    res.init();
    return res;
  }
  
  protected BlockAttractor() {
    super(ModObject.blockAttrator, TileAttractor.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileAttractor) {
      return new ContainerAttractor(player.inventory, (TileAttractor)te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileAttractor) {
      return new GuiAttractor(player.inventory, (TileAttractor)te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {    
    return GuiHandler.GUI_ID_ATTRACTOR;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:blankMachinePanel";
  }

}
