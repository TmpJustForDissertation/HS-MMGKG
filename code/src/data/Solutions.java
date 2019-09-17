package data;

import java.util.Random;

/*
 * 这个类的目标是产生更有意义的solution
 * ，实验发现
 * ，G-test的pvalue在真实数据上，意义不是很好
 * ，产生的结果很没有说服力
 * ，MDR好些
 * ，但在模拟数据上
 * ，MDR比G-test差了很多
 * ，我总希望能产生MDR和G-test的混合指标。
 * 我是这样设计的
 * ，在这个类里维护一个数组
 * ，存储历代中MDR和pvalue好的个体
 * ，什么是好？
 * 对这些个体的MDR和pvalue排序后的index相加
 * ，越小越好。
 */
public final class Solutions {
	/*
	 * 这个空间的大小
	 */
	private int n=0;
	/*
	 * 用于存储SNP组合的地方
	 */
	private E[] mem=null;
	public Solutions(int n){
		this.n=n;
		this.mem=new E[n];
		for(int i=0;i<n;i++){
			mem[i]=new E(new int[]{-1},-Double.MAX_VALUE,Double.MAX_VALUE,0,0,0);
		}
	}
	public final void add(int[] indexes,double v_mdr,double v_pvalue){
		for(int i=0;i<n;i++){
			if(mem[i].equals(new Indexes(indexes)))
				return;
		}
		//移除最后的元素
		for(int i=0;i<n-1;i++){
			if(mem[i]!=null){
				mem[i].fixAfterRemove(mem[n-1]);
			}
		}
		
		//添加一个元素到最后
		E e=new E(indexes,v_mdr,v_pvalue);
		for(int i=0;i<n-1;i++){
			if(mem[i]!=null){
				mem[i].fixAfterAdd(e);
			}
		}
		//确定最后一个元素的新位置
		int index=n-2;
		while(mem[index].rank_mp>e.rank_mp){
			index--;
			if(index==-1)
				break;
		}
		index++;
		for(int i=n-1;i>index;i--){
			mem[i]=mem[i-1];
		}
		mem[index]=e;
	}
	public final data.Solution[] getMem(){
		return mem;
	}
	public final void remove(Indexes e){
		for(int i=0;i<n;i++){
			if(mem[i].equals(e)){
				for(int j=0;j<n;j++){
					if(i!=j){
						mem[j].fixAfterRemove(mem[i]);
					}
				}
				mem[i].pvalue=Double.MAX_VALUE;
				mem[i].mdr=-Double.MAX_VALUE;
				for(int j=0;j<n;j++){
					if(i!=j){
						mem[j].fixAfterAdd(mem[i]);
					}
				}
				for(int j=i+1;j<n;j++){
					mem[j-1]=mem[j];
				}
				mem[n-1]=mem[i];
			}
		}
	}
	private final class E extends Solution{
		private static final long serialVersionUID = 1L;
		@SuppressWarnings("unused")
		private int rank_p=0;
		@SuppressWarnings("unused")
		private int rank_m=0;
		private int rank_mp=0;
		private E(int[] indexes,double mdr,double pvalue){
			super(indexes,mdr,pvalue);
		}
		private E(int[] indexes,double mdr,double pvalue,int rank_m,int rank_p,int rank_mp){
			super(indexes,mdr,pvalue);
			this.rank_m=rank_m;
			this.rank_p=rank_p;
			this.rank_mp=rank_mp;
		}
		private final void fixAfterRemove(E e){
			//e比这个对象好，移除e后，这个对象的rank下降
			if(e.pvalue<pvalue){
				rank_p--;
				rank_mp--;
			}
			if(e.mdr>mdr){
				rank_m--;
				rank_mp--;
			}
		}
		private final void fixAfterAdd(E e){
			//加入e后，e比这个对象差，e的rank升高
			//e比这个对象好，这个对象的rank升高
			if(pvalue<e.pvalue){
				e.rank_p++;
				e.rank_mp++;
			}
			else if(e.pvalue<pvalue){
				rank_p++;
				rank_mp++;
			}
			if(mdr>e.mdr){
				e.rank_m++;
				e.rank_mp++;
			}
			else if(e.mdr>mdr){
				rank_m++;
				rank_mp++;
			}
		}
	}
	@Override
	public String toString() {
		String s="";
		for(int i=0;i<n;i++){
			s=s+mem[i]+"\n";
		}
		return s;
	}
	public static void main(String[] args){
		Random ran=new Random(4);
		Solutions ss=new Solutions(4);
		ss.add(null,1,1);
		ss.add(null,2,2);
		ss.add(null,3,3);
		ss.add(null,4,4);
		ss.add(null,1,1);
		ss.add(null,2,2);
		ss.add(null,3,3);
		ss.add(null,4,4);
		while(true){
			ss.add(null, ran.nextInt(), ran.nextInt());
		}
	}
	public final Solution[] getSolutions(int n2,double p) {
		if(n2==0){
			/*
			 * 将p-value不大于p的组合作为结果返回
			 */
			int l=0;
			for(int i=0;i<n;i++){
				if(mem[i].pvalue<=p){
					l++;
				}
			}
			if(l==0){
				/*
				 * 没有找到符合p-value要求的SNP组合
				 * ，将排名第一的组合作为结果返回
				 */
				return new Solution[]{mem[0]};
			}
			else{
				/*
				 * 找到符合p-value要求的所有组合
				 * ，返回这些组合
				 * ，这种情况我觉得只有在模拟数据上才会适用
				 * ，在真实数据上返回所有符合p-value要求的组合假阳性太高了。
				 */
				Solution[] r=new Solution[l];
				for(int i=0,j=0;i<n;i++){
					if(mem[i].pvalue<=p){
						r[j++]=mem[i];
					}
				}
				return r;
			}
		}
		else{
			/*
			 * 程序要求返回指定数目的组合
			 * ，将排在前面的n2个组合作为结果返回。
			 */
			Solution[] r=new Solution[Math.min(n2,mem.length)];
			for(int i=0;i<r.length;i++){
				r[i]=mem[i];
			}
			return r;
		}
	}
}





















