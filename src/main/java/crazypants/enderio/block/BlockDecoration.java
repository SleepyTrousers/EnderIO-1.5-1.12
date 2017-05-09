package crazypants.enderio.block;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.property.EnumDecoBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockDecoration1;

public class BlockDecoration extends Block implements IHaveRenderers {

  public static BlockDecoration create() {
    BlockDecoration blockDecoration = new BlockDecoration(blockDecoration1.getUnlocalisedName());
    blockDecoration.init();
    return blockDecoration;
  }

  @Nonnull
  protected final String name;

  protected BlockDecoration(@Nonnull String name) {
    super(Material.ROCK);
    this.name = name;
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(name);
    setRegistryName(name);
    setHardness(0.5F);
    setSoundType(SoundType.METAL);
    setHarvestLevel("pickaxe", 0);
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState());
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumDecoBlock.TYPE });
  }


  protected void init() {
    GameRegistry.register(this);
    GameRegistry.register(new ItemBlockDecoration(this, getName()) {
      @Override
      public String getUnlocalizedName(@Nonnull ItemStack stack) {
        return EnumDecoBlock.getTypeFromMeta(stack.getMetadata()).getUnlocalizedName(this);
      }
    });
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
    if (tab != null) {
      for (EnumDecoBlock type : EnumDecoBlock.values()) {
        list.add(new ItemStack(itemIn, 1, EnumDecoBlock.getMetaFromType(type)));
      }
    }
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState().withProperty(EnumDecoBlock.TYPE, EnumDecoBlock.getTypeFromMeta(meta));
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return EnumDecoBlock.getMetaFromType(state.getValue(EnumDecoBlock.TYPE));
  }

  @Override
  public int damageDropped(IBlockState st) {
    return getMetaFromState(st);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    Item item = Item.getItemFromBlock(this);
    Map<IBlockState, ModelResourceLocation> locations = new DefaultStateMapper().putStateModelLocations(this);
    for (EnumDecoBlock type : EnumDecoBlock.TYPE.getAllowedValues()) {
      IBlockState state = getDefaultState().withProperty(EnumDecoBlock.TYPE, type);
      ModelResourceLocation mrl = locations.get(state);
      ModelLoader.setCustomModelResourceLocation(item, EnumDecoBlock.getMetaFromType(type), mrl);
    }
  }

  @Override
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {
    EnumDecoBlock type = state.getValue(EnumDecoBlock.TYPE);
    return type != EnumDecoBlock.TYPE00 && type != EnumDecoBlock.TYPE14 && type != EnumDecoBlock.TYPE15;
  }

  @Override
  public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
    return isOpaqueCube(base_state);
  }

}