package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class MachineRecipeInput {

    public static ItemStack getInputForSlot(int slot, MachineRecipeInput... inputs) {
        for (MachineRecipeInput ri : inputs) {
            if (ri.slotNumber == slot) {
                return ri.item;
            }
        }
        return null;
    }

    public static MachineRecipeInput create(int slotNumber, ItemStack item) {
        return new MachineRecipeInput(slotNumber, item);
    }

    public final int slotNumber;
    public final ItemStack item;

    public final FluidStack fluid;

    public MachineRecipeInput(int slotNumber, ItemStack item) {
        this.slotNumber = slotNumber;
        this.item = item;
        fluid = null;
    }

    public MachineRecipeInput(int slotNumber, FluidStack fluid) {
        this.slotNumber = slotNumber;
        item = null;
        this.fluid = fluid;
    }

    public MachineRecipeInput(int slotNumber, ItemStack item, FluidStack fluid) {
        this.slotNumber = slotNumber;
        this.item = item;
        this.fluid = fluid;
    }

    public MachineRecipeInput copy() {
        if (isFluid()) {
            return new MachineRecipeInput(slotNumber, fluid.copy());
        } else {
            return new MachineRecipeInput(slotNumber, item == null ? (ItemStack) null : item.copy());
        }
    }

    public static MachineRecipeInput readFromNBT(NBTTagCompound root) {
        int slotNum = root.getInteger("slotNum");
        ItemStack item = null;
        FluidStack fluid = null;
        if (root.hasKey("itemStack")) {
            NBTTagCompound stackRoot = root.getCompoundTag("itemStack");
            item = ItemStack.loadItemStackFromNBT(stackRoot);
        } else if (root.hasKey("fluidStack")) {
            NBTTagCompound stackRoot = root.getCompoundTag("fluidStack");
            fluid = FluidStack.loadFluidStackFromNBT(stackRoot);
        }
        return new MachineRecipeInput(slotNum, item, fluid);
    }

    public void writeToNbt(NBTTagCompound root) {
        if (item != null) {
            NBTTagCompound stackRoot = new NBTTagCompound();
            item.writeToNBT(stackRoot);
            root.setTag("itemStack", stackRoot);
        } else if (fluid != null) {
            NBTTagCompound stackRoot = new NBTTagCompound();
            fluid.writeToNBT(stackRoot);
            root.setTag("fluidStack", stackRoot);
        }
        root.setInteger("slotNum", slotNumber);
    }

    public boolean isFluid() {
        return fluid != null;
    }
}
