package crazypants.enderio.item;

import java.util.Collection;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.ChatUtil;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.IHideFacades;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ItemConduitProbe extends Item implements IResourceTooltipProvider, IHideFacades {

  public static ItemConduitProbe create() {

    PacketHandler.INSTANCE.registerMessage(PacketConduitProbe.class, PacketConduitProbe.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketConduitProbeMode.class, PacketConduitProbeMode.class, PacketHandler.nextID(), Side.SERVER);

    ItemConduitProbe result = new ItemConduitProbe();
    result.init();
    return result;
  }
  
  public static boolean copyPasteSettings(EntityPlayer player, ItemStack stack, IConduitBundle bundle, EnumFacing dir) {
    boolean isCopy = player.isSneaking();
    boolean clearedData = false;
    NBTTagCompound nbt = stack.getTagCompound();
    
    if(nbt == null) {
      nbt = new NBTTagCompound();
      stack.setTagCompound(nbt);
    }
    
    boolean performedAction = false;
    Collection<IConduit> conduits = bundle.getConduits();
    for(IConduit conduit : conduits) {
      if(conduit.getExternalConnections().contains(dir)) {
        if(isCopy && !clearedData) {
          nbt = new NBTTagCompound();
          stack.setTagCompound(nbt);
          clearedData = true;
        }
        if(isCopy) {
          performedAction |= conduit.writeConnectionSettingsToNBT(dir, nbt);
        } else {
          performedAction |= conduit.readConduitSettingsFromNBT(dir, nbt);
        }        
      }
    }

    if(isCopy && performedAction && player.worldObj.isRemote) {
      ChatUtil.sendNoSpamClient("Copied conduit settings");
    }
    
    return performedAction;
  }

  protected ItemConduitProbe() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName("enderio." + ModObject.itemConduitProbe.name());
    setMaxStackSize(1);
    setHasSubtypes(true);
  }
  
  @Override
  public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
         TileEntity te = world.getTileEntity(pos);
    if(!(te instanceof IConduitBundle)) {
      return false;
    }
        
    if(itemStack.getItemDamage() == 0) {      
      if(PacketConduitProbe.canCreatePacket(world, pos.getX(), pos.getY(), pos.getZ())) {
        if(world.isRemote) {
          PacketHandler.INSTANCE.sendToServer(new PacketConduitProbe(pos.getX(), pos.getY(), pos.getZ(), side));
        }
        return true;
      }
    } 
    return false;
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemConduitProbe.unlocalisedName);
  }

 
  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player) {  
    return true;
  }
  
  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }
}
