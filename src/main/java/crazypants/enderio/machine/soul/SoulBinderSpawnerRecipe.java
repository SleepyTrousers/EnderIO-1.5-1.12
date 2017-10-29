package crazypants.enderio.machine.soul;



import java.util.List;

import com.enderio.core.common.util.EntityUtil;

import crazypants.enderio.config.Config;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.machine.spawner.BlockPoweredSpawner;
import crazypants.util.CapturedMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SoulBinderSpawnerRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderSpawnerRecipe instance = new SoulBinderSpawnerRecipe();

  public SoulBinderSpawnerRecipe() {
    super(Config.soulBinderBrokenSpawnerRF, Config.soulBinderBrokenSpawnerLevels, "SoulFuserSpawnerRecipe");
  }

  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    return mobType.toStack(ModObject.itemBrokenSpawner.getItem(), input.getMetadata(), 1);
  }

  @Override
  protected boolean isValidInputSoul(CapturedMob mobType) {
    return getSupportedSouls().contains(mobType.getEntityName()) && !BlockPoweredSpawner.isBlackListed(mobType.getEntityName());
  }

  @Override
  public ItemStack getInputStack() {    
    return new ItemStack(ModObject.itemBrokenSpawner.getItem());
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(ModObject.itemBrokenSpawner.getItem());
  }

  @Override
  public List<ResourceLocation> getSupportedSouls() {
    return EntityUtil.getAllRegisteredMobNames();
  }

  
  
}
