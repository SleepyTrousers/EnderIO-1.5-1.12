package crazypants.enderio.machine.enchanter;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import crazypants.enderio.ModObject;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.config.Config;

public class TileEnchanter extends TileEntityEio implements ISidedInventory {

    private ItemStack[] inv = new ItemStack[3];

    private short facing = (short) ForgeDirection.NORTH.ordinal();

    public void setFacing(short s) {
        facing = s;
    }

    public short getFacing() {
        return facing;
    }

    @Override
    protected void writeCustomNBT(NBTTagCompound root) {
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < inv.length; i++) {
            if (inv[i] != null) {
                NBTTagCompound itemStackNBT = new NBTTagCompound();
                itemStackNBT.setByte("Slot", (byte) i);
                inv[i].writeToNBT(itemStackNBT);
                itemList.appendTag(itemStackNBT);
            }
        }
        root.setTag("Items", itemList);
        root.setShort("facing", facing);
    }

    @Override
    protected void readCustomNBT(NBTTagCompound root) {
        NBTTagList itemList = (NBTTagList) root.getTag("Items");
        if (itemList != null) {
            for (int i = 0; i < itemList.tagCount(); i++) {
                NBTTagCompound itemStack = itemList.getCompoundTagAt(i);
                byte slot = itemStack.getByte("Slot");
                if (slot >= 0 && slot < inv.length) {
                    inv[slot] = ItemStack.loadItemStackFromNBT(itemStack);
                }
            }
        }
        facing = root.getShort("facing");
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public int getSizeInventory() {
        return inv.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= inv.length) {
            return null;
        }
        return inv[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (amount <= 0 || slot < 0 || slot >= inv.length || inv[slot] == null) {
            return null;
        }
        ItemStack fromStack = inv[slot];
        if (fromStack == null) {
            return null;
        }
        if (fromStack.stackSize <= amount) {
            inv[slot] = null;
            return fromStack;
        }
        ItemStack result = new ItemStack(fromStack.getItem(), amount, fromStack.getItemDamage());
        if (fromStack.stackTagCompound != null) {
            result.stackTagCompound = (NBTTagCompound) fromStack.stackTagCompound.copy();
        }
        fromStack.stackSize -= amount;
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack contents) {
        if (contents == null) {
            inv[slot] = contents;
        } else {
            inv[slot] = contents.copy();
        }
        if (contents != null && contents.stackSize > getInventoryStackLimit()) {
            contents.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public String getInventoryName() {
        return ModObject.blockEnchanter.unlocalisedName;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (slot == 0) {
            return Items.writable_book == stack.getItem();
        }
        if (slot == 1) {
            return EnchanterRecipeManager.getInstance().getEnchantmentRecipeForInput(stack) != null;
        }
        return false;
    }

    public EnchanterRecipe getCurrentEnchantmentRecipe() {
        if (inv[0] == null) {
            return null;
        }
        if (inv[1] == null) {
            return null;
        }
        EnchanterRecipe ench = EnchanterRecipeManager.getInstance().getEnchantmentRecipeForInput(inv[1]);
        if (ench == null) {
            return null;
        }
        int level = ench.getLevelForStackSize(inv[1].stackSize);
        if (level <= 0) {
            return null;
        }
        return ench;
    }

    public EnchantmentData getCurrentEnchantmentData() {
        EnchanterRecipe rec = getCurrentEnchantmentRecipe();
        if (rec == null || inv[1] == null) {
            return null;
        }
        int level = rec.getLevelForStackSize(inv[1].stackSize);
        if (level <= 0) {
            return null;
        }
        return new EnchantmentData(rec.getEnchantment(), level);
    }

    // public static int getEnchantmentCost(EnchantmentData enchData) {
    // if(enchData == null) {
    // return 0;
    // }
    // int level = enchData.enchantmentLevel;
    // Enchantment enchantment = enchData.enchantmentobj;
    //
    // if(level > enchantment.getMaxLevel()) {
    // level = enchantment.getMaxLevel();
    // }
    //
    // int costPerLevel = 0;
    // switch (enchantment.getWeight()) {
    // case 1:
    // costPerLevel = 8;
    // //Stops silk touch and infinity being too cheap
    // if(enchantment.getMaxLevel() == 1) {
    // level = 2;
    // }
    // break;
    // case 2:
    // costPerLevel = 4;
    // case 3:
    // case 4:
    // case 6:
    // case 7:
    // case 8:
    // case 9:
    // default:
    // break;
    // case 5:
    // costPerLevel = 2;
    // break;
    // case 10:
    // costPerLevel = 1;
    // }
    //
    // int res = 4;
    // for (int i = 0; i < level; i++) {
    // res += costPerLevel * level;
    // }
    // return res;
    // }

    public int getCurrentEnchantmentCost() {
        return getEnchantmentCost(getCurrentEnchantmentRecipe());
    }

    private int getEnchantmentCost(EnchanterRecipe currentEnchantment) {
        ItemStack item = inv[1];
        if (item == null) {
            return 0;
        }
        if (currentEnchantment == null) {
            return 0;
        }
        Enchantment enchantment = currentEnchantment.getEnchantment();
        int level = currentEnchantment.getLevelForStackSize(item.stackSize);
        return getEnchantmentCost(currentEnchantment, level);
    }

    public static int getEnchantmentCost(EnchanterRecipe recipe, int level) {
        if (level > recipe.getEnchantment().getMaxLevel()) {
            level = recipe.getEnchantment().getMaxLevel();
        }
        int costPerLevel = recipe.getCostPerLevel();
        int res = Config.enchanterBaseLevelCost;
        for (int i = 0; i < level; i++) {
            res += costPerLevel * level;
        }
        return res;
    }

    public void setOutput(ItemStack output) {
        inv[inv.length - 1] = output;
        markDirty();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
        return false;
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
        return false;
    }
}
