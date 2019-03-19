package crazypants.enderio.base.item.eggs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemOwlEgg extends Item {

  private static final float VELOCITY_DEFAULT = 1.5F;
  private static final float INACCURACY_DEFAULT = 1.0F;
  private static final float PITCHOFFSET = 0.0F;

  public static ItemOwlEgg create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemOwlEgg(modObject);
  }

  private ItemOwlEgg(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOMaterials);
    setHasSubtypes(false);
    modObject.apply(this);
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if (!playerIn.capabilities.isCreativeMode) {
      itemStackIn.shrink(1);
    }
    worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.BLOCKS, 0.5F,
        0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
    if (!worldIn.isRemote) {
      EntityOwlEgg entityEgg = new EntityOwlEgg(worldIn, playerIn);
      // without setHeading the egg just falls to the players feet
      entityEgg.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, PITCHOFFSET, VELOCITY_DEFAULT, INACCURACY_DEFAULT);
      worldIn.spawnEntity(entityEgg);
    }
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
  }

}
