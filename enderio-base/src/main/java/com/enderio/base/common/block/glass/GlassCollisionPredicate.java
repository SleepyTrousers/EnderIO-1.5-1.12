package com.enderio.base.common.block.glass;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Glass collision predicate wrapper.
 * Contains the predicate, the description id for the tooltip and the icon for the itemstack.
 */
public enum GlassCollisionPredicate {
    NONE(ctx -> false, null),

    PLAYERS_PASS(ctx -> ctx
        .getEntity()
        .map(ent -> ent instanceof Player)
        .orElse(false), "Not solid to players"),

    PLAYERS_BLOCK(ctx -> ctx
        .getEntity()
        .map(ent -> !(ent instanceof Player))
        .orElse(true), "Only solid to players"),

    MOBS_PASS(ctx -> ctx
        .getEntity()
        .map(ent -> ent instanceof Mob)
        .orElse(false), "Not solid to monsters"),

    MOBS_BLOCK(ctx -> ctx
        .getEntity()
        .map(ent -> !(ent instanceof Mob))
        .orElse(true), "Only solid to monsters"),

    ANIMALS_PASS(ctx -> ctx
        .getEntity()
        .map(ent -> ent instanceof Animal)
        .orElse(false), "Not solid to animals"),

    ANIMALS_BLOCK(ctx -> ctx
        .getEntity()
        .map(ent -> !(ent instanceof Animal))
        .orElse(true), "Only solid to animals");

    private final Predicate<EntityCollisionContext> predicate;
    private final @Nullable String description;

    GlassCollisionPredicate(Predicate<EntityCollisionContext> predicate, @Nullable String description) {
        this.predicate = predicate;
        this.description = description;
    }

    public boolean canPass(EntityCollisionContext context) {
        return predicate.test(context);
    }

    // TODO: Get description id for tooltip, not just a string
    public Optional<String> getDescription() {
        if (description != null)
            return Optional.of(description);
        return Optional.empty();
    }

    // TODO: Get icon for overlay
}
