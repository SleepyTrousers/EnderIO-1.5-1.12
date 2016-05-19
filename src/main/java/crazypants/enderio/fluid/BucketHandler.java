package crazypants.enderio.fluid;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BucketHandler {

  public static BucketHandler instance = new BucketHandler();

  static {
    MinecraftForge.EVENT_BUS.register(instance);
  }

  private Map<BlockFluidClassic, Item> buckets = new HashMap<BlockFluidClassic, Item>();

  private BucketHandler() {
  }

  public void registerFluid(BlockFluidClassic fluidBlock, Item fullBucket) {
    buckets.put(fluidBlock, fullBucket);
  }

  @SubscribeEvent
  public void onBucketFill(FillBucketEvent event) {
    // no instanceof check, someone may subclass the vanilla bucket
    if (event.current != null && event.current.getItem() == Items.bucket && event.current.stackSize > 0) {
      ItemStack res = getFilledBucket(event.world, event.target);
      if (res != null) {
        event.result = res;
        event.setResult(Result.ALLOW);
      }
    }
  }

  private ItemStack getFilledBucket(World world, RayTraceResult pos) {
    final BlockPos blockPos = pos.getBlockPos();
    final Block block = world.getBlockState(blockPos).getBlock();
    if (block instanceof BlockFluidClassic && buckets.containsKey(block) && ((BlockFluidClassic) block).isSourceBlock(world, blockPos)) {
      final Item bucket = buckets.get(block);
      if (bucket != null) {
        world.setBlockToAir(blockPos);
        return new ItemStack(bucket);
      }
    }
    return null;
  }

  //  @SubscribeEvent
  //  public void addFuelTooltip(ItemTooltipEvent evt) {
  //    ItemStack stack = evt.itemStack;
  //    if(stack == null) {
  //      return;
  //    }
  //
  //    if(Config.addFuelTooltipsToAllFluidContainers || stack.getItem() instanceof ItemBucketEio) {
  //      FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
  //      if(fluidStack == null) {
  //        return;
  //      }
  //      addTooltipForFluid(evt.toolTip, fluidStack.getFluid());
  //    }
  //  }
  //
  //  protected void addTooltipForFluid(List list, Fluid fluid) {
  //    if(fluid != null) {
  //      Fuel fuel = IronEngineFuel.getFuelForFluid(fluid);
  //      if(fuel != null) {
  //        list.add(EnderIO.lang.localize("fuel.tooltip.heading"));
  //        list.add(EnumChatFormatting.ITALIC + " " + PowerDisplayUtil.formatPowerPerTick(fuel.powerPerCycle));
  //        list.add(EnumChatFormatting.ITALIC + " " + fuel.totalBurningTime + " " + EnderIO.lang.localize("fuel.tooltip.burnTime"));
  //      }
  //    }
  //  }

}
