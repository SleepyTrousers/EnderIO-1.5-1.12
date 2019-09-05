package crazypants.enderio.base.handler.darksteel.gui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.util.Prep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public enum SlotSelector implements ISlotSelector {
  MAIN(1, EntityEquipmentSlot.MAINHAND),
  HEAD(2, EntityEquipmentSlot.HEAD),
  CHEST(3, EntityEquipmentSlot.CHEST),
  LEGS(4, EntityEquipmentSlot.LEGS),
  FEET(5, EntityEquipmentSlot.FEET),
  OFFH(6, EntityEquipmentSlot.OFFHAND)

  ;

  public static final ISlotSelector ANVIL = new ISlotSelector() {

    @Override
    public boolean isAnvil() {
      return true;
    }

    @Override
    public boolean isItem() {
      return false;
    }

    @Override
    public boolean isSlot() {
      return false;
    }

    @Override
    @Nonnull
    public EntityEquipmentSlot getSlot() {
      throw new RuntimeException(INTERNAL_LOGIC_ERROR);
    }

    @Override
    public int getTabOrder() {
      return 0;
    }

    @Override
    @Nonnull
    public ItemStack getItem(@Nonnull EntityPlayer player) {
      return new ItemStack(Blocks.ANVIL);
    }

    @Override
    @Nonnull
    public Slot setContainerSlot(@Nonnull Slot containerSlot) {
      throw new RuntimeException(INTERNAL_LOGIC_ERROR);
    }

    @Override
    public Slot getContainerSlot() {
      throw new RuntimeException(INTERNAL_LOGIC_ERROR);
    }

  };

  private static final String INTERNAL_LOGIC_ERROR = "Internal Logic Error";

  public static SlotSelector fromEntityEquipmentSlot(EntityEquipmentSlot slot) {
    for (SlotSelector ss : values()) {
      if (ss.slot == slot) {
        return ss;
      }
    }
    throw new RuntimeException("No such EntityEquipmentSlot " + slot);
  }

  public static class SlotItem implements ISlotSelector {

    private @Nullable Slot containerSlot = null;

    public SlotItem() {
    }

    @Override
    public @Nonnull Slot setContainerSlot(@Nonnull Slot containerSlot) {
      return this.containerSlot = containerSlot;
    }

    @Override
    public boolean isAnvil() {
      return false;
    }

    @Override
    public boolean isItem() {
      return true;
    }

    @Override
    public boolean isSlot() {
      return false;
    }

    @Override
    @Nonnull
    public EntityEquipmentSlot getSlot() {
      throw new RuntimeException(INTERNAL_LOGIC_ERROR);
    }

    @Override
    public int getTabOrder() {
      return 7;
    }

    @Override
    @Nonnull
    public ItemStack getItem(@Nonnull EntityPlayer player) {
      return containerSlot != null ? containerSlot.getStack() : Prep.getEmpty();
    }

    @Override
    public @Nullable Slot getContainerSlot() {
      return containerSlot;
    }

  }

  public static class RawItem extends SlotItem {

    private final ItemStack item;

    public RawItem(ItemStack item) {
      this.item = item;
    }

    @Override
    public @Nonnull Slot setContainerSlot(@Nonnull Slot containerSlot) {
      throw new RuntimeException(INTERNAL_LOGIC_ERROR);
    }

    @Override
    public boolean isAnvil() {
      return false;
    }

    @Override
    public boolean isItem() {
      return true;
    }

    @Override
    public boolean isSlot() {
      return false;
    }

    @Override
    @Nonnull
    public EntityEquipmentSlot getSlot() {
      throw new RuntimeException(INTERNAL_LOGIC_ERROR);
    }

    @Override
    public int getTabOrder() {
      return 7;
    }

    @Override
    @Nonnull
    public ItemStack getItem(@Nonnull EntityPlayer player) {
      return item;
    }

    @Override
    public @Nullable Slot getContainerSlot() {
      throw new RuntimeException(INTERNAL_LOGIC_ERROR);
    }

  }

  private final EntityEquipmentSlot slot;
  private final int tabOrder;

  private SlotSelector(int tabOrder, EntityEquipmentSlot slot) {
    this.tabOrder = tabOrder;
    this.slot = slot;
  }

  @Override
  public boolean isAnvil() {
    return false;
  }

  @Override
  public boolean isItem() {
    return false;
  }

  @Override
  public boolean isSlot() {
    return true;
  }

  @Override
  public @Nonnull EntityEquipmentSlot getSlot() {
    return slot;
  }

  @Override
  public int getTabOrder() {
    return tabOrder;
  }

  @Override
  public @Nonnull ItemStack getItem(@Nonnull EntityPlayer player) {
    return player.getItemStackFromSlot(slot);
  }

  @Override
  public @Nonnull Slot setContainerSlot(@Nonnull Slot containerSlot) {
    throw new RuntimeException(INTERNAL_LOGIC_ERROR);
  }

  @Override
  @Nonnull
  public Slot getContainerSlot() {
    throw new RuntimeException(INTERNAL_LOGIC_ERROR);
  }

}
