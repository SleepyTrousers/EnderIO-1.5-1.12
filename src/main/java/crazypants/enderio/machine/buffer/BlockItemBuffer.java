package crazypants.enderio.machine.buffer;

import java.util.List;

import javax.annotation.Nullable;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.EnderIO;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.power.forge.PowerHandlerItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.capacitor.CapacitorKey.BUFFER_POWER_BUFFER;

public class BlockItemBuffer extends ItemBlock implements IOverlayRenderAware {

  public BlockItemBuffer(Block block, String name) {
    super(block);
    setHasSubtypes(true);
    setMaxDamage(0);
    setRegistryName(name);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return BufferType.values()[stack.getItemDamage()].getUnlocalizedName();
  }

  @Override
  public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      IBlockState newState) {
    super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);

    if (newState.getBlock() == block) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileBuffer) {
        TileBuffer buffer = ((TileBuffer) te);
        BufferType t = BufferType.values()[block.getMetaFromState(newState)];
        buffer.setHasInventory(t.hasInventory);
        buffer.setHasPower(t.hasPower);
        buffer.setCreative(t.isCreative);
        buffer.markDirty();
      }
    }
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    super.addInformation(stack, playerIn, tooltip, advanced);
    tooltip.add(PainterUtil2.getTooltTipText(stack));
  }

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    if (stack.stackSize == 1 && EnderIO.blockBuffer.getStateFromMeta(stack.getMetadata()).getValue(BufferType.TYPE).hasPower) {
      PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
    }
  }

  @Override
  public boolean hasEffect(ItemStack stack) {
    return EnderIO.blockBuffer.getStateFromMeta(stack.getMetadata()).getValue(BufferType.TYPE).isCreative || super.hasEffect(stack);
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    return new InnerProv(stack);
  }

  private class InnerProv implements ICapabilityProvider {

    private final ItemStack container;
    
    public InnerProv(ItemStack container) {
      this.container = container;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityEnergy.ENERGY && EnderIO.blockBuffer.getStateFromMeta(container.getMetadata()).getValue(BufferType.TYPE).hasPower;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      if(capability != CapabilityEnergy.ENERGY) {
        return null;
      }
      BufferType type = EnderIO.blockBuffer.getStateFromMeta(container.getMetadata()).getValue(BufferType.TYPE);
      if(!type.hasPower || container.stackSize > 1) {
        return null;
      }
      if(type.isCreative) {
        return (T)new CreativePowerCap(container);
      }
      return (T)new PowerHandlerItemStack(container, BUFFER_POWER_BUFFER.get(DefaultCapacitorData.BASIC_CAPACITOR), Config.powerConduitTierThreeRF / 20, Config.powerConduitTierThreeRF / 20);
    }
  }
  
  private class CreativePowerCap extends PowerHandlerItemStack {

    public CreativePowerCap (ItemStack container) {
      super(container, BUFFER_POWER_BUFFER.get(DefaultCapacitorData.BASIC_CAPACITOR), Config.powerConduitTierThreeRF, Config.powerConduitTierThreeRF);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return maxReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
      return maxExtract;
    }
  }

}
