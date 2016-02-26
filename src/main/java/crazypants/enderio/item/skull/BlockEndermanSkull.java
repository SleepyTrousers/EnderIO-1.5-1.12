package crazypants.enderio.item.skull;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.util.ClientUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.crafting.IInfusionStabiliser;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
public class BlockEndermanSkull extends BlockEio<TileEndermanSkull> implements IInfusionStabiliser {

  public enum SkullType implements IStringSerializable {

    BASE("base", false),
    REANIMATED("reanimated", true),
    TORMENTED("tormented", false),
    REANIMATED_TORMENTED("reanimatedTormented", true);

    final String name;
    final boolean showEyes;

    SkullType(String name, boolean showEyes) {
      this.name = name;
      this.showEyes = showEyes;
    }

    @Override
    public String getName() {
      return name;
    }
  }

  public static final PropertyEnum<SkullType> VARIANT = PropertyEnum.<SkullType> create("variant", SkullType.class);

  public static BlockEndermanSkull create() {
    BlockEndermanSkull res = new BlockEndermanSkull();
    res.init();
    return res;
  }

  private BlockEndermanSkull() {
    super(ModObject.blockEndermanSkull.unlocalisedName, TileEndermanSkull.class, Material.circuits);
    setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 0.5F, 0.75F);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, ItemEndermanSkull.class, name);
    GameRegistry.registerTileEntity(teClass, name + "TileEntity");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.CUTOUT;
  }

  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    Item item = Item.getItemFromBlock(this);
    List<ResourceLocation> variants = new ArrayList<ResourceLocation>();
    for (SkullType st : SkullType.values()) {
      variants.add(new ResourceLocation(EnderIO.MODID.toLowerCase(), ModObject.blockEndermanSkull.unlocalisedName + "_" + st.name));
    }
    ModelBakery.registerItemVariants(item, variants.toArray(new ResourceLocation[variants.size()]));
    int num = SkullType.values().length;
    for (int i = 0; i < num; i++) {
      SkullType st = SkullType.values()[i];
      ClientUtil.regRenderer(item, i, ModObject.blockEndermanSkull.unlocalisedName + "_" + st.name);
    }
  }

  @Override
  public int damageDropped(IBlockState state) {
    SkullType var = state.getValue(VARIANT);
    return var.ordinal();
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    SkullType var = SkullType.values()[meta];
    return getDefaultState().withProperty(VARIANT, var);
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    SkullType var = state.getValue(VARIANT);
    return var.ordinal();
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { VARIANT });
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    int inc = MathHelper.floor_double(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
    float facingYaw = -22.5f * inc;
    TileEndermanSkull te = (TileEndermanSkull) world.getTileEntity(pos);
    te.setYaw(facingYaw);
    if (world.isRemote) {
      return;
    }
    world.setBlockState(pos, getStateFromMeta(stack.getItemDamage()));
    world.markBlockForUpdate(pos);
  }

  @Override
  @Optional.Method(modid = "Thaumcraft")
  public boolean canStabaliseInfusion(World world, BlockPos pos) {
    return true;
  }
}
