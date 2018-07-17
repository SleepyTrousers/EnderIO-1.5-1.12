package crazypants.enderio.machines.machine.basin;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.task.PoweredTaskProgress;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import crazypants.enderio.base.recipe.basin.BasinMachineRecipe;
import crazypants.enderio.base.recipe.basin.BasinRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBasinProgress extends MessageTileEntity<TileBasin> {
  
  private float progress = -1;
  
  private Plane orientation = Plane.VERTICAL;
  private FluidStack inputA, inputB;
  
  public PacketBasinProgress() {}
  
  public PacketBasinProgress(@Nonnull TileBasin basin) {
    super(basin);
    this.progress = basin.getProgress();
    
    IPoweredTask task = basin.getCurrentTask();
    if (task == null) {
      return;
    }
    BasinMachineRecipe machineRecipe = (BasinMachineRecipe) task.getRecipe();
    if (machineRecipe == null) {
      return;
    }
    BasinRecipe rec = (BasinRecipe) machineRecipe.getRecipeForInputs(task.getInputs());
    if (rec == null) {
      return;
    }
    
    this.orientation = rec.getOrientation();

    NNList<MachineRecipeInput> inputs = task.getInputs();
    if (orientation == Plane.VERTICAL) {
      inputA = inputs.get(0).fluid;
      inputB = inputs.get(1).fluid;
    } else {
      inputA = inputs.get(2).fluid;
      inputB = inputs.get(3).fluid;
    }
  }
  
  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeFloat(progress);
    buf.writeByte(orientation.ordinal());
    ByteBufUtils.writeTag(buf, inputA == null ? null : inputA.writeToNBT(new NBTTagCompound()));
    ByteBufUtils.writeTag(buf, inputB == null ? null : inputB.writeToNBT(new NBTTagCompound()));
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    progress = buf.readFloat();
    orientation = Plane.values()[buf.readByte()];
    NBTTagCompound tag = ByteBufUtils.readTag(buf);
    inputA = tag == null ? null : FluidStack.loadFluidStackFromNBT(tag);
    tag = ByteBufUtils.readTag(buf);
    inputB = tag == null ? null : FluidStack.loadFluidStackFromNBT(tag);
  }
  
  public static class Handler implements IMessageHandler<PacketBasinProgress, IMessage> {
    
    @Override
    public IMessage onMessage(PacketBasinProgress message, MessageContext ctx) {
      TileBasin basin = message.getTileEntity(EnderIO.proxy.getClientWorld());
      basin.orientation = message.orientation;
      if (message.progress >= 0) {
        basin.setClientTask(new PoweredTaskProgress(message.progress));
        basin.inputA = message.inputA;
        basin.inputB = message.inputB;
      } else {
        basin.setClientTask(null);
        basin.inputA = null;
        basin.inputB = null;
      }
      return null;
    }
  }

}
