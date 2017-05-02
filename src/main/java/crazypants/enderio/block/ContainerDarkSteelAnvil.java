package crazypants.enderio.block;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.config.Config;
import crazypants.util.Prep;
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

import static crazypants.enderio.ModObject.blockDarkSteelAnvil;

public class ContainerDarkSteelAnvil extends ContainerRepair {

  private int x, y, z;

  private final Field _outputSlot = ReflectionHelper.findField(ContainerRepair.class, "outputSlot", "field_82852_f");
  private final Field _inputSlots = ReflectionHelper.findField(ContainerRepair.class, "inputSlots", "field_82853_g");

  // public at the moment
  // private final Field _materialCost = ReflectionHelper.findField(ContainerRepair.class, "materialCost", "stackSizeToBeUsedInRepair", "field_82856_l");

  public ContainerDarkSteelAnvil(InventoryPlayer playerInv, final World world, final int x, final int y, final int z, EntityPlayer player) {
    super(playerInv, world, new BlockPos(x, y, z), player);

    final BlockPos blockPosIn = new BlockPos(x, y, z);
    final IInventory outputSlot, inputSlots;
//    final int materialCost;

    try {
      outputSlot = (IInventory) _outputSlot.get(this);
      inputSlots = (IInventory) _inputSlots.get(this);
      // materialCost = _materialCost.getInt(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    this.x = x;
    this.y = y;
    this.z = z;

    this.inventorySlots.set(2, new Slot(outputSlot, 2, 134, 47) {

      @Override
      public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
      }

      @Override
      public boolean canTakeStack(EntityPlayer stack) {
        return (stack.capabilities.isCreativeMode || stack.experienceLevel >= ContainerDarkSteelAnvil.this.maximumCost)
            && ContainerDarkSteelAnvil.this.maximumCost > 0 && this.getHasStack();
      }

      @Override
      public void onPickupFromSlot(EntityPlayer playerIn, @Nonnull ItemStack stack) {
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
        IBlockState iblockstate = world.getBlockState(blockPosIn);

        if (!playerIn.capabilities.isCreativeMode && !world.isRemote && iblockstate.getBlock() == blockDarkSteelAnvil.getBlock()
            && playerIn.getRNG().nextFloat() < Config.darkSteelAnvilDamageChance) {
          int l = iblockstate.getValue(BlockAnvil.DAMAGE).intValue();
          ++l;

          if (l > 2) {
            world.setBlockToAir(blockPosIn);
            world.playEvent(1029, blockPosIn, 0);
          } else {
            world.setBlockState(blockPosIn, iblockstate.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(l)), 2);
            world.playEvent(1030, blockPosIn, 0);
          }
        } else if (!world.isRemote) {
          world.playEvent(1030, blockPosIn, 0);
        }
      }
    });
  }

  @Override
  public boolean canInteractWith(EntityPlayer player) {
    return player.world.getBlockState(new BlockPos(x, y, z)).getBlock() == blockDarkSteelAnvil.getBlock();
  }
}
