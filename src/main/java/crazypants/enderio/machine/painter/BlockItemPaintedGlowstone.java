package crazypants.enderio.machine.painter;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;

public class BlockItemPaintedGlowstone extends ItemBlock {

    public BlockItemPaintedGlowstone(Block p_i45328_1_) {
        super(p_i45328_1_);
        setHasSubtypes(true);
    }

    public BlockItemPaintedGlowstone() {
        super(EnderIO.blockPaintedGlowstone);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
            return false;
        }
        Block b = PainterUtil.getSourceBlock(stack);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityPaintedBlock) {
            TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
            int meta = PainterUtil.getSourceBlockMetadata(stack);
            meta = PainterUtil.adjustFacadeMetadata(b, meta, side);
            tef.setSourceBlock(b);
            tef.setSourceBlockMetadata(meta);
            world.markBlockForUpdate(x, y, z);
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack item, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        super.addInformation(item, par2EntityPlayer, list, par4);
        list.add(PainterUtil.getTooltTipText(item));
    }
}
