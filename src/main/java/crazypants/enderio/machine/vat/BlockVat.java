package crazypants.enderio.machine.vat;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.network.PacketHandler;

public class BlockVat extends AbstractMachineBlock<TileVat> {

    public static int renderId;

    public static BlockVat create() {
        PacketHandler.INSTANCE
                .registerMessage(PacketTanks.class, PacketTanks.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketVatProgress.class, PacketVatProgress.class, PacketHandler.nextID(), Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketDumpTank.class, PacketDumpTank.class, PacketHandler.nextID(), Side.SERVER);
        BlockVat res = new BlockVat();
        res.init();
        return res;
    }

    protected IIcon onIcon;
    protected IIcon topIcon;
    protected IIcon blockIconSingle;
    protected IIcon blockIconSingleOn;
    protected IIcon[][] overlays;

    public BlockVat() {
        super(ModObject.blockVat, TileVat.class);
    }

    protected String getModelIconKey(boolean active) {
        return "enderio:vatModel";
    }

    @Override
    public int getLightOpacity() {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void registerOverlayIcons(IIconRegister iIconRegister) {
        super.registerOverlayIcons(iIconRegister);

        overlays = new IIcon[2][IoMode.values().length];

        overlays[0][IoMode.PULL.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pullSides");
        overlays[0][IoMode.PUSH.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pushSides");
        overlays[0][IoMode.PUSH_PULL.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pushPullSides");
        overlays[0][IoMode.DISABLED.ordinal()] = iIconRegister.registerIcon("enderio:overlays/disabledNoCenter");

        overlays[1][IoMode.PULL.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pullTopBottom");
        overlays[1][IoMode.PUSH.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pushTopBottom");
        overlays[1][IoMode.PUSH_PULL.ordinal()] = iIconRegister.registerIcon("enderio:overlays/pushPullTopBottom");
        overlays[1][IoMode.DISABLED.ordinal()] = overlays[0][IoMode.DISABLED.ordinal()];
    }

    @Override
    public IIcon getOverlayIconForMode(TileVat tile, ForgeDirection face, IoMode mode) {
        ForgeDirection side = tile.getFacingDir().getRotation(ForgeDirection.DOWN);
        if (mode == IoMode.DISABLED || face == side || face == side.getOpposite()) {
            return super.getOverlayIconForMode(tile, face, mode);
        } else {
            if (face == ForgeDirection.UP) {
                return overlays[1][mode.ordinal()];
            }
            return overlays[0][mode.ordinal()];
        }
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // The server needs the container as it manages the adding and removing of
        // items, which are then sent to the client for display
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileVat) {
            return new ContainerVat(player.inventory, (TileVat) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileVat) {
            return new GuiVat(player.inventory, (TileVat) te);
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_STILL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_,
            int p_149646_5_) {
        return true;
    }

    @Override
    protected String getMachineFrontIconKey(boolean active) {
        return getBackIconKey(active);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        // Spit some "steam" out the spout
        if (isActive(world, x, y, z)) {
            TileVat te = (TileVat) world.getTileEntity(x, y, z);
            float pX = x + 0.5f;
            float pY = y + 0.7f;
            float pZ = z + 0.5f;

            ForgeDirection dir = te.getFacingDir();
            pX += 0.6f * dir.offsetX;
            pZ += 0.6f * dir.offsetZ;

            double velX = ((rand.nextDouble() * 0.075) + 0.025) * dir.offsetX;
            double velZ = ((rand.nextDouble() * 0.075) + 0.025) * dir.offsetZ;

            int num = rand.nextInt(4) + 2;
            for (int k = 0; k < num; k++) {
                EffectRenderer er = Minecraft.getMinecraft().effectRenderer;
                EntitySmokeFX fx = new EntitySmokeFX(world, pX, pY, pZ, 1, 1, 1);
                fx.setRBGColorF(
                        1 - (rand.nextFloat() * 0.2f),
                        1 - (rand.nextFloat() * 0.1f),
                        1 - (rand.nextFloat() * 0.2f));
                fx.setVelocity(velX, -0.06, velZ);
                er.addEffect(fx);
            }
        }
    }
}
