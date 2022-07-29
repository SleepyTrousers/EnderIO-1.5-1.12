package crazypants.util;

import cofh.api.block.IBlockAppearance;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

@Optional.Interface(modid = "chisel", iface = "com.cricketcraft.chisel.api.IFacade")
public interface IFacade extends com.cricketcraft.chisel.api.IFacade, IBlockAppearance {
    Block getFacade(IBlockAccess world, int x, int y, int z, int side);

    int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side);
}
