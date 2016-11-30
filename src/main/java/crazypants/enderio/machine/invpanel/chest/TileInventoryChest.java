package crazypants.enderio.machine.invpanel.chest;

import java.util.EnumMap;
import java.util.Map;

import crazypants.enderio.ModObject;
import crazypants.enderio.capability.EnderInventory;
import crazypants.enderio.capability.EnderInventory.Type;
import crazypants.enderio.capability.InventorySlot;
import crazypants.enderio.machine.AbstractCapabilityMachineEntity;
import crazypants.enderio.paint.IPaintable;
import crazypants.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Storable
public abstract class TileInventoryChest extends AbstractCapabilityMachineEntity implements IPaintable.IPaintableTileEntity {

  private static final Map<EnumChestSize, Class<? extends TileInventoryChest>> CLASSES = new EnumMap<EnumChestSize, Class<? extends TileInventoryChest>>(
      EnumChestSize.class);

  @Storable
  public static class Meta0 extends TileInventoryChest {
    public Meta0() {
      super(EnumChestSize.TINY);
    }
  }

  @Storable
  public static class Meta1 extends TileInventoryChest {
    public Meta1() {
      super(EnumChestSize.SMALL);
    }
  }

  @Storable
  public static class Meta2 extends TileInventoryChest {
    public Meta2() {
      super(EnumChestSize.MEDIUM);
    }
  }

  @Storable
  public static class Meta3 extends TileInventoryChest {
    public Meta3() {
      super(EnumChestSize.BIG);
    }
  }

  @Storable
  public static class Meta4 extends TileInventoryChest {
    public Meta4() {
      super(EnumChestSize.LARGE);
    }
  }

  @Storable
  public static class Meta5 extends TileInventoryChest {
    public Meta5() {
      super(EnumChestSize.HUGE);
    }
  }

  @Storable
  public static class Meta6 extends TileInventoryChest {
    public Meta6() {
      super(EnumChestSize.ENORMOUS);
    }
  }

  @Storable
  public static class Meta7 extends TileInventoryChest {
    public Meta7() {
      super(EnumChestSize.WAREHOUSE);
    }
  }

  @Storable
  public static class Meta8 extends TileInventoryChest {
    public Meta8() {
      super(EnumChestSize.WAREHOUSE13);
    }
  }

  public static void create() {
    CLASSES.put(EnumChestSize.TINY, Meta0.class);
    CLASSES.put(EnumChestSize.SMALL, Meta1.class);
    CLASSES.put(EnumChestSize.MEDIUM, Meta2.class);
    CLASSES.put(EnumChestSize.BIG, Meta3.class);
    CLASSES.put(EnumChestSize.LARGE, Meta4.class);
    CLASSES.put(EnumChestSize.HUGE, Meta5.class);
    CLASSES.put(EnumChestSize.ENORMOUS, Meta6.class);
    CLASSES.put(EnumChestSize.WAREHOUSE, Meta7.class);
    CLASSES.put(EnumChestSize.WAREHOUSE13, Meta8.class);

    for (EnumChestSize size : EnumChestSize.values()) {
      GameRegistry.registerTileEntity(CLASSES.get(size), ModObject.blockInventoryChest.getUnlocalisedName() + size.getName() + "TileEntity");
    }
  }

  public static TileInventoryChest create(EnumChestSize size) {
    try {
      return CLASSES.get(size).newInstance();
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private final EnumChestSize size;

  @Store({ StoreFor.SAVE, StoreFor.ITEM })
  private final EnderInventory chestInventory;

  // called by our block
  private TileInventoryChest(EnumChestSize size) {
    super(new EnderInventory());
    chestInventory = getInventory();
    this.size = size;
    for (int i = 0; i < size.getSlots(); i++) {
      getInventory().add(Type.INOUT, "slot" + i, new InventorySlot());
    }
  }

  @Override
  public String getMachineName() {
    return ModObject.blockInventoryChest.getUnlocalisedName();
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    return false;
  }

  public int getComparatorInputOverride() {
    if (size == null) {
      return 0;
    }
    int count = 0;
    for (InventorySlot slot : getInventory().getView(EnderInventory.Type.INOUT)) {
      if (Prep.isValid(slot.getStackInSlot(0))) {
        count++;
      }
    }
    return count == 0 ? 0 : (14 * count / size.getSlots() + 1);
  }

}
