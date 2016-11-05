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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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

import static crazypants.enderio.ModObject.itemLocationPrintout;

public class ItemLocationPrintout extends Item implements IGuiHandler {

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
    setUnlocalizedName(itemLocationPrintout.getUnlocalisedName());
    setRegistryName(itemLocationPrintout.getUnlocalisedName());
    setMaxStackSize(1);
  }

  @Override
  public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
    return true;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
    if (player.isSneaking() && TelepadTarget.readFromNBT(stack) != null) {
      player.openGui(EnderIO.instance, GuiHandler.GUI_ID_LOCATION_PRINTOUT, world, hand.ordinal(), 0, 0);
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
  }

  @Override
  public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
      float hitZ) {

    TelepadTarget targ = TelepadTarget.readFromNBT(stack);
    if (targ == null) {
      player.addChatMessage(new TextComponentString("No location? but how.."));
      return EnumActionResult.SUCCESS;
    }

    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof TileTelePad) {
      TileTelePad tile = (TileTelePad) te;
      return onTelepadClicked(stack, player, worldIn, tile, targ);
    } else if (te instanceof TileDialingDevice) {
      TileDialingDevice dd = (TileDialingDevice) te;
      dd.addTarget(targ);
      if (worldIn.isRemote) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localizeExact("item.itemLocationPrintout.chat.addTarget") + " " + targ.getChatString()));
      }
      return EnumActionResult.SUCCESS;
    }

    return EnumActionResult.PASS;
  }

  private EnumActionResult onTelepadClicked(ItemStack stack, EntityPlayer player, World worldIn, TileTelePad tile, TelepadTarget targ) {
    if (tile.canBlockBeAccessed(player)) {
      tile.setTarget(targ);
      if (worldIn.isRemote) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localizeExact("item.itemLocationPrintout.chat.setTarget") + " " + targ.getChatString()));
      }
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

      boolean foundPaper = false;
      for (int paperIndex = 0; paperIndex < player.inventoryContainer.inventorySlots.size() && !foundPaper; paperIndex++) {
        ItemStack invItem = player.inventoryContainer.inventorySlots.get(paperIndex).getStack();
        if (invItem != null && invItem.getItem() == Items.PAPER) {
          player.inventoryContainer.inventorySlots.get(paperIndex).decrStackSize(1);
          player.inventoryContainer.detectAndSendChanges();
          foundPaper = true;
        }
      }
      if (!foundPaper) {
        player.addChatMessage(new TextComponentString(EnderIO.lang.localizeExact("item.itemLocationPrintout.chat.noPaper")));
        return null;
      }

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

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    return;
  }

}
