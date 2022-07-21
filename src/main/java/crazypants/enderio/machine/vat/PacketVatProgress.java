package crazypants.enderio.machine.vat;

import com.enderio.core.common.network.MessageTileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.IPoweredTask;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.PoweredTaskProgress;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fluids.FluidRegistry;

public class PacketVatProgress extends MessageTileEntity<TileVat>
        implements IMessageHandler<PacketVatProgress, IMessage> {

    private float progress = 0;

    private int inputFluidId = -1;
    private int outputFluidId = -1;

    public PacketVatProgress() {}

    public PacketVatProgress(TileVat vat) {
        super(vat);
        progress = vat.getProgress();

        IPoweredTask task = vat.getCurrentTask();
        if (task == null) {
            return;
        }

        for (MachineRecipeInput input : task.getInputs()) {
            if (input.fluid != null && input.fluid.getFluid() != null) {
                inputFluidId = input.fluid.getFluid().getID();
                break;
            }
        }

        IMachineRecipe rec = task.getRecipe();
        if (rec == null) {
            return;
        }
        for (ResultStack res : rec.getCompletedResult(1.0f, task.getInputs())) {
            if (res.fluid != null && res.fluid.getFluid() != null) {
                outputFluidId = res.fluid.getFluid().getID();
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeFloat(progress);
        buf.writeInt(inputFluidId);
        buf.writeInt(outputFluidId);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        progress = buf.readFloat();
        inputFluidId = buf.readInt();
        outputFluidId = buf.readInt();
    }

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
                if (message.inputFluidId > 0) {
                    tile.currentTaskInputFluid = FluidRegistry.getFluid(message.inputFluidId);
                }
                if (message.outputFluidId > 0) {
                    tile.currentTaskOutputFluid = FluidRegistry.getFluid(message.outputFluidId);
                }
            }
        }
        return null;
    }
}
