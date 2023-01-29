package crazypants.enderio.machine.painter;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;

public class BlockItemPaintedCarpet extends ItemBlock {

    public BlockItemPaintedCarpet(Block p_i45328_1_) {
        super(p_i45328_1_);
        setHasSubtypes(true);
    }

    public BlockItemPaintedCarpet() {
        super(EnderIO.blockPaintedCarpet);
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
