package project.graphicEditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.util.Vector;

public class ShapeRe {
	
	public Shape shape = null;
	
	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Color cl;
	public int thick;
	public int l_f;
	public int fill;
	public int getFill() {
		return fill;
	}

	public void setFill(int fill) {
		this.fill = fill;
	}

	public Vector<Point> point = null;
	public int form;
	
	public int width;
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int height;
	
	public Point point1;
	public Point getPoint1() {
		return point1;
	}

	public void setPoint1(Point point1) {
		this.point1 = point1;
	}

	public Point getPoint2() {
		return point2;
	}

	public void setPoint2(Point point2) {
		this.point2 = point2;
	}

	public Point point2;
	public String address;
	public int x;
	public int y;
	
	
	public Vector<Point> getPoint() {
		return point;
	}

	public ShapeRe(Shape shape, Color cl, int thick, int l_f, int fill, int form, int width, int height, Point point1, Point point2) {
		this.shape = shape;
		this.cl = cl;
		this.thick = thick;
		this.l_f = l_f;
		this.fill = fill;
		this.form = form;
		this.width = width;
		this.height = height;
		this.point1 = point1;
		this.point2 = point2;
	}
	
	public ShapeRe(Vector<Point> point, Color cl, int thick, int l_f, int form) {
		this.point = point;
		this.cl = cl;
		this.thick = thick;
		this.l_f = l_f;
		this.form = form;
		
	}
	
	public ShapeRe(int form, String address, int x, int y, int width, int height) {
		this.form = form;
		this.address = address;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setPoint(Vector<Point> lastElement) {
		this.point = lastElement;
		
	}

}
