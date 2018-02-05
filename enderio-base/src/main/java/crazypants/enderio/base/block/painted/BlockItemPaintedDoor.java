package crazypants.enderio.base.block.painted;

import javax.annotation.Nonnull;

import crazypants.enderio.base.block.darksteel.door.BlockItemDarkSteelDoor;
import crazypants.enderio.base.paint.PaintUtil;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemPaintedDoor extends BlockItemDarkSteelDoor {

  private final @Nonnull BlockPaintedDoor block;

  public BlockItemPaintedDoor(@Nonnull BlockPaintedDoor block) {
    super(block, null);
    this.block = block;
  }

  // copied verbatim from ItemDoor because vanilla...
  @SuppressWarnings({ "hiding", "cast" })
  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (facing != EnumFacing.UP) {
      return EnumActionResult.FAIL;
    } else {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      Block block = iblockstate.getBlock();

      if (!block.isReplaceable(worldIn, pos)) {
        pos = pos.offset(facing);
      }

      ItemStack itemstack = player.getHeldItem(hand);

      if (player.canPlayerEdit(pos, facing, itemstack) && this.block.canPlaceBlockAt(worldIn, pos)) {
        EnumFacing enumfacing = EnumFacing.fromAngle((double) player.rotationYaw);
        int i = enumfacing.getFrontOffsetX();
        int j = enumfacing.getFrontOffsetZ();
        boolean flag = i < 0 && hitZ < 0.5F || i > 0 && hitZ > 0.5F || j < 0 && hitX > 0.5F || j > 0 && hitX < 0.5F;
        placeDoor(worldIn, pos, enumfacing, this.block, flag);

        // EIO ADD START
        this.block.setPaintSource(worldIn.getBlockState(pos), worldIn, pos, PaintUtil.getSourceBlock(itemstack));
        this.block.setPaintSource(worldIn.getBlockState(pos.up()), worldIn, pos.up(), PaintUtil.getSourceBlock(itemstack));
        // EIO ADD END

        SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, player);
        worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
        itemstack.shrink(1);
        return EnumActionResult.SUCCESS;
      } else {
        return EnumActionResult.FAIL;
      }
    }
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return this.block.getUnlocalizedName();
  }

  @Override
  public @Nonnull String getUnlocalizedName() {
    return this.block.getUnlocalizedName();
  }

  @Override
  public int getItemBurnTime(@Nonnull ItemStack itemStack) {
    return block.getDefaultState().getMaterial() == Material.WOOD ? -1 : 0;
  }

}
