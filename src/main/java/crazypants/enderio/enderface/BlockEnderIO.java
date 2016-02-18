package crazypants.enderio.enderface;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnderIO extends BlockEio implements IResourceTooltipProvider {

  public static BlockEnderIO create() {

    PacketHandler.INSTANCE.registerMessage(PacketOpenServerGUI.class, PacketOpenServerGUI.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketLockClientContainer.Handler.class, PacketLockClientContainer.class, PacketHandler.nextID(), Side.CLIENT);

    BlockEnderIO result = new BlockEnderIO();
    result.init();
    return result;
  }

  // @SideOnly(Side.CLIENT)
  // IIcon frameIcon;
  // @SideOnly(Side.CLIENT)
  // IIcon selectedOverlayIcon;
  // @SideOnly(Side.CLIENT)
  // IIcon highlightOverlayIcon;

  //TODO: 1.8
  @SideOnly(Side.CLIENT)
  TextureAtlasSprite selectedOverlayIcon;
  @SideOnly(Side.CLIENT)
  TextureAtlasSprite highlightOverlayIcon;
  @SideOnly(Side.CLIENT)
  TextureAtlasSprite enderEyeTex;
  @SideOnly(Side.CLIENT)
  TextureAtlasSprite frameIcon;
  
  static int pass;

  private BlockEnderIO() {
    super(ModObject.blockEnderIo.unlocalisedName, TileEnderIO.class);
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack item) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileEnderIO) {
      TileEnderIO eio = (TileEnderIO) te;
      eio.initUiPitch = -player.rotationPitch;
      eio.initUiYaw = -player.rotationYaw + 180;
      eio.lastUiPitch = eio.initUiPitch;
      eio.lastUiYaw = eio.initUiYaw;
      if (player instanceof EntityPlayer) {
        eio.setPlacedBy((EntityPlayer) player);
      }
      world.markBlockForUpdate(pos);
    }
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof ITravelAccessable) {
      ITravelAccessable ta = (ITravelAccessable) te;
      if (ta.canUiBeAccessed(entityPlayer)) {
        entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TRAVEL_ACCESSABLE, world, pos.getX(), pos.getY(), pos.getZ());
      } else {
        BlockTravelAnchor.sendPrivateChatMessage(entityPlayer, ta.getOwner());
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public int getRenderType() {
    return -1;
  }

  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
    return 13;
  }

  @Override
  public int getLightOpacity() {
    return 100;
  }

  // @Override
  // @SideOnly(Side.CLIENT)
  // public void registerBlockIcons(IIconRegister iIconRegister) {
  // super.registerBlockIcons(iIconRegister);
  // frameIcon = iIconRegister.registerIcon("enderio:enderIOFrame");
  // highlightOverlayIcon =
  // iIconRegister.registerIcon("enderio:enderIOHighlight");
  // selectedOverlayIcon =
  // iIconRegister.registerIcon("enderio:enderIOSelected");
  // }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }
}
