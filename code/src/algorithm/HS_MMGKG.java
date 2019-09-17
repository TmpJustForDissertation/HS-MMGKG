package algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * Shouheng T, Zhang J, Yuan X, et al. FHSA-SED: Two-Locus Model Detection for Genome-Wide Association Study with Harmony Search Algorithm:[J]. Plos One, 2016, 11(3):e0150669.
 * 在这个算法的基础上加入互信息
 * 让算法对群体中最优的值偏向更明显
 */
public class HS_MMGKG implements Algorithm{
	private static long startTime=0;
	private static final long serialVersionUID = 1L;
	private static int MAX_RETRY=400000;
	/*
	 * genotype data
	 */
	private data.GenotypeFolds geno=null;
	/*
	 * epistasis order to test
	 */
	private int order=0;
	/*
	 * harmony memory size
	 */
	private int hms=0;
	/*
	 * harmony memory consider rate
	 */
	private double hmcr=0;
	/*
	 * the rate of choosing a neighboring value
	 */
	private double par=0;
	/*
	 * harmony memory MDR CE Gini K2
	 */
	private data.Indexes[] hm0=null;
	private data.Indexes[] hm1=null;
	private data.Indexes[] hm2=null;
	private data.Indexes[] hm3=null;
	private data.Indexes[] hm4=null;
	/*
	 * fitness memory
	 */
	private double[] fitness0=null;
	private double[] fitness1=null;
	private double[] fitness2=null;
	private double[] fitness3=null;
	private double[] fitness4=null;
	private data.Solutions solutions=null;
	/*
	 * the algorithm status
	 */
	private int status=0;
	/*
	 * tracing the harmony
	 */
	private HashSet<data.Indexes> tracing=null;
	/*
	 * the random creator for this algorithm
	 */
	private Random ran=null;
	/*
	 * T max for harmony search
	 */
	private long tMax=0;
	private long gen=0;
	/*
	 * 记录不能作为解的集合
	 * ，我现在在检测2阶的上位性
	 * ，单的显著的snp不应该被考虑
	 * ，这个集合维护这些东西。
	 */
	private data.Indexes solution=null;
	/*
	 * 记录那些SNP或组合，不被探索
	 * ，例如我指定SNP1在exclusions里
	 * ，那么我在探索新的组合时
	 * ，SNP1
	 * ，SNP1 SNP2
	 * ，SNP1 SNP3
	 * ，都将不能被访问
	 */
	private HashSet<data.Indexes> exclusions;
	/*
	 * 统计我一共计算了多少次适应度，用于生成统计算法优劣的结果。
	 */
	private int times;
	/*
	 * 在程序中，回显的SNP组合数目。
	 */
	private int nShow;
	/*
	 * 常规p-value阈值。
	 */
	private double pvalue=1;
	public HS_MMGKG(
			data.GenotypeFolds geno
			,int order
			,int hms
			,double hmcr
			,double par
			,long tMax
			,double pvalue
			,int nShow
			,HashSet<data.Indexes> exclusions
			,data.Indexes solution){
		this.geno=geno;
		this.order=order;
		this.hms=hms;
		this.hmcr=hmcr;
		this.par=par;
		this.pvalue=pvalue;
		this.hm0=null;
		this.hm1=null;
		this.hm2=null;
		this.hm3=null;
		this.hm4=null;
		this.status=Algorithm.STATUS_CREATED;
		this.tracing=new HashSet<data.Indexes>();
		this.ran=new Random();
		this.tMax=tMax;
		this.gen=0;
		this.nShow=nShow;
		this.solutions=new data.Solutions(hms);
		if(exclusions==null){
			exclusions=new HashSet<data.Indexes>();
		}
		else{
			this.exclusions=exclusions;
		}
		if(solution!=null){
			/*
			 * 为了防止solution在98 99给模拟数据的分析带来的影响
			 * ，随机从data里选两个位置和solution交换
			 */
			int[] ranPosi=sample(geno.getNumOfSnps(),order);
			while(sort(ranPosi)){
				ranPosi=sample(geno.getNumOfSnps(),order);
			}
			this.solution=new data.Indexes(ranPosi);
			geno.exchange(solution.getIndexes(),ranPosi);
		}
		times=0;
		//printSolution();
	}
	void printSolution(){
		double[] f=fitness.Computer.MDR_CE_Gini_K2_G(geno, solution.getIndexes());
		System.out.println(String.format("%f,%f,%f,%f",f[0],f[1],f[2],f[3]));
		
	}
	@Override
	public final void fit() {
		/*
		 * 备忘一下
		 * MDR越大越好
		 * CE越小越好
		 * Gini越大越好
		 * K2越大越好
		 * G越小越好
		 */
		gen++;
		if(status==Algorithm.STATUS_RUNNING){
			data.Indexes a=generateAHarmony();
			double[] f=fitness.Computer.MDR_CE_Gini_K2_G(geno, a.getIndexes());
			times++;
			boolean b=false;
			if(f[0]>fitness0[hms-1]){
				b=true;
				hm0[hms-1]=a;
				fitness0[hms-1]=f[0];
				fitness.Computer.update(fitness0, hm0, hms, hms-1, 0, false);
			}
			if(f[1]<fitness1[hms-1]){
				hm1[hms-1]=a;
				fitness1[hms-1]=f[1];
				fitness.Computer.update(fitness1, hm1, hms, hms-1, 0, true);
			}
			if(f[2]>fitness2[hms-1]){
				hm2[hms-1]=a;
				fitness2[hms-1]=f[2];
				fitness.Computer.update(fitness2, hm2, hms, hms-1, 0, false);
			}
			if(f[3]>fitness3[hms-1]){
				hm3[hms-1]=a;
				fitness3[hms-1]=f[3];
				fitness.Computer.update(fitness3, hm3, hms, hms-1, 0, false);
			}
			if(f[4]<fitness4[hms-1]){
				b=true;
				hm4[hms-1]=a;
				fitness4[hms-1]=f[4];
				fitness.Computer.update(fitness4, hm4, hms, hms-1, 0, true);
			}
			if(b){
				solutions.add(a.getIndexes(), f[0], f[4]);
			}
		}
		else if(status==Algorithm.STATUS_RESULT_UPDATED){
			int nNull=0;
			int tNull=0;
			for(int i=0;i<hms;i++){
				if(inExclusions(hm0[i])){
					tNull++;
					hm0[i]=null;
					fitness0[i]=-Double.MAX_VALUE;
				}
			}
			nNull=Math.max(nNull, tNull);
			tNull=0;
			for(int i=0;i<hms;i++){
				if(inExclusions(hm1[i])){
					tNull++;
					hm1[i]=null;
					fitness1[i]=Double.MAX_VALUE;
				}
			}
			nNull=Math.max(nNull, tNull);
			tNull=0;
			for(int i=0;i<hms;i++){
				if(inExclusions(hm2[i])){
					tNull++;
					hm2[i]=null;
					fitness2[i]=-Double.MAX_VALUE;
				}
			}
			nNull=Math.max(nNull, tNull);
			tNull=0;
			for(int i=0;i<hms;i++){
				if(inExclusions(hm3[i])){
					tNull++;
					hm3[i]=null;
					fitness3[i]=-Double.MAX_VALUE;
				}
			}
			nNull=Math.max(nNull, tNull);
			tNull=0;
			for(int i=0;i<hms;i++){
				if(inExclusions(hm4[i])){
					tNull++;
					hm4[i]=null;
					fitness4[i]=Double.MAX_VALUE;
				}
			}
			nNull=Math.max(nNull, tNull);
			HashSet<data.Indexes> h=new HashSet<data.Indexes>();
			for(data.Indexes ind:solutions.getMem()){
				if(inExclusions(ind)){
					h.add(ind);
				}
			}
			Iterator<data.Indexes> it=h.iterator();
			while(it.hasNext()){
				solutions.remove(it.next());
			}
			fitness.Computer.sort(fitness0, hm0, false);
			fitness.Computer.sort(fitness1, hm1, true);
			fitness.Computer.sort(fitness2, hm2, false);
			fitness.Computer.sort(fitness3, hm3, false);
			fitness.Computer.sort(fitness4, hm4, true);
			clearTracing();
			for(int i=0;i<nNull;i++){
				data.Indexes a=generateAHarmonyFromSpace();
				double[] f=fitness.Computer.MDR_CE_Gini_K2_G(geno, a.getIndexes());
				times++;
				boolean b=false;
				if(f[0]>fitness0[hms-1]){
					b=true;
					hm0[hms-1]=a;
					fitness0[hms-1]=f[0];
					fitness.Computer.update(fitness0, hm0, hms, hms-1, 0, false);
				}
				if(f[1]<fitness1[hms-1]){
					hm1[hms-1]=a;
					fitness1[hms-1]=f[1];
					fitness.Computer.update(fitness1, hm1, hms, hms-1, 0, true);
				}
				if(f[2]>fitness2[hms-1]){
					hm2[hms-1]=a;
					fitness2[hms-1]=f[2];
					fitness.Computer.update(fitness2, hm2, hms, hms-1, 0, false);
				}
				if(f[3]>fitness3[hms-1]){
					hm3[hms-1]=a;
					fitness3[hms-1]=f[3];
					fitness.Computer.update(fitness3, hm3, hms, hms-1, 0, false);
				}
				if(f[4]<fitness4[hms-1]){
					b=true;
					hm4[hms-1]=a;
					fitness4[hms-1]=f[4];
					fitness.Computer.update(fitness4, hm4, hms, hms-1, 0, true);
				}
				if(b){
					solutions.add(a.getIndexes(), f[0], f[4]);
				}
			}
			status=Algorithm.STATUS_RUNNING;
		}
		else if(status==Algorithm.STATUS_CREATED){
			hm0=new data.Indexes[hms];
			hm1=new data.Indexes[hms];
			hm2=new data.Indexes[hms];
			hm3=new data.Indexes[hms];
			hm4=new data.Indexes[hms];
			fitness0=new double[hms];
			fitness1=new double[hms];
			fitness2=new double[hms];
			fitness3=new double[hms];
			fitness4=new double[hms];
			/*
			 * initial hm 0 1 2 3
			 */
			for(int i=0;i<hms;i++){
				data.Indexes a=generateAHarmonyFromSpace();
				double[] f=fitness.Computer.MDR_CE_Gini_K2_G(geno,a.getIndexes());
				times++;
				hm0[i]=a;
				fitness0[i]=f[0];
				hm1[i]=a;
				fitness1[i]=f[1];
				hm2[i]=a;
				fitness2[i]=f[2];
				hm3[i]=a;
				fitness3[i]=f[3];
				hm4[i]=a;
				fitness4[i]=f[4];
				solutions.add(a.getIndexes(), f[0], f[4]);
			}
			fitness.Computer.sort(fitness0, hm0, false);
			fitness.Computer.sort(fitness1, hm1, true);
			fitness.Computer.sort(fitness2, hm2, false);
			fitness.Computer.sort(fitness3, hm3, false);
			fitness.Computer.sort(fitness4, hm4, true);
			status=Algorithm.STATUS_RUNNING;
		}
		if(tracing.contains(solution)){
			status=Algorithm.STATUS_OVER_NOT_STOP;
		}
	}
	private final data.Indexes generateAHarmony() {
		data.Indexes a=null;
		int nRetry=0;
		do{
			int[] r=new int[order];
			do{
				for(int i=0;i<order;i++){
					if(ran.nextDouble()<hmcr){
						int selected=ran.nextInt(5*hms);
						if(selected<hms){
							r[i]=hm0[selected].getIndexes()[ran.nextInt(order)];
							if(ran.nextDouble()<par){
								r[i]=r[i]+((int)((ran.nextDouble())*(hm0[0].getIndexes()[ran.nextInt(order)]-r[i])));
							}
						}
						else if(selected<2*hms){
							r[i]=hm1[selected-hms].getIndexes()[ran.nextInt(order)];
							if(ran.nextDouble()<par){
								r[i]=r[i]+((int)((ran.nextDouble())*(hm1[0].getIndexes()[ran.nextInt(order)]-r[i])));
							}
						}
						else if(selected<3*hms){
							r[i]=hm2[selected-2*hms].getIndexes()[ran.nextInt(order)];
							if(ran.nextDouble()<par){
								r[i]=r[i]+((int)((ran.nextDouble())*(hm2[0].getIndexes()[ran.nextInt(order)]-r[i])));
							}
						}
						else if(selected<4*hms){
							r[i]=hm3[selected-3*hms].getIndexes()[ran.nextInt(order)];
							if(ran.nextDouble()<par){
								r[i]=r[i]+((int)((ran.nextDouble())*(hm3[0].getIndexes()[ran.nextInt(order)]-r[i])));
							}
						}
						else{
							r[i]=hm4[selected-4*hms].getIndexes()[ran.nextInt(order)];
							if(ran.nextDouble()<par){
								r[i]=r[i]+((int)((ran.nextDouble())*(hm4[0].getIndexes()[ran.nextInt(order)]-r[i])));
							}
						}
					}
					else{
						r[i]=ran.nextInt(geno.getNumOfSnps());
					}
					if(r[i]<0)
						r[i]=0;
					if(r[i]>=geno.getNumOfSnps())
						r[i]=geno.getNumOfSnps()-1;
				}
			}while(sort(r));
			a=new data.Indexes(r);
			nRetry++;
			if(nRetry>MAX_RETRY){
				clearTracing();
			}
		}
		while(tracing.contains(a)||inExclusions(a));
		tracing.add(a);
		return a;
	}
	/*
	 * 从解空间里随机生成一个解
	 */
	private final data.Indexes generateAHarmonyFromSpace() {
		int[] a=sample(geno.getNumOfSnps(),order);
		while(sort(a)){
			//a有重复
			a=sample(geno.getNumOfSnps(),order);
		}
		data.Indexes tmp=new data.Indexes(a);
		while(tracing.contains(tmp)||inExclusions(tmp)){
			a=sample(geno.getNumOfSnps(),order);
			while(sort(a)){
				//a有重复
				a=sample(geno.getNumOfSnps(),order);
			}
			tmp=new data.Indexes(a);
		}
		tracing.add(tmp);
		//此时tmp中存有一个可用的解
		return tmp;
	}
	
	/*
	 * 判断这个tmp所代表的解是否在exclusions之中
	 * ，当exclusions中存在tmp或它的子集返回true
	 * ，否则tmp是一个合法的解，返回false
	 */
	private final boolean inExclusions(data.Indexes tmp) {
		if(exclusions==null){
			return false;
		}
		Iterator<data.Indexes> it=exclusions.iterator();
		while(it.hasNext()){
			if(tmp.contains(it.next())){
				return true;
			}
		}
		return false;
	}
	/*
	 * 非重复从n里随机抽取k个整数
	 */
	private final int[] sample(int n,int k){
		int[] r=new int[k];
		for(int i=0;i<k;i++){
			int t=ran.nextInt(n);
			boolean b=true;
			while(b){
				b=false;
				for(int j=0;j<i;j++){
					if(r[j]==t){
						b=true;
						t=ran.nextInt(n);
						break;
					}
				}
			}
			r[i]=t;
		}
		return r;
	}
	@Override
	public final String getAlgorithmDescription() {
		String s=fitness1[0]+"\t"+fitness2[0];
		return s;
	}

	@Override
	public final int getStatus() {
		return status;
	}

	@Override
	public final void setStatus(int status) {
		this.status=status;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void fillObject(Object o, int type) {
		/*
		 * 所有的回显信息从Solutions类中获得
		 */
		data.Solution[] mm=solutions.getMem();
		if(type==2){
			/*
			 * 填充window界面里的一个button，显示现在Solutions中最优的解。
			 */
			JButton area=(JButton)o;
			String s="generation    "+gen+"    [";
			for(int j=0;j<order-1;j++){
				s=s+mm[0].getIndexes()[j]+",";
			}
			s=s+mm[0].getIndexes()[order-1]+"]    [";
			for(int j=0;j<order-1;j++){
				s=s+geno.getNames()[mm[0].getIndexes()[j]]+",";
			}
			s=s+geno.getNames()[mm[0].getIndexes()[order-1]]+"]    =>    "+mm[0].getMDR()+"    "+mm[0].getPvalue();
			area.setText(s);
		}
		else if(type==3){
			/*
			 * 在command界面里显示solutions中的前nShow个最好的组合作为回显。
			 */
			System.out.println("generation    "+gen+"    :");
			for(int i=0;i<nShow&&i<hms;i++){
				String s=i+"    [";
				for(int j=0;j<order-1;j++){
					s=s+mm[i].getIndexes()[j]+",";
				}
				s=s+mm[i].getIndexes()[order-1]+"]    [";
				for(int j=0;j<order-1;j++){
					s=s+geno.getNames()[mm[i].getIndexes()[j]]+",";
				}
				s=s+geno.getNames()[mm[i].getIndexes()[order-1]]+"]    =>    "+mm[i].getMDR()+"    "+mm[i].getPvalue();
				System.out.println(s);
			}
		}
		else if(type==0){
			/*
			 * 在window界面的dialog里显示前nShow个最好的SNP组合作为回显。
			 */
			JPanel area=(JPanel)o;
			java.awt.Component[] cs=area.getComponents();
			for(int i=0;i<nShow&&i<hms;i++){
				String s=i+"    [";
				for(int j=0;j<order-1;j++){
					s=s+mm[i].getIndexes()[j]+",";
				}
				s=s+mm[i].getIndexes()[order-1]+"]    [";
				for(int j=0;j<order-1;j++){
					s=s+geno.getNames()[mm[i].getIndexes()[j]]+",";
				}
				s=s+geno.getNames()[mm[i].getIndexes()[order-1]]+"]    =>    "+mm[i].getMDR()+"    "+mm[i].getPvalue();
				((JLabel)cs[i]).setText(s);
			}
		}
		else if(type==1){
			/*
			 * exclusions列表发生更新，更改算法的状态，在下一次fit时，算法将根据新的exclusions更新和声库。
			 */
			exclusions=(HashSet<data.Indexes>)o;
			status=Algorithm.STATUS_RESULT_UPDATED;
		}
	}
	/*
	 * 排序x，如果x中有重复的元素，返回true，排序顺利，返回false
	 */
	private final boolean sort(int [] x){
		boolean b=true;
		while(b){
			b=false;
			for(int i=0;i<x.length-1;i++){
				if(x[i]==x[i+1]){
					return true;
				}
				else if(x[i]>x[i+1]){
					int t=x[i];
					x[i]=x[i+1];
					x[i+1]=t;
					b=true;
					break;
				}
			}
		}
		return false;
	}
	@Override
	public final data.Solution[] getSolutions(int n){
		/*
		 * 作为最终的结果
		 * ，如果我使用p-value
		 * ，会有很高的假阳性
		 * ，因为很多p-value为0的组合
		 * ，很明显MDR很低
		 * ，这应该算是p-value的内部问题。
		 * 如果我用MDR
		 * ，在模拟数据上表现太差
		 * ，能不能两个一起用？
		 * 对于n>0的情况
		 * ，我让solutions里包含的是所有的p-value低于阈值
		 * ，但是要用MDR来排序。
		 */
		return solutions.getSolutions(n,pvalue);
	}
	@Override
	public data.Criteria run() {
		for(int i=0;i<tMax&&status!=Algorithm.STATUS_OVER_NOT_STOP;i++){
			fit();
		}
		int tp=0;
		int fp=0;
		//认为所有在solutions中的都是positive
		data.Solution[] solutions=getSolutions(0);
		for(int i=0;i<solutions.length;i++){
			if(solutions[i].equals(solution)){
				tp++;
			}
			else{
				fp++;
			}
		}
		/*
		//认为只有solutions中的第一个是positive
		data.Solution[] solutions=getSolutions(1);
		if(solutions[0].equals(solution)){
			tp=1;
			fp=0;
		}
		else{
			tp=0;
			fp=1;
		}
		*/
		int tn=(int)tMax-1-fp;
		int fn=1-tp;
		return new data.Criteria(geno.getFilename(), times,((double)(System.currentTimeMillis()-startTime))/1000, tp, fp, tn, fn);
	}
	public static final data.Criteria mainForAFile(String filename,long tMax,int order,int hms,double hmcr,double par,data.Indexes solution){
		System.out.println(filename);
		startTime=System.currentTimeMillis();
		data.GenotypeFolds data=new data.GenotypeFolds(filename,1,5);
		data.load();
		/*
		long comb=1;
		for(int i=data.getNumOfSnps();i>data.getNumOfSnps()-order;i--){
			comb=comb*i;
		}
		for(int i=2;i<=order;i++){
			comb=comb/i;
		}
		*/
		HS_MMGKG a=new HS_MMGKG(
				data
				,order
				,hms
				,hmcr
				,par
				,tMax
				,0.05/tMax
				,4
				,null
				,solution);
		data.Criteria c=a.run();
		return c;
	}
	public static void main(String [] args){
		String[] models={"DME -1","DME -2","DME -3","DME -4","DME -5","DME -6","DME -7","DME -8","DME -9","DME -10","DME -11","DME -12","DNME -1","DNME -2","DNME -3","DNME -4","DNME -5","DNME -6","DNME -7","DNME -8","DNME -9","DNME -10","DNME -11","DNME -12","DNME -13","DNME -14"};
		//String[] models={"DME01_1600_100","DME02_1600_100","DME03_1600_100","DME04_1600_100","DME05_1600_100","DME06_1600_100","DME07_1600_100","DME08_1600_100","DME09_1600_100","DME10_1600_100","DME11_1600_100","DME12_1600_100"};
		for(String model:models){
			String workspace=String.format("D:\\workspace\\working\\simulated_data\\%s",model);
			FileOutputStream fos=null;
			try {
				double meanTimes=0;
				fos=new FileOutputStream(workspace+".result.hs_mmgkg.txt");
				for(String filename : new File(workspace).list()){
					data.Criteria c=mainForAFile(workspace+"/"+filename,99*100/2,2,20,0.8,0.4,new data.Indexes(new int[]{98,99}));
					fos.write(c.toString().getBytes());
					meanTimes+=c.getTimes();
				}
				System.out.println(meanTimes/100);
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
	private final void clearTracing(){
		System.out.println("tracing clear");
		tracing.clear();
		for(int i=0;i<hms;i++){
			if(hm0[i]!=null)
				tracing.add(hm0[i]);
			if(hm1[i]!=null)
				tracing.add(hm1[i]);
			if(hm2[i]!=null)
				tracing.add(hm2[i]);
			if(hm3[i]!=null)
				tracing.add(hm3[i]);	
			if(hm4[i]!=null)
				tracing.add(hm4[i]);
		}
	}
}

