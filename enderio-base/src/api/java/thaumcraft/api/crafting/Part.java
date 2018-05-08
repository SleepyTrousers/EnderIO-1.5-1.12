package thaumcraft.api.crafting;
/**
 * 
 * @author Azanor
 * 
 * Part used in multiblock crafting
 *
 */
public class Part {
	private Object source; // can be a block, itemstack or material
	private Object target; // anything other than an itemstack will cause an air block to be placed 
	private boolean opp;
	private int priority;
	private boolean applyPlayerFacing;

	public Part(Object source, Object target, boolean opp, int priority) {
		this.setSource(source);
		this.setTarget(target);
		this.setOpp(opp);
		this.setPriority(priority);
	}
	
	public Part(Object source, Object target, boolean opp) {
		this.setSource(source);
		this.setTarget(target);
		this.setOpp(opp);
		this.setPriority(50);
	}
	
	public Part(Object source, Object target) {
		this.setSource(source);
		this.setTarget(target);
		this.setOpp(false);
		this.setPriority(50);
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public boolean isOpp() {
		return opp;
	}

	public void setOpp(boolean opp) {
		this.opp = opp;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public boolean getApplyPlayerFacing() {
		return applyPlayerFacing;
	}

	public Part setApplyPlayerFacing(boolean applyFacing) {
		this.applyPlayerFacing = applyFacing;
		return this;
	}
}