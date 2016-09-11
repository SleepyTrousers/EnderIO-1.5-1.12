package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.creativetab.CreativeTabs;

public class StandardDataResolver implements IPackDataResolver
{
    private String modID;
    private String[] unlocalizedNames;
    private CreativeTabs creativeTab;

    public StandardDataResolver (String modID, String[] unlocalizedNames) {
        this.modID = modID;
        this.unlocalizedNames = unlocalizedNames;
    }

    public StandardDataResolver (String modID, String[] unlocalizedNames, CreativeTabs creativeTab) {
        this(modID, unlocalizedNames);
        this.creativeTab = creativeTab;
    }

    @Override
    public String getPackModID () {
        return modID;
    }

    protected String makeBlockName (String name) {
        return getPackModID().toLowerCase() + "." + name;
    }

    @Override
    public String getBlockName (BlockConfiguration blockConfig) {
        switch (blockConfig.getBlockType()) {
            case Drawers:
            case DrawersSorting:
                if (blockConfig.getDrawerCount() == 1)
                    return makeBlockName("fullDrawers1");
                if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth())
                    return makeBlockName("fullDrawers2");
                if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth())
                    return makeBlockName("fullDrawers4");
                if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth())
                    return makeBlockName("halfDrawers2");
                if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth())
                    return makeBlockName("halfDrawers4");
                break;
            case Trim:
                return makeBlockName("trim");
        }
        return null;
    }

    @Override
    public CreativeTabs getCreativeTabs (BlockType type) {
        return creativeTab;
    }

    @Override
    public boolean isValidMetaValue (int meta) {
        if (meta < 0 || meta >= unlocalizedNames.length)
            return false;

        return unlocalizedNames != null && unlocalizedNames[meta] != null;
    }

    @Override
    public String getUnlocalizedName (int meta) {
        if (!isValidMetaValue(meta))
            return null;

        return unlocalizedNames[meta];
    }

    protected String getBaseTexturePath () {
        return getPackModID() + ":";
    }

    protected String getTextureMetaName (int meta) {
        return getUnlocalizedName(meta);
    }

    @Override
    public String getTexturePath (TextureType type, int meta) {
        switch (type) {
            case Front1:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_front_1";
            case Front2:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_front_2";
            case Front4:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_front_4";
            case Side:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_side";
            case SideSort:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_sort";
            case SideVSplit:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_side_v";
            case SideHSplit:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_side_h";
            case TrimBorder:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_trim";
            case TrimBlock:
                return getBaseTexturePath() + "drawers_" + getTextureMetaName(meta) + "_side";
            default:
                return "";
        }
    }
}
