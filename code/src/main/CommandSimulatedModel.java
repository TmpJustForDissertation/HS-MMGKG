package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public final class CommandSimulatedModel {
	private static String[] models=null;
	private static int order=2;
	private static int hms=5;
	private static double hmcr=0.8;
	private static double par=0.4;
	private static long tMax=-1;
	private static ArrayList<String> list=new ArrayList<String>();
	private static int status=-1;
	private static data.Indexes solution=null;
	private final static void fill(){
		switch(status){
		case 0:
			models=new String[list.size()];
			list.toArray(models);
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
		case 5:
			int[] t=new int[list.size()];
			for(int j=0;j<t.length;j++){
				t[j]=Integer.parseInt(list.get(j));
			}
			solution=new data.Indexes(t);
			list.clear();
			break;
		case 6:
			tMax=Integer.parseInt(list.get(0));
			list.clear();
			break;
		}
	}
	public static void main(String[] args){
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-models")){
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
			else if(args[i].equals("-generation")){
				fill();
				status=6;
			}
			else{
				if(status==-1){
					help();
					return;
				}
				else{
					list.add(args[i]);
				}
			}
		}
		fill();
		if(models==null){
			help();
			return;
		}
		for(String model:models){
			FileOutputStream fos=null;
			try {
				fos=new FileOutputStream(model+".result.hs_mmgk.txt");
				for(String filename : new File(model).list()){
					data.Criteria c=algorithm.HS_MMGKG.mainForAFile(model+"/"+filename,tMax,order,hms,hmcr,par,solution);
					fos.write(c.toString().getBytes());
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private static final void help(){
		System.out.println("parameters error");
	}
}
