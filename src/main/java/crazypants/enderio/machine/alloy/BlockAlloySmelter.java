package crazypants.enderio.machine.alloy;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.MachineSmartModel;
import crazypants.enderio.render.SmartModelAttacher;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAlloySmelter extends AbstractMachineBlock<TileAlloySmelter> {

  public static BlockAlloySmelter create() {

    PacketHandler.INSTANCE.registerMessage(PacketClientState.class, PacketClientState.class, PacketHandler.nextID(), Side.SERVER);

    BlockAlloySmelter res = new BlockAlloySmelter();
    res.init();    
    MinecraftForge.EVENT_BUS.register(res);    
    return res;
  }

  public String name() {
    return name;
  }

  @SideOnly(Side.CLIENT)
  TextureAtlasSprite vanillaSmeltingOn;
  @SideOnly(Side.CLIENT)
  TextureAtlasSprite vanillaSmeltingOff;
  @SideOnly(Side.CLIENT)
  TextureAtlasSprite vanillaSmeltingOnly;

  private BlockAlloySmelter() {
    super(ModObject.blockAlloySmelter, TileAlloySmelter.class);
  }


//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    super.registerBlockIcons(iIconRegister);
//    vanillaSmeltingOn = iIconRegister.registerIcon("enderio:furnaceSmeltingOn");
//    vanillaSmeltingOff = iIconRegister.registerIcon("enderio:furnaceSmeltingOff");
//    vanillaSmeltingOnly = iIconRegister.registerIcon("enderio:furnaceSmeltingOnly");
//  }
  
  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Pre event) {    
    vanillaSmeltingOn = event.map.registerSprite(new ResourceLocation(EnderIO.MODID, "blocks/furnaceSmeltingOn"));
    vanillaSmeltingOff = event.map.registerSprite(new ResourceLocation(EnderIO.MODID, "blocks/furnaceSmeltingOff"));
    vanillaSmeltingOnly = event.map.registerSprite(new ResourceLocation(EnderIO.MODID, "blocks/furnaceSmeltingOnly"));          
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileAlloySmelter) {
      return new ContainerAlloySmelter(player.inventory, (TileAlloySmelter) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    return new GuiAlloySmelter(player.inventory, (TileAlloySmelter) te);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_ALLOY_SMELTER;
  }

}
