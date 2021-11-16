package com.enderio.machines.common.blockentity;

import java.util.List;
import java.util.Optional;

import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.recipe.IEnchanterRecipe;
import com.enderio.machines.common.recipe.MachineRecipes;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class EnchanterBlockEntity extends AbstractMachineBlockEntity{

    @Getter
    private ItemHandlerMaster itemHandlerMaster = new ItemHandlerMaster(getConfig(), 4, List.of(0,1,2), List.of(3)) {
        protected void onContentsChanged(int slot) {
            if (slot != 3) {
                Optional<IEnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(itemHandlerMaster), level);
                if (recipe.isPresent()) {
                    itemHandlerMaster.setStackInSlot(3, recipe.get().assemble(new RecipeWrapper(itemHandlerMaster)));
                }
                else {
                    itemHandlerMaster.setStackInSlot(3, ItemStack.EMPTY);
                }
            }
            setChanged();
        };
    };
    
    @Override
    public CompoundTag save(CompoundTag pTag) {
        pTag.put("Items", itemHandlerMaster.serializeNBT());
        return super.save(pTag);
    }
    
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandlerMaster.deserializeNBT(pTag.getCompound("Items"));
    }
    
    public EnchanterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new EnchanterMenu(this, pInventory, pContainerId);
    }
}
