package crazypants.enderio.machine.buffer;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.RenderMappers;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.paint.IPaintable;
import crazypants.util.IFacade;

public class BlockBuffer extends AbstractMachineBlock<TileBuffer> implements IFacade, IPaintable.ISolidBlockPaintableBlock {

  public static BlockBuffer create() {
    PacketHandler.INSTANCE.registerMessage(PacketBufferIO.class, PacketBufferIO.class, PacketHandler.nextID(), Side.SERVER);
    BlockBuffer res = new BlockBuffer();
    res.init();
    return res;
  } 

  private BlockBuffer() {
    super(ModObject.blockBuffer, TileBuffer.class, BlockItemBuffer.class);
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO)
        .withProperty(BufferType.TYPE, BufferType.ITEM));
  }
  
  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumRenderMode.RENDER, BufferType.TYPE });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(BufferType.TYPE, BufferType.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return BufferType.getMetaFromType(state.getValue(BufferType.TYPE));
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return state.withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO);
  }

  @Override
  public int damageDropped(IBlockState st) {
    return getMetaFromState(st);
  }
  @Override
  protected void init() {
    super.init();
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if(te instanceof TileBuffer) {
      return new ContainerBuffer(player.inventory, (TileBuffer) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
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
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
    if(entity instanceof EntityPlayer) {
      TileEntity te = world.getTileEntity(pos);
      if(te instanceof TileBuffer) {
        TileBuffer ta = (TileBuffer) te;
        if(stack.getTagCompound() != null) {
          ta.readCommon(stack.getTagCompound());
        }
        int heading = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        ta.setFacing(getFacingForHeading(heading));
        world.markBlockForUpdate(pos);
      }
    }
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
  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    TileBuffer te = getTileEntity(world, pos);
    if(te == null){ 
      return null;
    }
    return te.getSourceBlock();
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, IBlockState paintSource) {
    // TODO Auto-generated method stub
    TileBuffer te = getTileEntity(world, pos);
    if (te == null) {
      return;
    }
    te.setSourceBlock(paintSource);
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, IBlockState paintSource) {
    // TODO Auto-generated method stub
    PainterUtil.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    // TODO Auto-generated method stub
    return getFacade(world, pos, null);
  }

  @Override
  public IBlockState getPaintSource(Block block, ItemStack stack) {
    // TODO Auto-generated method stub
    return PainterUtil.getSourceBlockState(stack);
  }

  // @Override
  // public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumFacing side, float hitX, float hitY, float
  // hitZ) {
  // // if (entityPlayer.isSneaking() && !world.isRemote) {
  // // world.markBlockForUpdate(pos);
  // // // if (RANDOM.nextBoolean()) {
  // // // setPaintSource(state, world, pos, null);
  // // // return true;
  // // // }
  // // // if (RANDOM.nextBoolean()) {
  // // // setPaintSource(state, world, pos, Blocks.noteblock.getDefaultState());
  // // // return true;
  // // // }
  // // // if (RANDOM.nextBoolean()) {
  // // // setPaintSource(state, world, pos, Blocks.bedrock.getDefaultState());
  // // // return true;
  // // // }
  // // // if (RANDOM.nextBoolean()) {
  // // // setPaintSource(state, world, pos, Blocks.sea_lantern.getDefaultState());
  // // // return true;
  // // // }
  // // if (RANDOM.nextBoolean()) {
  // // setPaintSource(state, world, pos, Blocks.stone_stairs.getDefaultState());
  // // return true;
  // // }
  // // // if (RANDOM.nextBoolean()) {
  // // // setPaintSource(state, world, pos, Blocks.torch.getDefaultState());
  // // // return true;
  // // // }
  // // // if (RANDOM.nextBoolean()) {
  // // // setPaintSource(state, world, pos, Blocks.mycelium.getDefaultState());
  // // // return true;
  // // // }
  // // setPaintSource(state, world, pos, null);
  // // return true;
  // // }
  // return super.onBlockActivated(world, pos, state, entityPlayer, side, hitX, hitY, hitZ);
  // }

}
