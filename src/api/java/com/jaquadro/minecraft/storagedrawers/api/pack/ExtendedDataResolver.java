/*package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

public class ExtendedDataResolver extends StandardDataResolver implements IExtendedDataResolver
{
    private Block[] planks = new Block[16];
    private int[] planksMeta = new int[16];

    private Block[] slabs = new Block[16];
    private int[] slabsMeta = new int [16];

    public ExtendedDataResolver (String modID, String[] unlocalizedNames) {
        super(modID, unlocalizedNames);
    }

    public ExtendedDataResolver (String modID, String[] unlocalizedNames, CreativeTabs creativeTab) {
        super(modID, unlocalizedNames, creativeTab);
    }

    @Override
    public Block getBlock (BlockConfiguration blockConfig) {
        return null;
    }

    @Override
    public Block getPlankBlock (int meta) {
        return planks[meta];
    }

    @Override
    public Block getSlabBlock (int meta) {
        return slabs[meta];
    }

    @Override
    public int getPlankMeta (int meta) {
        return planksMeta[meta];
    }

    @Override
    public int getSlabMeta (int meta) {
        return slabsMeta[meta];
    }

    public void init () {

    }

    protected void setPlankSlab (int meta, Block plank, int plankMeta, Block slab, int slabMeta) {
        if (plank != null) {
            planks[meta] = plank;
            planksMeta[meta] = plankMeta;
        }

        if (slab != null) {
            slabs[meta] = slab;
            slabsMeta[meta] = slabMeta;
        }
    }
}
*/