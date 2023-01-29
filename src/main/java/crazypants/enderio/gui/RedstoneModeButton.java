package crazypants.enderio.gui;

import com.enderio.core.api.client.gui.IGuiScreen;
import com.enderio.core.client.gui.button.CycleButton;
import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;

public class RedstoneModeButton extends CycleButton<RedstoneControlMode> {

    private IRedstoneModeControlable model;

    private BlockCoord bc;

    private String tooltipKey = "enderio.gui.tooltip.redstoneControlMode";

    public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, IRedstoneModeControlable model) {
        this(gui, id, x, y, model, null);
    }

    public RedstoneModeButton(IGuiScreen gui, int id, int x, int y, IRedstoneModeControlable model, BlockCoord bc) {
        super(gui, id, x, y, RedstoneControlMode.class);
        this.model = model;
        this.bc = bc;
        setMode(model.getRedstoneControlMode());
    }

    public void setMode(RedstoneControlMode newMode) {
        if (model == null) {
            return;
        }
        super.setMode(newMode);
        model.setRedstoneControlMode(getMode());
        if (bc != null) {
            PacketHandler.INSTANCE.sendToServer(new PacketRedstoneMode(model, bc.x, bc.y, bc.z));
        }
        setTooltipKey(tooltipKey); // forces our behavior
    }

    public void setTooltipKey(String key) {
        tooltipKey = key;
        setToolTip(EnderIO.lang.localizeExact(tooltipKey), getMode().getTooltip());
    }
}
