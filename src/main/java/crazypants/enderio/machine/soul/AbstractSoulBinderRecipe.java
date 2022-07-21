package crazypants.enderio.machine.soul;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.recipe.RecipeBonusType;
import crazypants.enderio.xp.XpUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;

public abstract class AbstractSoulBinderRecipe implements IMachineRecipe, ISoulBinderRecipe {

    private final int energyRequired;
    private final String uid;
    private final int xpLevelsRequired;
    private final int xpRequired;

    private final List<String> supportedEntities;

    protected AbstractSoulBinderRecipe(int energyRequired, int xpLevelsRequired, String uid, Class<?> entityClass) {
        this(energyRequired, xpLevelsRequired, uid, (String) EntityList.classToStringMapping.get(entityClass));
    }

    protected AbstractSoulBinderRecipe(int energyRequired, int xpLevelsRequired, String uid, String... entityNames) {
        this.energyRequired = energyRequired;
        this.xpLevelsRequired = xpLevelsRequired;
        this.xpRequired = XpUtil.getExperienceForLevel(xpLevelsRequired);
        this.uid = uid;
        this.supportedEntities = Arrays.asList(entityNames);
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
        for (MachineRecipeInput input : inputs) {
            if (isValidInput(input)) {
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
        for (MachineRecipeInput input : inputs) {
            if (input != null && EnderIO.itemSoulVessel.containsSoul(input.item)) {
                mobType = EnderIO.itemSoulVessel.getMobTypeFromStack(input.item);
            }
        }
        if (!getSupportedSouls().contains(mobType)) {
            return new ResultStack[0];
        }
        ItemStack resultStack = getOutputStack(mobType);
        ItemStack soulVessel = new ItemStack(EnderIO.itemSoulVessel);
        return new ResultStack[] {new ResultStack(soulVessel), new ResultStack(resultStack)};
    }

    @Override
    public float getExperienceForOutput(ItemStack output) {
        return 0;
    }

    @Override
    public boolean isValidInput(MachineRecipeInput input) {
        if (input == null || input.item == null) {
            return false;
        }
        int slot = input.slotNumber;
        ItemStack item = input.item;
        if (slot == 0) {
            String type = EnderIO.itemSoulVessel.getMobTypeFromStack(item);
            return getSupportedSouls().contains(type);
        }
        if (slot == 1) {
            return item.isItemEqual(getInputStack());
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
        for (MachineRecipeInput input : inputs) {
            if (input != null && input.item != null) {
                ItemStack resStack = input.item.copy();
                resStack.stackSize = 1;
                MachineRecipeInput mri = new MachineRecipeInput(input.slotNumber, resStack);
                result.add(mri);
            }
        }
        return result;
    }

    protected ItemStack getOutputStack(String mobType) {
        return getOutputStack();
    }

    @Override
    public List<String> getSupportedSouls() {
        return supportedEntities;
    }

    @Override
    public int getEnergyRequired() {
        return energyRequired;
    }
}
