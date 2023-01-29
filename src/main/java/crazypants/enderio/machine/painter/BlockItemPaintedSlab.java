package crazypants.enderio.machine.painter;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;

public class BlockItemPaintedSlab extends ItemSlab {

    private boolean isFullBlock;

    public BlockItemPaintedSlab(Block blk) {
        super(blk, EnderIO.blockPaintedSlab, EnderIO.blockPaintedDoubleSlab, blk == EnderIO.blockPaintedDoubleSlab);
        setHasSubtypes(true);
        isFullBlock = blk == EnderIO.blockPaintedDoubleSlab;
        setUnlocalizedName(ModObject.blockPaintedSlab.unlocalisedName);
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

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World world, int x, int y, int z, int side,
            float par8, float par9, float par10) {

        if (this.isFullBlock) {
            return super.onItemUse(par1ItemStack, player, world, x, y, z, side, par8, par9, par10);
        } else if (par1ItemStack.stackSize == 0) {
            return false;
        } else if (!player.canPlayerEdit(x, y, z, side, par1ItemStack)) {
            return false;
        } else {
            Block i1 = world.getBlock(x, y, z);
            int j1 = world.getBlockMetadata(x, y, z);
            int k1 = j1 & 7;
            boolean flag = (j1 & 8) != 0;

            if ((side == 1 && !flag || side == 0 && flag) && i1 == EnderIO.blockPaintedSlab
                    && k1 == par1ItemStack.getItemDamage()) {

                if (world.checkNoEntityCollision(
                        EnderIO.blockPaintedDoubleSlab.getCollisionBoundingBoxFromPool(world, x, y, z))) {

                    TileEntity te = world.getTileEntity(x, y, z);
                    if (te instanceof TileEntityPaintedSlab) {
                        ((TileEntityPaintedSlab) te).isConvertingToFullBlock = true;
                    }

                    if (world.setBlock(x, y, z, EnderIO.blockPaintedDoubleSlab, k1, 3)) {

                        te = world.getTileEntity(x, y, z);
                        if (te instanceof TileEntityPaintedBlock) {

                            Block b = PainterUtil.getSourceBlock(par1ItemStack);
                            TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
                            tef.setSourceBlock(b);
                            tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(par1ItemStack));
                            world.markBlockForUpdate(x, y, z);
                        }

                        world.playSoundEffect(
                                x + 0.5F,
                                y + 0.5F,
                                z + 0.5F,
                                EnderIO.blockPaintedDoubleSlab.stepSound.getStepResourcePath(),
                                (EnderIO.blockPaintedDoubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                                EnderIO.blockPaintedDoubleSlab.stepSound.getPitch() * 0.8F);
                        --par1ItemStack.stackSize;
                    } else {
                        if (te instanceof TileEntityPaintedSlab) {
                            ((TileEntityPaintedSlab) te).isConvertingToFullBlock = false;
                        }
                    }
                }

                return true;
            } else {
                if (mergeWithTopSlab(par1ItemStack, player, world, x, y, z, side)) {
                    return true;
                }
                return super.onItemUse(par1ItemStack, player, world, x, y, z, side, par8, par9, par10);
            }
        }
    }

    private boolean mergeWithTopSlab(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World world, int x, int y,
            int z, int side) {
        if (side == 0) {
            --y;
        } else if (side == 1) {
            ++y;
        } else if (side == 2) {
            --z;
        } else if (side == 3) {
            ++z;
        } else if (side == 4) {
            --x;
        } else if (side == 5) {
            ++x;
        }

        Block i1 = world.getBlock(x, y, z);
        int j1 = world.getBlockMetadata(x, y, z);
        int k1 = j1 & 7;

        if (i1 == EnderIO.blockPaintedSlab && k1 == par1ItemStack.getItemDamage()) {

            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityPaintedSlab) {
                ((TileEntityPaintedSlab) te).isConvertingToFullBlock = true;
            }

            if (world.checkNoEntityCollision(
                    EnderIO.blockPaintedDoubleSlab.getCollisionBoundingBoxFromPool(world, x, y, z))
                    && world.setBlock(x, y, z, EnderIO.blockPaintedDoubleSlab, k1, 3)) {

                te = world.getTileEntity(x, y, z);
                if (te instanceof TileEntityPaintedBlock) {

                    Block b = PainterUtil.getSourceBlock(par1ItemStack);
                    TileEntityPaintedBlock tef = (TileEntityPaintedBlock) te;
                    tef.setSourceBlock(b);
                    tef.setSourceBlockMetadata(PainterUtil.getSourceBlockMetadata(par1ItemStack));
                    world.markBlockForUpdate(x, y, z);
                }

                world.playSoundEffect(
                        x + 0.5F,
                        y + 0.5F,
                        z + 0.5F,
                        EnderIO.blockPaintedDoubleSlab.stepSound.getStepResourcePath(),
                        (EnderIO.blockPaintedDoubleSlab.stepSound.getVolume() + 1.0F) / 2.0F,
                        EnderIO.blockPaintedDoubleSlab.stepSound.getPitch() * 0.8F);
                --par1ItemStack.stackSize;

            } else {
                if (te instanceof TileEntityPaintedSlab) {
                    ((TileEntityPaintedSlab) te).isConvertingToFullBlock = false;
                }
            }
            return true;

        } else {
            return false;
        }
    }
}
