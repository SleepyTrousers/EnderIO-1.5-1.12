package crazypants.enderio.conduit.geom;



/**
 * Offset vectors are based on Y up.
 * @author brad
 */
public enum Offset {
    
    NONE(0,0),
    TOP(0,1),
    BOTTOM(0,-1),
    LEFT(-1,0),
    RIGHT(1,0),
    TL(-1,1),
    TR(1,1),
    BL(-1,-1),
    BR(1,-1);
    
    public final int xOffset;
    public final int yOffset;
    
    private Offset(int xOffset, int yOffset) {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
    }
  
}


