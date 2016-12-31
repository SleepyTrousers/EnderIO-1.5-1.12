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
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemConduitProbe extends Item implements IResourceTooltipProvider, IHideFacades, IHaveRenderers {

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
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName("enderio." + ModObject.itemConduitProbe.getUnlocalisedName());
    setRegistryName(ModObject.itemConduitProbe.name());
    setMaxStackSize(1);
    setHasSubtypes(true);
  }
  
  
  
  @Override
  public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX,
      float hitY, float hitZ) {
  
    if(itemStack.getItemDamage() == 0) {      
      if(PacketConduitProbe.canCreatePacket(world, pos.getX(), pos.getY(), pos.getZ())) {
        if(world.isRemote) {
          PacketHandler.INSTANCE.sendToServer(new PacketConduitProbe(pos.getX(), pos.getY(), pos.getZ(), side));
        }
        return EnumActionResult.SUCCESS;
      }
    } 
    return EnumActionResult.PASS;
  }

  protected void init() {
    GameRegistry.register(this);
  }
 
  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }
  
  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {   
    return true;
  }
  
  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    ClientUtil.regRenderer(this, 0, ModObject.itemConduitProbe.getUnlocalisedName());
    ClientUtil.regRenderer(this, 1, ModObject.itemConduitProbe.getUnlocalisedName() + "Variant");
  }

}
