package com.enderio.base.common.item.tool;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class YetaWrenchItem extends Item {

    public YetaWrenchItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockState state = pContext.getLevel().getBlockState(pContext.getClickedPos());
        Optional<Either<DirectionProperty, EnumProperty<Direction.Axis>>> property = getRotationProperty(state);
        if (property.isPresent()) {
            if (pContext.getLevel().isClientSide)
                return InteractionResult.SUCCESS;
            state = getNextState(pContext, state, property.get());
            pContext.getLevel().setBlock(
                pContext.getClickedPos(),
                state,
                Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
            return InteractionResult.CONSUME;
        }
        return super.useOn(pContext);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @SuppressWarnings("unchecked")
    private static Optional<Either<DirectionProperty, EnumProperty<Direction.Axis>>> getRotationProperty(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof DirectionProperty directionProperty
                && directionProperty.getName().equals("facing")) {

                return Optional.of(Either.left(directionProperty));
            }
            if (property instanceof EnumProperty enumProperty
                && enumProperty.getName().equals("axis")
                && enumProperty.getValueClass().equals(Direction.Axis.class)) {

                return Optional.of(Either.right(enumProperty));
            }
        }
        return Optional.empty();
    }

    private static BlockState getNextState(UseOnContext pContext, BlockState state, Either<DirectionProperty, EnumProperty<Direction.Axis>> property) {
        return handleProperties(pContext, state, property.left(), property.right());
    }

    private static BlockState handleProperties(UseOnContext pContext, BlockState state, Optional<DirectionProperty> directionProperty, Optional<EnumProperty<Direction.Axis>> axisProperty) {
         if (directionProperty.isPresent())
             return handleProperty(pContext, state, directionProperty.get());
         if (axisProperty.isPresent())
             return handleProperty(pContext, state, axisProperty.get());

         throw new IllegalArgumentException("Atleast one Optional should be set");
    }

    private static <T extends Comparable<T>> BlockState handleProperty(UseOnContext pContext, BlockState state, Property<T> property) {
        int noValidStateIndex = 0;
        do {
            state = getNextBlockState(state, property);
            noValidStateIndex++;
        } while (noValidStateIndex != property.getPossibleValues().size()
            && !state.getBlock().canSurvive(state, pContext.getLevel(), pContext.getClickedPos()));
        return state;
    }

    private static <T extends Comparable<T>> BlockState getNextBlockState(BlockState currentState, Property<T> property) {
        return currentState.setValue(property, getNextValue(currentState.getValue(property), property));
    }
    private static <T extends Comparable<T>> T getNextValue(T value, Property<T> property) {
        boolean foundValid = false;
        for (T possibleValue : property.getPossibleValues()) {
            if (foundValid)
                return possibleValue;
            foundValid = possibleValue == value;
        }
        return property.getPossibleValues().iterator().next();
    }

}
