package crazypants.enderio.integration.tic.modifiers;

import javax.annotation.Nonnull;

import crazypants.enderio.base.integration.tic.ITicModifierHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tinkering.TinkersItem;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ModBeheading;

public class TicModifierHandler implements ITicModifierHandler {

  public static final @Nonnull TicModifierHandler instance = new TicModifierHandler();

  protected boolean isToolWithTrait(@Nonnull ItemStack itemStack, ITrait trait) {
    return TinkerUtil.hasTrait(TagUtil.getTagSafe(itemStack), trait.getIdentifier());
  }

  protected int getModifierLevel(@Nonnull ItemStack itemStack, Modifier modifier) {
    NBTTagCompound tag = TinkerUtil.getModifierTag(itemStack, modifier.getIdentifier());
    return ModifierNBT.readTag(tag).level;
  }

  @Override
  public int getBehadingLevel(@Nonnull ItemStack itemStack) {
    int level = getModifierLevel(itemStack, TinkerModifiers.modBeheading);
    if (level == 0) {
      level = getModifierLevel(itemStack, ModBeheading.CLEAVER_BEHEADING_MOD);
    }
    return level;
  }

  @Override
  public boolean isTinkerItem(@Nonnull ItemStack itemStack) {
    return itemStack.getItem() instanceof TinkersItem;
  }

  @Override
  public boolean isBroken(ItemStack itemStack) {
    return ToolHelper.isBroken(itemStack);
  }

}
