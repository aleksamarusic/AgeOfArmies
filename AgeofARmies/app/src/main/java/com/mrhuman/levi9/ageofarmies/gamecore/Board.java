package com.mrhuman.levi9.ageofarmies.gamecore;

import java.util.ArrayList;

public class Board {

	 int dimX;
	 int dimY;

	ArrayList<Building> buildings;

	public Board(int dimX, int dimY) {
		this.dimX = dimX;
		this.dimY = dimY;
		buildings = new ArrayList<Building>();
	}

	public boolean add(Building building, int x, int y) {
		if (at(x,y) == null) {
			buildings.add(building);
			return true;
		}
		return false;
	}
	
	public Building at(int x, int y) {
	    for(int i = buildings.size() - 1; i >= 0; i--)
        {
            Building b =buildings.get(i);
            if(b == null)
                return null;
            if (b.getX() == x && b.getY() == y)
                return b;
        }
        return null;
	    /*
		for (Building building : buildings) {
			if (building.getX() == x && building.getY() == y)
				return building;
		}
		return null;*/
	}
	
	public Building remove(int x, int y) {
		Building building = at(x,y);
		if (building != null) {
			buildings.remove(building);
			return building;
		}
		return null;
	}
	
}

