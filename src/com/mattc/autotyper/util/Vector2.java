package com.mattc.autotyper.util;

import java.awt.Dimension;
import java.awt.Point;

/**
 * Represents a Two Dimensional Vector. <br />
 * <br />
 * This can be used to represent either a (x, y) coordinate or to just contain 2
 * values. Such as Width and Height. This is extremely simplified (lightweight) to be
 * used for GUI Positioning and supports conversion to and from
 * {@link java.awt.Dimension} and {@link java.awt.Point}.
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

	public Vector2 add(Vector2 other) {
		this.x += other.x;
		this.y += other.y;

		return this;
	}

	public Vector2 sub(Vector2 other) {
		this.x -= other.x;
		this.y -= other.y;

		return this;
	}

	public Vector2 scl(int scalar) {
		this.x *= scalar;
		this.y *= scalar;

		return this;
	}

	public Vector2 scl(int xScl, int yScl) {
		this.x *= xScl;
		this.y *= yScl;

		return this;
	}

	public Vector2 scl(Vector2 sclVect) {
		this.x *= sclVect.x;
		this.y *= sclVect.y;

		return this;
	}

	public int dot(Vector2 other) {
		return (this.x * other.x) + (this.y * other.y);
	}

	public int crs(Vector2 other) {
		return (this.x * other.y) - (this.y * other.x);
	}

	public Vector2 mulAdd(Vector2 vect, int scalar) {
		return add(vect.scl(scalar));
	}

	public Vector2 mulAdd(Vector2 vect, Vector2 scl) {
		return add(vect.scl(scl));
	}

	public int dist(Vector2 other) {
		final int dx = this.x - other.x;
		final int dy = this.y - other.y;
		return (int) Math.round(Math.sqrt((dx * dx) + (dy * dy)));
	}

	public int dist2(Vector2 other) {
		final int dx = this.x - other.x;
		final int dy = this.y - other.y;

		return (dx * dx) + (dy * dy);
	}

	public int len() {
		return (int) Math.round(Math.sqrt((this.x * this.x) + (this.y * this.y)));
	}

	public int len2() {
		return (this.x * this.x) + (this.y * this.y);
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

	public Vector2 toZero() {
		this.x = 0;
		this.y = 0;

		return this;
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

	public boolean isUnit() {
		return (this.x == 1) && (this.y == 1);
	}

	public boolean isZero() {
		return (this.x == 0) && (this.y == 0);
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
