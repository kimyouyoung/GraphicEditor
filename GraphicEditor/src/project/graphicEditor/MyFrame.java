package project.graphicEditor;

import java.awt.BasicStroke;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileSystemView;

public class MyFrame extends JFrame{

	JPanel panel_1;
	JPanel panel_2;

	Choice ch, er, line_f;
	Checkbox cb, cb2, cb3, cb4;
	
	JFileChooser filechose = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
	Image img = null;
	String address = "";
	BufferedImage buf = new BufferedImage(800, 810, BufferedImage.TYPE_INT_RGB);
	
	Color cl;
	Color wh = Color.WHITE;
	int form = 1;
	int e_len = 20;
	int thick = 2;
	int l_f = 1, fill = 0;
	int drag = 0, copy = 0;
	float[] dash= new float[]{10,5,5,5};
	

	ShapeRe target = null;
	ShapeRe resize = null;
	
	boolean first = true;
	boolean f_copy = true;

	int re = 0;
	
	
	ArrayList<ShapeRe> shapes = new ArrayList<ShapeRe>();
	
	Vector<Vector<Point>> pens = new Vector<Vector<Point>>();
	Vector<Vector<Point>> polys = new Vector<Vector<Point>>();
	int poly_check = 0;
	
	Vector<Point> d_pens = new Vector<Point>();
	int f_d = 0;
	
	Stack<ShapeRe> redo = new Stack<ShapeRe>();
	
	public MyFrame() {
		
		// Frame
		this.setSize(800, 860);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem[] item = new JMenuItem[3];
		String[] name_item = {"Open", "Save", "Exit"};
		
		
		for(int i = 0; i < name_item.length; i++) {
			item[i] = new JMenuItem(name_item[i]);
			menu.add(item[i]);
			item[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String str = e.getActionCommand();
					if(str.equals("Exit")) {
						System.exit(0);
					}else if(str.equals("Open")) {
						int returnvalue = filechose.showOpenDialog(null);
						if(returnvalue == JFileChooser.APPROVE_OPTION) {
							File select = filechose.getSelectedFile();
							address = select.getAbsolutePath();
							form = 10;
						}
					}else if(str.equals("Save")) {
						int returnvalue = filechose.showSaveDialog(null);
						if(returnvalue == JFileChooser.APPROVE_OPTION) {
							File select = filechose.getSelectedFile();
							address = select.getAbsolutePath();
						}
						panel_2.printAll(buf.getGraphics());
						try {
							String string = address + ".jpg";
							ImageIO.write(buf, "jpg", new File(string));
						}catch(IOException t) {
							t.printStackTrace();
						}
					}
					
				}
				
			});
		}
		
		
		menuBar.add(menu);
		setJMenuBar(menuBar);
		
		this.setTitle("Graphic Editor");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		
		panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 800, 100);
		panel_1.setBackground(new Color(71, 71, 71));
		JButton[] btn = new JButton[9];
		String[] button_name = {"<<", ">>", "Line", "Poly", "Rect", "Circle", "Pen", "Clear", "Eraser"};
		

		for(int i = 0; i < button_name.length; i++) {
			btn[i] = new JButton(button_name[i]);
			panel_1.add(btn[i]);
			btn[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					String str = e.getActionCommand();
					if(str.equals("Line"))
						form = 1;
					else if(str.equals("Poly")) {
						form = 2;
					}else if(str.equals("Rect"))
						form = 3;
					else if(str.equals("Circle"))
						form = 4;
					else if(str.equals("Pen"))
						form = 5;
					else if(str.equals("Clear")) {
						form = 6;
						shapes.clear();
						redo.clear();
						repaint();
					}else if(str.equals("Eraser")) 
						form = 7;
					else if(str.equals("<<")) {
						form = 8;
						repaint();
					}else if(str.equals(">>")) {
						form = 9;
						repaint();
					}
					
				}
			});
		}
		
		JButton[] cbtn = new JButton[9];
		String[] c = {"Black", "Light_Gray", "Blue", "Green", "Yellow", "Orange", "Red", "Pink", "Color"};
		
		for(int i = 0; i < cbtn.length; i++) {
			cbtn[i] = new JButton(c[i]);
			panel_1.add(cbtn[i]);
			cbtn[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String c = e.getActionCommand();
					if(c.equals("Black"))
						cl = Color.BLACK;
					else if(c.equals("Light_Gray"))
						cl = Color.LIGHT_GRAY;
					else if(c.equals("Blue"))
						cl = Color.BLUE;
					else if(c.equals("Green"))
						cl = Color.GREEN;
					else if(c.equals("Yellow"))
						cl = Color.YELLOW;
					else if(c.equals("Orange"))
						cl = Color.ORANGE;
					else if(c.equals("Red"))
						cl = Color.RED;
					else if(c.equals("Pink"))
						cl = Color.PINK;
					else if(c.equals("Color"))
						cl = JColorChooser.showDialog(null, "Color", Color.GREEN);
					
				}
				
			});
		}
		
		ch = new Choice();
		ch.add("1");
		ch.add("2");
		ch.add("4");
		ch.add("8");
		ch.add("16");
		ch.add("32");
		ch.add("64");
		ch.add("128");
		
		ch.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(ch.getSelectedIndex() == 0) {
					thick = 1;
				}else if(ch.getSelectedIndex() == 1)
					thick = 2;
				else if(ch.getSelectedIndex() == 2)
					thick = 4;
				else if(ch.getSelectedIndex() == 3)
					thick = 8;
				else if(ch.getSelectedIndex() == 4)
					thick = 16;
				else if(ch.getSelectedIndex() == 5) {
					thick = 32;
				}else if(ch.getSelectedIndex() == 6)
					thick = 64;
				else if(ch.getSelectedIndex() == 7)
					thick = 128;
				
			}
			
		});
		
		er = new Choice();
		er.add("10");
		er.add("20");
		er.add("30");
		er.add("40");
		er.add("50");
		
		er.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(er.getSelectedIndex() == 0)
					e_len = 10;
				else if(er.getSelectedIndex() == 1)
					e_len = 20;
				else if(er.getSelectedIndex() == 2)
					e_len = 30;
				else if(er.getSelectedIndex() == 3)
					e_len = 40;
				else if(er.getSelectedIndex() == 4)
					e_len = 50;
				
			}
			
		});
		
		line_f = new Choice();
		line_f.add("Full");
		line_f.add("Datted");
		
		line_f.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(line_f.getSelectedIndex() == 0)
					l_f = 1;
				else if(line_f.getSelectedIndex() == 1)
					l_f = 2;
			}
			
		});
		
		cb = new Checkbox("Fill");
		cb.setForeground(Color.WHITE);
		cb2 = new Checkbox("Drag & Drop");
		cb2.setForeground(Color.WHITE);
		cb3 = new Checkbox("Copy & Paste");
		cb3.setForeground(Color.WHITE);
		cb4 = new Checkbox("Resize");
		cb4.setForeground(Color.WHITE);
		
		cb.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED && form != 1) {
					fill = 1;
				}else {
					fill = 0;
				}
			}
			
		});
		
		cb2.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					drag = 1;
					form = 0;
					first = true;
				}
				
			}
			
		});
		
		cb3.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					copy = 1;
					form = 20;
					f_copy = true;
				}
				
			}
			
		});
		
		cb4.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					re = 1;
					form = 30;
				}
				
			}
			
		});
		
		
		
		panel_1.add(ch);
		panel_1.add(er);
		panel_1.add(line_f);
		panel_1.add(cb);
		panel_1.add(cb2);
		panel_1.add(cb3);
		panel_1.add(cb4);
		
		
		this.add(panel_1);
		
		
		panel_2 = new MyPanel();
		panel_2.setBounds(0, 100, 800, 810);
		panel_2.setBackground(new Color(255, 255, 255));
		
		this.add(panel_2);
		this.setVisible(true);
	}
	
	class MyPanel extends JPanel{
			
			Point start_p = null;
			Point end_p = null; 
			
			Vector<Point> poly;
			
			
			public MyPanel() {
				MyMouseListener ml = new MyMouseListener();
				
				this.addMouseListener(ml);
				this.addMouseMotionListener(ml);
			}
			
			public void paintComponent(Graphics g) {				
				Graphics2D g2 = (Graphics2D)g;
				
				super.paintComponent(g2);
			
				if(form == 6) {
					g2.clearRect(0, 0, 800, 750);
				}
				else{
					if(form == 8) {
						if(!shapes.isEmpty()) {
							redo.add(shapes.get(shapes.size()-1));
							shapes.remove(shapes.size()-1);
						}
					}else if(form == 9) {
						if(redo != null)
							shapes.add(redo.pop());
					}
					
					
					
					for(ShapeRe s: shapes) {
						g2.setColor(s.cl);
						if(s.l_f == 1) {
							g2.setStroke(new BasicStroke(s.thick));
						}else {
							g2.setStroke(new BasicStroke(s.thick,0,BasicStroke.JOIN_MITER,1.0f,dash, 0));
						}
						
						if(s.shape != null) {
							if(s.fill == 0)
								g2.draw(s.shape);
							else
								g2.fill(s.shape);
						}
						else if(s.form == 2 && !s.point.isEmpty()) {
							Point st = s.point.firstElement();
							for(Point p : s.point) {
								g2.drawLine(st.x, st.y, p.x, p.y);
								st = p;
							}
						}
						else if(s.form == 5 && !s.point.isEmpty()){	
							if(!s.point.isEmpty()) {
								Point st = s.point.firstElement();
								for(Point p : s.point) {
									g2.drawLine(st.x, st.y, p.x, p.y);
									st = p;
								}		
							}
						}else if(s.form == 10) {
							try {
								img = ImageIO.read(new File(address));
							}catch(IOException t) {
								System.out.println(t.getMessage());
								t.printStackTrace();
							}
							g2.drawImage(img, s.x, s.y, s.width, s.height, this);
						}
					}
					
					
					if(start_p != null) {
						
						int o_x = Math.min(start_p.x, end_p.x);
						int o_y = Math.min(start_p.y, end_p.y);
						int width = Math.abs(start_p.x - end_p.x);
						int height = Math.abs(start_p.y - end_p.y);
						
						g2.setColor(cl);
						if(l_f == 1)
							g2.setStroke(new BasicStroke(thick));
						else
							g2.setStroke(new BasicStroke(thick,0,BasicStroke.JOIN_MITER,1.0f,dash, 0));
						
						if(form == 1)
							g2.drawLine(start_p.x, start_p.y, end_p.x, end_p.y);
						else if(form == 3) {
							if(fill == 0)
								g2.drawRect(o_x, o_y, width, height);
							else
								g2.fillRect(o_x, o_y, width, height);
						}
						else if(form == 4) {
							if(fill == 0)
								g2.drawOval(o_x, o_y, width, height);
							else 
								g2.fillOval(o_x, o_y, width, height);
						}
						else if(form == 2) {
							Point st = poly.firstElement();
							for(Point p : poly) {
								g2.drawLine(st.x, st.y, p.x, p.y);
								st = p;
							}
							
						}
					}
					
				}
				
			}
			
			class MyMouseListener extends MouseAdapter implements MouseListener, DropTargetListener{
				
				
				public void mousePressed(MouseEvent e) {
					Graphics g = getGraphics();
					Graphics2D g2 = (Graphics2D) g;
					
					start_p = e.getPoint();
					
					if(re == 1 && form == 30) {
						for(ShapeRe s : shapes) {
							if(s.shape != null) {
								if((((start_p.x+10 >= s.point1.x) && (start_p.x-10 <= s.point1.x)) && ((start_p.y+10 >= s.point1.y) && (start_p.y-10 <= s.point1.y))) || (((start_p.x+10 >= s.point2.x) && (start_p.x-10 <= s.point2.x)) && ((start_p.y+10 >= s.point2.y) && (start_p.y-10 <= s.point2.y)))) {
									resize = s;
									break;
								}else if(s.form == 4) {
									if((((start_p.x+(s.width/2) >= s.point1.x) && (start_p.x-(s.height/2)<= s.point1.x)) && ((start_p.y+(s.width/2) >= s.point1.y) && (start_p.y-(s.height/2) <= s.point1.y))) || (((start_p.x+(s.width/2) >= s.point2.x) && (start_p.x-(s.width/2) <= s.point2.x)) && ((start_p.y+(s.height/2) >= s.point2.y) && (start_p.y-(s.height/2) <= s.point2.y)))) {
										resize = s;
										break;
									}
								}
							}
						}
					}
					
					
								
					
					int f = 0, m_x = 0, m_y = 0;
					
					if((drag == 1 && form == 0) || (copy == 1 && form == 20)) {
						for(ShapeRe s : shapes) {
							if(s.shape != null) {
								if(s.form == 1) {
									int min_x = Math.min(Math.abs(start_p.x-s.point1.x), Math.abs(start_p.x-s.point2.x));
									int min_y = Math.min(Math.abs(start_p.y-s.point1.y), Math.abs(start_p.y-s.point2.y));
									
									if(f == 0) {
										m_x = min_x;
										m_y = min_y;
										target = s;
										f = 1;
									}else if(m_x > min_x || m_y > min_y) {
										m_x = min_x;
										m_y = min_y;
										target = s;
									}
							
								}else if(s.shape.contains(start_p))
									target = s;
							}
							else if(s.point != null) {
								
							}
						}
						
					}
					
					
					g2.setColor(cl);
					if(l_f == 1)
						g2.setStroke(new BasicStroke(thick));
					else if(l_f == 2)
						g2.setStroke(new BasicStroke(thick,0,BasicStroke.JOIN_MITER,1.0f,dash, 0));
		
					
					if(form == 2) {
						if(poly_check == 0) {
							poly = new Vector<Point>();
							polys.add(poly);
							poly_check = 1;
						}
							
						polys.lastElement().add(start_p);
						if(e.getClickCount() == 2) {
							ShapeRe sh = new ShapeRe(polys.lastElement(), cl, thick, l_f, form);
							shapes.add(sh);
							poly_check = 0;
							repaint();
						}
						
					}else if(form == 5 || form == 7) {
						Vector<Point> pen = new Vector<Point>();
						pens.add(pen);
						ShapeRe sh;
						if(!pens.isEmpty()) {
							if(form == 7) {
								sh = new ShapeRe(pen, Color.WHITE, e_len, l_f, form);
							}
							else {
								sh = new ShapeRe(pen, cl, thick, l_f, form);
							}
							shapes.add(sh);
						}
					}
					
				}
				public void mouseReleased(MouseEvent e) {
					
					end_p = e.getPoint();
					
					ShapeRe sh;
					
					int o_x = Math.min(start_p.x, end_p.x);
					int o_y = Math.min(start_p.y, end_p.y);
					int width = Math.abs(start_p.x- end_p.x);
					int height = Math.abs(start_p.y - end_p.y);
					
					
					int plus_x = 0, plus_y = 0;
					int a = 0, b = 0;
					int x = 0, y = 0;
					
					if(re == 1 && form == 30) {
						if(resize != null) {
							if(resize.form == 4 || resize.form == 3){
								if(resize.form == 4) {
									plus_x = (resize.width/2);
									plus_y = (resize.height/2);
								}else {
									plus_x = 10;
									plus_y = 10;
								}
								if((((start_p.x+plus_x >= resize.point1.x) && (start_p.x-plus_x<= resize.point1.x)) && ((start_p.y+plus_y >= resize.point1.y) && (start_p.y-plus_y <= resize.point1.y)))) {
									System.out.println("1111111");
									o_x = Math.min(resize.point2.x, end_p.x);
									o_y = Math.min(resize.point2.y, end_p.y);
									width = Math.abs(resize.point2.x - end_p.x);
									height = Math.abs(resize.point2.y- end_p.y);
									resize.setPoint1(end_p);
								}else  if((((start_p.x+plus_x >= resize.point2.x) && (start_p.x-plus_x<= resize.point2.x)) && ((start_p.y+plus_y >= resize.point2.y) && (start_p.y-plus_y <= resize.point2.y)))){
									System.out.println("22222222");
									o_x = Math.min(resize.point1.x, end_p.x);
									o_y = Math.min(resize.point1.y, end_p.y);
									width = Math.abs(resize.point1.x- end_p.x);
									height = Math.abs(resize.point1.y - end_p.y);
									resize.setPoint2(resize.point1);
									resize.setPoint1(end_p);
								}else {
									System.out.println("33333333");
									if((start_p.x+plus_x >= resize.point1.x-resize.width) && (start_p.x-plus_x <= resize.point1.x-resize.width)) {
										o_x = Math.min(end_p.x, resize.point1.x+resize.width);
										width = Math.abs(resize.point1.x+resize.width - end_p.x);
										a = 1;
										x = resize.point1.x + width;
									}else if((start_p.x+plus_x >= resize.point1.x+resize.width) && (start_p.x+plus_x <= resize.point1.x+resize.width)) {
										o_x = Math.min(end_p.x, resize.point1.x-resize.width);
										width = Math.abs((resize.point1.x-resize.width - end_p.x));
										x = resize.point1.x-resize.width;
									}
									if((start_p.y+plus_y >= resize.point1.y-resize.height) && (start_p.y-plus_y <= resize.point1.y-resize.height)) {
										o_y = Math.min(end_p.y, resize.point1.y+resize.height);
										height = Math.abs(resize.point1.y+resize.height - end_p.y);
										b = 1;
										y = resize.point1.y+resize.height;
									}else if((start_p.y+plus_y >= resize.point1.y+resize.height) && (start_p.y-plus_y <= resize.point1.y+resize.height)) {
										o_y = Math.min(end_p.y, resize.point1.y-resize.height);
										height = Math.abs(resize.point1.y-resize.height - end_p.y);
										y = resize.point1.y-resize.height;
									}
									
									if((start_p.x+plus_x >= resize.point2.x-resize.width) && (start_p.x-plus_x <= resize.point2.x-resize.width)) {
										o_x = Math.min(end_p.x, resize.point2.x+resize.width);
										width = Math.abs(resize.point2.x+resize.width - end_p.x);
										a = 2;
										x = resize.point2.x+resize.width;
									}else if((start_p.x+plus_x >= resize.point2.x+resize.width) && (start_p.x+plus_x <= resize.point2.x+resize.width)) {
										o_x = Math.min(end_p.x, resize.point2.x-resize.width);
										width = Math.abs((resize.point2.x-resize.width - end_p.x));
										x = resize.point2.x-resize.width;
										a = 3;
									}
									if((start_p.y+plus_y >= resize.point2.y-resize.height) && (start_p.y+plus_y <= resize.point2.y-resize.height)) {
										o_y = Math.min(end_p.y, resize.point2.y+resize.height);
										height = Math.abs(resize.point2.y+resize.height - end_p.y);
										b = 2;
										y = resize.point2.y+resize.height;
									}else if((start_p.y+plus_y >= resize.point2.y+resize.height) && (start_p.y+plus_y <= resize.point2.y+resize.height)) {
										o_y = Math.min(end_p.y, resize.point2.y-resize.height);
										height = Math.abs(resize.point2.y-resize.height - end_p.y);
										y = resize.point2.y-resize.height;
										b = 3;
									}
									resize.setPoint2(end_p);
									resize.setPoint1(new Point(x, y));
										
								}
								
							}
							if(resize.form == 1) {
								if(((start_p.x+10 >= resize.point1.x) && (start_p.x-10 <= resize.point1.x)) && ((start_p.y+10 >= resize.point1.y) && (start_p.y-10 <= resize.point1.y))) {
									resize.setShape(new Line2D.Double(end_p, resize.point2));
									resize.setPoint1(end_p);
								}else {
									resize.setShape(new Line2D.Double(end_p, resize.point1));
									resize.setPoint2(resize.point1);
									resize.setPoint1(end_p);
								}
								resize.setWidth(width);
								resize.setHeight(height);
							}else if(resize.form == 3) {
								resize.setShape(new Rectangle2D.Double(o_x, o_y, width, height));
								resize.setWidth(width);
								resize.setHeight(height);
							}else if(resize.form == 4) {
								resize.setShape(new Ellipse2D.Double(o_x, o_y, width, height));
								resize.setWidth(width);
								resize.setHeight(height);
							}
						}
					}
					
					
					if(form == 1) {
						sh = new ShapeRe(new Line2D.Double(start_p, end_p), cl, thick, l_f, fill, form, width, height, start_p, end_p);
						shapes.add(sh);
					}else if(form == 3) { 
						sh = new ShapeRe(new Rectangle2D.Double(o_x, o_y, width, height), cl, thick, l_f, fill, form, width, height, start_p, end_p);
						shapes.add(sh);
					}else if(form == 4) {
						sh = new ShapeRe(new Ellipse2D.Double(o_x, o_y, width, height), cl, thick, l_f, fill, form, width, height, start_p, end_p);
						shapes.add(sh);
					}else if(form == 5) {
						pens.lastElement().add(end_p);
						int index = shapes.size() - 1;
						shapes.get(index).setPoint(pens.lastElement());
					}
					
					else if(copy == 1 && form == 20) {
						if(!shapes.isEmpty()) {
							int s_x = start_p.x;
							int s_y = start_p.y;
							int e_x = end_p.x;
							int e_y = end_p.y;
							
							if(target.form == 1){
								sh = new ShapeRe(new Line2D.Double(e_x, e_y, e_x+target.width, e_y+target.height), target.cl, target.thick, target.l_f, target.fill, target.form, target.width, target.height, target.point1, target.point2);
								shapes.add(sh);
							}else if(target.form == 3) { 
								sh = new ShapeRe(new Rectangle2D.Double((e_x- s_x)+((target.point1.x+target.point2.x)/2), (e_y - s_y)+((target.point1.y+target.point2.y)/2), target.width, target.height), target.cl, target.thick, target.l_f, target.fill, target.form, target.width, target.height, target.point1, target.point2);
								shapes.add(sh);
							}else if(target.form == 4) {
								sh = new ShapeRe(new Ellipse2D.Double((e_x- s_x)+((target.point1.x+target.point2.x)/2), (e_y - s_y)+((target.point1.y+target.point2.y)/2), target.width, target.height), target.cl, target.thick, target.l_f, target.fill, target.form, target.width, target.height, target.point1, target.point2);
								shapes.add(sh);
							}
						}
					}else if(drag == 1 && form == 0) {
						
						
					}
					if(form == 10)
						shapes.add(new ShapeRe(form, address, start_p.x, start_p.y, Math.abs(end_p.x-start_p.x), Math.abs(end_p.y-start_p.y)));
					
					repaint();

				}
				public void mouseDragged(MouseEvent e) {
					
					ShapeRe sh;
					
					end_p = e.getPoint();
					
					if(form == 5 || form == 7) {
						pens.lastElement().add(end_p);
					}
					
					else if(drag == 1 && form == 0) {
						
						int s_x = start_p.x;
						int s_y = start_p.y;
						int e_x = end_p.x;
						int e_y = end_p.y;
						
						int min_x = Math.min(target.point1.x, target.point2.x);
						int min_y = Math.min(target.point1.y, target.point2.y);
						
						if(target != null) {
							if(target.form == 1)
								target.setShape(new Line2D.Double(e_x, e_y, e_x+target.width, e_y+target.height));
							else if(target.form == 3)
								target.setShape(new Rectangle2D.Double((e_x- s_x)+min_x, (e_y - s_y)+min_y, target.width, target.height));
							else if(target.form == 4)
								target.setShape(new Ellipse2D.Double((e_x- s_x)+min_x, (e_y - s_y)+min_y, target.width, target.height));
							
						}
						
					}
					else if(copy == 1 && form == 20) {
						if(!shapes.isEmpty()) {
							int s_x = start_p.x;
							int s_y = start_p.y;
							int e_x = end_p.x;
							int e_y = end_p.y;
							
							if(f_copy) {
								f_copy = false;
							}
							else {
								shapes.remove(shapes.size()-1);	
							}
							
							if(target.form == 1){
								sh = new ShapeRe(new Line2D.Double(e_x, e_y, e_x+target.width, e_y+target.height), target.cl, target.thick, target.l_f, target.fill, target.form, target.width, target.height, target.point1, target.point2);
								shapes.add(sh);
							}else if(target.form == 3) { 
								sh = new ShapeRe(new Rectangle2D.Double((e_x- s_x)+((target.point1.x+target.point2.x)/2), (e_y - s_y)+((target.point1.y+target.point2.y)/2), target.width, target.height), target.cl, target.thick, target.l_f, target.fill, target.form, target.width, target.height, target.point1, target.point2);
								shapes.add(sh);
							}else if(target.form == 4) {
								sh = new ShapeRe(new Ellipse2D.Double((e_x- s_x)+((target.point1.x+target.point2.x)/2), (e_y - s_y)+((target.point1.y+target.point2.y)/2), target.width, target.height), target.cl, target.thick, target.l_f, target.fill, target.form, target.width, target.height, target.point1, target.point2);
								shapes.add(sh);
							}
						}
					}
					
					repaint();
				}
				@Override
				public void dragEnter(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void dragOver(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void dropActionChanged(DropTargetDragEvent dtde) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void dragExit(DropTargetEvent dte) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void drop(DropTargetDropEvent dtde) {
					// TODO Auto-generated method stub
					
				}
	
			}
	}
}
