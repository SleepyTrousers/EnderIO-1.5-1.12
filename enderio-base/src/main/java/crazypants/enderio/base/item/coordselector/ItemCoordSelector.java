package crazypants.enderio.base.item.coordselector;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemCoordSelector extends Item implements IResourceTooltipProvider {

  public static ItemCoordSelector create(@Nonnull IModObject modObject) {
    return new ItemCoordSelector(modObject);
  }

  private ItemCoordSelector(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack stack = new ItemStack(this);
      list.add(stack);
    }
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (printCoords(stack, world, player)) {
      player.swingArm(hand);
    }
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  public boolean canDestroyBlockInCreative(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return false;
  }

  private boolean printCoords(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityPlayer player) {

    Vector3d headVec = Util.getEyePositionEio(player);
    Vec3d start = headVec.getVec3();
    Vec3d lookVec = player.getLook(1.0F);
    double reach = 500;
    headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    RayTraceResult mop = world.rayTraceBlocks(start, headVec.getVec3());
    if (mop == null) {
      return false;
    }

    BlockPos bc = BlockCoord.get(mop);
    if (!player.isSneaking()) {
      EnumFacing dir = mop.sideHit;
      bc = bc.offset(dir);
    }

    return ModObject.itemLocationPrintout.openClientGui(world, bc, player, null, ItemLocationPrintout.GUI_ID_LOCATION_PRINTOUT_CREATE);
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }
}
