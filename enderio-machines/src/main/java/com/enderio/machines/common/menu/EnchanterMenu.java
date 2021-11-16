package com.enderio.machines.common.menu;

import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;

import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.recipe.IEnchanterRecipe;
import com.enderio.machines.common.recipe.MachineRecipes;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class EnchanterMenu extends MachineMenu<EnchanterBlockEntity>{
    private Level level;

    public EnchanterMenu(EnchanterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ENCHANTER.get(), pContainerId);
        this.level = blockEntity.getLevel();
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 0, 16, 35));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 1, 65, 35));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 2, 85, 35));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 3, 144, 35) {
                @Override
                public void onTake(Player pPlayer, ItemStack pStack) {
                    Optional<IEnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(blockEntity.getItemHandlerMaster()), level);
                    if (recipe.isPresent() && (pPlayer.experienceLevel > recipe.get().getLevel() || pPlayer.isCreative())) {
                        blockEntity.getItemHandlerMaster().getStackInSlot(0).shrink(1);
                        blockEntity.getItemHandlerMaster().getStackInSlot(1).shrink(recipe.get().getIngredients().get(0).getItems()[0].getCount());
                        blockEntity.getItemHandlerMaster().getStackInSlot(2).shrink(1);
                        pPlayer.experienceLevel -= recipe.get().getLevel();
                    }
                    super.onTake(pPlayer, pStack);
                }
                
                @Override
                public boolean mayPickup(Player playerIn) {
                    Optional<IEnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(blockEntity.getItemHandlerMaster()), level);
                    if (recipe.isPresent() && (playerIn.experienceLevel > recipe.get().getLevel() || playerIn.isCreative())) {
                        return super.mayPickup(playerIn);
                    }
                    return false;
                }
            });
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 142));
        }
    }

    public static EnchanterMenu factory(@Nullable MenuType<EnchanterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof EnchanterBlockEntity castBlockEntity)
            return new EnchanterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new EnchanterMenu(null, inventory, pContainerId);
    }
    
    @Override
    public boolean stillValid(Player pPlayer) {
        return getBlockEntity() != null;
    }

}
