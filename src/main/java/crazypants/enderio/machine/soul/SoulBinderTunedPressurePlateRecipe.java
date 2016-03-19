package crazypants.enderio.machine.soul;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.enderio.core.common.util.EntityUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.painter.blocks.EnumPressurePlateType;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import crazypants.enderio.xp.XpUtil;

import static crazypants.util.NbtValue.MOBTYPE;

public class SoulBinderTunedPressurePlateRecipe implements IMachineRecipe, ISoulBinderRecipe {

  public static SoulBinderTunedPressurePlateRecipe instance1 = new SoulBinderTunedPressurePlateRecipe(false, "iTunesRecipe");
  public static SoulBinderTunedPressurePlateRecipe instance2 = new SoulBinderTunedPressurePlateRecipe(true, "winampRecipe");

  private final boolean silent;
  private final String uid;

  public SoulBinderTunedPressurePlateRecipe(boolean silent, String uid) {
    this.silent = silent;
    this.uid = uid;
  }

  @Override
  public String getUid() {
    return "SoulFuser:" + uid;
  }

  @Override
  public int getEnergyRequired(MachineRecipeInput... inputs) {
    return Config.soulBinderTunedPressurePlateRF;
  }

  @Override
  public RecipeBonusType getBonusType(MachineRecipeInput... inputs) {
    return RecipeBonusType.NONE;
  }

  @Override
  public int getExperienceLevelsRequired() {
    return Config.soulBinderTunedPressurePlateLevels;
  }

  @Override
  public int getExperienceRequired() {
    return XpUtil.getExperienceForLevel(Config.soulBinderTunedPressurePlateLevels);
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
    ItemStack inPlate = null;
    for(MachineRecipeInput input : inputs) {
      if(input != null && EnderIO.itemSoulVessel.containsSoul(input.item)) {
        mobType = EnderIO.itemSoulVessel.getMobTypeFromStack(input.item);
      }
      if (input != null && Block.getBlockFromItem(input.item.getItem()) == EnderIO.blockPaintedPressurePlate) {
        inPlate = input.item;
      }
    }
    if (mobType == null || inPlate == null) {
      return new ResultStack[0];
    }
    final ItemStack result = MOBTYPE.setStringCopy(inPlate, mobType, 1);
    result.setItemDamage(EnumPressurePlateType.getMetaFromType(EnumPressurePlateType.TUNED, silent));
    return new ResultStack[] { new ResultStack(new ItemStack(EnderIO.itemSoulVessel)), new ResultStack(result) };
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
      String mobType = EnderIO.itemSoulVessel.getMobTypeFromStack(item);
      return mobType != null && !EnderIO.blockPoweredSpawner.isBlackListed(mobType);
    } 
    if(slot == 1) {
      if (Block.getBlockFromItem(item.getItem()) == EnderIO.blockPaintedPressurePlate) {
        EnumPressurePlateType type = EnumPressurePlateType.getTypeFromMeta(item.getMetadata());
        boolean silentFromMeta = EnumPressurePlateType.getSilentFromMeta(item.getMetadata());
        return (type == EnumPressurePlateType.SOULARIUM || type == EnumPressurePlateType.TUNED) && silentFromMeta == silent;
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
    return new ItemStack(EnderIO.blockPaintedPressurePlate, 1, EnumPressurePlateType.SOULARIUM.getMetaFromType(silent));
  }

  @Override
  public ItemStack getOutputStack() {
    return new ItemStack(EnderIO.blockPaintedPressurePlate, 1, EnumPressurePlateType.TUNED.getMetaFromType(silent));
  }

  @Override
  public List<String> getSupportedSouls() {
    List<String> res = EntityUtil.getAllRegisteredMobNames(false);
    return res;
  }

  @Override
  public int getEnergyRequired() {
    return Config.soulBinderTunedPressurePlateRF;
  }
  
  
}
