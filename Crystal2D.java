
import java.awt.*;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Crystal2D {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Crystal 2D");
		Crystal2D c = new Crystal2D();
		Crystal crystal = c.new Crystal(300, 300);
		crystal.initCrystalOneAtomMiddle();
		CrystalPane cp = c.new CrystalPane("Bajs");
		cp.setMatrix(crystal.getMatrix());
		frame.getContentPane().add(cp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300 + 22);
		frame.setVisible(true);
		double dropRadius = 300 / 2 - 1;
		while (true) {
			double angle = Math.random() * 2 * Math.PI;
			double randomX = Math.cos(angle) * dropRadius + 150;
			double randomY = Math.sin(angle) * dropRadius + 150;
			int x = (int)Math.round(randomX);
			int y = (int)Math.round(randomY);
			while (!crystal.attached(x, y) && crystal.insideCircle(x, y)) {
				x = Crystal2D.getNewLocation(x);
				y = Crystal2D.getNewLocation(y);
			}
			cp.repaint();
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
		private int x;
		private int y;
		private int middleX;
		private int middleY;

		public Crystal(int sizeX, int sizeY) {
			this.x = sizeX;
			this.y = sizeY;
			this.middleX = this.x / 2;
			this.middleY = this.y / 2;
			this.matrix = new boolean[this.y][this.x];
			int x, y;
			for (y = 0; y < this.y; y++) {
				for (x = 0; x < this.x; x++) {
					this.matrix[y][x] = false;
				}
			}
		}

		public void initCrystalOneAtomMiddle() {
			this.matrix[this.middleY][this.middleX] = true;
		}

		public boolean attached(int xLocation, int yLocation) {
			int xBegin = xLocation - 1;
			int yBegin = yLocation - 1;
			int xEnd = xLocation + 1;
			int yEnd = yLocation + 1;
			if (xBegin < 0) {
				xBegin = 0;
			}
			if (yBegin < 0) {
				yBegin = 0;
			}
			if (xEnd >= this.x) {
				xEnd = this.x - 1;
			}
			if (yEnd >= this.y) {
				yEnd = this.y - 1;
			}
			for (int y = yBegin; y <= yEnd; y++) {
				for (int x = xBegin; x <= xEnd; x++) {
					if (this.matrix[y][x]) {
						this.matrix[yLocation][xLocation] = true;
						return true;
					}
				}
			}
			return false;
		}

		public boolean insideCircle(int x, int y) {
			// From http://stackoverflow.com/questions/481144/equation-for-testing-if-a-point-is-inside-a-circle
			// (x-center_x)^2 + (y - center_y)^2 < radius^2
			return Math.sqrt((double)(x - this.middleX)) + Math.sqrt((double)(y - this.middleY)) < this.x * this.x; 
		}

		public boolean[][] getMatrix() {
			return this.matrix;
		}
	}
}

