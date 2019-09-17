package window;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelAlgorithm extends JPanel{
	private static final long serialVersionUID = 1L;
	private JButton message=null;
	public PanelAlgorithm(Main main) {
		message=new JButton();
		message.setEnabled(true);
		message.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				message.setEnabled(false);
				new DialogAlgorithmDetail(main);
			}});
		this.setLayout(new BorderLayout());
		this.add(message,BorderLayout.CENTER);
	}


	public final void setContentEnabled(boolean b){
		message.setEnabled(b);
	}
	public final JButton getMessage() {
		// TODO Auto-generated method stub
		return message;
	}

}
