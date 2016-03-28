package crazypants.enderio.teleport.telepad;

import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemCoordSelector extends Item implements IResourceTooltipProvider {

  public static ItemCoordSelector create() {
    ItemCoordSelector ret = new ItemCoordSelector();
    GameRegistry.registerItem(ret, ModObject.itemCoordSelector.getUnlocalisedName());
    return ret;
  }

  private ItemCoordSelector() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemCoordSelector.getUnlocalisedName());
//    setTextureName("EnderIO:" + ModObject.itemCoordSelector.unlocalisedName);
    setMaxStackSize(1);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void getSubItems(Item item, CreativeTabs p_150895_2_, @SuppressWarnings("rawtypes") List list) {
    ItemStack stack = new ItemStack(item);
    init(stack);
    list.add(stack);
  }

  public static void init(ItemStack stack) {
    stack.setTagCompound(new NBTTagCompound());
    new BlockCoord().writeToNBT(stack.getTagCompound());
    stack.getTagCompound().setBoolean("default", true);
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
    if(stack != null && stack.getTagCompound() != null && !stack.getTagCompound().getBoolean("default")) {
      list.add(getCoords(stack).chatString(EnumChatFormatting.GRAY));
    }
    super.addInformation(stack, player, list, p_77624_4_);
  }

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    if (rayTraceCoords(stack, world, player)) {
      player.swingItem();
    }
    return super.onItemRightClick(stack, world, player);
  }
  
  

  
  @Override
  public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!rayTraceCoords(stack, world, player)) {
      return false;
    }

    TileEntity te = world.getTileEntity(pos);
    if(te instanceof ITelePad) {
      ITelePad tp = (ITelePad) te;
      TileTelePad tile = null;
      if (te instanceof TileTelePad) {
        tile = (TileTelePad) te;
      }
      if(tp.canBlockBeAccessed(player)) {
        BlockCoord bc = getCoords(stack);
        BlockCoord cur = new BlockCoord(tp.getX(), tp.getY(), tp.getZ());
        int dim = getDimension(stack);
        int curDim = tp.getTargetDim();

        if(!bc.equals(cur)) {
          if(tile != null) {
            tile.setCoords_internal(bc);
          } else {
            tp.setCoords(bc);
          }
          if(!world.isRemote) {
            player.addChatMessage(new ChatComponentText(EnderIO.lang.localize("itemCoordSelector.chat.setCoords", bc.chatString())));
          }
        }

        if(dim != curDim) {
          if(tile != null) {
            tile.setTargetDim_internal(dim);
          } else {
            tp.setTargetDim(dim);
          }
          if(!world.isRemote) {
            player.addChatMessage(new ChatComponentText(EnderIO.lang.localize("itemCoordSelector.chat.setDimension", EnumChatFormatting.GREEN.toString(),
                Integer.toString(dim))));
          }
        }

        if(bc.equals(cur) && dim == curDim) {
          return false;
        }
      } else {
        BlockTravelAnchor.sendPrivateChatMessage(player, tp.getOwner());
      }
    }
    
    if(world.isRemote) {
      sendItemUsePacket(stack, player, world, pos.getX(), pos.getY(), pos.getZ(), side.ordinal(), hitX, hitY, hitZ);
    }
    
    return true;
  }

  // returns false if the raytrace provided no new information
  private boolean rayTraceCoords(ItemStack stack, World world, EntityPlayer player) {
    Vector3d headVec = Util.getEyePositionEio(player);
    Vec3 start = headVec.getVec3();
    Vec3 lookVec = player.getLook(1.0F);
    double reach = 500;
    headVec.add(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
    MovingObjectPosition mop = world.rayTraceBlocks(start, headVec.getVec3());
    if (mop == null) {
      return false;
    }
    
    BlockCoord bc = new BlockCoord(mop);
    BlockCoord onStack = getCoords(stack);
    TileEntity te = bc.getTileEntity(world);
    if(te instanceof ITelePad) {
      return true;
    }

    if(!player.isSneaking()) {
      EnumFacing dir = mop.sideHit;
      bc = bc.getLocation(dir);
    }
    
    int dim = world.provider.getDimensionId();
    int curDim = getDimension(stack);
    
    boolean changed = false;
    
    if(!bc.equals(onStack)) {
      setCoords(stack, bc);
      onCoordsChanged(player, bc);
      changed = true;
    }

    if (dim != curDim) {
      setDimension(stack, world);
      onDimensionChanged(player, getDimension(stack));
      changed = true;
    }
    
    return changed;
  }

  private void sendItemUsePacket(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
    NetHandlerPlayClient netClientHandler = (NetHandlerPlayClient) FMLClientHandler.instance().getClientPlayHandler();
    netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(x, y, z), side, player.inventory.getCurrentItem(), hitX, hitY, hitZ));
  }

  private void onCoordsChanged(EntityPlayer player, BlockCoord bc) {
    if(!player.worldObj.isRemote) {
      player.addChatMessage(new ChatComponentText(EnderIO.lang.localize("itemCoordSelector.chat.newCoords", bc.chatString())));
    }
  }
  
  private void onDimensionChanged(EntityPlayer player, int dim) {
    if(!player.worldObj.isRemote) {
      player.addChatMessage(new ChatComponentText(EnderIO.lang.localize("itemCoordSelector.chat.newDimension", EnumChatFormatting.GREEN.toString(), Integer.toString(dim))));
    }
  }

  public void setCoords(ItemStack stack, BlockCoord bc) {
    stack.getTagCompound().setBoolean("default", false);
    bc.writeToNBT(stack.getTagCompound());
  }
  
  public void setDimension(ItemStack stack, World world) {
    stack.getTagCompound().setInteger("dimension", world.provider.getDimensionId());
  }

  public BlockCoord getCoords(ItemStack stack) {
    return BlockCoord.readFromNBT(stack.getTagCompound());
  }
  
  public int getDimension(ItemStack stack) {
    return stack.getTagCompound().getInteger("dimension");
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }
}
