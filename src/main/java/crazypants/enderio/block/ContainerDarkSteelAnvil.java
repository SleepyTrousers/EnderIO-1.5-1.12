package crazypants.enderio.block;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ContainerDarkSteelAnvil extends ContainerRepair {

  private int x, y, z;
  
  @SuppressWarnings("unchecked")
  public ContainerDarkSteelAnvil(InventoryPlayer playerInv, final World world, final int x, final int y, final int z, EntityPlayer player) {
    super(playerInv, world, x, y, z, player);

    this.x = x;
    this.y = y;
    this.z = z;
    
    this.inventorySlots.set(2, new Slot(this.outputSlot, 2, 134, 47) {

      public boolean isItemValid(ItemStack stack) {
        return false;
      }

      public boolean canTakeStack(EntityPlayer stack) {
        return (stack.capabilities.isCreativeMode || stack.experienceLevel >= ContainerDarkSteelAnvil.this.maximumCost)
            && ContainerDarkSteelAnvil.this.maximumCost > 0 && this.getHasStack();
      }

      public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
        if(!player.capabilities.isCreativeMode) {
          player.addExperienceLevel(-ContainerDarkSteelAnvil.this.maximumCost);
        }

        ContainerDarkSteelAnvil.this.inputSlots.setInventorySlotContents(0, (ItemStack) null);

        if(ContainerDarkSteelAnvil.this.stackSizeToBeUsedInRepair > 0) {
          ItemStack itemstack1 = ContainerDarkSteelAnvil.this.inputSlots.getStackInSlot(1);

          if(itemstack1 != null && itemstack1.stackSize > ContainerDarkSteelAnvil.this.stackSizeToBeUsedInRepair) {
            itemstack1.stackSize -= ContainerDarkSteelAnvil.this.stackSizeToBeUsedInRepair;
            ContainerDarkSteelAnvil.this.inputSlots.setInventorySlotContents(1, itemstack1);
          } else {
            ContainerDarkSteelAnvil.this.inputSlots.setInventorySlotContents(1, (ItemStack) null);
          }
        } else {
          ContainerDarkSteelAnvil.this.inputSlots.setInventorySlotContents(1, (ItemStack) null);
        }

        ContainerDarkSteelAnvil.this.maximumCost = 0;

        if(!player.capabilities.isCreativeMode && !world.isRemote && world.getBlock(x, y, z) == Blocks.anvil
            && player.getRNG().nextFloat() < Config.darkSteelAnvilDamageChance) {
          int i1 = world.getBlockMetadata(x, y, z);
          int k = i1 & 3;
          int l = i1 >> 2;
          ++l;

          if(l > 2) {
            world.setBlockToAir(x, y, z);
            world.playAuxSFX(1020, x, y, z, 0);
          } else {
            world.setBlockMetadataWithNotify(x, y, z, k | l << 2, 2);
            world.playAuxSFX(1021, x, y, z, 0);
          }
        } else if(!world.isRemote) {
          world.playAuxSFX(1021, x, y, z, 0);
        }
      }
    });
  }
  
  @Override
  public boolean canInteractWith(EntityPlayer player) {
    return player.worldObj.getBlock(this.x, this.y, this.z) == EnderIO.blockDarkSteelAnvil;
  }
}
