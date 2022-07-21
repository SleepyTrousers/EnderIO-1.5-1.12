package crazypants.enderio.machine.crafter;

import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCrafter extends AbstractMachineBlock<TileCrafter> {

    public static BlockCrafter create() {
        PacketHandler.INSTANCE.registerMessage(
                PacketCrafter.class, PacketCrafter.class, PacketHandler.nextID(), Side.SERVER);
        BlockCrafter res = new BlockCrafter();
        res.init();
        return res;
    }

    protected BlockCrafter() {
        super(ModObject.blockCrafter, TileCrafter.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCrafter) {
            return new ContainerCrafter(player.inventory, (TileCrafter) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCrafter) {
            return new GuiCrafter(player.inventory, (TileCrafter) te);
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_CRAFTER;
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        return "enderio:crafter";
    }

    @Override
    protected String getSideIconKey(boolean active) {
        return "enderio:crafterSide";
    }

    @Override
    protected String getBackIconKey(boolean active) {
        return getSideIconKey(active);
    }

    @Override
    protected String getTopIconKey(boolean active) {
        return "enderio:crafterTop";
    }
}
