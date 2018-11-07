package crazypants.enderio.api.capacitor;

/**
 * A scaler translates a raw capacitor level into a factor that can be used to scale a dynamic value of a machine. The input for a scaler is the capacitor
 * level, a float between 0.0 and ~5.2. There's no hard upper limit, but results tend to explode outside this range. The output of the scaler is multiplied with
 * the base value of the CapacitorKey, so it should stay in a 0 to 20 range to prevent absurd results.
 * 
 * Note: A scaler is a "y = f(x)" function, which you can plot out on graph paper. Try it!
 *
 */
public interface Scaler {

  float scaleValue(float idx);

}
