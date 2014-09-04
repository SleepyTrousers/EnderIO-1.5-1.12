package crazypants.enderio.machine.slicensplice;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.soul.BlockSoulBinder;
import crazypants.enderio.machine.soul.ContainerSoulBinder;
import crazypants.enderio.machine.soul.GuiSoulBinder;
import crazypants.enderio.machine.soul.SoulBinderSpawnerRecipe;
import crazypants.enderio.machine.soul.TileSoulBinder;

public class BlockSliceAndSplice extends AbstractMachineBlock<TileSliceAndSplice> {
  
  public static int renderId;
  
  public static BlockSliceAndSplice create() {    
    BlockSliceAndSplice result = new BlockSliceAndSplice();
    result.init();
    return result;
  }
  
  protected BlockSliceAndSplice() {
    super(ModObject.blockSliceAndSplice, TileSliceAndSplice.class);
  }
  
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileSliceAndSplice) {
      return new ContainerSliceAndSplice(player.inventory, (AbstractMachineEntity) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileSliceAndSplice) {
      return new GuiSliceAndSplice(player.inventory, (AbstractMachineEntity) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_SLICE_N_SPLICE;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:blankMachinePanel";
  }

//  @Override
//  public boolean renderAsNormalBlock() {
//    return false;
//  }
//
//  @Override
//  public boolean isOpaqueCube() {
//    return false;
//  }
//  
//  @Override
//  public int getLightOpacity() {
//    return 7;
//  }
  
}
