package crazypants.enderio.machines.machine.tank;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerTank extends AbstractMachineContainer<TileTank> {

  static private final Things slotItemsFull = new Things().add(Fluids.getAllBuckets());
  static private final Things slotItemsEmpty = new Things().add(Items.BUCKET);
  static private final Things mendables = new Things("minecraft:iron_shovel", "minecraft:iron_pickaxe", "minecraft:iron_axe", "minecraft:iron_sword",
      "minecraft:iron_hoe", "minecraft:iron_helmet", "minecraft:iron_chestplate", "minecraft:iron_leggings", "minecraft:iron_boots", "minecraft:bow");

  private static final int inFull = 0, inEmpty = 1, trashcan = 2, outEmpty = 3, outFull = 4;

  public ContainerTank(@Nonnull InventoryPlayer playerInv, @Nonnull TileTank te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new InventorySlot(getInv(), inFull, 44, 21));
    addSlotToContainer(new InventorySlot(getInv(), inEmpty, 116, 21));
    addSlotToContainer(new InventorySlot(getInv(), trashcan, 10000, 10000) {

      @Override
      @SideOnly(Side.CLIENT)
      public @Nonnull ResourceLocation getBackgroundLocation() {
        return IconEIO.TRASHCAN.getMap().getTexture();
      }

      @Override
      @SideOnly(Side.CLIENT)
      public @Nonnull TextureAtlasSprite getBackgroundSprite() {
        return IconEIO.TRASHCAN.getAsTextureAtlasSprite();
      }

    });
    addSlotToContainer(new InventorySlot(getInv(), outEmpty, 44, 52));
    addSlotToContainer(new InventorySlot(getInv(), outFull, 116, 52));
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(slotItemsFull.getItemStacks(), getSlotFromInventory(inFull)));
    if (getTe().tank.isEmpty() || !getTe().tank.hasFluid(Fluids.XP_JUICE.getFluid())) {
      slots.add(new GhostBackgroundItemSlot(slotItemsEmpty.getItemStacks(), getSlotFromInventory(inEmpty)));
    } else {
      slots.add(new GhostBackgroundItemSlot(mendables.getItemStacks(), getSlotFromInventory(inEmpty)));
    }
    slots.add(new GhostBackgroundItemSlot(slotItemsEmpty.getItemStacks(), getSlotFromInventory(outEmpty)));
    slots.add(new GhostBackgroundItemSlot(slotItemsFull.getItemStacks(), getSlotFromInventory(outFull)));
  }

}
