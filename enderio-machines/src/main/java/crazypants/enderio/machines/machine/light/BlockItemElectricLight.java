package crazypants.enderio.machines.machine.light;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.ItemEIO;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemElectricLight extends ItemEIO implements IResourceTooltipProvider {

  public BlockItemElectricLight(@Nonnull BlockElectricLight block) {
    super(block);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
    setHasSubtypes(true);
    setMaxDamage(0);
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    return getUnlocalizedName() + LightType.fromMetadata(par1ItemStack.getMetadata()).getUnlocalizedSuffix();
  }

  @Override
  public boolean placeBlockAt(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side,
      float hitX, float hitY, float hitZ, @Nonnull IBlockState newState) {

    LightType type = LightType.fromMetadata(stack.getItemDamage());
    IBlockState state = newState.withProperty(BlockElectricLight.TYPE, type).withProperty(BlockElectricLight.FACING, side.getOpposite());
    if (!world.setBlockState(pos, state, 3)) {
      return false;
    }
    state = world.getBlockState(pos);
    if (state.getBlock() == block) {
      setTileEntityNBT(world, player, pos, stack);
      block.onBlockPlacedBy(world, pos, state, player, stack);
    }

    IBlockState bs = world.getBlockState(pos);
    if (bs.getBlock() == block) {
      EnumFacing onFace = side;
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileElectricLight) {
        TileElectricLight el = ((TileElectricLight) te);
        el.setFace(onFace.getOpposite());
      }
    }
    return true;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }
}
