package crazypants.enderio.machine.soul;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.ItemSoulVessel;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.material.FrankenSkull;

public class SoulBinderReanimationRecipe implements IMachineRecipe {

  public static SoulBinderReanimationRecipe instance = new SoulBinderReanimationRecipe();
  
  @Override
  public String getUid() {
    return "SoulBinderReanimationRecipe";
  }

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    return Config.soulBinderReanimationRF;
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
    if(!isZombie(mobType)) {
      return new ResultStack[0];
    }
    ItemStack frankenZombie = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.FRANKEN_ZOMBIE.ordinal());
    ItemStack soulVessel = new ItemStack(EnderIO.itemSoulVessel);    
    return new ResultStack[] {new ResultStack(soulVessel), new ResultStack(frankenZombie)};
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
      return  isZombie(EnderIO.itemSoulVessel.getMobTypeFromStack(item));
    } 
    if(slot == 1) {
      return item.getItem() == EnderIO.itemFrankenSkull && item.getItemDamage() == FrankenSkull.ZOMBIE_CONTROLLER.ordinal();
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
  
  boolean isZombie(String mobType) {    
    return mobType != null && mobType.equals("Zombie");
  }

}
