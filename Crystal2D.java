
//
// TODO:
//
// Optimize the drawing. Is it possible to render only a small part of the pane?
// Threads? Many concurrent particles?
// Draw more than one point at once with drawLine().
// Export to BMP file.
// Only repaint after every 100 particles. Grow exponentially.
// Statistics.
//

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import finnhakansson.*;

public class Crystal2D {
	private final static boolean DEBUG = false;
	private final static int TITLE_HEIGHT = 22;
	private final static int RIM_RADIUS = 300;
	private final static int DROP_DISTANCE = 5;
	private final static boolean ENABLE_ANTI_ALIASING = true;
	private final static boolean DRAW_RIM = false;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Crystal 2D");
		Crystal2D c = new Crystal2D();
		Crystal crystal = c.new Crystal(RIM_RADIUS);
		crystal.initCrystalOneAtomMiddle();
		CrystalPane cp = c.new CrystalPane(RIM_RADIUS * 2);
		cp.setMatrix(crystal.getMatrix());
		frame.getContentPane().add(cp);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(RIM_RADIUS * 2, RIM_RADIUS * 2 + TITLE_HEIGHT);
		frame.setVisible(true);

		int repaintLimit = 100;
		int numberOfParticles = 1;
		boolean crystalTouchingRim = false;
		while (!crystalTouchingRim) {
			Particle p = c.new Particle(crystal.getDropRadius(), RIM_RADIUS);
			if (DEBUG) {
				cp.setParticle(p);
				cp.repaint();
				try {
					Thread.currentThread().sleep(1);
				} catch (InterruptedException ie) {
					//
				}
			}
			boolean particleIsFree = true;
			do {
				p.move();
				if (!crystal.insideRim(p)) {
					particleIsFree = false;
				} else if (crystal.near(p)) {
					crystal.attach(p);
					numberOfParticles++;
					if (numberOfParticles % repaintLimit == 0) {
						cp.repaint();
					}
					crystalTouchingRim = crystal.onRim(p);
					particleIsFree = false;
				}
			} while (particleIsFree);
		}
		cp.repaint();
		System.out.println(numberOfParticles);
		System.out.println("Generation complete.");
		System.out.println("Creating BMP image...");
		byte[] BMPHeader = BmpFile.CreateBitMapFileHeader(crystal.getWidth(), crystal.getHeight());
		byte[] BMPInfoHeader = BmpFile.CreateBitMapInfoHeader(crystal.getWidth(), crystal.getHeight());
        String filename = "/Users/karin/Desktop/crystal_01.bmp";
        File f = new File(filename);
        try {
            // Create an output stream to the file.
            FileOutputStream file_output = new FileOutputStream(f);
            // Wrap the FileOutputStream with a DataOutputStream
            DataOutputStream data_out = new DataOutputStream(file_output);

            data_out.write(BMPHeader, 0, BMPHeader.length);
            data_out.write(BMPInfoHeader, 0, BMPInfoHeader.length);

            boolean[][] matrix = crystal.getMatrix();
            byte[] buf = new byte[3];
            for (int y = 0; y < crystal.getHeight(); y++) {
                for (int x = 0; x < crystal.getWidth(); x++) {
                	byte r = 0;
                    byte g = 0;
                    byte b = 0;
                    if (matrix[y][x]) {
                    	r = (byte)0xff;
                    	g = (byte)0xff;
                    }
                    buf[0] = b;
                    buf[1] = g;
                    buf[2] = r;
                	data_out.write(buf, 0, buf.length);
                }
            }
            file_output.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
        }
        System.out.println("Done.");
	}

	class CrystalPane extends JComponent {
		private static final long serialVersionUID = 1L;
		private int side;
		private boolean[][] matrix;
		private Particle p;
		private Color color;
		private Color antiAliasColor;

		public CrystalPane(int side) {
			this.side = side;
			this.p = (Particle)null;
			//this.color = Color.RED;
			this.color = new Color(0.9f, 0.9f, 0.1f, 1.0f);
			this.antiAliasColor = new Color(0.1f, 0.1f, 0.01f, 1.0f);
		}
		
		public void setMatrix(boolean[][] matrix) {
			this.matrix = matrix;
		}

		public void paintComponent(Graphics g) {
			g.setColor(this.color);

			if (DRAW_RIM) {
				g.drawArc(0, 0, this.side - 1, this.side - 1, 0, 360);
			}

			for (int y = 0; y < this.side; y++) {
				for (int x = 0; x < this.side; x++) {
					if (this.matrix[y][x]) {
						g.drawLine(x, y, x, y);
					}
				}
			}

			if (DEBUG) {
				g.setColor(Color.RED);
				if (this.p != (Particle)null) {
					g.drawLine(this.p.getX(), this.p.getY(), this.p.getX(), this.p.getY());
				}
				g.setColor(Color.BLACK);
			}

			if (ENABLE_ANTI_ALIASING) {
				// Apply anti-aliasing.
				g.setColor(this.antiAliasColor);
				for (int y = 1; y < this.side; y++) {
					for (int x = 1; x < this.side; x++) {
						if (this.matrix[y][x]) {
							if (this.matrix[y - 1][x - 1]) {
								if (!this.matrix[y][x - 1]) {
									g.drawLine(x - 1, y, x - 1, y);
								}
								if (!this.matrix[y - 1][x]) {
									g.drawLine(x, y - 1, x, y - 1);
								}
							}
							if ((x + 1) < this.side && this.matrix[y - 1][x + 1]) {
								if (!this.matrix[y][x + 1]) {
									g.drawLine(x + 1, y, x + 1, y);
								}
								if (!this.matrix[y - 1][x]) {
									g.drawLine(x, y - 1, x, y - 1);
								}
							}
						}
					}
				}
			}
		}

		public void setParticle(Particle p) {
			this.p = p;
		}
	}

	// The model.
	class Crystal {
		private boolean matrix[][];
		private int radius;
		private int width;
		private int height;
		private int middleX;
		private int middleY;
		private int currentRadius;

		public Crystal(int radius) {
			this.radius = radius;
			this.width = radius * 2;
			this.height = radius * 2;
			this.middleX = this.width / 2;
			this.middleY = this.height / 2;
			this.matrix = new boolean[this.height][this.width];
			int x, y;
			for (y = 0; y < this.height; y++) {
				for (x = 0; x < this.width; x++) {
					this.matrix[y][x] = false;
				}
			}
			this.currentRadius = 1;
		}

		public void initCrystalOneAtomMiddle() {
			this.matrix[this.middleY][this.middleX] = true;
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public int getHeight() {
			return this.height;
		}

		public boolean near(Crystal2D.Particle p) {
			int xPos = p.getX();
			int yPos = p.getY();
			int xBegin = xPos - 1;
			int yBegin = yPos - 1;
			int xEnd = xPos + 1;
			int yEnd = yPos + 1;
			if (xBegin < 1) {
				xBegin = 1;
			}
			if (yBegin < 1) {
				yBegin = 1;
			}
			if (xEnd >= this.width) {
				xEnd = this.width - 1;
			}
			if (yEnd >= this.height) {
				yEnd = this.height - 1;
			}
			for (int y = yBegin; y <= yEnd; y++) {
				for (int x = xBegin; x <= xEnd; x++) {
					if (this.matrix[y][x]) {
						return true;
					}
				}
			}
			return false;
		}
		
		public void attach(Crystal2D.Particle p) {
			int xPos = p.getX();
			int yPos = p.getY();
			int radius = (int)Math.sqrt((xPos - this.middleX) * (xPos - this.middleX) + (yPos - this.middleY) * (yPos - this.middleY));
			if (radius > this.currentRadius) {
				this.currentRadius = radius;
			}
			this.matrix[yPos][xPos] = true;
		}

		public boolean insideRim(Crystal2D.Particle p) {
			int x = p.getX() - this.middleX;
			int y = p.getY() - this.middleY;
			int r = this.radius - 1;
			// From http://stackoverflow.com/questions/481144/equation-for-testing-if-a-point-is-inside-a-circle
			// (x - center_x)^2 + (y - center_y)^2 < radius^2
			return x * x + y * y < r * r;
		}

		public boolean onRim(Crystal2D.Particle p) {
			return this.currentRadius >= (this.radius - 2);
		}

		public boolean[][] getMatrix() {
			return this.matrix;
		}

		public int getDropRadius() {
			return Math.min(this.currentRadius + DROP_DISTANCE, this.radius);
		}
	}
	
	class Particle {
		private int x;
		private int y;
		private final int[][] positions = {
			{-1,  1}, {0,  1}, {1,  1},
			{-1,  0},          {1,  0},
			{-1, -1}, {0, -1}, {1, -1}
		};

		public Particle(int radius, int translation) {
			double angle = Math.random() * 2 * Math.PI; // Angle between 0 and 2 PI.
			this.x = (int)(Math.cos(angle) * radius) + translation;
			this.y = (int)(Math.sin(angle) * radius) + translation;
		}

		public int getX() {
			return this.x;
		}
		
		public int getY() {
			return this.y;
		}
		
		public void move() {
			int index = (int)Math.floor(Math.random() * this.positions.length);
			this.x += this.positions[index][0];
			this.y += this.positions[index][1];
		}

	}
}

