package crazypants.enderio.recipe.soul;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import crazypants.enderio.ModObject;
import crazypants.enderio.recipe.IMachineRecipe;
import crazypants.enderio.recipe.MachineRecipeInput;
import crazypants.enderio.recipe.RecipeBonusType;
import crazypants.enderio.xp.XpUtil;
import crazypants.util.CapturedMob;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.ModObject.itemSoulVessel;

public abstract class AbstractSoulBinderRecipe implements IMachineRecipe, ISoulBinderRecipe {

  private final int energyRequired;
  private final String uid;
  private final int xpLevelsRequired;
  private final int xpRequired;
  
  private final List<String> supportedEntities;
  
  protected AbstractSoulBinderRecipe(int energyRequired, int xpLevelsRequired, String uid, Class<?> entityClass) {
    this(energyRequired, xpLevelsRequired, uid, EntityList.CLASS_TO_NAME.get(entityClass));
  }
  
  protected AbstractSoulBinderRecipe(int energyRequired, int xpLevelsRequired, String uid, String... entityNames) {
    this.energyRequired = energyRequired;
    this.xpLevelsRequired = xpLevelsRequired;
    this.xpRequired = XpUtil.getExperienceForLevel(xpLevelsRequired);
    this.uid = uid;
    this.supportedEntities = Arrays.asList(entityNames);
  }

  protected AbstractSoulBinderRecipe(int energyRequired, int xpLevelsRequired, String uid) {
    this(energyRequired, xpLevelsRequired, uid, new String[0]);
  }

  @Override
  public String getUid() {
    return uid;
  }
    
  @Override
  public int getExperienceLevelsRequired() {
    return xpLevelsRequired;
  }

  @Override
  public int getExperienceRequired() {
    return xpRequired;
  }

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    return getEnergyRequired();
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
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
    CapturedMob mobType = null;
    ItemStack inputItem = null;
    for(MachineRecipeInput input : inputs) {
      if (input != null && input.slotNumber == 0 && CapturedMob.containsSoul(input.item)) {
        mobType = CapturedMob.create(input.item);
      } else if (input != null && input.slotNumber == 1 && isValidInputItem(input.item)) {
        inputItem = input.item;
      }
    }
    if (!isValidInputSoul(mobType) || inputItem == null) {
      return new ResultStack[0];
    }
    ItemStack resultStack = getOutputStack(inputItem, mobType);
    ItemStack soulVessel = new ItemStack(itemSoulVessel.getItem());
    return new ResultStack[] {new ResultStack(soulVessel), new ResultStack(resultStack)};
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
    if (slot == 0) {
      CapturedMob mobType = CapturedMob.create(item);
      return mobType != null && item.getItem() == itemSoulVessel.getItem() && isValidInputSoul(mobType);
    } 
    if(slot == 1) {
      return isValidInputItem(item);
    }
    return false;    
  }

  protected boolean isValidInputSoul(CapturedMob mobType) {
    return getSupportedSouls().contains(mobType.getEntityName());
  }

  protected boolean isValidInputItem(ItemStack item) {
    return item.isItemEqual(getInputStack());
  }

  @Override
  public String getMachineName() {
    return ModObject.blockSoulBinder.getUnlocalisedName();
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
  
  protected abstract ItemStack getOutputStack(ItemStack input, CapturedMob mobType);

  @Override
  public List<String> getSupportedSouls() {    
    return supportedEntities;
  }

  @Override
  public int getEnergyRequired() {
    return energyRequired;
  }

}
