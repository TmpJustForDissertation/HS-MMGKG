package window;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public final class PanelButton extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final String COMMAND_ALGORITHM="algorithm";
	private static final String COMMAND_CLEAR="clear";
	private static final String COMMAND_ADD_RESULT="add result";
	private static final String COMMAND_EXPORT_RESULT="export result";
	public static final int NONE=0;
	public static final int WAITING=1;
	public static final int RUNNING = 2;
	public static final int PAUSED = 3;
	public static final int RUNNING_OR_PAUSED=4;
	private int status=NONE;
	private boolean running=false;
	private JButton buttonAlgorithm=null;
	private JButton buttonClear=null;
	private JButton buttonAddResult=null;
	private JButton buttonExportResult=null;
	private Main main=null;
	private ActionListener al=new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(e.getActionCommand());
			if(e.getActionCommand().equals(COMMAND_ALGORITHM)){
				switch(status){
				case NONE:
					setStatus(WAITING);
					startAlgorithm();
					break;
				case RUNNING:
					setStatus(WAITING);
					pauseAlgorithm();
					setStatus(PAUSED);
					break;
				case PAUSED:
					setStatus(WAITING);
					restartAlgorithm();
					setStatus(RUNNING);
					break;
				}
			}
			else if(e.getActionCommand().equals(COMMAND_CLEAR)){
				setStatus(WAITING);
				clearAlgorithm();
			}
			else if(e.getActionCommand().equals(COMMAND_ADD_RESULT)){
				setStatus(WAITING);
				addResult();
			}
			else if(e.getActionCommand().equals(COMMAND_EXPORT_RESULT)){
				setStatus(WAITING);
				exportResult();
				setStatus(RUNNING_OR_PAUSED);
			}
		}
	};
	public PanelButton(Main main) {
		// TODO Auto-generated constructor stub
		this.main=main;
		this.setLayout(new GridLayout(1,0));
		/*
		buttonLoadData=new JButton(COMMAND_LOAD_DATA);
		buttonLoadData.setActionCommand(COMMAND_LOAD_DATA);
		buttonLoadData.addActionListener(al);
		*/
		buttonAlgorithm=new JButton("start algorithm");
		buttonAlgorithm.setActionCommand(COMMAND_ALGORITHM);
		buttonAlgorithm.addActionListener(al);
		buttonClear=new JButton(COMMAND_CLEAR);
		buttonClear.setActionCommand(COMMAND_CLEAR);
		buttonClear.setEnabled(false);
		buttonClear.addActionListener(al);
		buttonAddResult=new JButton("add result");
		buttonAddResult.setActionCommand(COMMAND_ADD_RESULT);
		buttonAddResult.addActionListener(al);
		buttonAddResult.setEnabled(false);
		buttonExportResult=new JButton("export result");
		buttonExportResult.setActionCommand(COMMAND_EXPORT_RESULT);
		buttonExportResult.addActionListener(al);
		buttonExportResult.setEnabled(false);
		//this.add(buttonLoadData);
		this.add(buttonAlgorithm);
		this.add(buttonAddResult);
		this.add(buttonExportResult);
		this.add(buttonClear);
		this.setStatus(NONE);
	}
	private final void restartAlgorithm() {
		// TODO Auto-generated method stub
		main.unWriteThread();
	}

	private final void pauseAlgorithm() {
		main.writeThread();
	}

	private final void startAlgorithm() {
		// TODO Auto-generated method stub
		new DialogAlgorithm(main);
	}
	public final void setStatus(int status2) {
		//System.out.println("set status = "+status2);
		if(status2==RUNNING){
			status=status2;
			buttonAlgorithm.setText("pause the algorithm");
			buttonAlgorithm.setEnabled(true);
			buttonClear.setEnabled(true);
			buttonAddResult.setEnabled(true);
			buttonExportResult.setEnabled(true);
			main.setAlgoirthmContentEnabled(true);
			running=true;
			main.setTitle(Main.name+" running algorithm");
		}
		else if(status2==PAUSED){
			status=status2;
			buttonAlgorithm.setText("restart the algorithm");
			buttonAlgorithm.setEnabled(true);
			buttonClear.setEnabled(true);
			buttonAddResult.setEnabled(true);
			buttonExportResult.setEnabled(true);
			running=false;
			main.setAlgoirthmContentEnabled(true);
			main.setTitle(Main.name+" pausing algorithm");
		}
		else if(status2==NONE){
			status=status2;
			buttonAlgorithm.setText("start algorithm");
			buttonAlgorithm.setEnabled(true);
			buttonClear.setEnabled(false);
			buttonAddResult.setEnabled(false);
			buttonExportResult.setEnabled(false);
			main.setAlgoirthmContentEnabled(false);
			running=false;
			main.setTitle(Main.name);
		}
		else if(status2==WAITING){
			status=status2;
			buttonAlgorithm.setEnabled(false);
			buttonClear.setEnabled(false);
			buttonExportResult.setEnabled(false);
			buttonAddResult.setEnabled(false);
			main.setAlgoirthmContentEnabled(false);
			main.setTitle(Main.name+" waiting");
		}
		else if(status2==RUNNING_OR_PAUSED){
			if(running){
				setStatus(RUNNING);
			}
			else{
				setStatus(PAUSED);
			}
		}
	}
	protected final int getStatus(){
		return status;
	}
	private final void clearAlgorithm(){
		main.finishAlgorithm();
		if(running==false){
			if(status!=NONE)
				main.unWriteThread();
		}
	}
	private final void addResult(){
		new DialogResult(main);
	}
	private final void exportResult(){
		main.exportResult();
	}
	protected final boolean getRunning(){
		return running;
	}
}
