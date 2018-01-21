package crazypants.enderio.base.item.darksteel.attributes;

import net.minecraft.entity.ai.attributes.AttributeModifier;

/**
 * Values for the <code>operation</code> parameter of the {@link AttributeModifier} (use {@link #ordinal()}).
 * <p>
 * It will first add all {@link #ADD} modifiers. Then all {@link #PERCENT_OF_BASE} modifiers will be calculated based on that value and then added. At last, the
 * value will be multiplied with all {@link #PERCENT_MULTIPLIER} modifiers. So:
 * <ol>
 * <li><code>v1 = v0 + ADD1 + ADD2 + ADD3</code>
 * <li><code>v2 = v1 + v1 * POB1 + v1 * POB2 + v1 * POB3</code>
 * <li><code>v3 = v2 * (1 + MUL1) * (1 + MUL2) * (1 + MUL3)</code>
 * </ol>
 * 
 * @author Henry Loenwind
 *
 */
enum Operation {
  ADD,
  PERCENT_OF_BASE,
  PERCENT_MULTIPLIER;
}