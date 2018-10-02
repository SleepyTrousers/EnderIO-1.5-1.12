package crazypants.enderio.base.block.darksteel.anvil;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.config.BlockConfig;
import crazypants.enderio.util.Prep;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import static crazypants.enderio.base.init.ModObject.blockDarkSteelAnvil;

public class ContainerDarkSteelAnvil extends ContainerRepair {

  private final @Nonnull BlockPos pos;

  private final Field _outputSlot = ReflectionHelper.findField(ContainerRepair.class, "outputSlot", "field_82852_f");
  private final Field _inputSlots = ReflectionHelper.findField(ContainerRepair.class, "inputSlots", "field_82853_g");

  // public at the moment
  // private final Field _materialCost = ReflectionHelper.findField(ContainerRepair.class, "materialCost", "stackSizeToBeUsedInRepair", "field_82856_l");

  public ContainerDarkSteelAnvil(@Nonnull InventoryPlayer playerInv, final @Nonnull World world, final @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    super(playerInv, world, pos, player);
    this.pos = pos;

    final IInventory outputSlot, inputSlots;
    // final int materialCost;

    try {
      outputSlot = (IInventory) _outputSlot.get(this);
      inputSlots = (IInventory) _inputSlots.get(this);
      // materialCost = _materialCost.getInt(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    this.inventorySlots.set(2, new Slot(NullHelper.notnullM(outputSlot, "ContainerRepair.outputSlot is null"), 2, 134, 47) {

      @Override
      public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
      }

      @Override
      public boolean canTakeStack(@Nonnull EntityPlayer playerIn) {
        return (playerIn.capabilities.isCreativeMode || playerIn.experienceLevel >= ContainerDarkSteelAnvil.this.maximumCost)
            && ContainerDarkSteelAnvil.this.maximumCost > 0 && this.getHasStack();
      }

      @Override
      public @Nonnull ItemStack onTake(@Nonnull EntityPlayer playerIn, @Nonnull ItemStack stack) {
        if (!playerIn.capabilities.isCreativeMode) {
          playerIn.addExperienceLevel(-ContainerDarkSteelAnvil.this.maximumCost);
        }

        inputSlots.setInventorySlotContents(0, Prep.getEmpty());

        if (materialCost > 0) {
          ItemStack itemstack1 = inputSlots.getStackInSlot(1);

          if (Prep.isValid(itemstack1) && itemstack1.getCount() > materialCost) {
            itemstack1.shrink(materialCost);
            inputSlots.setInventorySlotContents(1, itemstack1);
          } else {
            inputSlots.setInventorySlotContents(1, Prep.getEmpty());
          }
        } else {
          inputSlots.setInventorySlotContents(1, Prep.getEmpty());
        }

        ContainerDarkSteelAnvil.this.maximumCost = 0;
        IBlockState iblockstate = world.getBlockState(pos);

        if (!playerIn.capabilities.isCreativeMode && !world.isRemote && iblockstate.getBlock() == blockDarkSteelAnvil.getBlock()
            && playerIn.getRNG().nextFloat() < BlockConfig.darkSteelAnvilDamageChance.get()) {
          int l = iblockstate.getValue(BlockAnvil.DAMAGE).intValue();
          ++l;

          if (l > 2) {
            world.setBlockToAir(pos);
            world.playEvent(1029, pos, 0);
          } else {
            world.setBlockState(pos, iblockstate.withProperty(BlockAnvil.DAMAGE, l), 2);
            world.playEvent(1030, pos, 0);
          }
        } else if (!world.isRemote) {
          world.playEvent(1030, pos, 0);
        }
        return stack;
      }
    });
  }

  @Override
  public boolean canInteractWith(@Nonnull EntityPlayer player) {
    return player.world.getBlockState(pos).getBlock() == blockDarkSteelAnvil.getBlock()
        && player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
  }

}
