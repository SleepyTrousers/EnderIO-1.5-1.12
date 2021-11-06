package com.enderio.base.common.menu;

import com.enderio.base.common.item.EIOItems;
import com.enderio.base.common.item.LocationPrintoutItem;
import com.enderio.base.common.capability.location.CoordinateSelection;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Optional;

public class CoordinateMenu extends AbstractContainerMenu {

    private final CoordinateSelection selection;

    private final boolean isPrintout;
    private String name;

    protected CoordinateMenu(@Nullable MenuType<CoordinateMenu> pMenuType, int pContainerId, FriendlyByteBuf buf) {
        super(pMenuType, pContainerId);
        selection = new CoordinateSelection();
        selection.setPos(buf.readBlockPos());
        selection.setLevel(buf.readResourceLocation());
        isPrintout = buf.readBoolean();
        name = buf.readUtf(50);
    }

    /**
     * @param name is null when you used the coordinate selector, if it's the printout use the ItemStack name
     */
    public CoordinateMenu(int containerID, CoordinateSelection selection, @Nullable String name) {
        super(EIOMenus.COORDINATE.get(), containerID);
        this.selection = selection;
        this.isPrintout = name != null;
        this.name = name != null ? name : "";
    }

    public static CoordinateMenu factory(@Nullable MenuType<CoordinateMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new CoordinateMenu(pMenuType, pContainerId, buf);
    }

    /**
     * @param name is null when you used the coordinate selector, if it's the printout use the ItemStack name
     */
    public static FriendlyByteBuf writeAdditionalData(FriendlyByteBuf buf, CoordinateSelection selection, @Nullable String name) {
        buf.writeBlockPos(selection.getPos());
        buf.writeResourceLocation(selection.getLevel());
        buf.writeBoolean(name == null);
        buf.writeUtf(name == null ? "" : name, 50);
        return buf;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return findInHand(pPlayer, isPrintout ? EIOItems.LOCATION_PRINTOUT.get() : EIOItems.COORDINATE_SELECTOR.get());
    }

    private static boolean findInHand(Player pPlayer, Item toFind) {
        return pPlayer.getMainHandItem().getItem() == toFind || pPlayer.getOffhandItem().getItem() == toFind;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        Optional<ItemStack> paper = findPaper(pPlayer);
        if (paper.isPresent() && !isPrintout && pPlayer instanceof ServerPlayer severPlayer) {
            paper.get().shrink(1);
            ItemStack itemstack = EIOItems.LOCATION_PRINTOUT.get().getDefaultInstance();
            LocationPrintoutItem.setSelection(itemstack, selection);
            if (!StringUtils.isBlank(name))
                itemstack.setHoverName(new TextComponent(name).withStyle(ChatFormatting.AQUA));

            if (severPlayer.isAlive() && !severPlayer.hasDisconnected()) {
                severPlayer.getInventory().placeItemBackInInventory(itemstack);
            } else {
                severPlayer.drop(itemstack, false);
            }
        }
    }

    private static Optional<ItemStack> findPaper(Player player) {
        for (ItemStack stack: player.getInventory().items) {
            if (stack.getItem() == Items.PAPER)
                return Optional.of(stack);
        }
        for (ItemStack stack: player.getInventory().offhand) {
            if (stack.getItem() == Items.PAPER)
                return Optional.of(stack);
        }
        return Optional.empty();
    }

    public String getName() {
        return name;
    }

    /**
     * updates the name of the currently selected printout
     * @param name new Name
     * @param player
     */
    public void updateName(String name, ServerPlayer player) {
        setName(name);
        if (isPrintout) {
            for (InteractionHand hand: InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() == EIOItems.LOCATION_PRINTOUT.get()) {
                    if (StringUtils.isBlank(name)) {
                        stack.resetHoverName();
                    } else {
                        stack.setHoverName(new TextComponent(name).withStyle(ChatFormatting.AQUA));
                    }
                }
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoordinateSelection getSelection() {
        return selection;
    }
}
