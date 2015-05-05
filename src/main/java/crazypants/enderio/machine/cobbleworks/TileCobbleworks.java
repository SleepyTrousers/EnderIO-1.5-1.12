package crazypants.enderio.machine.cobbleworks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.framework.AbstractTileFramework;
import crazypants.enderio.machine.framework.IFrameworkMachine;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.PowerHandlerUtil;

public class TileCobbleworks extends AbstractTileFramework implements IFrameworkMachine {

  static final int SLOTS_PER_WORK = 4;
  static final int WORKS = 3;

  private static Item alloySmelter;
  private static Item sagMill;
  private static Item crafter;

  private Set<Item> upgradeMachines = new HashSet<Item>();
  private Mapping[] outputMapping = new Mapping[1 + SLOTS_PER_WORK * WORKS];
  private boolean inputsChanged = true;
  private int capTickLimit = 0;

  public TileCobbleworks() {
    super(new SlotDefinition(WORKS, 1 + SLOTS_PER_WORK * WORKS, 1));
    upgradeMachines.clear();
    upgradeMachines.add(alloySmelter = Item.getItemFromBlock(EnderIO.blockAlloySmelter));
    upgradeMachines.add(sagMill = Item.getItemFromBlock(EnderIO.blockCrusher));
    upgradeMachines.add(crafter = Item.getItemFromBlock(EnderIO.blockCrafter));
  }

  @Override
  public String getMachineName() {
    return ModObject.blockCobbleworks.unlocalisedName;
  }

  @Override
  public void init() {
    super.init();
    inputsChanged = true;
    computeOutputMapping();
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    if (slotDefinition.isInputSlot(i)) {
      return inventory[i] == null && upgradeMachines.contains(itemstack.getItem());
    } else if (slotDefinition.isOutputSlot(i)) {
      Mapping mapping = outputMapping[i - slotDefinition.minOutputSlot];
      return mapping != null && mapping.itemStack != null && itemstack != null && mapping.itemStack.isItemEqual(itemstack);
    } else {
      return false;
    }
  }

  @Override
  public int getInventoryStackLimit(int slot) {
    if (slotDefinition.isInputSlot(slot)) {
      return 1;
    }
    return 64;
  }

  @Override
  public int getInventoryStackLimit() {
    return getInventoryStackLimit(0);
  }

  // will be sent to the client to allow for visual effects
  private int active = 0;
  private int activeWorks = 0;

  @Override
  public boolean isActive() {
    return hasPower() && redstoneCheckPassed && active > 0;
  }

  boolean isActive(int work) {
    if (work == 0) {
      return activeWorks != 0;
    } else if (work == 1) {
      return (activeWorks & 8190) != 0; // 0b1111111111110
    } else if (work == 2) {
      return (activeWorks & 8160) != 0; // 0b1111111100000
    } else if (work == 3) {
      return (activeWorks & 7680) != 0; // 0b1111000000000
    } else {
      return false;
    }
  }

  @Override
  protected void updateEntityClient() {
    if (active > 0) {
      active--;
    }
    super.updateEntityClient();
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    boolean updateClient = computeOutputMapping();
    if (redstoneCheckPassed && reCraft()) {
      active = 40;
      return true;
    } else {
      if (active > 0) {
        active--;
      }
      return updateClient;
    }
  }

  private int outputSlotNo(int no) {
    return no + slotDefinition.minOutputSlot;
  }

  private ItemStack outputSlot(int no) {
    return inventory[outputSlotNo(no)];
  }

  private void outputSlot(int no, ItemStack is) {
    inventory[outputSlotNo(no)] = is;
  }

  int outputSlotNo(int work, int no) {
    return outputSlotNo(outputMappingNo(work, no));
  }

  private static int outputMappingNo(int work, int no) {
    return work == 0 ? 0 : (work - 1) * SLOTS_PER_WORK + no;
  }

  private void outputMapping(int work, int no, Mapping mapping) {
    outputMapping[outputMappingNo(work, no)] = mapping;
  }

  private Mapping outputMapping(int work, int no) {
    return outputMapping[outputMappingNo(work, no)];
  }

  int inputSlotNo(int no) {
    return no + slotDefinition.minInputSlot - 1;
  }

  private ItemStack inputSlot(int no) {
    return inventory[inputSlotNo(no)];
  }

  private static void clear(Object[] list) {
    for (int i = 0; i < list.length; i++) {
      list[i] = null;
    }
  }

  private List<Mapping> getInputForWork(int work) {
    List<Mapping> result = new ArrayList<Mapping>();
    if (work == 1) {
      result.add(outputMapping(0, 0));
    } else {
      for (int i = 1; i <= SLOTS_PER_WORK; i++) {
        result.add(outputMapping(work - 1, i));
      }
    }

    return result;
  }

  private boolean computeOutputMapping() {
    if (!inputsChanged) {
      return false;
    }
    inputsChanged = false;

    clear(outputMapping);

    outputMapping(0, 0, new Mapping(OperationType.NONE, new ItemStack(Item.getItemFromBlock(Blocks.cobblestone), 1),
        Config.cobbleWorksRfPerCobblestone, -1, 0));

    for (int work = 1; work <= WORKS; work++) {
      ItemStack machine = inputSlot(work);
      List<Mapping> input = getInputForWork(work);
      List<Mapping> output = new ArrayList<Mapping>();
      if (machine != null && machine.getItem() != null && !input.isEmpty()) {
        for (Mapping mapping : input) {
          if (mapping != null && mapping.itemStack != null) {
            if (machine.getItem() == crafter) {
              computeCrafterOutput(output, mapping);
            } else if (machine.getItem() == alloySmelter) {
              computeMachineOutput(output, mapping, ModObject.blockAlloySmelter.unlocalisedName, OperationType.SMELTING);
            } else if (machine.getItem() == sagMill) {
              computeMachineOutput(output, mapping, ModObject.blockSagMill.unlocalisedName, OperationType.CRUSHING);
            } else {
              // invalid machine...
            }
          }
        }
      }
      
      for (Mapping mapping : output) {
        boolean done = false;
        // do we already have this item in a previous step?
        if (outputMapping(0, 0) != null && outputMapping(0, 0).itemStack.isItemEqual(mapping.itemStack)) {
          done = true;
        }
        for (int w = 1; !done && w < work; w++) {
          for (int i = 1; !done && i <= SLOTS_PER_WORK; i++) {
            if (outputMapping(w, i) != null && outputMapping(w, i).itemStack.isItemEqual(mapping.itemStack)) {
              done = true;
            }
          }
        }

        // do we already have it in this step? If yes, keep the cheaper recipe
        for (int i = 1; !done && i <= SLOTS_PER_WORK; i++) {
          if (outputMapping(work, i) != null && outputMapping(work, i).itemStack.isItemEqual(mapping.itemStack)) {
            done = true;
            if (outputMapping(work, i).costInRF > mapping.costInRF) {
              mapping.position = outputMappingNo(work, i);
              outputMapping(work, i, mapping);
            }
          }
        }

        // can we add it to this work's slots?
        for (int i = 1; !done && i <= SLOTS_PER_WORK; i++) {
          if (outputMapping(work, i) == null) {
            done = true;
            mapping.position = outputMappingNo(work, i);
            outputMapping(work, i, mapping);
          }
        }
        if (!done) {
          // More outputs than slots. Log this?
        }
      }
    }
    return true;
  }

  private static void computeMachineOutput(List<Mapping> output, Mapping input, String machineName, OperationType operationType) {
    ItemStack stackcopy = input.itemStack.copy();
    stackcopy.stackSize = 1; // otherwise we'd get a 200% energy bonus on
                             // vanilla smelting
    MachineRecipeInput mri = new MachineRecipeInput(0, stackcopy);
    List<IMachineRecipe> recipes = MachineRecipeRegistry.instance.getRecipesForInput(machineName, mri);
    for (IMachineRecipe recipe : recipes) {
      ResultStack[] completedResult = recipe.getCompletedResult(0, mri);
      for (ResultStack resultStack : completedResult) {
        if (resultStack.item != null) {
          output.add(new Mapping(operationType, resultStack.item.copy(), recipe.getEnergyRequired(mri), input.position, 1));
        }
      }
    }
  }

  private void computeCrafterOutput(List<Mapping> output, Mapping input) {
    InventoryCrafting inv = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(EntityPlayer var1) {
        return false;
      }
    }, 3, 3);
    // 3x3
    for (int i = 0; i < 9; i++) {
      inv.setInventorySlotContents(i, input.itemStack);
    }
    ItemStack crafted = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    if (crafted != null) {
      output.add(new Mapping(OperationType.CRAFTING, crafted.copy(), Config.crafterRfPerCraft, input.position, 9));
    }
    // 2x2
    for (int i = 0; i < 9; i++) {
      inv.setInventorySlotContents(i, null);
    }
    inv.setInventorySlotContents(0, input.itemStack);
    inv.setInventorySlotContents(1, input.itemStack);
    inv.setInventorySlotContents(3, input.itemStack);
    inv.setInventorySlotContents(4, input.itemStack);
    crafted = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    if (crafted != null) {
      output.add(new Mapping(OperationType.CRAFTING, crafted.copy(), Config.crafterRfPerCraft, input.position, 4));
    }
    // 1x1
    for (int i = 0; i < 9; i++) {
      inv.setInventorySlotContents(i, null);
    }
    inv.setInventorySlotContents(0, input.itemStack);
    crafted = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    if (crafted != null) {
      output.add(new Mapping(OperationType.CRAFTING, crafted.copy(), Config.crafterRfPerCraft, input.position, 1));
    }
    // 3x2 (glass panes, cobblestone walls)
    for (int i = 0; i < 6; i++) {
      inv.setInventorySlotContents(i, input.itemStack);
    }
    for (int i = 6; i < 9; i++) {
      inv.setInventorySlotContents(i, null);
    }
    crafted = CraftingManager.getInstance().findMatchingRecipe(inv, worldObj);
    if (crafted != null) {
      output.add(new Mapping(OperationType.CRAFTING, crafted.copy(), Config.crafterRfPerCraft, input.position, 4));
    }
  }

  private int applyDiscounts(int RFcost, OperationType operationType) {
    int cost = RFcost;
    switch (operationType) {
    case CRAFTING:
      cost -= cost * Config.cobbleWorksRfDiscountForCrafting / 100;
      break;
    case CRUSHING:
      cost -= cost * Config.cobbleWorksRfDiscountForCrushing / 100;
      break;
    case SMELTING:
      cost -= cost * Config.cobbleWorksRfDiscountForSmelting / 100;
      break;
    default:
      break;
    }
    switch (getCapacitorType()) {
    case ENDER_CAPACITOR:
      cost -= cost * Config.cobbleWorksRfDiscountPerUpgrade / 100;
    case ACTIVATED_CAPACITOR:
      cost -= cost * Config.cobbleWorksRfDiscountPerUpgrade / 100;
    default:
    }
    if (cost < 0) {
      cost = 0;
    }
    return cost;
  }

  private boolean reCraft() {
    if (capTickLimit < getCapacitor().getMaxEnergyExtracted()) {
      capTickLimit += getCapacitor().getMaxEnergyExtracted();
    }
    boolean reSync = false;
    for (int i = outputMapping.length - 1; i >= 0; i--) {
      Mapping mapping = outputMapping[i];
      if (mapping != null) {
        ItemStack slot = outputSlot(mapping.position);
        if (slot == null) {
          slot = mapping.itemStack.copy();
          slot.stackSize = 0;
        }
        if (mapping.itemStack.isItemEqual(slot)) {
          boolean goodToGo = true;
          ItemStack parentStack = null;
          if (mapping.parent >= 0) {
            parentStack = outputSlot(mapping.parent);
            if (parentStack == null || parentStack.stackSize < mapping.inputAmount) {
              goodToGo = false;
            }
          }
          boolean parentChanged = false;
          boolean slotChanged = false;
          while (goodToGo && slot.stackSize < slot.getMaxStackSize()
              && slot.stackSize + mapping.itemStack.stackSize <= slot.getMaxStackSize()) {
            if (usePower(applyDiscounts(mapping.costInRF, mapping.operationType))) {
              slot.stackSize += mapping.itemStack.stackSize;
              slotChanged = true;
              activeWorks |= 1 << i;
              if (parentStack != null) {
                parentStack.stackSize -= mapping.inputAmount;
                parentChanged = true;
                if (parentStack.stackSize < mapping.inputAmount) {
                  goodToGo = false;
                  if (parentStack.stackSize <= 0) {
                    parentStack = null;
                  }
                }
              }
            } else {
              goodToGo = false;
            }
          }
          if (parentChanged) {
            outputSlot(mapping.parent, parentStack);
            reSync = true;
          }
          if (slotChanged) {
            if (slot.stackSize > 0) {
              outputSlot(mapping.position, slot);
            } else {
              outputSlot(mapping.position, null);
            }
            reSync = true;
          }
        }
      } else {
        // there is a wrong item in an output slot. Do nothing.
      }
    }
    return reSync;
  }

  public boolean usePower(int wantToUse) {
    if (wantToUse > getEnergyStored() || (wantToUse > capTickLimit && capTickLimit < getCapacitor().getMaxEnergyExtracted())) {
      return false;
    } else {
      setEnergyStored(getEnergyStored() - wantToUse);
      capTickLimit -= wantToUse;
      return true;
    }
  }

  @Override
  public void onCapacitorTypeChange() {
    switch (getCapacitorType()) {
    case BASIC_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.powerConduitTierOneRF, 100000, Config.powerConduitTierOneRF));
      break;
    case ACTIVATED_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.powerConduitTierTwoRF, 200000, Config.powerConduitTierTwoRF));
      break;
    case ENDER_CAPACITOR:
      setCapacitor(new BasicCapacitor(Config.powerConduitTierThreeRF, 500000, Config.powerConduitTierThreeRF));
      break;
    }
  }

  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    inputsChanged = true;
    computeOutputMapping();
    active = nbtRoot.getInteger("cwactive");
    activeWorks = nbtRoot.getInteger("activeWorks");
  }

  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    nbtRoot.setInteger("cwactive", active);
    nbtRoot.setInteger("activeWorks", activeWorks);
    activeWorks = 0;
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if (slotDefinition.isInputSlot(slot)) {
      inputsChanged = true;
    }
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    if (slotDefinition.isInputSlot(fromSlot)) {
      inputsChanged = true;
    }
    return super.decrStackSize(fromSlot, amount);
  }

  private static enum OperationType {
    NONE, CRAFTING, SMELTING, CRUSHING;
  }

  private static class Mapping {
    final OperationType operationType;
    final ItemStack itemStack;
    final int costInRF;
    final int parent;
    int position = 0;
    final int inputAmount;

    public Mapping(OperationType operationType, ItemStack itemStack, int costInRF, int parent, int inputAmount) {
      this.operationType = operationType;
      this.itemStack = itemStack;
      this.costInRF = costInRF;
      this.parent = parent;
      this.inputAmount = inputAmount;
    }
  }

  @Override
  public boolean hasTank(TankSlot tankSlot) {
    return true;
  }

  @Override
  public Fluid getTankFluid(TankSlot tankSlot) {
    switch (tankSlot) {
    case FRONT_LEFT:
    case BACK_RIGHT:
      return FluidRegistry.LAVA;
    case FRONT_RIGHT:
    case BACK_LEFT:
      return FluidRegistry.WATER;
    }
    return null;
  }

  @Override
  public boolean hasController() {
    return true;
  }

  @Override
  public AbstractMachineBlock getSlotMachine(TankSlot tankSlot) {
    if (tankSlot == TankSlot.FRONT_LEFT) {
      return null;
    }
    ItemStack stack = inputSlot(tankSlot.ordinal());
    if (stack != null && stack.getItem() != null) {
      if (stack.getItem() == crafter) {
        return EnderIO.blockCrafter;
      } else if (stack.getItem() == alloySmelter) {
        return EnderIO.blockAlloySmelter;
      } else if (stack.getItem() == sagMill) {
        return EnderIO.blockCrusher;
      }
    }
    return null;
  }

  @Override
  public String getControllerModelName() {
    return EnderIO.blockCobbleworks.getControllerModelName();
  }

}
