package crazypants.enderio.machines.machine.vat;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.machine.task.PoweredTaskProgress;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.IMachineRecipe.ResultStack;
import crazypants.enderio.base.recipe.MachineRecipeInput;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketVatProgress extends MessageTileEntity<TileVat> {

  private float progress = 0;

  private String inputFluidId = null;
  private String outputFluidId = null;

  public PacketVatProgress() {
  }

  public PacketVatProgress(@Nonnull TileVat vat) {
    super(vat);
    progress = vat.getProgress();

    IPoweredTask task = vat.getCurrentTask();
    if (task == null) {
      return;
    }

    for (MachineRecipeInput input : task.getInputs()) {
      if (input.fluid != null && input.fluid.getFluid() != null) {
        inputFluidId = input.fluid.getFluid().getName();
        break;
      }
    }

    IMachineRecipe rec = task.getRecipe();
    if (rec == null) {
      return;
    }
    for (ResultStack res : rec.getCompletedResult(1.0f, task.getInputs())) {
      final FluidStack fluid = res.fluid;
      if (fluid != null && fluid.getFluid() != null) {
        outputFluidId = fluid.getFluid().getName();
      }
    }

  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeFloat(progress);
    ByteBufUtils.writeUTF8String(buf, inputFluidId == null ? "" : inputFluidId);
    ByteBufUtils.writeUTF8String(buf, outputFluidId == null ? "" : outputFluidId);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    progress = buf.readFloat();
    inputFluidId = ByteBufUtils.readUTF8String(buf);
    if (inputFluidId.isEmpty()) {
      inputFluidId = null;
    }
    outputFluidId = ByteBufUtils.readUTF8String(buf);
    if (outputFluidId.isEmpty()) {
      outputFluidId = null;
    }
  }

  public static class Handler implements IMessageHandler<PacketVatProgress, IMessage> {

    @Override
    public IMessage onMessage(PacketVatProgress message, MessageContext ctx) {
      TileVat tile = message.getTileEntity(EnderIO.proxy.getClientWorld());
      if (tile != null) {
        tile.currentTaskInputFluid = null;
        tile.currentTaskOutputFluid = null;
        if (message.progress < 0) {
          tile.setClientTask(null);
        } else {
          tile.setClientTask(new PoweredTaskProgress(message.progress));
          if (message.inputFluidId != null) {
            tile.currentTaskInputFluid = FluidRegistry.getFluid(message.inputFluidId);
          }
          if (message.outputFluidId != null) {
            tile.currentTaskOutputFluid = FluidRegistry.getFluid(message.outputFluidId);
          }
        }
      }
      return null;
    }
  }

}
