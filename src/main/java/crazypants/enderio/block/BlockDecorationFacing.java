package crazypants.enderio.block;

import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.paint.render.PaintedBlockAccessWrapper;
import crazypants.enderio.render.property.EnumDecoBlock;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockDecoration2;

public class BlockDecorationFacing extends BlockDecoration {

  public static BlockDecorationFacing create() {
    BlockDecorationFacing blockDecorationFacing = new BlockDecorationFacing(blockDecoration2.getUnlocalisedName());
    blockDecorationFacing.init();
    return blockDecorationFacing;
  }

  public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class, EnumFacing.HORIZONTALS);
  public static final PropertyBool ACTIVE = PropertyBool.create("active");

  private BlockDecorationFacing(@Nonnull String name) {
    super(name);
  }

  @Override
  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH).withProperty(ACTIVE, false));
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumDecoBlock.TYPE, FACING, ACTIVE });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return super.getStateFromMeta(meta).withProperty(FACING, EnumFacing.SOUTH).withProperty(ACTIVE, false);
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    if (worldIn instanceof PaintedBlockAccessWrapper) {
      TileEntity tileEntity = ((PaintedBlockAccessWrapper) worldIn).getRealTileEntity(pos);
      if (tileEntity instanceof AbstractMachineEntity) {
        return state.withProperty(FACING, ((AbstractMachineEntity) tileEntity).getFacing()).withProperty(ACTIVE,
            ((AbstractMachineEntity) tileEntity).isActive());
      }
    }
    return state.withProperty(FACING, EnumFacing.SOUTH).withProperty(ACTIVE, false);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    Item item = Item.getItemFromBlock(this);
    Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(this);
    for (EnumDecoBlock type : EnumDecoBlock.TYPE.getAllowedValues()) {
      IBlockState state = getDefaultState().withProperty(EnumDecoBlock.TYPE, type).withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, false);
      ModelResourceLocation mrl = locations.get(state);
      ModelLoader.setCustomModelResourceLocation(item, EnumDecoBlock.getMetaFromType(type), mrl);
    }
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    EnumDecoBlock type = state.getValue(EnumDecoBlock.TYPE);
    return type != EnumDecoBlock.TYPE11 && type != EnumDecoBlock.TYPE12;
  }

}
