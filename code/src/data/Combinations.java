package data;

import java.io.Serializable;
import java.util.Arrays;

public final class Combinations implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int n=0;
	private int k=0;
	private int[] x=null;
	private boolean nexted=false;
	public Combinations(int n,int k){
		this.n=n;
		this.k=k;
		x=new int[k];
		for(int i=0;i<k;i++){
			x[i]=i;
		}
		nexted=true;
	}
	public final boolean hasNext(){
		if(nexted==true){
			if(x==null)
				return false;
			else
				return true;
		}
		else{
			fetchNext();
			if(x==null){
				return false;
			}
			else{
				return true;
			}
		}
	}
	public final int[] next(){
		if(nexted){
			nexted=false;
			return x;
		}
		else{
			fetchNext();
			nexted=false;
			return x;
		}
	}
	private final void fetchNext(){
		if(!nexted){
			//找到倒数第一个不用进位的位
			x=Arrays.copyOf(x,k);
			int i;
			for(i=k-1;i>=0;i--){
				x[i]++;
				if(x[i]!=n-(k-1-i)){
					break;
				}
			}
			if(i==-1){
				x=null;
			}
			else{
				for(int j=i+1;j<k;j++){
					x[j]=x[j-1]+1;
				}
			}
			nexted=true;
		}
	}
	/*
	public static void main(String[] args){
		Combinations c=new Combinations(4,2);
		System.out.println(str(c.next()));
		System.out.println(str(c.next()));
		while(c.hasNext()){
			System.out.println(str(c.next()));
		}
	}
	private static String str(int[] x){
		String r="["+x[0];
		for(int i=1;i<x.length;i++){
			r=r+","+x[i];
		}
		r=r+"]";
		return r;
	}
	*/
}
