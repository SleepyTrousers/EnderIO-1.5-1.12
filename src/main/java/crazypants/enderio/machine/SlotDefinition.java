package crazypants.enderio.machine;

public class SlotDefinition {

    public final int minUpgradeSlot;
    public final int maxUpgradeSlot;

    public final int minInputSlot;
    public final int maxInputSlot;

    public final int minOutputSlot;
    public final int maxOutputSlot;

    public SlotDefinition(int numInputs, int numOutputs, int numUpgradeSlots) {
        this.minInputSlot = 0;
        this.maxInputSlot = numInputs - 1;
        this.minOutputSlot = numOutputs > 0 ? numInputs : -10;
        this.maxOutputSlot = minOutputSlot + (numOutputs - 1);
        this.minUpgradeSlot = numUpgradeSlots > 0 ? numInputs + numOutputs : -1;
        this.maxUpgradeSlot = minUpgradeSlot + (numUpgradeSlots - 1);
    }

    public SlotDefinition(int numInputs, int numOutputs) {
        this.minInputSlot = 0;
        this.maxInputSlot = numInputs - 1;
        this.minOutputSlot = numOutputs > 0 ? numInputs : -10;
        this.maxOutputSlot = minOutputSlot + (numOutputs - 1);
        this.minUpgradeSlot = Math.max(maxInputSlot, maxOutputSlot) + 1;
        this.maxUpgradeSlot = minUpgradeSlot;
    }

    public SlotDefinition(
            int minInputSlot,
            int maxInputSlot,
            int minOutputSlot,
            int maxOutputSlot,
            int minUpgradeSlot,
            int maxUpgradeSlot) {
        this.minInputSlot = minInputSlot;
        this.maxInputSlot = maxInputSlot;
        this.minOutputSlot = minOutputSlot;
        this.maxOutputSlot = maxOutputSlot;
        this.minUpgradeSlot = minUpgradeSlot;
        this.maxUpgradeSlot = maxUpgradeSlot;
    }

    public boolean isUpgradeSlot(int slot) {
        return slot >= minUpgradeSlot && slot <= maxUpgradeSlot;
    }

    public boolean isInputSlot(int slot) {
        return slot >= minInputSlot && slot <= maxInputSlot;
    }

    public boolean isOutputSlot(int slot) {
        return slot >= minOutputSlot && slot <= maxOutputSlot;
    }

    public int getNumUpgradeSlots() {
        if (minUpgradeSlot < 0) {
            return 0;
        }
        return Math.max(0, maxUpgradeSlot - minUpgradeSlot + 1);
    }

    public int getNumInputSlots() {
        if (minInputSlot < 0) {
            return 0;
        }
        return Math.max(0, maxInputSlot - minInputSlot + 1);
    }

    public int getNumOutputSlots() {
        if (minOutputSlot < 0) {
            return 0;
        }
        return Math.max(0, maxOutputSlot - minOutputSlot + 1);
    }

    public int getNumSlots() {
        return Math.max(Math.max(getMaxInputSlot(), getMaxOutputSlot()), getMaxUpgradeSlot()) + 1;
    }

    public int getMinUpgradeSlot() {
        return minUpgradeSlot;
    }

    public int getMaxUpgradeSlot() {
        return maxUpgradeSlot;
    }

    public int getMinInputSlot() {
        return minInputSlot;
    }

    public int getMaxInputSlot() {
        return maxInputSlot;
    }

    public int getMinOutputSlot() {
        return minOutputSlot;
    }

    public int getMaxOutputSlot() {
        return maxOutputSlot;
    }

    @Override
    public String toString() {
        return "SlotDefinition [minUpgradeSlot=" + minUpgradeSlot + ", maxUpgradeSlot=" + maxUpgradeSlot
                + ", minInputSlot=" + minInputSlot + ", maxInputSlot=" + maxInputSlot + ", minOutputSlot="
                + minOutputSlot + ", maxOutputSlot=" + maxOutputSlot + ", nunSlots=" + getNumSlots() + " ]";
    }
}
