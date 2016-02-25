package crazypants.enderio.item.skull;

import crazypants.enderio.BlockEio;
import crazypants.enderio.ModObject;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thaumcraft.api.crafting.IInfusionStabiliser;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "Thaumcraft")
public class BlockEndermanSkull extends BlockEio<TileEndermanSkull> implements IInfusionStabiliser {

  public enum SkullType {

    BASE("base",false),
    REANIMATED("reanimated",true),
    TORMENTED("tormented",false),
    REANIMATED_TORMENTED("reanimatedTormented",true);

    final String name;
    final boolean showEyes;


    SkullType(String name, boolean showEyes) {
      this.name = name;
      this.showEyes = showEyes;
    }
  }

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

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//    frontIcon = iIconRegister.registerIcon("enderio:endermanSkullFront");
//    frontIconEyes = iIconRegister.registerIcon("enderio:endermanSkullFrontEyes");
//    sideIcon = iIconRegister.registerIcon("enderio:endermanSkullSide");
//    topIcon = iIconRegister.registerIcon("enderio:endermanSkullTop");
//  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int side, int meta) {
//    ForgeDirection orint = ForgeDirection.getOrientation(side);
//    if(orint == ForgeDirection.NORTH) {
//      meta = MathHelper.clamp_int(meta, 0, SkullType.values().length - 1);
//      return SkullType.values()[meta].showEyes ? frontIconEyes : frontIcon;
//    }
//    if(orint == ForgeDirection.UP || orint == ForgeDirection.DOWN || orint == ForgeDirection.SOUTH) {
//      return topIcon;
//    }
//    return sideIcon;
//  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public String getItemIconName() {
//    return "enderio:endermanSkull";
//  }


  
  
  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {  
    int inc = MathHelper.floor_double(player.rotationYaw * 16.0F / 360.0F + 0.5D) & 15;
    float facingYaw = -22.5f * inc;
    TileEndermanSkull te = (TileEndermanSkull) world.getTileEntity(pos);
    te.setYaw(facingYaw);
    if(world.isRemote) {
      return;
    }
    world.setBlockState(pos, getStateFromMeta(stack.getItemDamage()));
    world.markBlockForUpdate(pos);
  }

  @Override
  public int damageDropped(IBlockState state) {
    return getMetaFromState(state);
  }

  @Override
  @Optional.Method(modid = "Thaumcraft")
  public boolean canStabaliseInfusion(World world, BlockPos pos) {
    return true;
  }
}
