package window;

import java.awt.BorderLayout;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.GenotypeFolds;
import data.Result;

public class Main extends JFrame{
	private static final long serialVersionUID = 1L;
	private PanelAlgorithm panelAlgorithm=null;
	private PanelResult panelResult=null;
	private PanelButton panelButton=null;
	private DialogAlgorithmDetail dialogAlgorithmDetail=null;
	protected data.GenotypeFolds geno=null;
	private ReentrantReadWriteLock lockThread=null;
	private thread.ThreadAlgorithm threadAlgorithm=null;
	protected static String name="HS_MMGK";
	public Main(){
		this.setTitle(name);
		lockThread=new ReentrantReadWriteLock();
		//this.setLayout(new GridLayout(0,1));
		this.setLayout(new BorderLayout());
		panelAlgorithm=new PanelAlgorithm(this);
		panelResult=new PanelResult(this);
		panelButton=new PanelButton(this);
		this.add(panelAlgorithm,BorderLayout.NORTH);
		this.add(panelResult,BorderLayout.CENTER);
		this.add(panelButton,BorderLayout.SOUTH);
		panelResult.setBorder(BorderFactory.createLoweredBevelBorder());
        panelAlgorithm.setBorder(BorderFactory.createLoweredBevelBorder());
        panelButton.setBorder(BorderFactory.createLoweredBevelBorder());
		this.setSize(800,600);
		this.setLocationRelativeTo(null);
		this.setResizable(true);
        this.setVisible(true);
        
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public final JButton getAlgorithmMessage() {
		// TODO Auto-generated method stub
		return panelAlgorithm.getMessage();
	}
	public final void setRunningAlgorithm(thread.ThreadAlgorithm threadAlgorithm){
		this.threadAlgorithm=threadAlgorithm;
		if(threadAlgorithm==null)
			panelButton.setStatus(PanelButton.NONE);
		else
			panelButton.setStatus(PanelButton.RUNNING);
	}
	/*
	public final void setRunningAlgorithm(algorithm.Algorithm a){
		this.a=a;
		if(a==null)
			panelButton.setStatus(PanelButton.NONE);
		else
			panelButton.setStatus(PanelButton.RUNNING);
	}
	*/
	public final void readThread(){
		lockThread.readLock().lock();
	}
	public final void unReadThread(){
		lockThread.readLock().unlock();
	}
	public final void writeThread(){
		lockThread.writeLock().lock();
	}
	public final void unWriteThread(){
		lockThread.writeLock().unlock();
	}
	public final void finishAlgorithm() {
		threadAlgorithm.finish();
		panelResult.clear();
	}
	public final data.Solution[] getSolutions(int n) {
		// TODO Auto-generated method stub
		return threadAlgorithm.getSolutions(n);
	}
	/*
	public final void finishAlgorithm() {
		a.setStatus(algorithm.Algorithm.STATUS_STOP_CREATED);
	}
	public final data.Solution[] getSolutions(int n) {
		// TODO Auto-generated method stub
		return a.getSolutions(n);
	}
	*/
	public final void setStatus(int status) {
		// TODO Auto-generated method stub
		panelButton.setStatus(status);
	}
	public final void setGeno(GenotypeFolds geno2) {
		// TODO Auto-generated method stub
		geno=geno2;
	}
	public final void addResult(Result result) {
		// TODO Auto-generated method stub
		boolean b=panelButton.getRunning();
		if(b){
			this.writeThread();
			panelButton.setStatus(PanelButton.WAITING);
		}
		else{
			panelButton.setStatus(PanelButton.WAITING);
		}
		threadAlgorithm.updateResults(panelResult.addResult(result));
		if(b){
			this.unWriteThread();
			panelButton.setStatus(PanelButton.RUNNING);
		}
		else{
			panelButton.setStatus(PanelButton.PAUSED);
		}
	}
	public final void exportResult() {
		// TODO Auto-generated method stub
		panelResult.exportResult();
	}
	public final JPanel getDialogAlgorithmDetailContent(){
		if(dialogAlgorithmDetail==null){
			return null;
		}
		else{
			return dialogAlgorithmDetail.getContent();
		}
	}
	public final void setDialogAlgorithmDetail(DialogAlgorithmDetail d){
		this.dialogAlgorithmDetail=d;
	}
	public final void setAlgoirthmContentEnabled(boolean b){
		panelAlgorithm.setContentEnabled(b);
	}
}
