package crazypants.enderio.recipe.soul;

import java.util.List;

import com.enderio.core.common.util.EntityUtil;

import crazypants.enderio.block.painted.EnumPressurePlateType;
import crazypants.enderio.config.Config;
import crazypants.util.CapturedMob;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.init.ModObject.blockPaintedPressurePlate;

public class SoulBinderTunedPressurePlateRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderTunedPressurePlateRecipe instance1 = new SoulBinderTunedPressurePlateRecipe(false, "iTunesRecipe");
  public static SoulBinderTunedPressurePlateRecipe instance2 = new SoulBinderTunedPressurePlateRecipe(true, "winampRecipe");

  private final boolean silent;

  public SoulBinderTunedPressurePlateRecipe(boolean silent, String uid) {
    super(Config.soulBinderTunedPressurePlateRF, Config.soulBinderTunedPressurePlateLevels, "SoulFuser:" + uid);
    this.silent = silent;
  }

  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    ItemStack result = input.copy();
    result.setItemDamage(EnumPressurePlateType.getMetaFromType(EnumPressurePlateType.TUNED, silent));
    result.setTagCompound(mobType.toNbt(result.getTagCompound()));
    result.stackSize = 1;
    return result;
  }

  @Override
  protected boolean isValidInputItem(ItemStack item) {
    if (Block.getBlockFromItem(item.getItem()) == blockPaintedPressurePlate.getBlock()) {
      EnumPressurePlateType type = EnumPressurePlateType.getTypeFromMeta(item.getMetadata());
      boolean silentFromMeta = EnumPressurePlateType.getSilentFromMeta(item.getMetadata());
      return (type == EnumPressurePlateType.SOULARIUM || type == EnumPressurePlateType.TUNED) && silentFromMeta == silent;
    }
    return false;
  }

  @Override
  public ItemStack getInputStack() {    
    return new ItemStack(blockPaintedPressurePlate.getBlock(), 1, EnumPressurePlateType.SOULARIUM.getMetaFromType(silent));
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(blockPaintedPressurePlate.getBlock(), 1, EnumPressurePlateType.TUNED.getMetaFromType(silent));
  }

  @Override
  public List<String> getSupportedSouls() {
    List<String> res = EntityUtil.getAllRegisteredMobNames();
    return res;
  }

}
