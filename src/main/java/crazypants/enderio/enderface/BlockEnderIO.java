package crazypants.enderio.enderface;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.TextureRegistry;
import crazypants.enderio.render.TextureRegistry.TextureSupplier;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnderIO extends BlockEio<TileEnderIO> implements IResourceTooltipProvider {

  public static BlockEnderIO create() {

    PacketHandler.INSTANCE.registerMessage(PacketOpenServerGUI.class, PacketOpenServerGUI.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketLockClientContainer.Handler.class, PacketLockClientContainer.class, PacketHandler.nextID(), Side.CLIENT);

    BlockEnderIO result = new BlockEnderIO();    
    result.init();
    return result;
  }

  public static final TextureSupplier selectedOverlayIcon = TextureRegistry.registerTexture("blocks/enderIOSelected");
  public static final TextureSupplier highlightOverlayIcon = TextureRegistry.registerTexture("blocks/enderIOHighlight");
  public static final TextureSupplier enderEyeTex = TextureRegistry.registerTexture("items/ender_eye", false);
  public static final TextureSupplier frameIcon = TextureRegistry.registerTexture("blocks/enderIOFrame");

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
    System.out.println("BlockEnderIO.openGui: ");
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
    return 3;
  }

  @Override
  public int getLightValue(IBlockAccess world, BlockPos pos) {
    return 13;
  }

  @Override
  public int getLightOpacity() {
    return 100;
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.TRANSLUCENT;
  }
  
  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getSelectedOverlayIcon() {
    return selectedOverlayIcon.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getHighlightOverlayIcon() {
    return highlightOverlayIcon.get(TextureAtlasSprite.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getFrameIcon() {
    return frameIcon.get(TextureAtlasSprite.class);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }
}
