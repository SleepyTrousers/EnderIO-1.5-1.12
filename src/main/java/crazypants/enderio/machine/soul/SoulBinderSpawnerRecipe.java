package crazypants.enderio.machine.soul;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.init.EIOBlocks;
import crazypants.enderio.init.EIOItems;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.spawner.PoweredSpawnerConfig;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import crazypants.util.EntityUtil;

public class SoulBinderSpawnerRecipe implements IMachineRecipe, ISoulBinderRecipe {

  public static SoulBinderSpawnerRecipe instance = new SoulBinderSpawnerRecipe();
  
  @Override
  public String getUid() {
    return "SoulFuserSpawnerRecipe";
  }

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    return Config.soulBinderBrokenSpawnerRF;
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  @Override
  public int getExperienceRequired() {   
    return Config.soulBinderBrokenSpawnerLevels;
  }

  @Override
  public boolean isRecipe(MachineRecipeInput... inputs) {
    int validCount = 0;
    for(MachineRecipeInput input : inputs) {
      if(isValidInput(input)) {
        validCount++;
      } else {
        return false;
      }
    }
    return validCount == 2;
  }

  @Override
  public ResultStack[] getCompletedResult(float randomChance, MachineRecipeInput... inputs) {
    String mobType = null;
    for(MachineRecipeInput input : inputs) {
      if(input != null && EIOItems.itemSoulVessel.containsSoul(input.item)) {
        mobType = EIOItems.itemSoulVessel.getMobTypeFromStack(input.item);
      }
    }
    if(mobType == null) {
      return new ResultStack[0];
    }
    ItemStack spawner = EIOItems.itemBrokenSpawner.createStackForMobType(mobType);
    if(spawner == null) {
      return new ResultStack[0];
    }
    ItemStack soulVessel = new ItemStack(EIOItems.itemSoulVessel);    
    return new ResultStack[] {new ResultStack(soulVessel), new ResultStack(spawner)};
  }

  @Override
  public float getExperienceForOutput(ItemStack output) {
    return 0;
  }

  @Override
  public boolean isValidInput(MachineRecipeInput input) {
    if(input == null || input.item == null) {
      return false;
    }
    int slot = input.slotNumber;
    ItemStack item = input.item;
    if(slot == 0) {
      String mobType = EIOItems.itemSoulVessel.getMobTypeFromStack(item);
      return mobType != null && !EIOBlocks.blockPoweredSpawner.isBlackListed(mobType);
    } 
    if(slot == 1) {
      return item.getItem() == EIOItems.itemBrokenSpawner;
    }
    return false;    
  }

  private boolean isBlackListed(String entityId) {
    return PoweredSpawnerConfig.getInstance().isBlackListed(entityId);
  }
  
  @Override
  public String getMachineName() {
    return ModObject.blockSoulBinder.unlocalisedName;
  }

  @Override
  public List<MachineRecipeInput> getQuantitiesConsumed(MachineRecipeInput[] inputs) {    
    List<MachineRecipeInput> result = new ArrayList<MachineRecipeInput>(inputs.length);
    for(MachineRecipeInput input : inputs) {
      if(input != null && input.item != null) {
        ItemStack resStack = input.item.copy();
        resStack.stackSize = 1;
        MachineRecipeInput mri = new MachineRecipeInput(input.slotNumber, resStack);
        result.add(mri);
      }      
    }    
    return result;
  }

  @Override
  public ItemStack getInputStack() {    
    return new ItemStack(EIOItems.itemBrokenSpawner);
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(EIOItems.itemBrokenSpawner);
  }

  @Override
  public List<String> getSupportedSouls() {
    List<String> res = EntityUtil.getAllRegisteredMobNames(!Config.soulVesselCapturesBosses);    
    return res;
  }

  @Override
  public int getEnergyRequired() {
    return Config.soulBinderBrokenSpawnerRF;
  }
  
  
}
