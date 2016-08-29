package crazypants.enderio.teleport.telepad;

import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.teleport.anchor.BlockTravelAnchor;
import crazypants.enderio.teleport.telepad.gui.GuiLocationPrintout;
import crazypants.enderio.teleport.telepad.packet.PacketUpdateLocationPrintout;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ItemLocationPrintout extends Item implements IGuiHandler {

  public static final String NAME = "itemLocationPrintout";

  public static ItemLocationPrintout create() {

    PacketHandler.INSTANCE.registerMessage(PacketUpdateLocationPrintout.class, PacketUpdateLocationPrintout.class, PacketHandler.nextID(), Side.SERVER);

    ItemLocationPrintout result = new ItemLocationPrintout();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_LOCATION_PRINTOUT, result);
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_LOCATION_PRINTOUT_CREATE, result);

    GameRegistry.register(result);
    return result;
  }

  protected ItemLocationPrintout() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
    setMaxStackSize(1);
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
    if (player.isSneaking()) {
      player.openGui(EnderIO.instance, GuiHandler.GUI_ID_LOCATION_PRINTOUT, world, hand.ordinal(), 0, 0);
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
  }

  @Override
  public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
      float hitZ) {

    TileEntity te = worldIn.getTileEntity(pos);
    if (!(te instanceof TileTelePad)) {
      return EnumActionResult.PASS;
    }

    TileTelePad tile = (TileTelePad) te;;    
    if (tile.canBlockBeAccessed(player)) {      
      TelepadTarget targ = TelepadTarget.readFromNBT(stack);
      if(targ == null) {
        player.addChatMessage(new TextComponentString("No location? but how.."));
        return EnumActionResult.SUCCESS;        
      }
      tile.setTarget(targ);
      if (worldIn.isRemote) {
        //player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemCoordSelector.chat.setCoords", BlockCoord.chatString(pos))));
        
        player.addChatMessage(new TextComponentString(EnderIO.lang.localizeExact("item.itemLocationPrintout.chat.setTarget") + " " + targ.getChatString()));
      }      

//      int dim = TelepadTarget.getTargetDimension(stack.getTagCompound());
//      BlockCoord cur = new BlockCoord(tp.getX(), tp.getY(), tp.getZ());
//      
//      int curDim = tp.getTargetDim();
//      if (!bc.equals(cur)) {
//        if (tile != null) {
//          tile.setCoords_internal(bc);
//        } else {
//          tp.setCoords(bc);
//        }
//        if (worldIn.isRemote) {
//          player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemCoordSelector.chat.setCoords", BlockCoord.chatString(pos))));
//        }
//      }
//
//      if (dim != curDim) {
//        if (tile != null) {
//          tile.setTargetDim_internal(dim);
//        } else {
//          tp.setTargetDim(dim);
//        }
//        if (worldIn.isRemote) {
//          player.addChatMessage(new TextComponentString(
//              EnderIO.lang.localize("itemCoordSelector.chat.setDimension", TextFormatting.GREEN.toString(), TelepadTarget.getDimenionName(dim))));
//        }
//      }
//
//      if (bc.equals(cur) && dim == curDim) {
//        player.addChatMessage(new TextComponentString(EnderIO.lang.localize("itemCoordSelector.chat.alreadySet")));
//      }
    } else {
      BlockTravelAnchor.sendPrivateChatMessage(player, tile.getOwner());
    }
    return EnumActionResult.SUCCESS;
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean p_77624_4_) {
    if (stack != null && stack.getTagCompound() != null && !stack.getTagCompound().getBoolean("default")) {
      TelepadTarget target = TelepadTarget.readFromNBT(stack.getTagCompound());
      if (target != null) {
        if (target.getLocation() != null) {
          list.add(new BlockCoord(target.getLocation()).chatString(TextFormatting.GRAY));
          list.add(TelepadTarget.getDimenionName(target.getDimension()));
        }
      }
    }
    super.addInformation(stack, player, list, p_77624_4_);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (ID == GuiHandler.GUI_ID_LOCATION_PRINTOUT_CREATE) {
      TelepadTarget target = new TelepadTarget(new BlockPos(x, y, z), world.provider.getDimension());
      ItemStack stack = new ItemStack(this);
      target.writeToNBT(stack);
      return new GuiLocationPrintout(stack);
    } else {
      EnumHand hand = x == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
      EntityEquipmentSlot slot = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
      return new GuiLocationPrintout(player, slot);
    }
  }

}
