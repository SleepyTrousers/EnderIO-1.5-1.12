package crazypants.enderio.machine.generator.stirling;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockStirlingGenerator extends AbstractMachineBlock<TileEntityStirlingGenerator> {

    public static BlockStirlingGenerator create() {

        PacketHandler.INSTANCE.registerMessage(
                PacketBurnTime.class, PacketBurnTime.class, PacketHandler.nextID(), Side.CLIENT);

        BlockStirlingGenerator gen = new BlockStirlingGenerator();
        gen.init();
        return gen;
    }

    protected BlockStirlingGenerator() {
        super(ModObject.blockStirlingGenerator, TileEntityStirlingGenerator.class);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new StirlingGeneratorContainer(
                player.inventory, (TileEntityStirlingGenerator) world.getTileEntity(x, y, z));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GuiStirlingGenerator(player.inventory, (TileEntityStirlingGenerator) world.getTileEntity(x, y, z));
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_STIRLING_GEN;
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        if (active) {
            return "enderio:stirlingGenFrontOn";
        }
        return "enderio:stirlingGenFrontOff";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileEntityStirlingGenerator te = (TileEntityStirlingGenerator) world.getTileEntity(x, y, z);
        if (te != null && te.isActive()) {
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

                world.spawnParticle("smoke", px, y + 0.1, pz, vx, 0, vz);
            }
        }
    }
}
