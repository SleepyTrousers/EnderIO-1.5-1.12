package crazypants.util;

import java.util.Random;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.liquid.PacketFluidLevel;
import crazypants.enderio.machine.generator.combustion.PacketCombustionTank;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.PacketBurnTime;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;

public class ClientUtil {

  public static void doFluidLevelUpdate(int x, int y, int z, PacketFluidLevel pkt) {
    TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(x, y, z));
    if(pkt.tc == null || !(tile instanceof IConduitBundle)) {
      return;
    }
    IConduitBundle bundle = (IConduitBundle) tile;
    ILiquidConduit con = bundle.getConduit(ILiquidConduit.class);
    if(con == null) {
      return;
    }
    con.readFromNBT(pkt.tc, TileConduitBundle.NBT_VERSION);
  }

//  public static void doGasLevelUpdate(int x, int y, int z, PacketGasLevel pkt) {
//    TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(x, y, z));
//    if(pkt.tc == null || !(tile instanceof IConduitBundle)) {
//      return;
//    }
//    IConduitBundle bundle = (IConduitBundle) tile;
//    IGasConduit con = bundle.getConduit(IGasConduit.class);
//    if(con == null) {
//      return;
//    }
//    con.readFromNBT(pkt.tc, TileConduitBundle.NBT_VERSION);
//  }

  public static void spawnFarmParcticles(Random rand, BlockPos bc) {
    double xOff = 0.5 + (rand.nextDouble() - 0.5) * 1.1;
    double yOff = 0.5 + (rand.nextDouble() - 0.5) * 0.2;
    double zOff = 0.5 + (rand.nextDouble() - 0.5) * 1.1;
    Minecraft.getMinecraft().theWorld.spawnParticle(EnumParticleTypes.PORTAL, bc.getX() + xOff, bc.getY() + yOff, bc.getZ() + zOff, (rand.nextDouble() - 0.5) * 1.5, -rand.nextDouble(),
        (rand.nextDouble() - 0.5) * 1.5);
  }

  public static void setTankNBT(PacketCombustionTank message, int x, int y, int z) {
    TileCombustionGenerator tile = (TileCombustionGenerator) Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(x, y, z));
    if(tile == null) {
      //no loaded on client when receiving message, can happen when loading the chunks 
      return;
    }

    if(message.nbtRoot.hasKey("coolantTank")) {
      NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("coolantTank");
      tile.getCoolantTank().readFromNBT(tankRoot);
    } else {
      tile.getCoolantTank().setFluid(null);
    }
    if(message.nbtRoot.hasKey("fuelTank")) {
      NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("fuelTank");
      tile.getFuelTank().readFromNBT(tankRoot);
    } else {
      tile.getFuelTank().setFluid(null);
    }
  }

  public static void setStirlingBurnTime(PacketBurnTime message, int x, int y, int z) {

    TileEntityStirlingGenerator tile = (TileEntityStirlingGenerator) Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(x, y, z));
    if(tile == null) {
      //no loaded on client when receiving message, can happen when loading the chunks 
      return;
    }

    tile.burnTime = message.burnTime;
    tile.totalBurnTime = message.totalBurnTime;
  }
  
  
  public static void regRenderer(Item item, int meta, String name) {
    regRenderer(item, meta, EnderIO.MODID, name);
  }

  public static void regRenderer(Item item, int meta, String modId, String name) {
    String resourceName;
    if (modId != null) {
      resourceName = modId + ":" + name;
    } else {
      resourceName = name;
    }
    regRenderer(item, meta, new ResourceLocation(resourceName));
  }
  
  public static void regRenderer(Item item, int meta, ResourceLocation loc) {
    RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
    renderItem.getItemModelMesher().register(item, meta, new ModelResourceLocation(loc, "inventory"));
  }

  public static void registerRenderer(Item item, String name) {
    regRenderer(item, 0, name);
  }
  
}
