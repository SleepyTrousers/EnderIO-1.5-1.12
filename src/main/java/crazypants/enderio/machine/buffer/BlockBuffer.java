package crazypants.enderio.machine.buffer;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.IPaintableTileEntity;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.IFacade;

public class BlockBuffer extends AbstractMachineBlock<TileBuffer> implements IFacade {

  public static BlockBuffer create() {
    PacketHandler.INSTANCE.registerMessage(PacketBufferIO.class, PacketBufferIO.class, PacketHandler.nextID(), Side.SERVER);
    BlockBuffer res = new BlockBuffer();
    res.init();
    return res;
  }

  private static final String[] textureNames = new String[] { "blockBufferItem", "blockBufferPower", "blockBufferOmni", "blockBufferCreative" };
  @SideOnly(Side.CLIENT)
  private IIcon[] textures;

  private BlockBuffer() {
    super(ModObject.blockBuffer, TileBuffer.class);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemBuffer.class, modObject.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, modObject.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileBuffer) {
      return new ContainerBuffer(player.inventory, (TileBuffer) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileBuffer) {
      return new GuiBuffer(player.inventory, (TileBuffer) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_BUFFER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    super.registerBlockIcons(iIconRegister);
    textures = new IIcon[textureNames.length];
    for (int i = 0; i < textureNames.length; i++) {
      textures[i] = iIconRegister.registerIcon("enderio:" + textureNames[i]);
    }
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return getSideIconKey(active);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int blockSide, int blockMeta) {
    return blockSide > 1 ? textures[blockMeta] : super.getIcon(blockSide, blockMeta);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileBuffer) {
      TileBuffer tef = (TileBuffer) te;
      final Block sourceBlock = tef.getSourceBlock();
      if (sourceBlock != null) {
        return sourceBlock.getIcon(blockSide, tef.getSourceBlockMetadata());
      } else if(blockSide > 1) {
        return textures[world.getBlockMetadata(x, y, z)];
      }
    }
    return super.getIcon(world, x, y, z, blockSide);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
    if(entity instanceof EntityPlayer) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileBuffer) {
        TileBuffer ta = (TileBuffer) te;
        if(stack.stackTagCompound != null) {
          ta.readCommon(stack.stackTagCompound);
        }
        world.markBlockForUpdate(x, y, z);
      }
    }
  }

  @Override
  public int damageDropped(int meta) {
    return meta;
  }

  public ItemStack createItemStackForSourceBlock(ItemStack machine, Block block, int sourceMeta) {
    PainterUtil.setSourceBlock(machine, block, sourceMeta);
    return machine;
  }

  public final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate() {
      super(BlockBuffer.this);
    }

    @Override
    public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
      ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
      if(paintSource == null) {
        return new ResultStack[0];
      }
      ItemStack target = MachineRecipeInput.getInputForSlot(0, inputs);
      target = target.copy();
      target.stackSize = 1;
      return new ResultStack[] { new ResultStack(createItemStackForSourceBlock(target, Block.getBlockFromItem(paintSource.getItem()),
          paintSource.getItemDamage())) };
    }
  }

  @Override
  public int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileBuffer) {
      return ((TileBuffer) te).getSourceBlockMetadata();
    }
    return 0;
  }

  @Override
  @Nonnull
  public Block getFacade(IBlockAccess world, int x, int y, int z, int side) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof IPaintableTileEntity) {
      Block sourceBlock = ((IPaintableTileEntity) te).getSourceBlock();
      if (sourceBlock != null) {
        return sourceBlock;
      }
    }
    return this;
  }

  @Override
  @Nonnull
  public Block getVisualBlock(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return getFacade(world, x, y, z, side.ordinal());
  }

  @Override
  public int getVisualMeta(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return getFacadeMetadata(world, x, y, z, side.ordinal());
  }

  @Override
  public boolean supportsVisualConnections() {
    return true;
  }
}
