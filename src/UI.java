import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


@SuppressWarnings("serial")
public class UI extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public void run() {
		try {
			UI frame = new UI();
			Icon icon = new ImageIcon("QR_Code.png");
			JLabel label = new JLabel("", icon, JLabel.CENTER);
			frame.add(label);
			frame.setTitle("QR Code");
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*r
	 * Create the frame.
	 */
	public UI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 155, 155);		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

}
