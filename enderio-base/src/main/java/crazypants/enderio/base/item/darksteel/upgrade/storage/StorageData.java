package crazypants.enderio.base.item.darksteel.upgrade.storage;

import javax.annotation.Nonnull;

import net.minecraft.inventory.EntityEquipmentSlot;

public class StorageData {

  protected static int cols(EntityEquipmentSlot slot) {
    switch (slot) {
    case LEGS:
      return 5;
    case FEET:
      return 3;
    case CHEST:
      return 9;
    case HEAD:
    default:
      return 1;
    }
  }

  protected static int slots(EntityEquipmentSlot slot, int level) {
    return cols(slot) * level * 2;
  }

  protected static final @Nonnull EntityEquipmentSlot[] ARMOR = { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS,
      EntityEquipmentSlot.FEET };

  protected static final @Nonnull EntityEquipmentSlot[] ARMOR_INDEX = { null, null, null, null };
  static {
    for (EntityEquipmentSlot slot : ARMOR) {
      ARMOR_INDEX[slot.getIndex()] = slot;
    }
  }

}
