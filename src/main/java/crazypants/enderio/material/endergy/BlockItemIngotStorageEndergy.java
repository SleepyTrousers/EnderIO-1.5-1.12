package crazypants.enderio.material.endergy;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockItemIngotStorageEndergy extends ItemBlock {

    public BlockItemIngotStorageEndergy(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + AlloyEndergy.values()[stack.getItemDamage()].unlocalisedName;
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (AlloyEndergy alloy : AlloyEndergy.values()) {
            list.add(new ItemStack(this, 1, alloy.ordinal()));
        }
    }
}
