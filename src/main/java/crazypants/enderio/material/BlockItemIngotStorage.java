package crazypants.enderio.material;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemIngotStorage extends ItemBlock {

    public BlockItemIngotStorage(Block block) {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + Alloy.values()[stack.getItemDamage()].unlocalisedName;
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (Alloy alloy : Alloy.values()) {
            list.add(new ItemStack(this, 1, alloy.ordinal()));
        }
    }
}
