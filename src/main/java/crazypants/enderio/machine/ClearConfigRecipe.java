package crazypants.enderio.machine;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.BlockItemCapBank;
import net.minecraft.block.Block;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class ClearConfigRecipe implements IRecipe {
  
  static {
    RecipeSorter.register("EnderIO:clearConfig", ClearConfigRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
  }

  @Override
  public boolean matches(InventoryCrafting inv, World world) {
    return match(inv) != null;
  }

  private ItemStack match(InventoryCrafting inv) {
    ItemStack input = null;

    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (checkStack != null) {
        if (input != null) {
          return null;
        }
        input = checkStack;
      }
    }
    if (input != null && Block.getBlockFromItem(input.getItem()) instanceof AbstractMachineBlock && input.hasTagCompound()
        && input.getTagCompound().getBoolean("eio.abstractMachine")) {
      return input;
    }
    if (input != null && input.getItem() instanceof BlockItemCapBank && input.hasTagCompound() && !input.getTagCompound().hasNoTags()) {
      return input;
    }
    return null;
  }

  private ItemStack lastOutput = null;

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    ItemStack input = match(inv);
    if (input == null) {
      lastOutput = null;
    } else {
      lastOutput = input.copy();
      lastOutput.setTagCompound(new NBTTagCompound());
      lastOutput.stackSize = 1;
    }
    return lastOutput;
  }

  @Override
  public int getRecipeSize() {
    return 1;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return null;
  }
  
  @SubscribeEvent
  public void onTooltip(ItemTooltipEvent event) {
    if (lastOutput != null && ItemStack.areItemStacksEqual(lastOutput, event.getItemStack())) {
      if ((event.getEntityPlayer().openContainer instanceof ContainerWorkbench
          && ((ContainerWorkbench) event.getEntityPlayer().openContainer).craftResult.getStackInSlot(0) == event.getItemStack())
          || (event.getEntityPlayer().openContainer instanceof ContainerPlayer
              && ((ContainerPlayer) event.getEntityPlayer().openContainer).craftResult.getStackInSlot(0) == event.getItemStack())) {
          event.getToolTip().add(TextFormatting.RED.toString() + TextFormatting.ITALIC + EnderIO.lang.localize("machine.tooltip.clearConfig"));
      }
    }
  }

  @Override
  public ItemStack[] getRemainingItems(InventoryCrafting inv) {
    return new ItemStack[inv.getSizeInventory()];
  }
}
