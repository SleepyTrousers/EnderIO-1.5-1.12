package crazypants.enderio.base.machine.gui;

import java.awt.Point;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderInventory.View;
import com.enderio.core.common.inventory.EnderSlot;
import com.enderio.core.common.util.Util;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractCapabilityMachineContainer<E extends AbstractCapabilityMachineEntity> extends ContainerEnderCap<EnderInventory, E> {

  protected Slot upgradeSlot;

  public AbstractCapabilityMachineContainer(@Nonnull InventoryPlayer playerInv, @Nonnull E te) {
    super(playerInv, te.getInventory(), te);
  }

  @Override
  protected void addSlots() {
    addMachineSlots();

    View upgrades = getItemHandler().getView(Type.UPGRADE);
    for (int i = 0; i < upgrades.getSlots(); i++) {
      addSlotToContainer(upgradeSlot = new EnderSlot(Type.UPGRADE, upgrades.getSlot(i), getUpgradeOffset().x, getUpgradeOffset().y) {

        @Override
        public int getSlotStackLimit() {
          return 1;
        }

        @Override
        public boolean isItemValid(@Nonnull ItemStack itemStack) {
          return getTileEntity().isValidUpgrade(itemStack);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public @Nonnull ResourceLocation getBackgroundLocation() {
          return IconEIO.CAPACITOR.getMap().getTexture();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public @Nonnull TextureAtlasSprite getBackgroundSprite() {
          return IconEIO.CAPACITOR.getAsTextureAtlasSprite();
        }

      });
    }
  }

  @Override
  public @Nonnull Point getPlayerInventoryOffset() {
    return new Point(8, 84);
  }

  public @Nonnull Point getUpgradeOffset() {
    return new Point(12, 60);
  }

  public Slot getUpgradeSlot() {
    return upgradeSlot;
  }

  /**
   * ATTN: Do not access any non-static field from this method. Your object has not yet been constructed when it is called!
   */
  protected abstract void addMachineSlots();

  protected int getProgressScaled(int scale) {
    AbstractCapabilityMachineEntity te = getTileEntity();
    if (te instanceof IProgressTile) {
      Util.getProgressScaled(scale, (IProgressTile) te);
    }
    return 0;
  }

  private int guiID = -1;

  public void setGuiID(int id) {
    guiID = id;
  }

  public int getGuiID() {
    return guiID;
  }
}
