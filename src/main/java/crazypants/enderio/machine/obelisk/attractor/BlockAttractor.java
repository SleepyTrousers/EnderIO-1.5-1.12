package crazypants.enderio.machine.obelisk.attractor;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockAttractor extends BlockObeliskAbstract<TileAttractor> {

    public static BlockAttractor create() {
        BlockAttractor res = new BlockAttractor();
        res.init();
        MinecraftForge.EVENT_BUS.register(new EndermanFixer());
        return res;
    }

    protected BlockAttractor() {
        super(ModObject.blockAttractor, TileAttractor.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileAttractor) {
            return new ContainerAttractor(player.inventory, (TileAttractor) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileAttractor) {
            return new GuiAttractor(player.inventory, (TileAttractor) te);
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_ATTRACTOR;
    }
}
