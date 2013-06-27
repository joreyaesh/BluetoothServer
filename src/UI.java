import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


@SuppressWarnings("serial")
public class UI extends JFrame {

	private JPanel contentPane;
	private JLabel label;
	private UI frame;

	/**
	 * Launch the application.
	 * @author Josh Whaley
	 */
	public void run() {
		try {
			frame = new UI();
			Icon icon = new ImageIcon("QR_Code.png");
			label = new JLabel("", icon, JLabel.CENTER);
			frame.add(label);
			frame.setTitle("QR Code");
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	 * Create the frame.
	 */
	public UI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 255, 255);
		
		// Position the frame in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		// Add border around QR code		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}


	/**
	 * Minimizes the frame
	 * @see UI#restore()
	 */
	public void minimize() {
		frame.setExtendedState(ICONIFIED);
	}
	
	/**
	 * Restores the frame to its normal state
	 * @see UI#minimize()
	 */
	public void restore() {
		frame.setExtendedState(NORMAL);
	}

}
