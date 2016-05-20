package crazypants.enderio.machine.soul;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.EntityUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.util.CapturedMob;

public class SoulBinderSpawnerRecipe extends AbstractSoulBinderRecipe {

  public static SoulBinderSpawnerRecipe instance = new SoulBinderSpawnerRecipe();

  public SoulBinderSpawnerRecipe() {
    super(Config.soulBinderBrokenSpawnerRF, Config.soulBinderBrokenSpawnerLevels, "SoulFuserSpawnerRecipe");
  }

  @Override
  protected ItemStack getOutputStack(ItemStack input, CapturedMob mobType) {
    return mobType.toStack(EnderIO.itemBrokenSpawner, input.getMetadata(), 1);
  }

  @Override
  protected boolean isValidInputSoul(CapturedMob mobType) {
    return getSupportedSouls().contains(mobType.getEntityName()) && !EnderIO.blockPoweredSpawner.isBlackListed(mobType.getEntityName());
  }

  @Override
  public ItemStack getInputStack() {    
    return new ItemStack(EnderIO.itemBrokenSpawner);
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(EnderIO.itemBrokenSpawner);
  }

  @Override
  public List<String> getSupportedSouls() {    
    return EntityUtil.getAllRegisteredMobNames();
  }

  
  
}
