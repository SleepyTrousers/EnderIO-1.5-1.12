package crazypants.enderio.conduit.item.filter;

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
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.machine.AbstractMachineBlock;

public class ClearFilterRecipe implements IRecipe{

  static {
    RecipeSorter.register("EnderIO:clearFilter", ClearFilterRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
  }
  
  private ItemStack output;
  
  @Override
  public boolean matches(InventoryCrafting inv, World world) {
    output = null;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (checkStack != null) {
        if (checkStack.getItem() instanceof IItemFilterUpgrade) {
          output = checkStack.copy();
          output.stackTagCompound = null;
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
    if (this.output != null && ItemStack.areItemStacksEqual(output, event.itemStack)) {
      event.toolTip.add(EnumChatFormatting.RED.toString() + EnumChatFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.clearConfigWarning"));
    }
  }

}
