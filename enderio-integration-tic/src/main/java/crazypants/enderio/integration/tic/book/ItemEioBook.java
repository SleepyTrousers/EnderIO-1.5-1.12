package crazypants.enderio.integration.tic.book;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemEioBook extends Item {

  public static ItemEioBook create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemEioBook(modObject);
  }

  protected ItemEioBook(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIO);
    modObject.apply(this);
    setHasSubtypes(false);
    setMaxDamage(0);
    setMaxStackSize(1);
  }

  @Override
  public boolean isDamageable() {
    return false; // sic! super.isDamageable() is stupid
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
    ItemStack itemStack = playerIn.getHeldItem(handIn);
    if (worldIn.isRemote) {
      EioBook.INSTANCE.openGui(itemStack);
    }
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
  }

}
