package crazypants.enderio.machine.soul;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.farm.BlockFarmStation;
import crazypants.enderio.machine.farm.PacketFarmAction;
import crazypants.enderio.machine.farm.PacketUpdateNotification;
import crazypants.enderio.machine.farm.TileFarmStation;
import crazypants.enderio.machine.painter.GuiPainter;
import crazypants.enderio.machine.painter.PainterContainer;
import crazypants.enderio.machine.painter.TileEntityPainter;
import crazypants.enderio.network.PacketHandler;

public class BlockSoulFuser extends AbstractMachineBlock<TileSoulFuser> {

  
  public static BlockSoulFuser create() {    
    BlockSoulFuser result = new BlockSoulFuser();
    result.init();
    return result;
  }

  protected BlockSoulFuser() {
    super(ModObject.blockSoulFuser, TileSoulFuser.class);
  }
  
  @Override
  protected void init() {    
    super.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockSoulFuser.unlocalisedName, SoulFuserSpawnerRecipe.instance);
  }
  
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileSoulFuser) {
      return new ContainerSoulFuser(player.inventory, (AbstractMachineEntity) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileSoulFuser) {
      return new GuiSoulFuser(player.inventory, (AbstractMachineEntity) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_SOUL_FUSER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:blockSoulFuserOn";
    }
    return "enderio:blockSoulFuser";
  }
}
