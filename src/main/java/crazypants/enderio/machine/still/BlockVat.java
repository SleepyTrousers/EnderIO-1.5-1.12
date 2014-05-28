package crazypants.enderio.machine.still;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.render.VertexRotation;
import crazypants.util.FluidUtil;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;

public class BlockVat extends AbstractMachineBlock<TileVat> {

  public static int renderId;

  public static BlockVat create() {
    EnderIO.packetPipeline.registerPacket(PacketTanks.class);
    BlockVat res = new BlockVat();
    res.init();
    return res;
  }

  protected IIcon onIcon;
  protected IIcon topIcon;
  protected IIcon blockIconSingle;
  protected IIcon blockIconSingleOn;

  public BlockVat() {
    super(ModObject.blockVat, TileVat.class);
  }

  @Override
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon("enderio:vatFront");
    blockIconSingle = iIconRegister.registerIcon("enderio:vatFrontSingle");
    blockIconSingleOn = iIconRegister.registerIcon("enderio:vatFrontOnSingle");
    onIcon = iIconRegister.registerIcon("enderio:vatFrontOn");

    topIcon = iIconRegister.registerIcon("enderio:vatTop");

    overlayIconPull = iIconRegister.registerIcon("enderio:vatOverlayPull");
    overlayIconPush = iIconRegister.registerIcon("enderio:vatOverlayPush");
    overlayIconPushPull = iIconRegister.registerIcon("enderio:vatOverlayPushPull");
    overlayIconDisabled = iIconRegister.registerIcon("enderio:vatOverlayDisabled");
    overlayIconNone = iIconRegister.registerIcon("enderio:machineOverlayNone");
  }

  @Override
  public int getLightOpacity() {
    return 0;
  }

  @Override
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



  @Override
  public IIcon getIcon(int blockSide, int blockMeta) {
    if(blockSide == ForgeDirection.UP.ordinal() || blockSide == ForgeDirection.DOWN.ordinal()) {
      return topIcon;
    }
    return blockIcon;
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileVat)) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    TileVat vat = (TileVat) te;
    ItemStack item = entityPlayer.inventory.getCurrentItem();
    if(item == null) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    //check for filled fluid containers and see if we can empty them into our input tank
    FluidStack fluid = FluidUtil.getFluidFromItem(item);
    if(fluid != null) {
      int filled = vat.fill(ForgeDirection.UP, fluid, false);
      if(filled >= fluid.amount) {
        vat.fill(ForgeDirection.UP, fluid, true);
        if(!entityPlayer.capabilities.isCreativeMode) {
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, Util.consumeItem(item));
        }
        return true;
      }
    }

    //now check for empty fluid containers to fill
    FluidStack available = vat.outputTank.getFluid();
    if(available != null) {
      ItemStack res = FluidContainerRegistry.fillFluidContainer(available.copy(), item);
      FluidStack filled = FluidContainerRegistry.getFluidForFilledItem(res);

      if(filled == null) { //this shouldn't be necessary but it appears to be a bug as the above method doesnt work
        FluidContainerData[] datas = FluidContainerRegistry.getRegisteredFluidContainerData();
        for (FluidContainerData data : datas) {
          if(data.fluid.getFluid().getName().equals(available.getFluid().getName()) && data.emptyContainer.isItemEqual(item)) {
            res = data.filledContainer.copy();
            filled = FluidContainerRegistry.getFluidForFilledItem(res);
          }
        }
      }

      if(filled != null) {
        vat.drain(ForgeDirection.DOWN, filled, true);
        if(item.stackSize > 1) {
          item.stackSize--;
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, item);
          for (int i = 0; i < entityPlayer.inventory.mainInventory.length; i++) {
            if(entityPlayer.inventory.mainInventory[i] == null) {
              entityPlayer.inventory.setInventorySlotContents(i, res);
              return true;
            }
          }
          if(!world.isRemote) {
            Util.dropItems(world, res, x, y, z, true);
          }

        } else {
          entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, res);
        }

        return true;
      }
    }

    return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
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
