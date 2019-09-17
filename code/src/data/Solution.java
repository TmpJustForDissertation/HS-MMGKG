package data;


public class Solution extends Indexes{
	private static final long serialVersionUID = 1L;
	protected double mdr;
	protected double pvalue;
	public Solution(int[] indexes,double mdr,double pvalue) {
		super(indexes);
		this.mdr=mdr;
		this.pvalue=pvalue;
	}
	@Override
	public final String toString() {
		return super.toString()+"    "+mdr+"    "+pvalue;
	}
	public final double getMDR() {
		return mdr;
	}
	public final double getPvalue(){
		return pvalue;
	}
}
