package mods.immibis.microblocks.api;

import static mods.immibis.microblocks.api.EnumAxisPosition.*;
import static mods.immibis.microblocks.api.EnumPositionClass.*;
import mods.immibis.core.api.util.Dir;

public enum EnumPosition {
	// DO NOT CHANGE ORDER - Ordinals are saved in NBT
	// Changing order WILL break existing saves!
	
	Centre(EnumAxisPosition.Centre, EnumAxisPosition.Centre, EnumAxisPosition.Centre, EnumPositionClass.Centre),
	FaceNX(Negative, Span, Span, Face),
	FacePX(Positive, Span, Span, Face),
	FaceNY(Span, Negative, Span, Face),
	FacePY(Span, Positive, Span, Face),
	FaceNZ(Span, Span, Negative, Face),
	FacePZ(Span, Span, Positive, Face),
	EdgeNXNY(Negative, Negative, Span, Edge),
	EdgeNXPY(Negative, Positive, Span, Edge),
	EdgePXNY(Positive, Negative, Span, Edge),
	EdgePXPY(Positive, Positive, Span, Edge),
	EdgeNXNZ(Negative, Span, Negative, Edge),
	EdgeNXPZ(Negative, Span, Positive, Edge),
	EdgePXNZ(Positive, Span, Negative, Edge),
	EdgePXPZ(Positive, Span, Positive, Edge),
	EdgeNYNZ(Span, Negative, Negative, Edge),
	EdgeNYPZ(Span, Negative, Positive, Edge),
	EdgePYNZ(Span, Positive, Negative, Edge),
	EdgePYPZ(Span, Positive, Positive, Edge),
	CornerNXNYNZ(Negative, Negative, Negative, Corner),
	CornerNXNYPZ(Negative, Negative, Positive, Corner),
	CornerNXPYNZ(Negative, Positive, Negative, Corner),
	CornerNXPYPZ(Negative, Positive, Positive, Corner),
	CornerPXNYNZ(Positive, Negative, Negative, Corner),
	CornerPXNYPZ(Positive, Negative, Positive, Corner),
	CornerPXPYNZ(Positive, Positive, Negative, Corner),
	CornerPXPYPZ(Positive, Positive, Positive, Corner),
	
	PostX(Span, EnumAxisPosition.Centre, EnumAxisPosition.Centre, Post),
	PostY(EnumAxisPosition.Centre, Span, EnumAxisPosition.Centre, Post),
	PostZ(EnumAxisPosition.Centre, EnumAxisPosition.Centre, Span, Post),
	
	;
	
	private EnumPosition(EnumAxisPosition x, EnumAxisPosition y, EnumAxisPosition z, EnumPositionClass clazz) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.clazz = clazz;
	}
	
	public final EnumAxisPosition x, y, z;
	public final EnumPositionClass clazz;
	
	public static EnumPosition getCornerPosition(int x, int y, int z) {
		if(x < 0)
			if(y < 0)
				if(z < 0)
					return CornerNXNYNZ;
				else
					return CornerNXNYPZ;
			else
				if(z < 0)
					return CornerNXPYNZ;
				else
					return CornerNXPYPZ;
		else
			if(y < 0)
				if(z < 0)
					return CornerPXNYNZ;
				else
					return CornerPXNYPZ;
			else
				if(z < 0)
					return CornerPXPYNZ;
				else
					return CornerPXPYPZ;
	}
	
	private static EnumPosition edgePositionLookup[][] = {
		{null, null, EdgeNYNZ, EdgeNYPZ, EdgeNXNY, EdgePXNY},
		{null, null, EdgePYNZ, EdgePYPZ, EdgeNXPY, EdgePXPY},
		{EdgeNYNZ, EdgePYNZ, null, null, EdgeNXNZ, EdgePXNZ},
		{EdgeNYPZ, EdgePYPZ, null, null, EdgeNXPZ, EdgePXPZ},
		{EdgeNXNY, EdgeNXPY, EdgeNXNZ, EdgeNXPZ, null, null},
		{EdgePXNY, EdgePXPY, EdgePXNZ, EdgePXPZ, null, null},
	};
	
	private static boolean edgeTouches(EnumPosition edge, int face) {
		switch(face) {
		case Dir.NX: return edge.x == EnumAxisPosition.Negative;
		case Dir.PX: return edge.x == EnumAxisPosition.Positive;
		case Dir.NY: return edge.y == EnumAxisPosition.Negative;
		case Dir.PY: return edge.y == EnumAxisPosition.Positive;
		case Dir.NZ: return edge.z == EnumAxisPosition.Negative;
		case Dir.PZ: return edge.z == EnumAxisPosition.Positive;
		}
		throw new IllegalArgumentException("Invalid face "+face);
	}
	
	static {
		for(int a = 0; a < 6; a++)
			for(int b = 0; b < 6; b++)
				assert(edgePositionLookup[a][b] == null || (edgeTouches(edgePositionLookup[a][b], a) && edgeTouches(edgePositionLookup[a][b], b)));
	}
	
	/**
	 * Returns the edge microblock position that intersects both the given faces.
	 * 
	 * @param side1 One of the faces.
	 * @param side2 The other face.
	 * @return The matching microblock position, or null for invalid face combinations (eg +Y and -Y)
	 */
	public static EnumPosition getEdgePosition(int side1, int side2) {
		if(side1 < 0 || side2 < 0 || side1 > 5 || side2 > 5)
			return null;
		return edgePositionLookup[side2][side1];
	}

	public static EnumPosition getFacePosition(int side) {
		switch(side) {
		case Dir.NX: return FaceNX;
		case Dir.PX: return FacePX;
		case Dir.NY: return FaceNY;
		case Dir.PY: return FacePY;
		case Dir.NZ: return FaceNZ;
		case Dir.PZ: return FacePZ;
		default: throw new IllegalArgumentException("Invalid direction: "+side);
		}
	}
}
