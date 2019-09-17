package window;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DialogAlgorithm extends JDialog{
	protected static int MAX_SHOW=400;
	private static final long serialVersionUID = 1L;
	private JButton buttonLoadData=null;
	private JLabel labelData=null;
	private Main main=null;
	private String filename=null;
	private JTextField textFold=null;
	private JTextField textOrder=null;
	private JTextField textHms=null;
	private JTextField textHmcr=null;
	private JTextField textPar=null;
	private JTextField textTMax=null;
	private JTextField textIShow=null;
	private JTextField textNShow=null;
	private JTextField textPvalue=null;
	private JButton buttonOk=null;
	private JButton buttonCancel=null;
	private boolean isOk=false;
	public DialogAlgorithm(Main main) {
		super(main,"algorithm options",true);
		this.main=main;
		this.getContentPane().setLayout(new GridLayout(0,2));
		
		
		buttonLoadData=new JButton("load data");
		buttonLoadData.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loadData();
			}});
		labelData=new JLabel("null");
		this.getContentPane().add(buttonLoadData);
		this.getContentPane().add(labelData);
		
		
		textFold=new JTextField("5");
		this.getContentPane().add(new JLabel("fold : "));
		this.getContentPane().add(textFold);
		
		
		textOrder=new JTextField("2");
		this.getContentPane().add(new JLabel("order : "));
		this.getContentPane().add(textOrder);
		
		
		textHms=new JTextField("400");
		this.getContentPane().add(new JLabel("harmony memory size : "));
		this.getContentPane().add(textHms);
		
		
		textHmcr=new JTextField("0.8");
		this.getContentPane().add(new JLabel("harmony memory consider rate : "));
		this.getContentPane().add(textHmcr);
		
		
		textPar=new JTextField("0.4");
		this.getContentPane().add(new JLabel("the rate of choosing a neighboring value : "));
		this.getContentPane().add(textPar);
		
		
		textTMax=new JTextField("-1");
		this.getContentPane().add(new JLabel("max generation in harmony search : "));
		this.getContentPane().add(textTMax);
		
		
		textPvalue=new JTextField("0.05");
		this.getContentPane().add(new JLabel("pvalue : "));
		this.getContentPane().add(textPvalue);
		
		
		textIShow=new JTextField("4000");
		this.getContentPane().add(new JLabel("show information every ? of generations : "));
		this.getContentPane().add(textIShow);
		
		
		textNShow=new JTextField("400");
		this.getContentPane().add(new JLabel("the number of harmony shown in the algorithm detail dialog : "));
		this.getContentPane().add(textNShow);
		
		
		buttonOk=new JButton("OK");
		buttonCancel=new JButton("Cancel");
		this.getContentPane().add(buttonOk);
		this.getContentPane().add(buttonCancel);
		buttonCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}});
		buttonOk.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int order=Integer.parseInt(textOrder.getText().toString());
					int hms=Integer.parseInt(textHms.getText().toString());
					double hmcr=Double.parseDouble(textHmcr.getText().toString());
					double par=Double.parseDouble(textPar.getText().toString());
					long tMax=Long.parseLong(textTMax.getText().toString());
					int fold=Integer.parseInt(textFold.getText().toString());
					int iShow=Integer.parseInt(textIShow.getText().toString());
					int nShow=Integer.parseInt(textNShow.getText().toString());
					double pvalue=Double.parseDouble(textPvalue.getText().toString());
					startAlgorithm(filename,order,hms,hmcr,par,tMax,pvalue,fold,iShow,nShow);
					isOk=true;
				}
				catch(Exception ee){
					ee.printStackTrace();
					JOptionPane.showMessageDialog(main, "error occurs while start the algorithm", "algorithm error", JOptionPane.ERROR_MESSAGE);
				}
				dispose();
			}});
		
		setLocationRelativeTo(null);
		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
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
				if(!isOk)
					main.setRunningAlgorithm(null);
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
				dispose();
			}		
		});
	}
	private void loadData(){
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("select the data file");
		chooser.setMultiSelectionEnabled(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("plink file format(*.tped or *.tfam)","tped","tfam");
		chooser.setFileFilter(filter);
		if(JFileChooser.APPROVE_OPTION==chooser.showOpenDialog(main)){
			filename=chooser.getSelectedFile().getAbsolutePath();
			labelData.setText(filename);
		}
		else{
			System.out.println("no file has been selected");
		}
	}
	private final void startAlgorithm(String filename,int order,int hms,double hmcr,double par,long tMax,double pvalue,int fold,int iShow,int nShow){
		System.out.println("Dialog Algorithm startAlgorithm");
		main.setStatus(PanelButton.WAITING);
		MAX_SHOW=nShow;
		new thread.ThreadAlgorithm(main,filename,order,hms,hmcr,par,tMax,pvalue,fold,iShow,nShow,0).start();
	}
}
