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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemCoordSelector extends Item implements IResourceTooltipProvider {

  public static ItemCoordSelector create() {
    ItemCoordSelector ret = new ItemCoordSelector();
    GameRegistry.register(ret);
    return ret;
  }

  private ItemCoordSelector() {
    setCreativeTab(EnderIOTab.tabEnderIO);    
    setRegistryName(ModObject.itemCoordSelector.name());
    setUnlocalizedName(ModObject.itemCoordSelector.getUnlocalisedName());
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
    if (!stack.hasTagCompound()) {
      stack.setTagCompound(new NBTTagCompound());
      new BlockCoord().writeToNBT(stack.getTagCompound());
      stack.getTagCompound().setBoolean("default", true);
    }
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {    
    if(stack != null && stack.getTagCompound() != null && !stack.getTagCompound().getBoolean("default")) {
      list.add(getCoords(stack).chatString(TextFormatting.GRAY));
    }
    super.addInformation(stack, player, list, p_77624_4_);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
    init(stack);
    if (rayTraceCoords(stack, world, player)) {
      player.swingArm(hand);
    }
    return super.onItemRightClick(stack, world, player, hand);
  }
  

  @Override
  public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
      EnumHand hand) {
    init(stack);
    

    if(world.isRemote) {
      //If we dont return pass on the client this wont get called on the server
      return EnumActionResult.PASS;
    }
    
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof ITelePad && player.isSneaking()) {
      ITelePad tp = (ITelePad) te;
      ITileTelePad tile = null;
      if (te instanceof ITileTelePad) {
        tile = (ITileTelePad) te;
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
          player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemCoordSelector.chat.setCoords", bc.chatString())));          
        }

        if(dim != curDim) {
          if(tile != null) {
            tile.setTargetDim_internal(dim);
          } else {
            tp.setTargetDim(dim);
          }          
          player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemCoordSelector.chat.setDimension", TextFormatting.GREEN.toString(), Integer.toString(dim))));          
        }

        if(bc.equals(cur) && dim == curDim) {
          player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemCoordSelector.chat.alreadySet")));
        }
      } else {
        BlockTravelAnchor.sendPrivateChatMessage(player, tp.getOwner());
      }      
      return EnumActionResult.SUCCESS;
    }
    
    if (rayTraceCoords(stack, world, player)) {
      return EnumActionResult.SUCCESS;  
    }    
    return EnumActionResult.PASS;        
  }

  // returns false if the raytrace provided no new information
  private boolean rayTraceCoords(ItemStack stack, World world, EntityPlayer player) {
    Vector3d headVec = Util.getEyePositionEio(player);
    Vec3d start = headVec.getVec3();
    Vec3d lookVec = player.getLook(1.0F);
    double reach = 500;
    headVec.add(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
    RayTraceResult mop = world.rayTraceBlocks(start, headVec.getVec3());
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
    
    int dim = world.provider.getDimension();
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
  
  private void onCoordsChanged(EntityPlayer player, BlockCoord bc) {
    if(!player.worldObj.isRemote) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemCoordSelector.chat.newCoords", bc.chatString())));
    }
  }
  
  private void onDimensionChanged(EntityPlayer player, int dim) {
    if(!player.worldObj.isRemote) {
      player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemCoordSelector.chat.newDimension", TextFormatting.GREEN.toString(), Integer.toString(dim))));
    }
  }

  public void setCoords(ItemStack stack, BlockCoord bc) {
    stack.getTagCompound().setBoolean("default", false);
    bc.writeToNBT(stack.getTagCompound());
  }
  
  public void setDimension(ItemStack stack, World world) {
    stack.getTagCompound().setInteger("dimension", world.provider.getDimension());
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
