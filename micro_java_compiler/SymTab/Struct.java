/* MicroJava Type Structures  (HM 06-12-28)
   =========================
A type structure stores the type attributes of a declared object.
 */
package MJ.SymTab;

import MJ.Parser;

public class Struct {
	public static final int // structure kinds
			None = 0, Int = 1, Char = 2, Arr = 3, Class = 4, Void = 5;
	public int kind; // None, Int, Char, Arr, Class
	public Struct elemType; // Arr: element type
	public int nFields; // Class: number of fields
	public Obj fields; // Class: fields

	public Struct() {
		this.kind = None;
	}

	public Struct(int kind) {
		this.kind = kind;
	}

	public Struct(int kind, Struct elemType) {
		this.kind = kind;
		this.elemType = elemType;
	}
	
	// Checks if this is a reference type
	public boolean isRefType() {
		return kind == Class || kind == Arr;
	}

	// Checks if two types are equal
	public boolean equals(Struct other) {
		if (kind == Arr)
			return other.kind == Arr && other.elemType == elemType;
		else
			return other.kind == this.kind;
	}

	// Checks if two types are compatible (e.g. in a comparison)
	public boolean compatibleWith(Struct other) {
		return this.equals(other) 
				|| this == Tab.nullType && other.isRefType()
				|| other == Tab.nullType && this.isRefType();
	}

	// Checks if an object with type "this" 
	// can be assigned to an object with type "dest"
	public boolean assignableTo(Struct dest) {
		return this.equals(dest) 
				|| this == Tab.nullType && dest.isRefType()
				|| this.kind == Arr && dest.kind == Arr && dest.elemType == Tab.noType;
	}

	// Set the kind of Struct.
	// Check the Token kind and convert to a Struct type.
	public void setKind(int k) {
		switch (k) {
		case Parser.number:
			this.kind = Int;
			break;
		case Parser.charCon:
			this.kind = Char;
			break;
		case Parser.class_:
			this.kind = Class;
			break;
		default:
			this.kind = None;
			break;
		}
	}
	
	// Set the kind of Struct.
	// Check the Token kind and convert to a Struct type.
	public void setElemType(int k) {
		switch (k) {
		case Parser.number:
			this.elemType = new Struct(Int);
			break;
		case Parser.charCon:
			this.elemType = new Struct(Char);
			break;
		case Parser.class_:
			this.elemType = new Struct(Class);
			break;
		default:
			this.elemType = new Struct(None);
			break;
		}
	}
	
	// Retorna o nome do tipo.
	public String getTypeName() {
		String name;
		
		switch(kind) {
		case None:
			name = "None";
			break;
		case Int:
			name = "Int";
			break;
		case Char:
			name = "Char";
			break;
		case Arr:
			name = "Array";
			break;
		case Class:
			name = "Class";
			break;
		case Void:
			name = "Void";
			break;
		default:
			name = "Undefined";
		}
		
		return name;
	}
}