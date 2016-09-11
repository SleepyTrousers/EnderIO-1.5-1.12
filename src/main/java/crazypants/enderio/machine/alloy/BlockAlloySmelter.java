package crazypants.enderio.machine.alloy;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;

public class BlockAlloySmelter extends AbstractMachineBlock<TileAlloySmelter> {

  public static BlockAlloySmelter create() {

    PacketHandler.INSTANCE.registerMessage(PacketClientState.class, PacketClientState.class, PacketHandler.nextID(), Side.SERVER);

    BlockAlloySmelter ppainter = new BlockAlloySmelter();
    ppainter.init();
    return ppainter;
  }

  IIcon vanillaSmeltingOn;
  IIcon vanillaSmeltingOff;
  IIcon vanillaSmeltingOnly;

  private BlockAlloySmelter() {
    super(ModObject.blockAlloySmelter, TileAlloySmelter.class);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);
    vanillaSmeltingOn = iIconRegister.registerIcon("enderio:furnaceSmeltingOn");
    vanillaSmeltingOff = iIconRegister.registerIcon("enderio:furnaceSmeltingOff");
    vanillaSmeltingOnly = iIconRegister.registerIcon("enderio:furnaceSmeltingOnly");
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileAlloySmelter) {
      return new ContainerAlloySmelter(player.inventory, (TileAlloySmelter) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    return new GuiAlloySmelter(player.inventory, (TileAlloySmelter) te);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_ALLOY_SMELTER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:alloySmelterFrontOn";
    }
    return "enderio:alloySmelterFront";
  }

}
