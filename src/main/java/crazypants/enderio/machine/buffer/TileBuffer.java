package crazypants.enderio.machine.buffer;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.painter.IPaintableTileEntity;
import crazypants.enderio.machine.painter.PainterUtil;
import crazypants.util.BlockCoord;

public class TileBuffer extends AbstractMachineEntity implements IPaintableTileEntity {

  private Block sourceBlock;
  private int sourceBlockMetadata;
  
  public TileBuffer() {
    super(new SlotDefinition(9, 0, 0));
  }

  @Override
  public String getMachineName() {
    return ModObject.blockBuffer.unlocalisedName;
  }

  @Override
  protected boolean isMachineItemValidForSlot(int i, ItemStack itemstack) {
    return true;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public float getProgress() {
    return 0; // no tasks
  }

  @Override
  protected boolean processTasks(boolean redstoneCheckPassed) {
    return false; // no tasks
  }
  
  @Override
  public boolean canInsertItem(int slot, ItemStack var2, int side) {
   return super.canInsertItem(slot, var2, side) && getIoMode(ForgeDirection.VALID_DIRECTIONS[side]).canRecieveInput();
  }

  @Override
  public boolean canExtractItem(int slot, ItemStack itemstack, int side) {
    return super.canExtractItem(slot, itemstack, side) && getIoMode(ForgeDirection.VALID_DIRECTIONS[side]).canOutput();
  }
  
  @Override
  protected boolean doPush(ForgeDirection dir) {

    if(worldObj.getTotalWorldTime() % 20 != 0) {
      return false;
    }

    BlockCoord loc = getLocation().getLocation(dir);
    TileEntity te = worldObj.getTileEntity(loc.x, loc.y, loc.z);
    return doPush(dir, te, slotDefinition.minInputSlot, slotDefinition.maxInputSlot);
  }
  
  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    PainterUtil.setSourceBlock(nbtRoot, sourceBlock, sourceBlockMetadata);
  }
  
  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    this.sourceBlock = PainterUtil.getSourceBlock(nbtRoot);
    this.sourceBlockMetadata = PainterUtil.getSourceBlockMetadata(nbtRoot);
  }

  @Override
  public void setSourceBlockMetadata(int sourceBlockMetadata) {
    this.sourceBlockMetadata = sourceBlockMetadata;
  }

  @Override
  public int getSourceBlockMetadata() {
    return sourceBlockMetadata;
  }

  @Override
  public void setSourceBlock(Block sourceBlock) {
    this.sourceBlock = sourceBlock;
  }

  @Override
  public Block getSourceBlock() {
    return sourceBlock;
  }
}
