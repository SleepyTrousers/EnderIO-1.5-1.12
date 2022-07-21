package crazypants.util;

import com.enderio.core.common.util.BlockCoord;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.gas.IGasConduit;
import crazypants.enderio.conduit.gas.PacketGasLevel;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.liquid.PacketFluidLevel;
import crazypants.enderio.machine.generator.combustion.PacketCombustionTank;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.PacketBurnTime;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ClientUtil {

    public static void doFluidLevelUpdate(int x, int y, int z, PacketFluidLevel pkt) {
        TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
        if (pkt.tc == null || !(tile instanceof IConduitBundle)) {
            return;
        }
        IConduitBundle bundle = (IConduitBundle) tile;
        ILiquidConduit con = bundle.getConduit(ILiquidConduit.class);
        if (con == null) {
            return;
        }
        con.readFromNBT(pkt.tc, TileConduitBundle.NBT_VERSION);
    }

    public static void doGasLevelUpdate(int x, int y, int z, PacketGasLevel pkt) {
        TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
        if (pkt.tc == null || !(tile instanceof IConduitBundle)) {
            return;
        }
        IConduitBundle bundle = (IConduitBundle) tile;
        IGasConduit con = bundle.getConduit(IGasConduit.class);
        if (con == null) {
            return;
        }
        con.readFromNBT(pkt.tc, TileConduitBundle.NBT_VERSION);
    }

    public static void spawnFarmParcticles(Random rand, BlockCoord bc) {
        double xOff = 0.5 + (rand.nextDouble() - 0.5) * 1.1;
        double yOff = 0.5 + (rand.nextDouble() - 0.5) * 0.2;
        double zOff = 0.5 + (rand.nextDouble() - 0.5) * 1.1;
        Minecraft.getMinecraft()
                .theWorld
                .spawnParticle(
                        "portal",
                        bc.x + xOff,
                        bc.y + yOff,
                        bc.z + zOff,
                        (rand.nextDouble() - 0.5) * 1.5,
                        -rand.nextDouble(),
                        (rand.nextDouble() - 0.5) * 1.5);
    }

    public static void setTankNBT(PacketCombustionTank message, int x, int y, int z) {
        TileCombustionGenerator tile =
                (TileCombustionGenerator) Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
        if (tile == null) {
            // no loaded on client when receiving message, can happen when loading the chunks
            return;
        }

        if (message.nbtRoot.hasKey("coolantTank")) {
            NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("coolantTank");
            tile.getCoolantTank().readFromNBT(tankRoot);
        } else {
            tile.getCoolantTank().setFluid(null);
        }
        if (message.nbtRoot.hasKey("fuelTank")) {
            NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("fuelTank");
            tile.getFuelTank().readFromNBT(tankRoot);
        } else {
            tile.getFuelTank().setFluid(null);
        }
    }

    public static void setStirlingBurnTime(PacketBurnTime message, int x, int y, int z) {

        TileEntityStirlingGenerator tile =
                (TileEntityStirlingGenerator) Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
        if (tile == null) {
            // no loaded on client when receiving message, can happen when loading the chunks
            return;
        }

        tile.burnTime = message.burnTime;
        tile.totalBurnTime = message.totalBurnTime;
    }
}
