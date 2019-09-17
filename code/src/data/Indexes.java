package data;

import java.io.Serializable;
import java.util.Arrays;

public class Indexes implements Serializable{
	private static final long serialVersionUID = 1L;
	protected int [] indexes;
	@Override
	public final boolean equals(Object obj) {
		try{
			Indexes s=(Indexes)obj;
			if(indexes.length!=s.indexes.length)
				return false;
			for(int i=0;i<indexes.length;i++){
				if(indexes[i]!=s.indexes[i]){
					return false;
				}
			}
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public final boolean equals(int[] indexes){
		if(this.indexes.length!=indexes.length)
			return false;
		for(int i=0;i<this.indexes.length;i++){
			if(this.indexes[i]!=indexes[i])
				return false;
		}
		return true;
	}
	public Indexes(int[] indexes){
		this.indexes=Arrays.copyOf(indexes,indexes.length);
	}
	@Override
	public int hashCode() {
		int l=indexes.length;
		int r=indexes[0];
		for(int i=1;i<l;i++){
			r=r^indexes[i];
		}
		return r;
	};
	public final int[] getIndexes(){
		return indexes;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s="snps combination=[";
		for(int i=0;i<indexes.length-1;i++){
			s=s+indexes[i]+" ";
		}
		s=s+indexes[indexes.length-1]+"]";
		return s;
	}
	public final boolean contains(Indexes ind){
		if(this.indexes.length>ind.indexes.length){
			int e=0;
			for(int i=0,j=0;i<ind.indexes.length;i++){
				while(j<this.indexes.length){
					if(this.indexes[j]==ind.indexes[i]){
						j++;
						e++;
						break;
					}
					else{
						j++;
					}
				}
			}
			return e==ind.indexes.length;
		}
		else if(this.equals(ind)){
			return true;
		}
		else{
			return false;
		}
	}
	public static void main(String[] args){
		Indexes a=new Indexes(new int[]{1,2,3,4});
		System.out.println(a.contains(new Indexes(new int[]{1})));
		System.out.println(a.contains(new Indexes(new int[]{2})));
		System.out.println(a.contains(new Indexes(new int[]{3})));
		System.out.println(a.contains(new Indexes(new int[]{4})));
		System.out.println(a.contains(new Indexes(new int[]{1,2})));
		System.out.println(a.contains(new Indexes(new int[]{2,3})));
		System.out.println(a.contains(new Indexes(new int[]{3,4})));
		System.out.println(a.contains(new Indexes(new int[]{1,4})));
		System.out.println(a.contains(new Indexes(new int[]{1,2,3})));
		System.out.println(a.contains(new Indexes(new int[]{2,3,4})));
		System.out.println(a.contains(new Indexes(new int[]{1,3,4})));
		System.out.println(a.contains(new Indexes(new int[]{1,2,4})));
		System.out.println(a.contains(new Indexes(new int[]{1,2,3,4})));
		
		System.out.println(a.contains(new Indexes(new int[]{1,2,3,4,5})));
		System.out.println(a.contains(new Indexes(new int[]{1,2,3,5})));
		System.out.println(a.contains(new Indexes(new int[]{1,5})));
		System.out.println(a.contains(new Indexes(new int[]{44})));





	}
}
