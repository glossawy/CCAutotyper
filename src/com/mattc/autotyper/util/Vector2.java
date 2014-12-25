/*
 * Argus Installer v2 -- A Better School Zip Alternative Copyright (C) 2014 Matthew
 * Crocco
 * 
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.mattc.autotyper.util;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Represents a Two Dimensional Vector. <br />
 * <br />
 * This can be used to represent either a (x, y) coordinate or to just contain <br />
 * 2 values. Such as Width and Height. This is extremely simplified (lightweight) <br />
 * to be used for GUI Positioning.
 * 
 * @author Matthew
 *
 */
public class Vector2 {

	public static final Vector2 X_UNIT = new Vector2(1, 0);
	public static final Vector2 Y_UNIT = new Vector2(0, 1);
	public static final Vector2 ZERO = new Vector2(0, 0);

	public int x;
	public int y;

	public Vector2(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 v) {
		this.x = v.y;
		this.y = v.y;
	}

	public void set(Vector2 vect) {
		this.x = vect.x;
		this.y = vect.y;
	}

	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 cpy() {
		return new Vector2(this);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public Dimension toDimensionAWT() {
		return new Dimension(this.x, this.y);
	}

	public Point toPointAWT() {
		return new Point(this.x, this.y);
	}

	public static Vector2 toVector2(Dimension dim) {
		return new Vector2(dim.width, dim.height);
	}

	public static Vector2 toVector2(Point p) {
		return new Vector2(p.x, p.y);
	}

	@Override
	public String toString() {
		return String.format("(%d, %d)", this.x, this.y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + this.x;
		result = (prime * result) + this.y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Vector2 other = (Vector2) obj;
		if (this.x != other.x) return false;
		if (this.y != other.y) return false;
		return true;
	}

}
