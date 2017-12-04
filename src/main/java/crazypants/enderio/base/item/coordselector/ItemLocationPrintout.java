package crazypants.enderio.base.item.coordselector;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ChatUtil;
import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.Lang;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObjectRegistry;
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
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemLocationPrintout extends Item implements IEioGuiHandler {

  static final int GUI_ID_LOCATION_PRINTOUT_CREATE = 1337;
  static final int GUI_ID_LOCATION_PRINTOUT = 31337;

  public static ItemLocationPrintout create(@Nonnull IModObject modObject) {
    return new ItemLocationPrintout(modObject);
  }

  protected ItemLocationPrintout(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxStackSize(1);
  }

  @Override
  public boolean doesSneakBypassUse(@Nonnull ItemStack stack, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
    return true;
  }

  @Override
  public @Nonnull ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    ItemStack stack = player.getHeldItem(hand);
    if (player.isSneaking() && TelepadTarget.readFromNBT(stack) != null) {
      ModObjectRegistry.getModObjectNN(this).openClientGui(world, player, GUI_ID_LOCATION_PRINTOUT, hand.ordinal(), 0);
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {

    ItemStack stack = player.getHeldItem(hand);
    TelepadTarget targ = TelepadTarget.readFromNBT(stack);
    if (targ == null) {
      player.sendMessage(new TextComponentString("No location? but how.."));
      return EnumActionResult.SUCCESS;
    }

    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof ICoordinateAware.SingleTarget) {
      ICoordinateAware.SingleTarget tile = (ICoordinateAware.SingleTarget) te;
      return onTelepadClicked(stack, player, worldIn, tile, targ);
    } else if (te instanceof ICoordinateAware.MultipleTargets) {
      ICoordinateAware.MultipleTargets dd = (ICoordinateAware.MultipleTargets) te;
      dd.addTarget(targ);
      if (worldIn.isRemote) {
        player.sendMessage(Lang.PRINTOUT_ADDTARGET.toChat(targ.getChatString()));
      }
      return EnumActionResult.SUCCESS;
    }

    return EnumActionResult.PASS;
  }

  private @Nonnull EnumActionResult onTelepadClicked(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull World worldIn,
      @Nonnull ICoordinateAware.SingleTarget tile, @Nonnull TelepadTarget targ) {
    if (tile.canBlockBeAccessed(player)) {
      tile.setTarget(targ);
      if (worldIn.isRemote) {
        player.sendMessage(Lang.PRINTOUT_SETTARGET.toChat(targ.getChatString()));
      }
    } else {
      sendPrivateChatMessage(player, tile.getOwner());
    }
    return EnumActionResult.SUCCESS;
  }

  public static void sendPrivateChatMessage(@Nonnull EntityPlayer player, @Nonnull UserIdent owner) {
    if (!player.isSneaking()) {
      ChatUtil.sendNoSpam(player, Lang.PRINTOUT_PRIVATE.get(TextFormatting.RED, owner.getPlayerName(), TextFormatting.WHITE));
    }
  }

  @Override
  public void addInformation(@Nonnull ItemStack stack, @Nonnull EntityPlayer player, @Nonnull List<String> list, boolean p_77624_4_) {
    TelepadTarget target = TelepadTarget.readFromNBT(stack);
    if (target != null) {
      if (target.isValid()) {
        list.add(BlockCoord.chatString(new BlockPos(target.getLocation()), TextFormatting.GRAY));
        list.add(TelepadTarget.getDimenionName(target.getDimension()));
      }
    }
    super.addInformation(stack, player, list, p_77624_4_);
  }

  @Override
  @Nullable
  public Object getGuiElement(boolean server, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int ID,
      int handID, int param3) {
    if (server) {
      return null;
    } else if (GUI_ID_LOCATION_PRINTOUT_CREATE == ID) {
      int foundPaper = -1;
      for (int paperIndex = 0; paperIndex < player.inventoryContainer.inventorySlots.size() && foundPaper < 0; paperIndex++) {
        ItemStack invItem = player.inventoryContainer.inventorySlots.get(paperIndex).getStack();
        if (invItem.getItem() == Items.PAPER) {
          foundPaper = paperIndex;
        }
      }
      if (foundPaper < 0) {
        player.sendMessage(Lang.PRINTOUT_NOPAPER.toChat());
        return null;
      }

      TelepadTarget target = new TelepadTarget(pos, world.provider.getDimension());
      ItemStack stack = new ItemStack(this);
      target.writeToNBT(stack);
      return new GuiLocationPrintout(target, stack, foundPaper);
    } else if (GUI_ID_LOCATION_PRINTOUT == ID) {
      EnumHand hand = handID == 0 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
      EntityEquipmentSlot slot = hand == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;

      TelepadTarget target = TelepadTarget.readFromNBT(player.getItemStackFromSlot(slot));
      if (target != null) {
        return new GuiLocationPrintout(target, player, slot);
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, @Nullable CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    return;
  }

}
