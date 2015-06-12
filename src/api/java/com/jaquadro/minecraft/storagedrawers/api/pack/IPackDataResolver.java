package com.jaquadro.minecraft.storagedrawers.api.pack;

import net.minecraft.creativetab.CreativeTabs;

public interface IPackDataResolver
{
    String getPackModID ();

    String getBlockName (BlockConfiguration blockConfig);

    CreativeTabs getCreativeTabs (BlockType type);

    boolean isValidMetaValue (int meta);

    String getUnlocalizedName (int meta);

    String getTexturePath (TextureType type, int meta);
}
