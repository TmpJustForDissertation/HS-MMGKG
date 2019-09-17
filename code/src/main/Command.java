package main;
import java.util.ArrayList;
//import java.util.Scanner;

public final class Command {
	private static String filename=null;
	private static int order=2;
	private static int hms=5;
	private static double hmcr=0.8;
	private static double par=0.4;
	private static long tMax=-1;
	private static ArrayList<String> list=new ArrayList<String>();
	private static int status=-1;
	private static int fold=5;
	private static int iShow=4000;
	private static int nShow=4;
	private static int nSolution=40;
	private static double pvalue=0.05;
	private final static void fill(){
		switch(status){
		case 0:
			filename=list.get(0);
			list.clear();
			break;
		case 1:
			order=Integer.parseInt(list.get(0));
			list.clear();
			break;
		case 2:
			hms=Integer.parseInt(list.get(0));
			list.clear();
			break;
		case 3:
			hmcr=Double.parseDouble(list.get(0));
			list.clear();
			break;
		case 4:
			par=Double.parseDouble(list.get(0));
			list.clear();
			break;
		case 6:
			tMax=Integer.parseInt(list.get(0));
			list.clear();
			break;
		case 7:
			fold=Integer.parseInt(list.get(0));
			list.clear();
			break;
		case 8:
			iShow=Integer.parseInt(list.get(0));
			list.clear();
			break;
		case 9:
			nShow=Integer.parseInt(list.get(0));
			list.clear();
			break;
		case 10:
			pvalue=Double.parseDouble(list.get(0));
			list.clear();
			break;
		case 11:
			nSolution=Integer.parseInt(list.get(0));
			list.clear();
			break;
		}
	}
	public static void main(String[] args){
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-file")){
				fill();
				status=0;
			}
			else if(args[i].equals("-order")){
				fill();
				status=1;
			}
			else if(args[i].equals("-hms")){
				fill();
				status=2;
			}
			else if(args[i].equals("-hmcr")){
				fill();
				status=3;
			}
			else if(args[i].equals("-par")){
				fill();
				status=4;
			}
			else if(args[i].equals("-solution")){
				fill();
				status=5;
			}
			else if(args[i].equals("-tmax")){
				fill();
				status=6;
			}
			else if(args[i].equals("-fold")){
				fill();
				status=7;
			}
			else if(args[i].equals("-ishow")){
				fill();
				status=8;
			}
			else if(args[i].equals("-nshow")){
				fill();
				status=9;
			}
			else if(args[i].equals("-pvalue")){
				fill();
				status=10;
			}
			else if(args[i].equals("-nsolution")){
				fill();
				status=11;
			}
			else{
				if(status==-1){
					HELP();
					return;
				}
				else{
					list.add(args[i]);
				}
			}
		}
		fill();
		if(filename==null){
			HELP();
			return;
		}
		thread.ThreadAlgorithm a=new thread.ThreadAlgorithm(null, filename, order, hms, hmcr, par, tMax, pvalue, fold, iShow,nShow,nSolution);
		a.start();
		/*
		Scanner sc= new Scanner(System.in);
		while(!sc.nextLine().equals("exit")){
			System.out.println("input exit if you want to exit the application.");			
		}
		*/
		try {
			a.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		a.finish();
		//sc.close();
	}
	private static final void HELP(){
		System.out.println("parameters error");
	}
	public static final void EXIT(){
		System.exit(0);
	}
}
