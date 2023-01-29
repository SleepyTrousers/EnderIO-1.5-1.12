package crazypants.enderio.machine.obelisk.aversion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;

public class BlockAversionObelisk extends BlockObeliskAbstract<TileAversionObelisk> {

    public static BlockAversionObelisk create() {
        BlockAversionObelisk res = new BlockAversionObelisk();
        res.init();

        // Just making sure its loaded
        AversionObeliskController.instance.toString();

        return res;
    }

    protected BlockAversionObelisk() {
        super(ModObject.blockSpawnGuard, TileAversionObelisk.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileAversionObelisk) {
            return new ContainerAversionObelisk(player.inventory, (TileAversionObelisk) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileAversionObelisk) {
            return new GuiAversionObelisk(player.inventory, (TileAversionObelisk) te);
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_SPAWN_GUARD;
    }
}
