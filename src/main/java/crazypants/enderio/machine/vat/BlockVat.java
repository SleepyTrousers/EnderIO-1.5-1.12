package crazypants.enderio.machine.vat;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.render.VertexRotation;
import crazypants.vecmath.Vector3d;

public class BlockVat extends AbstractMachineBlock<TileVat> {

  public static int renderId;

  public static BlockVat create() {
    PacketHandler.INSTANCE.registerMessage(PacketTanks.class,PacketTanks.class,PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketVatProgress.class, PacketVatProgress.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketDumpTank.class,PacketDumpTank.class,PacketHandler.nextID(), Side.SERVER);
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

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    // used to render the block in the world
    TileEntity te = world.getTileEntity(x, y, z);
    boolean on = false;
    if(te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;
      on = me.isActive();
    }

    if(blockSide == ForgeDirection.UP.ordinal() || blockSide == ForgeDirection.DOWN.ordinal()) {
      return topIcon;
    } else if(blockSide == ForgeDirection.EAST.ordinal() || blockSide == ForgeDirection.WEST.ordinal()) {
      if(on) {
        return blockIconSingleOn;
      } else {
        return blockIconSingle;
      }
    }

    if(on) {
      return onIcon;
    } else {
      return blockIcon;
    }
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
    if(mode == IoMode.DISABLED || face == side || face == side.getOpposite()) {
      return super.getOverlayIconForMode(tile, face, mode);
    } else {
      if(face == ForgeDirection.UP) {
        return overlays[1][mode.ordinal()];
      }
      return overlays[0][mode.ordinal()];
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int blockSide, int blockMeta) {
    if(blockSide == ForgeDirection.UP.ordinal() || blockSide == ForgeDirection.DOWN.ordinal()) {
      return topIcon;
    }
    return blockIcon;
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
    if(te instanceof TileVat) {
      return new ContainerVat(player.inventory, (TileVat) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileVat) {
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
  public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
    return true;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:vatFrontOn";
    }
    return "enderio:vatFront";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    // If active, randomly throw some smoke around
    if(isActive(world, x, y, z)) {
      float startX = x;
      float startY = y + 0.35F;
      float startZ = z;
      for (int k = 0; k < 2; k++) {
        int ran2adn5 = (int) Math.round((Math.random() * 3) + 2);
        ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[ran2adn5];
        if(dir.offsetY == 0) {
          for (int i = 0; i < 2; i++) {

            float xOffset = 0.52f * (1 - dir.offsetX) +  (dir.offsetX * (-0.1F + rand.nextFloat() * 0.2F));
            float yOffset = -0.1F + rand.nextFloat() * 0.2F;
            float zOffset = 0.52f * (1 - dir.offsetZ) +  (dir.offsetZ * (-0.1F + rand.nextFloat() * 0.2F));

            if(rand.nextFloat() > 0.5) {
              VertexRotation vr = new VertexRotation(Math.PI/4, new Vector3d(0,1,0), new Vector3d(0.5,0.25,0.5));
              Vector3d vec = new Vector3d(xOffset, yOffset, zOffset);
              vr.apply(vec);
              xOffset = (float)vec.x;
              yOffset = (float)vec.y;
              zOffset = (float)vec.z;

            }
            world.spawnParticle("smoke", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
          }
        }
      }
    }
  }

}
