package data;

public final class Criteria {
	/*
	 * 针对每个模拟的数据集（文件夹）和每一个算法应该有一个记录各种指标的结果文件
	 * 其格式应该能始终保持统一，以免该来该去，导致无尽的麻烦
	 * 第一列，文件路径
	 * 第二列，在进化算法运行的过程中，进行了几次评价函数的计算
	 * 第三列，算法运行时间
	 * 第四列，TP，在算法判断几个组合是致病的前提下，这里有多少个是致病的。
	 * 第五列，FP
	 * 第六列，TN
	 * 第七列，FN
	 */
	private String filename;
	private int times;
	private double time;
	private int tp;
	private int fp;
	private int tn;
	private int fn;
	public Criteria(String filename,int times,double time,int tp,int fp,int tn,int fn){
		this.filename=filename;
		this.times=times;
		this.time=time;
		this.tp=tp;
		this.fp=fp;
		this.tn=tn;
		this.fn=fn;
	}
	@Override
	public final String toString() {
		// TODO Auto-generated method stub
		return String.format("%s\t%d\t%f\t%d\t%d\t%d\t%d\n",filename,times,time,tp,fp,tn,fn);
	}
	public final int getTimes(){
		return times;
	}
	
}
