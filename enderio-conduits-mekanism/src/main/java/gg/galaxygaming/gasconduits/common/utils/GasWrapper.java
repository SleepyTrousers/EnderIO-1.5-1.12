package gg.galaxygaming.gasconduits.common.utils;

import javax.annotation.Nullable;
import mekanism.api.gas.IGasHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class GasWrapper {

    @Nullable
    public static IGasHandler getGasHandler(IBlockAccess world, BlockPos pos, EnumFacing side) {
        if (world == null || pos == null) {
            return null;
        }
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile.hasCapability(Capabilities.GAS_HANDLER_CAPABILITY, side)) {
            return tile.getCapability(Capabilities.GAS_HANDLER_CAPABILITY, side);
        }
        return null;
    }
}