
import java.awt.*;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Crystal2D {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Crystal 2D");
		Crystal2D c = new Crystal2D();
		frame.getContentPane().add(c.new CrystalPane("Bajs"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setVisible(true);
		Crystal crystal = c.new Crystal(300, 300);
		crystal.initCrystalOneAtomMiddle();
		int x = 0;
		int y = 0;
		// while loop here.
	}

	class CrystalPane extends JComponent {
		private static final long serialVersionUID = 1L;

		public CrystalPane(String x) {
		}

		public void paintComponent(Graphics g) {
			//g.drawString("APA", 100, 100);
		}
	}

	// The model.
	class Crystal {
		private boolean matrix[][];
		private int x;
		private int y;

		public Crystal(int sizeX, int sizeY) {
			this.x = sizeX;
			this.y = sizeY;
			this.matrix = new boolean[this.y][this.x];
			int x, y;
			for (y = 0; y < this.y; y++) {
				for (x = 0; x < this.x; x++) {
					this.matrix[y][x] = false;
				}
			}
		}
		
		public void initCrystalOneAtomMiddle() {
			int middle_x = this.x / 2;
			int middle_y = this.y / 2;
			this.matrix[middle_y][middle_x] = true;
			System.out.println("initCrystalOneAtomMiddle");
			System.out.println(middle_y);
			System.out.println(middle_x);
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
						this.matrix[yLocation][xLocation] = 1;
						return true;
					}
				}
			}
			return false;
		}

		boolean insideCircle(int x, int y) {
			// From http://stackoverflow.com/questions/481144/equation-for-testing-if-a-point-is-inside-a-circle
			// (x-center_x)^2 + (y - center_y)^2 < radius^2
			return true;
		}

	}
}

