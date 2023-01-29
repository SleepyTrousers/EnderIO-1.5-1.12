package crazypants.enderio.machine.crusher;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;

public class BlockCrusher extends AbstractMachineBlock {

    public static BlockCrusher create() {
        PacketHandler.INSTANCE.registerMessage(
                PacketGrindingBall.class,
                PacketGrindingBall.class,
                PacketHandler.nextID(),
                Side.CLIENT);

        BlockCrusher res = new BlockCrusher();
        res.init();
        return res;
    }

    private BlockCrusher() {
        super(ModObject.blockSagMill, TileCrusher.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // The server needs the container as it manages the adding and removing of
        // items, which are then sent to the client for display
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCrusher) {
            return new ContainerCrusher(player.inventory, (TileCrusher) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileCrusher) {
            return new GuiCrusher(player.inventory, (TileCrusher) te);
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_CRUSHER;
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        if (active) {
            return "enderio:crusherFrontOn";
        }
        return "enderio:crusherFront";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileCrusher te = (TileCrusher) world.getTileEntity(x, y, z);
        if (te != null && te.isActive()) {
            ForgeDirection front = ForgeDirection.values()[te.facing];

            for (int i = 0; i < 3; i++) {
                double px = x + 0.5 + front.offsetX * 0.51;
                double pz = z + 0.5 + front.offsetZ * 0.51;
                double py = y + world.rand.nextFloat() * 0.8f + 0.1f;
                double v = 0.05;
                double vx = 0;
                double vz = 0;

                if (front == ForgeDirection.NORTH || front == ForgeDirection.SOUTH) {
                    px += world.rand.nextFloat() * 0.8 - 0.4;
                    vz += front == ForgeDirection.NORTH ? -v : v;
                } else {
                    pz += world.rand.nextFloat() * 0.8 - 0.4;
                    vx += front == ForgeDirection.WEST ? -v : v;
                }

                world.spawnParticle("smoke", px, py, pz, vx, 0, vz);
            }
        }
    }
}
