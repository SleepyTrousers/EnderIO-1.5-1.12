package crazypants.util;

import java.util.Random;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.EnderIO;
import crazypants.enderio.IModObject;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.liquid.PacketFluidLevel;
import crazypants.enderio.machine.generator.combustion.PacketCombustionTank;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machine.generator.stirling.PacketBurnTime;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelLoader;

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

  public static void setTankNBT(PacketCombustionTank message, BlockPos pos) {
    TileCombustionGenerator tile = (TileCombustionGenerator) Minecraft.getMinecraft().theWorld.getTileEntity(pos);
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
  
  public static void regRenderer(Block block, int meta, String name) {
    Item item = Item.getItemFromBlock(block);
    ResourceLocation resourceLocation = block.getRegistryName();
    ModelResourceLocation modelResourceLocation = new ModelResourceLocation(resourceLocation, name);
    ModelLoader.setCustomModelResourceLocation(item, meta, modelResourceLocation);
  }
  
  public static void regRenderer(Item item, int meta, String name) {
    regRenderer(item, meta, EnderIO.DOMAIN, name);
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
    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(loc, "inventory"));
  }

  public static void registerDefaultItemRenderer(IModObject mo) {
    if (mo.getItem() != null) {
      regRenderer(mo.getItem(), 0, mo.getUnlocalisedName());
    }
  }

  public static void registerDefaultItemRenderer(BlockEnder<?> block) {
    if(block == null) {
      return;
    }
    regRenderer(Item.getItemFromBlock(block), 0, block.getName());
  }
  
  public static void registerRenderer(Item item, String name) {
    regRenderer(item, 0, name);
  }

  private static final Random rand = new Random();

  public static void spawnParcticles(double posX, double posY, double posZ, int count, EnumParticleTypes particle) {
    for (int i = 0; i < count; ++i) {
      double xOff, yOff, zOff, d0, d1, d2;
      if (particle == EnumParticleTypes.PORTAL) {
        xOff = (rand.nextDouble() - 0.5) * 1.1;
        yOff = (rand.nextDouble() - 0.5) * 1.1;
        zOff = (rand.nextDouble() - 0.5) * 1.1;
        d0 = (rand.nextDouble() - 0.5) * 1.5;
        d1 = -rand.nextDouble();
        d2 = (rand.nextDouble() - 0.5) * 1.5;
      } else if (particle == EnumParticleTypes.DAMAGE_INDICATOR) {
        xOff = 0.1D;
        yOff = 0.0D;
        zOff = 0.1D;
        d0 = d1 = d2 = 0.2D;
      } else {
        xOff = yOff = zOff = 0;
        d0 = rand.nextGaussian() * 0.02D;
        d1 = rand.nextGaussian() * 0.02D;
        d2 = rand.nextGaussian() * 0.02D;
      }
      Minecraft.getMinecraft().theWorld.spawnParticle(particle, posX + xOff, posY + yOff, posZ + zOff, d0, d1, d2);
    }
  }
  
}
