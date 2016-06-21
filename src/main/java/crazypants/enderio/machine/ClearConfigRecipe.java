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
    int count = 0;
    ItemStack input = null;
    
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (checkStack != null && Block.getBlockFromItem(checkStack.getItem()) instanceof AbstractMachineBlock) {
        count++;
      }
      input = count == 1 && checkStack != null ? checkStack : input;
    }
    
    if (count == 1 && input.stackTagCompound != null && input.stackTagCompound.getBoolean("eio.abstractMachine")) {
      ItemStack out = input.copy();
      out.stackTagCompound = new NBTTagCompound();
      out.stackTagCompound.setBoolean("clearedConfig", true);
      out.stackSize = 1;
      output = out;
    } else {
      output = null;
    }
    
    return count == 1 && output != null;
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
