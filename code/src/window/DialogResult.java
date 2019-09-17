package window;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class DialogResult extends JDialog{
	private static final long serialVersionUID = 1L;
	private JButton buttonCancel=null;
	private JButton buttonOK=null;
	private JTextField jtf=null;
	public DialogResult(Main main) {
		super(main,"add a result",true);
		this.setLayout(new GridLayout(0,2));
		this.add(new JLabel("input the snp index of a result(example 98,99) : "));
		data.Solution solution=main.getSolutions(1)[0];
		int[] indexes=solution.getIndexes();
		String s=String.valueOf(indexes[0]);
		for(int i=1;i<indexes.length;i++)
			s=s+","+indexes[i];
		jtf=new JTextField(s);
		this.add(jtf);
		buttonOK=new JButton("OK");
		this.add(buttonOK);
		buttonCancel=new JButton("Cancel");
		this.add(buttonCancel);
		buttonCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}});
		buttonOK.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try{
					String[] strs=jtf.getText().toString().split(",");
					int[] a=new int[strs.length];
					for(int i=0;i<a.length;i++){
						a[i]=Integer.parseInt(strs[i]);
					}
					String s="";
					data.Result result=new data.Result(a, main.geno);
					ArrayList<data.Result> results=data.Result.getResults(result, main.geno);
					Iterator<data.Result> it=results.iterator();
					while(it.hasNext()){
						s=s+it.next().toString();
					}
					s=s+"Do you confirm to add this result to the list ?\n"+result.toString();
					int r=JOptionPane.showConfirmDialog(main,s,"confirm dialog", JOptionPane.OK_CANCEL_OPTION);
					if(r==JOptionPane.OK_OPTION){
						main.addResult(result);
						main.setStatus(PanelButton.RUNNING_OR_PAUSED);
						dispose();
					}
				}
				catch(Exception ee){
					ee.printStackTrace();
					JOptionPane.showMessageDialog(main, "error occurs while adding a result , check the thing you input .", "error", JOptionPane.ERROR_MESSAGE);
				}
			}});
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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
			}
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("windowClosed");
				main.setStatus(PanelButton.RUNNING_OR_PAUSED);
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
			}		
		});
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}


}
