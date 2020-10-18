package crazypants.enderio.base.recipe.tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.material.upgrades.ItemUpgrades;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.util.Prep;
import info.loenwind.autoconfig.util.NullHelper;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TankMachineRecipe implements IMachineRecipe {

  public enum Logic {
    NONE,
    XPBOTTLE {
      @Override
      @Nonnull
      public FluidStack convertFluidResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
          @Nonnull ItemStack output) {
        FluidStack copy = recipeFluid.copy();
        copy.amount *= XpUtil.experienceToLiquid(3 + rand.nextInt(5) + rand.nextInt(5));
        return copy;
      }

      @Override
      public void executeSFX(boolean isFilling, @Nonnull World world, @Nonnull BlockPos pos) {
        if (!isFilling) {
          BlockPos eventPos = pos;
          if (world.rand.nextBoolean()) {
            eventPos = eventPos.south();
          }
          if (world.rand.nextBoolean()) {
            eventPos = eventPos.east();
          }
          world.playEvent(2002, eventPos, PotionUtils.getPotionColor(PotionTypes.WATER));
        }
      }
    },
    XP {
      @Override
      @Nonnull
      public FluidStack convertFluidResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
          @Nonnull ItemStack output) {
        FluidStack copy = recipeFluid.copy();
        copy.amount = XpUtil.experienceToLiquid(copy.amount);
        return copy;
      }
    },
    LEVELS {
      @Override
      @Nonnull
      public FluidStack convertFluidResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
          @Nonnull ItemStack output) {
        FluidStack copy = recipeFluid.copy();
        copy.amount = XpUtil.experienceToLiquid(XpUtil.getExperienceForLevel(copy.amount));
        return copy;
      }
    },
    /**
     * Calculates the fluid amount based on the Dark Steel Upgrade's level cost. Only looks at the output item. Forces the output item to be "enabled" if it not
     * already is.
     * <p>
     * see also
     * {@link crazypants.enderio.base.material.upgrades.ItemUpgrades#onItemRightClick(World, net.minecraft.entity.player.EntityPlayer, net.minecraft.util.EnumHand)}
     */
    DSU {
      @Override
      @Nonnull
      public FluidStack convertFluidResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
          @Nonnull ItemStack output) {
        IDarkSteelUpgrade upgrade = ItemUpgrades.getUpgrade(output);
        if (isFilling && upgrade != null) {
          FluidStack copy = recipeFluid.copy();
          copy.amount *= XpUtil.experienceToLiquid(XpUtil.getExperienceForLevel(upgrade.getLevelCost()));
          return copy;
        }
        return super.convertFluidResult(isFilling, input, machineFluid, recipeFluid, output);
      }

      @Override
      @Nonnull
      public ItemStack convertItemResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
          @Nonnull ItemStack output) {
        IDarkSteelUpgrade upgrade = ItemUpgrades.getUpgrade(output);
        if (isFilling && upgrade != null && !ItemUpgrades.isEnabled(output)) {
          return ItemUpgrades.setEnabled(output.copy(), true);
        }
        return super.convertItemResult(isFilling, input, machineFluid, recipeFluid, output);
      }
    };

    private static final @Nonnull Random rand = new Random();

    @Nonnull
    public ItemStack convertItemResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
        @Nonnull ItemStack output) {
      return output.copy();
    }

    @Nonnull
    public FluidStack convertFluidResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
        @Nonnull ItemStack output) {
      return recipeFluid.copy();
    }

    public void executeSFX(boolean isFilling, @Nonnull World world, @Nonnull BlockPos pos) {
    }
  }

  public static final @Nonnull FluidStack NOTHING = new FluidStack(FluidRegistry.WATER, 0);

  public class RecipeResult {
    @Nonnull
    ItemStack consumedInputItem = Prep.getEmpty();
    @Nonnull
    ItemStack remainingInputItem = Prep.getEmpty();
    @Nonnull
    ItemStack itemResult = Prep.getEmpty();
    @Nonnull
    FluidStack consumedInputFluid = NOTHING;
    @Nonnull
    FluidStack remainingInputFluid = NOTHING;

    public @Nonnull ItemStack getConsumedInputItem() {
      return consumedInputItem;
    }

    public @Nonnull ItemStack getRemainingInputItem() {
      return remainingInputItem;
    }

    public @Nonnull ItemStack getItemResult() {
      return itemResult;
    }

    public @Nonnull FluidStack getConsumedInputFluid() {
      return consumedInputFluid;
    }

    public @Nonnull FluidStack getRemainingInputFluid() {
      return remainingInputFluid;
    }
  }

  public @Nullable RecipeResult executeRecipe(@Nonnull ItemStack machineInput, @Nonnull FluidStack machineFluid) {
    RecipeResult result = new RecipeResult();

    result.itemResult = logic.convertItemResult(isFilling, machineInput, machineFluid, fluid, output.getItemStack());
    FluidStack fluidResult = logic.convertFluidResult(isFilling, machineInput, machineFluid, fluid, output.getItemStack());

    result.remainingInputItem = machineInput.copy();
    result.consumedInputItem = result.remainingInputItem.splitStack(input.getItemStack().getCount());

    if (isFilling) {
      result.remainingInputFluid = machineFluid.copy();
      result.remainingInputFluid.amount -= fluidResult.amount;
      if (result.remainingInputFluid.amount < 0) {
        return null;
      }
      result.consumedInputFluid = machineFluid.copy();
      result.consumedInputFluid.amount = fluidResult.amount;
    } else if (machineFluid.amount == 0) {
      result.remainingInputFluid = fluidResult;
      result.consumedInputFluid = machineFluid;
    } else {
      result.remainingInputFluid = machineFluid.copy();
      result.remainingInputFluid.amount += fluidResult.amount;
      result.consumedInputFluid = machineFluid.copy();
      result.consumedInputFluid.amount = 0;
    }

    return result;
  }

  private final @Nonnull String recipeName;
  private final boolean isFilling;
  private final @Nonnull Things input;
  private final @Nonnull FluidStack fluid;
  private final @Nonnull Things output;
  private final @Nonnull Logic logic;
  private final @Nonnull RecipeLevel recipelevel;

  public TankMachineRecipe(@Nonnull String recipeName, boolean isFilling, @Nonnull Things input, @Nonnull FluidStack fluid, Things output, @Nonnull Logic logic,
      @Nonnull RecipeLevel recipelevel) {
    this.recipeName = recipeName;
    this.isFilling = isFilling;
    this.input = input;
    this.fluid = fluid;
    this.output = output != null ? output : new Things();
    this.logic = logic;
    this.recipelevel = recipelevel;
  }

  @Override
  @Nonnull
  public String getUid() {
    return recipeName;
  }

  @Override
  public int getEnergyRequired(@Nonnull NNList<MachineRecipeInput> inputs) {
    return 0;
  }

  @Override
  public boolean isRecipe(@Nonnull RecipeLevel machineLevel, @Nonnull NNList<MachineRecipeInput> inputs) {
    if (!machineLevel.canMake(recipelevel)) {
      return false;
    }

    // we need exactly 2 inputs, an item and the fluid in the tank
    if (inputs.size() != 2) {
      return false;
    }

    ItemStack machineItemStack = getInputItem(inputs);

    // input item stack
    if (Prep.isInvalid(machineItemStack)) {
      return false;
    }

    if (!input.contains(machineItemStack)) {
      return false;
    }

    FluidStack machineFluid = getInputFluid(inputs);

    // filling always needs a fluid in the tank, emptying not
    if (isFilling && machineFluid.amount == 0) {
      return false;
    }

    // fluid in tank must match recipe or tank must be empty
    if (machineFluid.amount != 0 && !machineFluid.containsFluid(fluid)) {
      return false;
    }

    return true;
  }

  private @Nonnull ItemStack getInputItem(@Nonnull NNList<MachineRecipeInput> inputs) {
    for (MachineRecipeInput machineRecipeInput : inputs) {
      if (!machineRecipeInput.isFluid()) {
        return machineRecipeInput.item;
      }
    }
    return Prep.getEmpty();
  }

  private @Nonnull FluidStack getInputFluid(@Nonnull NNList<MachineRecipeInput> inputs) {
    for (MachineRecipeInput machineRecipeInput : inputs) {
      if (machineRecipeInput.isFluid()) {
        return NullHelper.first(machineRecipeInput.fluid, NOTHING);
      }
    }
    return NOTHING;
  }

  @Override
  @Nonnull
  public ResultStack[] getCompletedResult(long nextSeed, float chanceMultiplier, @Nonnull NNList<MachineRecipeInput> inputs) {
    RecipeResult recipeResult = executeRecipe(getInputItem(inputs), getInputFluid(inputs));
    if (recipeResult != null) {
      return new ResultStack[] { new ResultStack(recipeResult.itemResult), new ResultStack(recipeResult.remainingInputFluid) };
    } else {
      return new ResultStack[0];
    }
  }

  @Override
  public boolean isValidInput(@Nonnull RecipeLevel machineLevel, @Nonnull MachineRecipeInput machineInput) {
    if (machineInput.isFluid()) {
      return machineInput.fluid.containsFluid(fluid);
    } else {
      return input.contains(machineInput.item);
    }
  }

  @Override
  @Nonnull
  public String getMachineName() {
    return isFilling ? MachineRecipeRegistry.TANK_FILLING : MachineRecipeRegistry.TANK_EMPTYING;
  }

  @Override
  @Nonnull
  public List<MachineRecipeInput> getQuantitiesConsumed(@Nonnull NNList<MachineRecipeInput> inputs) {
    RecipeResult recipeResult = executeRecipe(getInputItem(inputs), getInputFluid(inputs));
    if (recipeResult == null) {
      return NNList.emptyList();
    }
    List<MachineRecipeInput> result = new ArrayList<>();
    for (MachineRecipeInput machineRecipeInput : inputs) {
      if (machineRecipeInput.isFluid()) {
        result.add(new MachineRecipeInput(machineRecipeInput.slotNumber, recipeResult.consumedInputFluid));
      } else {
        result.add(new MachineRecipeInput(machineRecipeInput.slotNumber, recipeResult.getConsumedInputItem()));
      }
    }
    return result;
  }

  public @Nonnull Logic getLogic() {
    return logic;
  }

  public @Nonnull String getRecipeName() {
    return recipeName;
  }

  public boolean isFilling() {
    return isFilling;
  }

  public @Nonnull Things getInput() {
    return input;
  }

  public @Nonnull FluidStack getFluid() {
    return fluid;
  }

  public @Nonnull Things getOutput() {
    return output;
  }

  @Override
  public @Nonnull RecipeLevel getRecipeLevel() {
    return recipelevel;
  }

}
