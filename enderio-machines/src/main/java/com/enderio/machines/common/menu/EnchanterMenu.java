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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class EnchanterMenu extends MachineMenu<EnchanterBlockEntity>{
    private Level level;

    public EnchanterMenu(EnchanterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ENCHANTER.get(), pContainerId);
        if (blockEntity != null) {
            this.level = blockEntity.getLevel();
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 0, 16, 35));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 1, 65, 35));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 2, 85, 35));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 3, 144, 35) {
                @Override
                public void onTake(Player pPlayer, ItemStack pStack) {
                    Optional<IEnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(blockEntity.getItemHandlerMaster()), level);
                    if (recipe.isPresent() && (pPlayer.experienceLevel > recipe.get().getLevelCost(new RecipeWrapper(blockEntity.getItemHandlerMaster())) || pPlayer.isCreative())) {
                        int amount = recipe.get().getAmount(new RecipeWrapper(blockEntity.getItemHandlerMaster()));
                        int lapizForLevel = recipe.get().getLapisForLevel(recipe.get().getEnchantmentLevel(blockEntity.getItemHandlerMaster().getStackInSlot(1).getCount()));
                        pPlayer.giveExperienceLevels(-recipe.get().getLevelCost(new RecipeWrapper(blockEntity.getItemHandlerMaster())));
                        blockEntity.getItemHandlerMaster().getStackInSlot(0).shrink(1);
                        blockEntity.getItemHandlerMaster().getStackInSlot(1).shrink(amount);
                        blockEntity.getItemHandlerMaster().getStackInSlot(2).shrink(lapizForLevel);
                    }
                    super.onTake(pPlayer, pStack);
                }
                
                @Override
                public boolean mayPickup(Player playerIn) {
                    Optional<IEnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(blockEntity.getItemHandlerMaster()), level);
                    if (recipe.isPresent() && (playerIn.experienceLevel > recipe.get().getLevelCost(new RecipeWrapper(blockEntity.getItemHandlerMaster())) || playerIn.isCreative()) && blockEntity.isAction()) {
                        return super.mayPickup(playerIn);
                    }
                    return false;
                }
            });
        }
        addInventorySlots(8,84);
    }

    public static EnchanterMenu factory(@Nullable MenuType<EnchanterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof EnchanterBlockEntity castBlockEntity)
            return new EnchanterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new EnchanterMenu(null, inventory, pContainerId);
    }
    
    public int getCurrentCost() {
        if (level != null) {
            Optional<IEnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(this.getBlockEntity().getItemHandlerMaster()), level);
            if (recipe.isPresent()) {
                return recipe.get().getLevelCost(new RecipeWrapper(this.getBlockEntity().getItemHandlerMaster()));
            }
        }
        return -1;
    }
}
