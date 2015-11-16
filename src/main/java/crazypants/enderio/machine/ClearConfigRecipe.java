package crazypants.enderio.machine;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import crazypants.enderio.EnderIO;

public class ClearConfigRecipe implements IRecipe {
  
  static {
    RecipeSorter.register("EnderIO:clearConfig", ClearConfigRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
  }

  private ItemStack output;
  
  @Override
  public boolean matches(InventoryCrafting inv, World world) {
    output = null;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (checkStack != null) {
        if (Block.getBlockFromItem(checkStack.getItem()) instanceof AbstractMachineBlock && checkStack.stackTagCompound != null
            && checkStack.stackTagCompound.getBoolean("eio.abstractMachine")) {
          output = checkStack.copy();
          output.stackTagCompound = new NBTTagCompound();
          output.stackTagCompound.setBoolean("clearedConfig", true);
          output.stackSize = 1;
        } else {
          output = null;
          return false;
        }
      }
    }
    return output != null;
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    return output.copy();
  }

  @Override
  public int getRecipeSize() {
    return 1;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return output;
  }
  
  @SubscribeEvent
  public void onTooltip(ItemTooltipEvent event) {
    if (output != null && ItemStack.areItemStacksEqual(output, event.itemStack)) {
      event.toolTip.add(EnumChatFormatting.RED.toString() + EnumChatFormatting.ITALIC + EnderIO.lang.localize("machine.tooltip.clearConfig"));
    }
  }
}
