package crazypants.enderio.base.item.darksteel.upgrade.storage;

import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import crazypants.enderio.util.Prep;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

/**
 * An itemstack nbt-based inventory. Mostly using Forge's implementation with 3 small additions:
 * <p>
 * <ol>
 * <li>The size is fixed, even if the nbt has a different size. If the inventory is connected to an item, the item's data will be forced to the right size. If
 * not, slots are afaked with slots that are forced empty.
 * <li>The data is read/saved from/to an itemstack
 * <li>Certain dangerous items are not allowed in.
 * </ol>
 * 
 * Note that this relies on the calling code having determined if the stack can have an inventory and how big that inventory is. The only thing that is checked
 * is that the stack is not empty.
 * 
 * @author Henry Loenwind
 *
 */
public class StorageCap extends ItemStackHandler {

  protected final @Nonnull NbtValue NBT;
  protected final @Nonnull EntityEquipmentSlot equipmentSlot;
  protected final @Nonnull ItemStack owner;
  protected final EntityPlayer player;
  protected Function<ItemStack, Boolean> validator = x -> true;

  /**
   * Connect this inventory to a real item
   * 
   */
  public StorageCap(@Nonnull NbtValue NBT, @Nonnull EntityEquipmentSlot equipmentSlot, int size, @Nonnull EntityPlayer player) {
    super(size);
    this.NBT = NBT;
    this.equipmentSlot = equipmentSlot;
    this.player = size > 0 ? player : null;
    this.owner = size > 0 ? player.getItemStackFromSlot(equipmentSlot) : Prep.getEmpty();
    size = Prep.isValid(owner) ? size : 0;
    deserializeNBT(NBT.getTag(owner));
    if (size != super.getSlots()) {
      if (size < super.getSlots()) {
        for (Iterator<ItemStack> itr = stacks.iterator(); itr.hasNext();) {
          final ItemStack next = itr.next();
          if (next == null || Prep.isInvalid(next)) {
            itr.remove();
          }
        }
      }
      while (size < super.getSlots()) {
        player.dropItem(stacks.remove(stacks.size() - 1), true);
      }
      while (size > super.getSlots()) {
        stacks.add(Prep.getEmpty());
      }
      onContentsChanged(0);
    }
  }

  public StorageCap(@Nonnull NbtValue NBT, @Nonnull ItemStack owner) {
    super(0);
    this.NBT = NBT;
    this.equipmentSlot = EntityEquipmentSlot.MAINHAND;
    this.player = null;
    this.owner = owner;
    deserializeNBT(NBT.getTag(owner));
  }

  /**
   * Have a fake inventory to use a client-side shadow
   */
  public StorageCap(@Nonnull EntityEquipmentSlot equipmentSlot, int size) {
    super(size);
    this.NBT = NbtValue.INVENTORY;
    this.equipmentSlot = equipmentSlot;
    this.owner = Prep.getEmpty();
    this.player = null;
  }

  public boolean isStillConnectedToPlayer() {
    return player == null || Prep.isInvalid(owner) || owner == player.getItemStackFromSlot(equipmentSlot);
  }

  public @Nonnull EntityEquipmentSlot getEquipmentSlot() {
    return equipmentSlot;
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (stack == owner || stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) != null || isLarge(stack)
        || (Prep.isValid(stack) && !validator.apply(stack))) {
      if (player != null) {
        if (stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null) != null) {
          player.sendStatusMessage(Lang.GUI_ARMOR_INVENTORY_CONTAINER.toChatServer(), true);
        } else if (isLarge(stack)) {
          player.sendStatusMessage(Lang.GUI_ARMOR_INVENTORY_BIG.toChatServer(), true);
        }
      }
      return stack;
    }

    return super.insertItem(slot, stack, simulate);
  }

  private boolean isLarge(@Nonnull ItemStack stack) {
    ByteBuf buffer = Unpooled.buffer();
    ByteBufUtils.writeItemStack(buffer, stack);
    boolean result = buffer.writerIndex() > DarkSteelConfig.slotLimitBytes.get();
    buffer.release();
    return result;
  }

  @Override
  protected void onContentsChanged(int slot) {
    if (Prep.isValid(owner)) {
      NBT.setTag(owner, serializeNBT());
    }
  }

  @Override
  public void setSize(int size) {
    // we need to be able to manipulate the size if reading from nbt changed it, NonNullList cannot do that
    stacks = new NNList<>(size, Prep.getEmpty());
  }

  public StorageCap setValidator(@Nonnull Function<ItemStack, Boolean> validator) {
    this.validator = validator;
    return this;
  }

}
