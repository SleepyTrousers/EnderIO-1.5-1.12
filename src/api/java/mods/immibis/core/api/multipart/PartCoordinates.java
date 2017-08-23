package mods.immibis.core.api.multipart;

public final class PartCoordinates {
	public final int x, y, z, part;
	public final boolean isCoverSystemPart;
	public PartCoordinates(int x, int y, int z, int part, boolean isCoverSystemPart) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.part = part;
		this.isCoverSystemPart = isCoverSystemPart;
	}
	
	@Override
	public int hashCode() {
		return ((x * 257 + y) * 257 + z) * 257 + part;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		try {
			PartCoordinates o = (PartCoordinates)obj;
			return x == o.x && y == o.y && z == o.z && part == o.part && isCoverSystemPart == o.isCoverSystemPart;
		} catch(ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "[" + x + "," + y + "," + z + ":" + part + "]";
	}
}
