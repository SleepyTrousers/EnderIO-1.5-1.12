package crazypants.enderio.machine.obelisk.aversion;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.obelisk.ObeliskRenderer;
import crazypants.enderio.machine.obelisk.ObeliskSpecialRenderer;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class AversionObeliskRenderer extends ObeliskSpecialRenderer<TileAversionObelisk> {

    private ItemStack offStack =
            new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
    private ItemStack onStack =
            new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.REANIMATED_TORMENTED.ordinal());

    public AversionObeliskRenderer(ObeliskRenderer renderer) {
        super(null, renderer);
    }

    @Override
    protected ItemStack getFloatingItem(TileAversionObelisk te) {
        if (te == null) {
            return offStack;
        }
        TileAversionObelisk sg = te;
        if (sg.isActive()) {
            return onStack;
        }
        return offStack;
    }
}
