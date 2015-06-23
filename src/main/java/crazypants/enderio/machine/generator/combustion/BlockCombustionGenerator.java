package crazypants.enderio.machine.generator.combustion;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
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

public class BlockCombustionGenerator extends AbstractMachineBlock<TileCombustionGenerator> {

  public static int renderId = -1;

  private IIcon overlayPullSides, overlayPullTopBottom, overlayDisabledNoCenter;

  public static BlockCombustionGenerator create() {
    PacketHandler.INSTANCE.registerMessage(PacketCombustionTank.class, PacketCombustionTank.class, PacketHandler.nextID(), Side.CLIENT);

    BlockCombustionGenerator gen = new BlockCombustionGenerator();
    gen.init();
    return gen;
  }

  protected BlockCombustionGenerator() {
    super(ModObject.blockCombustionGenerator, TileCombustionGenerator.class);
  }

  @Override
  public int getLightOpacity() {
    return 0;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCombustionGenerator) {
      return new ContainerCombustionEngine(player.inventory, (TileCombustionGenerator) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCombustionGenerator) {
      return new GuiCombustionGenerator(player.inventory, (TileCombustionGenerator) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_COMBUSTION_GEN;
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

  @SideOnly(Side.CLIENT)
  @Override
  protected void registerOverlayIcons(IIconRegister iIconRegister) {
    super.registerOverlayIcons(iIconRegister);
    overlayPullSides = iIconRegister.registerIcon("enderio:overlays/pullSides");
    overlayPullTopBottom = iIconRegister.registerIcon("enderio:overlays/pullTopBottom");
    overlayDisabledNoCenter = iIconRegister.registerIcon("enderio:overlays/disabledNoCenter");
  }

  @Override
  public IIcon getOverlayIconForMode(TileCombustionGenerator tile, ForgeDirection face, IoMode mode) {
    if(face.offsetY == 0 || mode == IoMode.NONE) {
      return super.getOverlayIconForMode(tile, face, mode);
    }
    return mode == IoMode.PULL ? face.offsetY == 0 ? overlayPullSides : overlayPullTopBottom : overlayDisabledNoCenter;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return getBackIconKey(active);
  }

  @Override
  protected String getModelIconKey(boolean active) {
    return "enderio:combustionGenModel";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    // If active, randomly throw some smoke around
    if(isActive(world, x, y, z)) {

      TileEntity te = world.getTileEntity(x, y, z);
      int facing = 3;
      if(te instanceof AbstractMachineEntity) {
        AbstractMachineEntity me = (AbstractMachineEntity) te;
        facing = me.facing;
      }
      ForgeDirection dir = ForgeDirection.getOrientation(facing);
      float startX = x + (dir.offsetX == 0 ? 0.5f : 0f);
      float startY = y + 0.5f;
      float startZ = z + (dir.offsetZ == 0 ? 0.5f : 0f);

      if(dir.offsetX == 1) {
        startX++;
      } else if(dir.offsetZ == 1) {
        startZ++;
      }

      for (int i = 0; i < 2; i++) {
        float xOffset = 0;
        float yOffset = 0;
        float zOffset = 0;
        world.spawnParticle("smoke", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
      }
    }
  }
}
