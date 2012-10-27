
import java.awt.*;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Crystal2D {
	private static int TITLE_HEIGHT = 22;
	private static int CRYSTAL_RADIUS = 150;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Crystal 2D");
		Crystal2D c = new Crystal2D();
		Crystal crystal = c.new Crystal(CRYSTAL_RADIUS);
		crystal.initCrystalOneAtomMiddle();
		CrystalPane cp = c.new CrystalPane("Bajs");
		cp.setMatrix(crystal.getMatrix());
		frame.getContentPane().add(cp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(CRYSTAL_RADIUS * 2, CRYSTAL_RADIUS * 2 + TITLE_HEIGHT);
		frame.setVisible(true);
		// TODO: Three problems:
		// The crystal doesn't stop to grow at the rim.
		// The particles are not evenly distributed on the rim.
		// It would be useful to visually inspect the random paths of the particles.
		boolean crystalTouchingRim = false;
		while (!crystalTouchingRim) {
			Particle p = c.new Particle();
			boolean attached = false;
			while (!(attached = crystal.attached(p)) && crystal.insideCircle(p)) {
				p.move();
			}
			cp.repaint();
			if (attached) {
				crystalTouchingRim = crystal.onRim(p);
			}
		}
	}

	private static int getNewLocation(int location) {
		return location + ((int)Math.floor(Math.random() * 3) - 1);
	}

	class CrystalPane extends JComponent {
		private static final long serialVersionUID = 1L;
		private boolean[][] matrix;

		public CrystalPane(String x) {
		}
		
		public void setMatrix(boolean[][] matrix) {
			this.matrix = matrix;
		}

		public void paintComponent(Graphics g) {
			//g.drawString("APA", 100, 100);
			g.drawArc(0, 0, 299, 299, 0, 360);
			for (int y = 0; y < 300; y++) {
				for (int x = 0; x < 300; x++) {
					if (this.matrix[y][x]) {
						g.drawLine(x, y, x, y);
					}
				}
			}
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
		}

		public void initCrystalOneAtomMiddle() {
			this.matrix[this.middleY][this.middleX] = true;
		}

		public boolean attached(Crystal2D.Particle p) {
			int xPos = p.getX();
			int yPos = p.getY();
			int xBegin = xPos - 1;
			int yBegin = yPos - 1;
			int xEnd = xPos + 1;
			int yEnd = yPos + 1;
			if (xBegin < 0) {
				xBegin = 0;
			}
			if (yBegin < 0) {
				yBegin = 0;
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
						this.matrix[yPos][xPos] = true;
						return true;
					}
				}
			}
			return false;
		}

		public boolean insideCircle(Crystal2D.Particle p) {
			int x = p.getX();
			int y = p.getY();
			// From http://stackoverflow.com/questions/481144/equation-for-testing-if-a-point-is-inside-a-circle
			// (x-center_x)^2 + (y - center_y)^2 < radius^2
			return Math.sqrt((double)(x - this.middleX)) + Math.sqrt((double)(y - this.middleY)) < this.radius * this.radius; 
		}
		
		public boolean onRim(Crystal2D.Particle p) {
			return !this.insideCircle(p);
			//return false;
		}

		public boolean[][] getMatrix() {
			return this.matrix;
		}
	}
	
	class Particle {
		private int x;
		private int y;
		
		public Particle() {
			// Generate an angle between 0 and 2 PI.
			double angle = Math.random() * 2 * Math.PI;
			double randomX = Math.cos(angle) * (CRYSTAL_RADIUS - 1) + CRYSTAL_RADIUS;
			double randomY = Math.sin(angle) * (CRYSTAL_RADIUS - 1) + CRYSTAL_RADIUS;
			this.x = (int)randomX;
			this.y = (int)randomY;
		}
		
		public int getX() {
			return this.x;
		}
		
		public int getY() {
			return this.y;
		}
		
		public void move() {
			this.x = getNewLocation(this.x);
			this.y = getNewLocation(this.y);
		}
		
		private int getNewLocation(int position) {
			return position + ((int)Math.floor(Math.random() * 3) - 1);
		}
	}
}

