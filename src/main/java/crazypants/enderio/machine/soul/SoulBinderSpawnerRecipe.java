package crazypants.enderio.machine.soul;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.common.registry.EntityRegistry;

import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
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
      if(input != null && EnderIO.itemSoulVessel.containsSoul(input.item)) {
        mobType = EnderIO.itemSoulVessel.getMobTypeFromStack(input.item);
      }
    }
    if(mobType == null) {
      return new ResultStack[0];
    }
    ItemStack spawner = EnderIO.itemBrokenSpawner.createStackForMobType(mobType);
    if(spawner == null) {
      return new ResultStack[0];
    }
    ItemStack soulVessel = new ItemStack(EnderIO.itemSoulVessel);    
    return new ResultStack[] {new ResultStack(soulVessel), new ResultStack(spawner)};
  }

  @Override
  public float getExperianceForOutput(ItemStack output) {
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
      String mobType = EnderIO.itemSoulVessel.getMobTypeFromStack(item);
      return mobType != null && !EnderIO.blockPoweredSpawner.isBlackListed(mobType);
    } 
    if(slot == 1) {
      return item.getItem() == EnderIO.itemBrokenSpawner;
    }
    return false;    
  }

  private boolean isBlackListed(String entityId) {
    for(String str : Config.poweredSpawnerBlackList) {
      if(str != null && str.equals(entityId)) {
        return true;
      }
    }
    return false;
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
    return new ItemStack(EnderIO.itemBrokenSpawner);
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(EnderIO.itemBrokenSpawner);
  }

  @Override
  public List<String> getSupportedSouls() {    
    return EntityUtil.getAllRegisteredMobNames(!Config.soulVesselCapturesBosses);
  }

  @Override
  public int getEnergyRequired() {
    return Config.soulBinderBrokenSpawnerRF;
  }
  
  
}
