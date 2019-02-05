package crazypants.enderio.base.item.darksteel.upgrade.storage;

import net.minecraft.inventory.EntityEquipmentSlot;

public class SlotEncoder {

  private int value = 0;

  public SlotEncoder() {
  }

  public SlotEncoder(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public void set(EntityEquipmentSlot slot, int slots) {
    int index = slot.getIndex() * 7;
    value |= slots << index;
  }

  public int get(EntityEquipmentSlot slot) {
    int index = slot.getIndex() * 7;
    return (value >>> index) & 0b1111111;
  }

  public boolean hasSlots() {
    return value != 0;
  }

}
