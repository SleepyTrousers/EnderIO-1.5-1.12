package crazypants.enderio.base.recipe.tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

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
      FluidStack convertFluidResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
          @Nonnull ItemStack output) {
        FluidStack copy = recipeFluid.copy();
        copy.amount *= XpUtil.experienceToLiquid(3 + rand.nextInt(5) + rand.nextInt(5));
        return copy;
      }

      @Override
      void executeSFX(boolean isFilling, @Nonnull World world, @Nonnull BlockPos pos) {
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
    AA_SOLID_XP;

    private static final @Nonnull Random rand = new Random();

    @Nonnull
    ItemStack convertItemResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
        @Nonnull ItemStack output) {
      return output;
    }

    @Nonnull
    FluidStack convertFluidResult(boolean isFilling, @Nonnull ItemStack input, @Nonnull FluidStack machineFluid, @Nonnull FluidStack recipeFluid,
        @Nonnull ItemStack output) {
      return recipeFluid;
    }

    void executeSFX(boolean isFilling, @Nonnull World world, @Nonnull BlockPos pos) {
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

  public @Nonnull RecipeResult executeRecipe(@Nonnull ItemStack machineInput, @Nonnull FluidStack machineFluid) {
    RecipeResult result = new RecipeResult();

    result.itemResult = logic.convertItemResult(isFilling, machineInput, machineFluid, fluid, output.getItemStack());
    FluidStack fluidResult = logic.convertFluidResult(isFilling, machineInput, machineFluid, fluid, output.getItemStack());

    result.remainingInputItem = machineInput.copy();
    result.consumedInputItem = result.remainingInputItem.splitStack(input.getItemStack().getCount());

    if (isFilling) {
      result.remainingInputFluid = machineFluid.copy();
      result.remainingInputFluid.amount -= fluidResult.amount;
      result.consumedInputFluid = machineFluid.copy();
      result.consumedInputFluid.amount = fluidResult.amount;
    } else if (machineFluid.amount == 0) {
      result.remainingInputFluid = fluidResult.copy();
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

    FluidStack machineFluid = getInputFluid(inputs);
    ItemStack machineItemStack = getInputItem(inputs);

    // input item stack
    if (Prep.isInvalid(machineItemStack)) {
      return false;
    }

    if (!input.contains(machineItemStack)) {
      return false;
    }

    // filling always needs a fluid in the tank, emptying not
    if (isFilling && machineFluid.amount == 0) {
      return false;
    }

    // fluid in tank must match recipe or tank must be empty
    if (machineFluid.amount > 0 && !machineFluid.containsFluid(fluid)) {
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
    return new ResultStack[] { new ResultStack(recipeResult.itemResult), new ResultStack(recipeResult.remainingInputFluid) };
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
    return MachineRecipeRegistry.TANK;
  }

  @Override
  @Nonnull
  public List<MachineRecipeInput> getQuantitiesConsumed(@Nonnull NNList<MachineRecipeInput> inputs) {
    RecipeResult recipeResult = executeRecipe(getInputItem(inputs), getInputFluid(inputs));
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

}
