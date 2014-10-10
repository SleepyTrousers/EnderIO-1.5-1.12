package crazypants.enderio.machine.transceiver;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.transceiver.gui.ContainerTransceiver;
import crazypants.enderio.machine.transceiver.gui.GuiTransceiver;
import crazypants.enderio.network.PacketHandler;

public class BlockTransceiver extends AbstractMachineBlock<TileTransceiver> {

  public static BlockTransceiver create() {
    
    PacketHandler.INSTANCE.registerMessage(PacketSendRecieveChannel.class, PacketSendRecieveChannel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketAddRemoveChannel.class, PacketAddRemoveChannel.class, PacketHandler.nextID(), Side.CLIENT);    
    PacketHandler.INSTANCE.registerMessage(PacketChannelList.class, PacketChannelList.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketSendRecieveChannelList.class, PacketSendRecieveChannelList.class, PacketHandler.nextID(), Side.CLIENT);

    ConnectionHandler ch = new ConnectionHandler();
    FMLCommonHandler.instance().bus().register(ch);
    MinecraftForge.EVENT_BUS.register(ch);
    
    BlockTransceiver res = new BlockTransceiver();
    res.init();
    return res;
  }

  private BlockTransceiver() {
    super(ModObject.blockTransceiver, TileTransceiver.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileTransceiver) {
      return new ContainerTransceiver(player.inventory, (TileTransceiver) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    return new GuiTransceiver(player.inventory, (TileTransceiver) te);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_TRANSCEIVER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:alloySmelterFrontOn";
    }
    return "enderio:alloySmelterFront";
  }
  
  @Override
  public int getRenderType() {
    return -1;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {    
  }
  
  
}
