
import java.awt.*;
import javax.swing.*;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class Crystal2D {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Crystal 2D");
		Crystal2D c = new Crystal2D();
		frame.getContentPane().add(c.new Crystal("Bajs"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setVisible(true);
	}

	class Crystal extends JComponent {
		private static final long serialVersionUID = 1L;
		public Crystal(String x) {
		}
		public void paintComponent(Graphics g) {
			g.drawString("APA", 100, 100);
		}
	}

	class Model {
		public Model() {
			
		}
	}
}
