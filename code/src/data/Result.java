package data;

import java.util.ArrayList;

public final class Result extends Indexes{
	private static final long serialVersionUID = 1L;
	private double mdr=Double.MAX_VALUE;
	private double g=Double.MAX_VALUE;
	private String[] names=null;
	public Result(int[] indexes,data.GenotypeFolds geno){
		super(indexes);
		double[] f=fitness.Computer.MDR__G(geno, indexes);
		mdr=f[0];
		g=f[1];
		this.names=geno.getNames();
	}
	@Override
	public final String toString() {
		String s="[";
		int order=indexes.length;
		for(int j=0;j<order-1;j++){
			s=s+indexes[j]+",";
		}
		s=s+indexes[order-1]+"]    [";
		for(int j=0;j<order-1;j++){
			s=s+names[indexes[j]]+",";
		}
		s=s+names[indexes[order-1]]+"]    =>    "+mdr+"    "+g+"\n";
		return s;
	}
	public final String toString2() {
		String s="[";
		int order=indexes.length;
		for(int j=0;j<order-1;j++){
			s=s+indexes[j]+",";
		}
		s=s+indexes[order-1]+"]\t[";
		for(int j=0;j<order-1;j++){
			s=s+names[indexes[j]]+",";
		}
		s=s+names[indexes[order-1]]+"]\t=>\t"+mdr+"\t"+g+"\n";
		return s;
	}
	public final static ArrayList<Result> getResults(Result result,GenotypeFolds geno){
		int order=result.getIndexes().length;
		ArrayList<Result> al=new ArrayList<Result>();
		for(int i=1;i<order;i++){
			Combinations c=new Combinations(order,i);
			while(c.hasNext()){
				int[] t=c.next();
				int[] tt=new int[i];
				for(int j=0;j<i;j++){
					tt[j]=result.getIndexes()[t[j]];
				}
				Result r=new Result(tt,geno);
				al.add(r);
			}
		}
		return al;
	}
}
