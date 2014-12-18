package crazypants.enderio.machine.buffer;

import info.jbcs.minecraft.chisel.api.IFacade;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.painter.BasicPainterTemplate;
import crazypants.enderio.machine.painter.PainterUtil;

public class BlockBuffer extends AbstractMachineBlock<TileBuffer> implements IFacade {
    
  public static BlockBuffer create() {
    BlockBuffer res = new BlockBuffer();
    res.init();
    return res;
  }
  
  private BlockBuffer() {
    super(ModObject.blockBuffer, TileBuffer.class);
    setBlockTextureName("enderio:blockBuffer");
  }
  
  @Override
  protected void init() {  
    GameRegistry.registerBlock(this, BlockItemBuffer.class, modObject.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, modObject.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockPainter.unlocalisedName, new PainterTemplate());
  }
  
  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileBuffer) {
      return new ContainerBuffer(player.inventory, (TileBuffer) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileBuffer) {
      return new GuiBuffer(player.inventory, (TileBuffer) te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_BUFFER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    return this.textureName;
  }
  
  @Override
  protected String getBackIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }
  
  @Override
  protected String getSideIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }
  
  @Override
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileBuffer) {
      TileBuffer tef = (TileBuffer) te;
      if(tef.getSourceBlock() != null) {
        return tef.getSourceBlock().getIcon(blockSide, tef.getSourceBlockMetadata());
      }
    }
    return super.getIcon(world, x, y, z, blockSide);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
    if(entity instanceof EntityPlayer) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileBuffer) {
        TileBuffer ta = (TileBuffer) te;
        if(stack.stackTagCompound != null) {
          ta.readCommon(stack.stackTagCompound);
        }
        world.markBlockForUpdate(x, y, z);
      }
    }
  }

  @Override
  public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
    return Lists.newArrayList();
  }

  @Override
  protected boolean shouldDropDefaultItem(World world, EntityPlayer player, int x, int y, int z) {
    return false;
  }
  
  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof TileBuffer) {
        TileBuffer cb = (TileBuffer) te;                
        if(!player.capabilities.isCreativeMode) {
          dropBlockAsItem(world, x, y, z, createDrop(cb));
        }
      }
    }
    return super.removedByPlayer(world, player, x, y, z, willHarvest);
  }

  private ItemStack createDrop(TileBuffer te) {
    ItemStack stack = new ItemStack(this, 1, BlockItemBuffer.Type.get(te).ordinal());
    stack.stackTagCompound = new NBTTagCompound();
    te.writeCommon(stack.stackTagCompound);
    return stack;
  }
  
  public ItemStack createItemStackForSourceBlock(ItemStack machine, Block block, int sourceMeta) {
    PainterUtil.setSourceBlock(machine, block, sourceMeta);
    return machine;
  }
  
  public final class PainterTemplate extends BasicPainterTemplate {

    public PainterTemplate() {
      super(BlockBuffer.this);
    }

    @Override
    public ResultStack[] getCompletedResult(float chance, MachineRecipeInput... inputs) {
      ItemStack paintSource = MachineRecipeInput.getInputForSlot(1, inputs);
      if(paintSource == null) {
        return new ResultStack[0];
      }
      return new ResultStack[] { new ResultStack(createItemStackForSourceBlock(MachineRecipeInput.getInputForSlot(0, inputs).copy(),
          Block.getBlockFromItem(paintSource.getItem()), paintSource.getItemDamage())) };
    }
  }

  @Override
  public int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileBuffer) {
      return ((TileBuffer)te).getSourceBlockMetadata();
    }
    return 0;
  }

  @Override
  public Block getFacade(IBlockAccess world, int x, int y, int z, int side) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileBuffer) {
      return ((TileBuffer)te).getSourceBlock();
    }
    return this;
  }

}
