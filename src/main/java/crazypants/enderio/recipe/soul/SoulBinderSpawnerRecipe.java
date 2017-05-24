package crazypants.enderio.recipe.soul;

import java.util.List;

import com.enderio.core.common.util.EntityUtil;

import crazypants.enderio.config.Config;
import crazypants.enderio.recipe.spawner.BlockPoweredSpawner;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.ModObject.itemBrokenSpawner;

public class SoulBinderSpawnerRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderSpawnerRecipe instance = new SoulBinderSpawnerRecipe();

  public SoulBinderSpawnerRecipe() {
    super(Config.soulBinderBrokenSpawnerRF, Config.soulBinderBrokenSpawnerLevels, "SoulFuserSpawnerRecipe");
  }

  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    return mobType.toStack(itemBrokenSpawner.getItem(), input.getMetadata(), 1);
  }

  @Override
  protected boolean isValidInputSoul(CapturedMob mobType) {
    return getSupportedSouls().contains(mobType.getEntityName()) && !BlockPoweredSpawner.isBlackListed(mobType.getEntityName());
  }

  @Override
  public ItemStack getInputStack() {    
    return new ItemStack(itemBrokenSpawner.getItem());
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(itemBrokenSpawner.getItem());
  }

  @Override
  public List<String> getSupportedSouls() {    
    return EntityUtil.getAllRegisteredMobNames();
  }

  
  
}
