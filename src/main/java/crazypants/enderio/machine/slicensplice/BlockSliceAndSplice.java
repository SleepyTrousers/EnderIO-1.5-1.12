package crazypants.enderio.machine.slicensplice;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSliceAndSplice extends AbstractMachineBlock<TileSliceAndSplice> {

    public static BlockSliceAndSplice create() {
        BlockSliceAndSplice result = new BlockSliceAndSplice();
        result.init();
        return result;
    }

    protected BlockSliceAndSplice() {
        super(ModObject.blockSliceAndSplice, TileSliceAndSplice.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileSliceAndSplice) {
            return new ContainerSliceAndSplice(player.inventory, (TileSliceAndSplice) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileSliceAndSplice) {
            return new GuiSliceAndSplice(player.inventory, (TileSliceAndSplice) te);
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_SLICE_N_SPLICE;
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        if (active) {
            return "enderio:sliceAndSpliceFrontOn";
        }
        return "enderio:sliceAndSpliceFront";
    }

    @Override
    protected String getSideIconKey(boolean active) {
        return "enderio:blockSoulMachineSide";
    }

    @Override
    protected String getTopIconKey(boolean active) {
        return "enderio:blockSoulMachineTop";
    }

    @Override
    protected String getBottomIconKey(boolean active) {
        return "enderio:blockSoulMachineBottom";
    }

    @Override
    protected String getBackIconKey(boolean active) {
        return "enderio:blockSoulMachineBack";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileSliceAndSplice te = (TileSliceAndSplice) world.getTileEntity(x, y, z);
        if (isActive(world, x, y, z) && te != null) {

            ForgeDirection front = ForgeDirection.values()[te.facing];

            for (int i = 0; i < 2; i++) {
                double px = x + 0.5 + front.offsetX * 0.6;
                double pz = z + 0.5 + front.offsetZ * 0.6;
                double v = 0.05;
                double vx = 0;
                double vz = 0;

                if (front == ForgeDirection.NORTH || front == ForgeDirection.SOUTH) {
                    px += world.rand.nextFloat() * 0.9 - 0.45;
                    vz += front == ForgeDirection.NORTH ? -v : v;
                } else {
                    pz += world.rand.nextFloat() * 0.9 - 0.45;
                    vx += front == ForgeDirection.WEST ? -v : v;
                }

                EntityFX fx =
                        Minecraft.getMinecraft().renderGlobal.doSpawnParticle("smoke", px, y + 0.5, pz, vx, 0, vz);
                if (fx != null) {
                    fx.setRBGColorF(
                            0.3f + (rand.nextFloat() * 0.1f),
                            0.1f + (rand.nextFloat() * 0.1f),
                            0.1f + (rand.nextFloat() * 0.1f));
                    fx.motionX *= 0.25f;
                    fx.motionY *= 0.25f;
                    fx.motionZ *= 0.25f;
                }
            }
        }
    }
}
