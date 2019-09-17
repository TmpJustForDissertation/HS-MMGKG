package window;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DialogAlgorithmDetail extends JDialog{
	private static final long serialVersionUID = 1L;
	private JPanel jta=null;
	public DialogAlgorithmDetail(Main main) {
		// TODO Auto-generated constructor stub
		jta=new JPanel();
		jta.setLayout(new GridLayout(0,1));
		for(int i=0;i<DialogAlgorithm.MAX_SHOW;i++)
			jta.add(new JLabel());
		//jta.setEditable(false);
		main.setDialogAlgorithmDetail(this);
		JScrollPane jsp=new JScrollPane(jta);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(jsp,BorderLayout.CENTER);
		this.setLocationRelativeTo(null);
		this.setSize(400,300);
		this.setVisible(true);
		this.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowOpened");
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowClosing");
				main.setDialogAlgorithmDetail(null);
				main.setAlgoirthmContentEnabled(true);
				dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowClosed");
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowIconified");
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowDeiconified");
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowActivated");
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowDeactivated");
			}});
	}

	public JPanel getContent() {
		// TODO Auto-generated method stub
		return jta;
	}

}
