package thread;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JPanel;

import data.Result;
import data.Solution;
import main.Command;

public class ThreadAlgorithm extends Thread{
	private String filename=null;
	private int order=0;
	private int hms=0;
	private double hmcr=0;
	private double par=0;
	private long tMax=0;
	private int fold=0;
	private int iShow=0;
	private int nShow=0;
	private int nSolution=0;
	private double pvalue=1;
	private window.Main main=null;
	private algorithm.Algorithm a=null;
	public ThreadAlgorithm(window.Main main,String filename,int order,int hms,double hmcr,double par,long tMax,double pvalue,int fold,int iShow,int nShow,int nSolution){
		this.main=main;
		this.filename=filename;
		this.order=order;
		this.hms=hms;
		this.hmcr=hmcr;
		this.par=par;
		this.tMax=tMax;
		this.fold=fold;
		this.iShow=iShow;
		this.nShow=nShow;
		this.pvalue=pvalue;
		this.nSolution=nSolution;
	}
	@Override
	public void run() {
		System.out.println(String.format("start HS_MMGK filename=%s order=%d hms=%d hmcr=%f par=%f tMax=%d fold=%d iShow=%d nShow=%d ",filename,order,hms,hmcr,par,tMax,fold,iShow,nShow));
		if(main==null){
			runCommand();
		}
		else{
			runWindow();
		}
	}
	private final void runCommand(){
		long gen=1;
		data.GenotypeFolds geno=null;
		if(filename==null){
			System.out.println("load data failed");
			return;
		}
		if(filename.endsWith(".tped")||filename.endsWith(".tfam")){
			geno=new data.GenotypeFolds(filename.substring(0,filename.length()-5), data.GenotypeFolds.TYPE_TPLINK, fold);
		}
		else{
			geno=new data.GenotypeFolds(filename, data.GenotypeFolds.TYPE_NONE, fold);
		}
		System.out.println("loading data ....");
		if(geno.load()){
			System.out.println("data loaded");
			a=new algorithm.HS_MMGKG(geno, order, hms, hmcr, par, tMax, pvalue ,nShow, null, null);
			long i=0;
			while(i<tMax||tMax==-1){
				if(a.getStatus()==algorithm.Algorithm.STATUS_STOP_CREATED){
					a.setStatus(algorithm.Algorithm.STATUS_STOPPED);
					break;
				}
				a.fit();
				if(gen++%iShow==0)
					a.fillObject(null,3);
				i++;
			}
			data.Solution[] solutions=a.getSolutions(nSolution);
			FileOutputStream fos=null;
			try{
				fos=new FileOutputStream(filename+".hs_mmgk.result");
				for(int j=0;j<solutions.length;j++){
					fos.write("####\n".getBytes());
					data.Result result=new Result(solutions[j].getIndexes(),geno);
					fos.write(result.toString2().getBytes());
					ArrayList<Result> al=Result.getResults(result, geno);
					Iterator<Result> iti=al.iterator();
					while(iti.hasNext()){
						fos.write(iti.next().toString2().getBytes());
					}
					fos.write("####\n".getBytes());
				}
				Command.EXIT();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			finally{
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			System.out.println("load data failed");
		}
	}
	private final void runWindow(){
		long gen=1;
		data.GenotypeFolds geno=null;
		if(filename==null){
			main.getAlgorithmMessage().setText("load data failed");
			main.setRunningAlgorithm(null);
			return;
		}
		if(filename.endsWith(".tped")||filename.endsWith(".tfam")){
			geno=new data.GenotypeFolds(filename.substring(0,filename.length()-5), data.GenotypeFolds.TYPE_TPLINK, fold);
		}
		else{
			geno=new data.GenotypeFolds(filename, data.GenotypeFolds.TYPE_NONE, fold);
		}
		main.getAlgorithmMessage().setText("loading data ....");
		if(geno.load()){
			main.setGeno(geno);
			main.getAlgorithmMessage().setText("data loaded");
			a=new algorithm.HS_MMGKG(geno, order, hms, hmcr, par, tMax,pvalue , nShow, null, null);
			main.setRunningAlgorithm(this);
			long i=0;
			while(i<tMax||tMax==-1){
				main.readThread();
				if(a.getStatus()==algorithm.Algorithm.STATUS_STOP_CREATED){
					main.unReadThread();
					a.setStatus(algorithm.Algorithm.STATUS_STOPPED);
					break;
				}
				a.fit();
				main.unReadThread();
				if(gen++%iShow==0)
					a.fillObject(main.getAlgorithmMessage(),2);
				JPanel jta=main.getDialogAlgorithmDetailContent();
				if(jta!=null){
					a.fillObject(jta, 0);
				}
				i++;
			}
		}
		else{
			main.getAlgorithmMessage().setText("load data failed");
			main.setGeno(null);
			main.setRunningAlgorithm(null);
		}
		main.setRunningAlgorithm(null);
		main.setGeno(null);
		main.getAlgorithmMessage().setText("null");
	}
	public final void finish() {
		// TODO Auto-generated method stub
		a.setStatus(algorithm.Algorithm.STATUS_STOP_CREATED);
	}
	public final Solution[] getSolutions(int n) {
		// TODO Auto-generated method stub
		return a.getSolutions(n);
	}
	public final void updateResults(HashSet<Result> results) {
		// TODO Auto-generated method stub
		a.fillObject(results,1);
	}

}
