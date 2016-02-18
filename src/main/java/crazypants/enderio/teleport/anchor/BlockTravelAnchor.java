package crazypants.enderio.teleport.anchor;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.ChatUtil;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.IPaintableTileEntity;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.ContainerTravelAccessable;
import crazypants.enderio.teleport.ContainerTravelAuth;
import crazypants.enderio.teleport.GuiTravelAccessable;
import crazypants.enderio.teleport.GuiTravelAuth;
import crazypants.enderio.teleport.packet.PacketAccessMode;
import crazypants.enderio.teleport.packet.PacketDrainStaff;
import crazypants.enderio.teleport.packet.PacketLabel;
import crazypants.enderio.teleport.packet.PacketOpenAuthGui;
import crazypants.enderio.teleport.packet.PacketPassword;
import crazypants.enderio.teleport.packet.PacketTravelEvent;
import crazypants.util.IFacade;
import crazypants.util.UserIdent;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTravelAnchor extends BlockEio implements IGuiHandler, ITileEntityProvider, IResourceTooltipProvider, IFacade {

  public static int renderId = -1;

  public static BlockTravelAnchor create() {

    PacketHandler.INSTANCE.registerMessage(PacketAccessMode.class, PacketAccessMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketLabel.class, PacketLabel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketTravelEvent.class, PacketTravelEvent.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketDrainStaff.class, PacketDrainStaff.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketOpenAuthGui.class, PacketOpenAuthGui.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPassword.Handler.class, PacketPassword.class, PacketHandler.nextID(), Side.SERVER);

    BlockTravelAnchor result = new BlockTravelAnchor();
    result.init();

    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TRAVEL_ACCESSABLE, result);
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_TRAVEL_AUTH, result);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, result.new PainterTemplate());

    return result;
  }

  //TODO: 1.8
  @SideOnly(Side.CLIENT)
  TextureAtlasSprite selectedOverlayIcon;
  @SideOnly(Side.CLIENT)
  TextureAtlasSprite highlightOverlayIcon;


  private BlockTravelAnchor() {
    super(ModObject.blockTravelAnchor.unlocalisedName, TileTravelAnchor.class);
    if (!Config.travelAnchorEnabled) {
      setCreativeTab(null);
    }
  }

  public BlockTravelAnchor(String unlocalisedName, Class<? extends TileEntityEio> teClass) {
    super(unlocalisedName, teClass);
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    super.registerBlockIcons(iIconRegister);
//    highlightOverlayIcon = iIconRegister.registerIcon("enderio:blockTravelAnchorHighlight");
//    selectedOverlayIcon = iIconRegister.registerIcon("enderio:blockTravelAnchorSelected");
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
//    TileEntity te = world.getTileEntity(x, y, z);
//    if (te instanceof IPaintableTileEntity) {
//      Block sourceBlock = ((IPaintableTileEntity) te).getSourceBlock();
//      if (sourceBlock != null && sourceBlock != this) {
//        return sourceBlock.getIcon(blockSide, ((IPaintableTileEntity) te).getSourceBlockMetadata());
//      }
//    }
//    return super.getIcon(world, x, y, z, blockSide);
//  }

  @Override
  public TileEntity createNewTileEntity(World var1, int var2) {
    return new TileTravelAnchor();
  }
  
  

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
  
    if (entity instanceof EntityPlayer) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileTravelAnchor) {
        TileTravelAnchor ta = (TileTravelAnchor) te;
        ta.setPlacedBy((EntityPlayer) entity);
        Block b = PainterUtil.getSourceBlock(stack);
        ta.setSourceBlock(b);
        ta.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(stack));
        world.markBlockForUpdate(pos);
      }
    }
  }

  
  
  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {   
    TileEntity te = world.getTileEntity(pos);
    if (!world.isRemote && te instanceof ITravelAccessable) {
      ITravelAccessable ta = (ITravelAccessable) te;
      if (ta.canUiBeAccessed(entityPlayer)) {
        entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_TRAVEL_ACCESSABLE, world, pos.getX(), pos.getY(), pos.getZ());
      } else {
        sendPrivateChatMessage(entityPlayer, ta.getOwner());
      }
    }
    return true;
  }

  public static void sendPrivateChatMessage(EntityPlayer player, UserIdent owner) {
    if (!player.isSneaking()) {
      ChatUtil.sendNoSpam(player, EnderIO.lang.localize("gui.travelAccessable.privateBlock1") + " " + EnumChatFormatting.RED
          + owner.getPlayerName() + EnumChatFormatting.WHITE + " " + EnderIO.lang.localize("gui.travelAccessable.privateBlock2"));
    }
  }
  
  

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof ITravelAccessable) {
      if (ID == GuiHandler.GUI_ID_TRAVEL_ACCESSABLE) {
        return new ContainerTravelAccessable(player.inventory, (ITravelAccessable) te, world);
      } else {
        return new ContainerTravelAuth(player.inventory);
      }
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof ITravelAccessable) {
      if (ID == GuiHandler.GUI_ID_TRAVEL_ACCESSABLE) {
        return new GuiTravelAccessable(player.inventory, (ITravelAccessable) te, world);
      } else {
        return new GuiTravelAuth(player, (ITravelAccessable) te, world);
      }
    }
    return null;
  }

  
  
  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, TileEntityEio te, ItemStack drop) {   
    TileTravelAnchor anchor = (TileTravelAnchor) te;
    if (anchor == null) {
      return;
    }
    ItemStack itemStack = new ItemStack(this);
    Block srcBlk = anchor.getSourceBlock();
    if (srcBlk != null) {
      itemStack = createItemStackForSourceBlock(anchor.getSourceBlock(), anchor.getSourceBlockMetadata());
      drop.setTagCompound((NBTTagCompound) itemStack.getTagCompound().copy());
    }
  }
  
  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {  
    return false;
  }

  
  @SideOnly(Side.CLIENT)
  @Override
  public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IPaintableTileEntity) {
      Block sourceBlock = ((IPaintableTileEntity) te).getSourceBlock();
      if (sourceBlock != null && sourceBlock != this) {
        return sourceBlock.colorMultiplier(world, pos);
      }
    }
    return super.colorMultiplier(world, pos);
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  public ItemStack createItemStackForSourceBlock(Block block, int damage) {
    if (block == this) {
      return new ItemStack(this);
    }
    ItemStack result = new ItemStack(this, 1, damage);
    PainterUtil.setSourceBlock(result, block, damage);
    return result;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  public final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate() {
      super(BlockTravelAnchor.this);
    }

    @Override
    public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
      ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
      if (paintSource == null) {
        return new ResultStack[0];
      }
      return new ResultStack[] { new ResultStack(createItemStackForSourceBlock(Block.getBlockFromItem(paintSource.getItem()), paintSource.getItemDamage())) };
    }
  }

  @Override
  public int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileTravelAnchor) {
      return ((TileTravelAnchor) te).getSourceBlockMetadata();
    }
    return 0;
  }

  @Override
  public Block getFacade(IBlockAccess world, int x, int y, int z, int side) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof IPaintableTileEntity) {
      Block sourceBlock = ((IPaintableTileEntity) te).getSourceBlock();
      if (sourceBlock != null) {
        return sourceBlock;
      }
    }
    return this;
  }
  
}
