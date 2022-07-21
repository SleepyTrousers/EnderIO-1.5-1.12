package crazypants.enderio.machine.painter;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemPaintedWall extends ItemBlock {

    public BlockItemPaintedWall(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        super.addInformation(item, par2EntityPlayer, list, par4);
        list.add(PainterUtil.getTooltTipText(item));
    }
}
