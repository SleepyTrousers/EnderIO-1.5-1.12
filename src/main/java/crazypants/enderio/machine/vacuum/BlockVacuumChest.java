package crazypants.enderio.machine.vacuum;

import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.power.ContainerCapacitorBank;
import crazypants.enderio.machine.power.GuiCapacitorBank;
import crazypants.enderio.machine.power.TileCapacitorBank;

public class BlockVacuumChest extends BlockEio implements IGuiHandler {

  public static BlockVacuumChest create() {
    BlockVacuumChest res = new BlockVacuumChest();
    res.init();
    return res;
  }

  public static int renderId;

  protected BlockVacuumChest() {
    super(ModObject.blockVacuumChest.unlocalisedName, TileVacuumChest.class);
    setBlockTextureName("enderio:blockVacuumChest");
  }
  
  @Override
  protected void init() {  
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_VACUUM_CHEST, this);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

    if(ConduitUtil.isToolEquipped(entityPlayer)) {
      if(entityPlayer.isSneaking() && entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(entityPlayer, x, y, z)) {
          removedByPlayer(world, entityPlayer, x, y, z, false);
          if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
            ((IToolWrench) entityPlayer.getCurrentEquippedItem().getItem()).wrenchUsed(entityPlayer, x, y, z);
          }
          return true;
        }
      } 
    }
    if(entityPlayer.isSneaking()) {
      return false;
    }
    entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_VACUUM_CHEST, world, x, y, z);
    return true;
  }

  @Override
  public int getRenderType() {    
    return renderId;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileVacuumChest) {
      return new ContainerVacuumChest(player, player.inventory, (TileVacuumChest) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileVacuumChest) {
      return new GuiVacuumChest(player, player.inventory, (TileVacuumChest) te);
    }
    return null;
  }

  public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_) {
    super.breakBlock(world, x, y, z, block, p_149749_6_);
    world.removeTileEntity(x, y, z);
  }

}
