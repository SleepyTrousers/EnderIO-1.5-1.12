package com.enderio.base.common.item.tool;

import com.enderio.base.common.capability.location.CoordinateSelection;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.menu.CoordinateMenu;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;

public class CoordinateSelectorItem extends Item {

    public CoordinateSelectorItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!checkPaper(pPlayer))
            return super.use(pLevel, pPlayer, pHand);
        BlockHitResult hitResult = pLevel.clip(
            new ClipContext(
                pPlayer.getEyePosition(),
                pPlayer.getLookAngle().scale(64).add(pPlayer.getEyePosition()), //Make range configurable(?)
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                pPlayer)
        );
        if (hitResult.getType() == HitResult.Type.MISS) {
            if (pPlayer instanceof LocalPlayer)
                pPlayer.displayClientMessage(EIOLang.COORDINATE_SELECTOR_NO_BLOCK, true);
            return super.use(pLevel, pPlayer, pHand);
        }
        if (pPlayer instanceof ServerPlayer serverPlayer)
            openMenu(serverPlayer, pLevel, hitResult.getBlockPos());
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getPlayer() != null && checkPaper(pContext.getPlayer())) {
            if (pContext.getPlayer() instanceof ServerPlayer serverPlayer)
                openMenu(serverPlayer, pContext.getLevel(), pContext.getClickedPos());
            return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide);
        }
        return super.useOn(pContext);
    }

    private static void openMenu(ServerPlayer player, Level level, BlockPos pos) {
        CoordinateSelection selection = CoordinateSelection.of(level, pos);

        NetworkHooks.openGui(player,new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TextComponent("");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                return new CoordinateMenu(pContainerId, selection, null);
            }
        }, buf -> CoordinateMenu.writeAdditionalData(buf, selection, ""));
    }

    private static boolean checkPaper(Player player) {
        if (player.getInventory().contains(Items.PAPER.getDefaultInstance()))
            return true;
        if (player instanceof LocalPlayer)
            player.displayClientMessage(EIOLang.COORDINATE_SELECTOR_NO_PAPER, true);
        return false;
    }
}
