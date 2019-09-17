package fitness;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.stat.inference.TestUtils;
public final class Computer {
	public static final double[] MDR_CE_Gini_K2_G(data.GenotypeFolds geno,int[] snps){
		int[][][] c=geno.getTable(snps);
		int lf=c.length;
		int lg=c[0][0].length;
		int[][] ca=new int[2][lg];
		for(int i=0;i<lf;i++){
			for(int j=0;j<2;j++){
				for(int k=0;k<lg;k++){
					ca[j][k]+=c[i][j][k];
				}
			}
		}
		
		
		/*
		 * 计算MDR
		 */
		int numCase=geno.getNumOfCases();
		int numControl=geno.getNumOfControls();
		//double or=((double)geno.getNumOfCases())/geno.getNumOfControls();
		//build MDR classification models for each test set
		int rightCase=0;
		int rightControl=0;
		for(int indexTest=0;indexTest<lf;indexTest++){
			for(int i=0;i<lg;i++){
				int numCaseIn=ca[1][i]-c[indexTest][1][i];
				int numControlIn=ca[0][i]-c[indexTest][0][i];
				//double model=((double)(ca[1][i]-c[indexTest][1][i]))/(ca[0][i]-c[indexTest][0][i]);
				if(numCaseIn*numControl>numControlIn*numCase){
					//classify this to case sample
					rightCase+=c[indexTest][1][i];
				}
				else{
					//control
					rightControl+=c[indexTest][0][i];
				}
			}
		}
		double mdr=0.5*rightCase/numCase+0.5*rightControl/numControl;
		
		
		double[][] d=new double[2][lg];
		double [] px=new double[lg];
		for(int i=0;i<lg;i++){
			d[0][i]=((double)ca[0][i])/geno.getNumOfSamples();
			px[i]=d[0][i];
		}
		for(int i=0;i<lg;i++){
			d[1][i]=((double)ca[1][i])/geno.getNumOfSamples();
			px[i]+=d[1][i];
		}
		double ce=0;
		for(int i=0;i<lg;i++){
			if(d[0][i]!=0)
				ce+=d[0][i]*Math.log(px[i]/d[0][i]);
		}
		for(int i=0;i<lg;i++){
			if(d[1][i]!=0)
				ce+=d[1][i]*Math.log(px[i]/d[1][i]);
		}
		
		
		double gi=0;
		int m=geno.getNumOfSamples();
		for(int i=0;i<lg;i++){
			double a=ca[0][i]+ca[1][i];
			if(a!=0){
				double p=((double)ca[0][i])/(ca[0][i]+ca[1][i]);
				gi=gi+a/m*2*p*(1-p);
			}
		}
		
		
		double k2=0;
		for(int i=0;i<lg;i++){
			//c[0][i]!*c[1][i]!/(c[0][i]+c[1][i]+1)!
			//c[1][i]!/(c[0][i]+1)*...*(c[0][i]+c[1][i]+1)
			//log
			//(0++log(c[1][i]))-log(c[0][i]+1)++log(c[0][i]+c[1][i]+1)
			double t=0;
			if(ca[0][i]==0&&ca[1][i]==0){
				continue;
			}
			else if(ca[0][i]>ca[1][i]){
				for(int j=2;j<=ca[1][i];j++){
					t=t+Math.log(j);
				}
				for(int j=ca[0][i]+1;j<=ca[0][i]+ca[1][i]+1;j++){
					t=t-Math.log(j);
				}
			}
			else{
				for(int j=2;j<=ca[0][i];j++){
					t=t+Math.log(j);
				}
				for(int j=ca[1][i]+1;j<=ca[0][i]+ca[1][i]+1;j++){
					t=t-Math.log(j);
				}
			}
			k2=k2+t;
		}
		
		int countZero=0;
		for(int i=0;i<lg;i++){
			if(ca[0][i]==0&&ca[1][i]==0){
				countZero++;
			}
		}
		long [][] cc=new long[2][lg-countZero];
		for(int i=0,j=0;i<lg;i++){
			if(ca[0][i]!=0||ca[1][i]!=0){
				cc[0][j]=ca[0][i];
				cc[1][j]=ca[1][i];
				j++;
			}
		}
		double g=0;
		if(cc[0].length==1){
			g=1;
		}
		else{
			double p=TestUtils.gDataSetsComparison(cc[0],cc[1]);
			ChiSquaredDistribution dd=new ChiSquaredDistribution(cc[0].length-1);
			g=1-dd.cumulativeProbability(p);
			if(Double.isNaN(g))
				g=1;
		}
		return new double[]{mdr,ce,-gi,k2,g};
	}
	public static final void sort(double[] fitness,data.Indexes[] mem,boolean ascend){
		if(ascend)
			sortInAscend(fitness,mem,0,fitness.length-1);
		else
			sortInDescend(fitness,mem,0,fitness.length-1);
	}
	private static final void sortInAscend(double[] arr,data.Indexes[] mem,int low,int high){
		int n=arr.length;
		boolean b=true;
		while(b){
			b=false;
			for(int i=1;i<n;i++){
				if(arr[i]<arr[i-1]){
					double t=arr[i];
					arr[i]=arr[i-1];
					arr[i-1]=t;
					data.Indexes tt=mem[i];
					mem[i]=mem[i-1];
					mem[i-1]=tt;
					b=true;
				}
			}
		}
		/*
		int l=low;
		int h=high;
		double povit=arr[low];
		while(l<h){
			while(l<h&&povit<arr[h])
				h--;
			if(l<h){
				double tmp=arr[h];
				data.Indexes tm=mem[h];
				arr[h]=arr[l];
				mem[h]=mem[l];
				arr[l]=tmp;
				mem[l]=tm;
				l++;
			}
			while(l<h&&arr[l]<povit)
				l++;
			if(l<h){
				double tmp=arr[h];
				data.Indexes tm=mem[h];
				arr[h]=arr[l];
				mem[h]=mem[l];
				arr[l]=tmp;
				mem[l]=tm;
				h--;
			}
		}
		if(l>low)
			sortInAscend(arr,mem,low,l-1);
		if(h<high)
			sortInAscend(arr,mem,l+1,high);
		*/
	}
	private static final void sortInDescend(double[] arr,data.Indexes[] mem,int low,int high){
		int n=arr.length;
		boolean b=true;
		while(b){
			b=false;
			for(int i=1;i<n;i++){
				if(arr[i]>arr[i-1]){
					double t=arr[i];
					arr[i]=arr[i-1];
					arr[i-1]=t;
					data.Indexes tt=mem[i];
					mem[i]=mem[i-1];
					mem[i-1]=tt;
					b=true;
				}
			}
		}
		/*
		int l=low;
		int h=high;
		double povit=arr[low];
		while(l<h){
			while(l<h&&povit>arr[h])
				h--;
			if(l<h){
				double tmp=arr[h];
				data.Indexes tm=mem[h];
				arr[h]=arr[l];
				mem[h]=mem[l];
				arr[l]=tmp;
				mem[l]=tm;
				l++;
			}
			while(l<h&&arr[l]>povit)
				l++;
			if(l<h){
				double tmp=arr[h];
				data.Indexes tm=mem[h];
				arr[h]=arr[l];
				mem[h]=mem[l];
				arr[l]=tmp;
				mem[l]=tm;
				h--;
			}
		}
		if(l>low)
			sortInDescend(arr,mem,low,l-1);
		if(h<high)
			sortInDescend(arr,mem,l+1,high);
			*/
	}
	public final static boolean update(double[] fitness,data.Indexes[] mem,int len,int pos,int orderCheck,boolean ascend){
		/*
		 * 根据fitness的内容排序fitness和mem
		 * ，len指定fitness和mem的长度，不用内置的length是因为可能len比length小，我只处理len这么长，忽略len之后的
		 * ，pos唯一无序的位置，输入的fitness是有序的，除了pos指定的位置，我的目的就是确定pos的新位置，使fitness有序
		 * ，orderCheck当pos更改的新位置小于orderCheck，返回true，否则false
		 */
		if(ascend)
			return updateInAscend(fitness,mem,len,pos,orderCheck);
		else
			return updateInDescend(fitness,mem,len,pos,orderCheck);
	}
	private final static boolean updateInAscend(double[] fitness,data.Indexes[] mem,int len,int pos,int orderCheck){
		double f=fitness[pos];
		data.Indexes tm=mem[pos];
		int lastIndexOfWorst=len-1;
		for(int i=len-2;i>=0;i--){
			if(f<fitness[i]||i==pos){
				lastIndexOfWorst=i;
			}
			else{
				break;
			}
		}
		if(pos>lastIndexOfWorst){
			for(int i=pos;i>lastIndexOfWorst;i--){
				fitness[i]=fitness[i-1];
				mem[i]=mem[i-1];
			}
			fitness[lastIndexOfWorst]=f;
			mem[lastIndexOfWorst]=tm;
		}
		else if(pos<lastIndexOfWorst){
			for(int i=pos;i<lastIndexOfWorst;i++){
				fitness[i]=fitness[i+1];
				mem[i]=mem[i+1];
			}
			fitness[lastIndexOfWorst]=f;
			mem[lastIndexOfWorst]=tm;
		}
		return lastIndexOfWorst<orderCheck;
	}
	private final static boolean updateInDescend(double[] fitness,data.Indexes[] mem,int len,int pos,int orderCheck){
		double f=fitness[pos];
		data.Indexes tm=mem[pos];
		int lastIndexOfWorst=len-1;
		for(int i=len-2;i>=0;i--){
			if(f>fitness[i]||i==pos){
				lastIndexOfWorst=i;
			}
			else{
				break;
			}
		}
		if(pos>lastIndexOfWorst){
			for(int i=pos;i>lastIndexOfWorst;i--){
				fitness[i]=fitness[i-1];
				mem[i]=mem[i-1];
			}
			fitness[lastIndexOfWorst]=f;
			mem[lastIndexOfWorst]=tm;
		}
		else if(pos<lastIndexOfWorst){
			for(int i=pos;i<lastIndexOfWorst;i++){
				fitness[i]=fitness[i+1];
				mem[i]=mem[i+1];
			}
			fitness[lastIndexOfWorst]=f;
			mem[lastIndexOfWorst]=tm;
		}
		return lastIndexOfWorst<orderCheck;
	}
	public static final double[] MDR__G__C(data.GenotypeFolds geno,int[] snps){
		int[][][] c=geno.getTable(snps);
		int lf=c.length;
		int lg=c[0][0].length;
		long[][] ca=new long[2][lg];
		for(int i=0;i<lf;i++){
			for(int j=0;j<2;j++){
				for(int k=0;k<lg;k++){
					ca[j][k]+=c[i][j][k];
				}
			}
		}
		
		
		/*
		 * 计算MDR
		 */
		int numCase=geno.getNumOfCases();
		int numControl=geno.getNumOfControls();
		//double or=((double)geno.getNumOfCases())/geno.getNumOfControls();
		//build MDR classification models for each test set
		int rightCase=0;
		int rightControl=0;
		for(int indexTest=0;indexTest<lf;indexTest++){
			for(int i=0;i<lg;i++){
				int numCaseIn=(int)ca[1][i]-c[indexTest][1][i];
				int numControlIn=(int)ca[0][i]-c[indexTest][0][i];
				//double model=((double)(ca[1][i]-c[indexTest][1][i]))/(ca[0][i]-c[indexTest][0][i]);
				if(numCaseIn*numControl>numControlIn*numCase){
					//classify this to case sample
					rightCase+=c[indexTest][1][i];
				}
				else{
					//control
					rightControl+=c[indexTest][0][i];
				}
			}
		}
		double mdr=0.5*rightCase/numCase+0.5*rightControl/numControl;
		
		
		/*
		double or=((double)geno.getNumOfCases())/geno.getNumOfControls();
		//build MDR classification models for each test set
		int right=0;
		int wrong=0;
		for(int indexTest=0;indexTest<lf;indexTest++){
			for(int i=0;i<lg;i++){
				double model=((double)(ca[1][i]-c[indexTest][1][i]))/(ca[0][i]-c[indexTest][0][i]);
				if(model>or){
					//classify this to case sample
					right+=c[indexTest][1][i];
					wrong+=c[indexTest][0][i];
				}
				else{
					//control
					right+=c[indexTest][0][i];
					wrong+=c[indexTest][1][i];
				}
			}
		}
		double mdr=((double)right)/(right+wrong);
		*/
		
		int countZero=0;
		for(int i=0;i<lg;i++){
			if(ca[0][i]==0&&ca[1][i]==0){
				countZero++;
			}
		}
		if(countZero>0){
			long [][] cc=new long[2][lg-countZero];
			for(int i=0,j=0;i<lg;i++){
				if(ca[0][i]!=0||ca[1][i]!=0){
					cc[0][j]=ca[0][i];
					cc[1][j]=ca[1][i];
					j++;
				}
			}
			ca=cc;
		}
		
		
		double g=0;
		double chisq=0;
		if(ca[0].length==1){
			g=1;
			chisq=1;
		}
		else{
			double p=TestUtils.gDataSetsComparison(ca[0],ca[1]);
			ChiSquaredDistribution d=new ChiSquaredDistribution(ca[0].length-1);
			g=1-d.cumulativeProbability(p);
			if(Double.isNaN(g))
				g=1;
			chisq=TestUtils.chiSquareTest(ca);
			if(Double.isNaN(chisq))
				chisq=1;
		}
		return new double[]{mdr,g,chisq};
	}
	public static final double[] MDR__G(data.GenotypeFolds geno,int[] snps){
		int[][][] c=geno.getTable(snps);
		int lf=c.length;
		int lg=c[0][0].length;
		long[][] ca=new long[2][lg];
		for(int i=0;i<lf;i++){
			for(int j=0;j<2;j++){
				for(int k=0;k<lg;k++){
					ca[j][k]+=c[i][j][k];
				}
			}
		}
		
		
		/*
		 * 计算MDR
		 */
		int numCase=geno.getNumOfCases();
		int numControl=geno.getNumOfControls();
		//double or=((double)geno.getNumOfCases())/geno.getNumOfControls();
		//build MDR classification models for each test set
		int rightCase=0;
		int rightControl=0;
		for(int indexTest=0;indexTest<lf;indexTest++){
			for(int i=0;i<lg;i++){
				int numCaseIn=(int)ca[1][i]-c[indexTest][1][i];
				int numControlIn=(int)ca[0][i]-c[indexTest][0][i];
				//double model=((double)(ca[1][i]-c[indexTest][1][i]))/(ca[0][i]-c[indexTest][0][i]);
				if(numCaseIn*numControl>numControlIn*numCase){
					//classify this to case sample
					rightCase+=c[indexTest][1][i];
				}
				else{
					//control
					rightControl+=c[indexTest][0][i];
				}
			}
		}
		double mdr=0.5*rightCase/numCase+0.5*rightControl/numControl;
		
		
		
		int countZero=0;
		for(int i=0;i<lg;i++){
			if(ca[0][i]==0&&ca[1][i]==0){
				countZero++;
			}
		}
		if(countZero>0){
			long [][] cc=new long[2][lg-countZero];
			for(int i=0,j=0;i<lg;i++){
				if(ca[0][i]!=0||ca[1][i]!=0){
					cc[0][j]=ca[0][i];
					cc[1][j]=ca[1][i];
					j++;
				}
			}
			ca=cc;
		}
		
		
		double g=0;
		if(ca[0].length==1){
			g=1;
		}
		else{
			double p=TestUtils.gDataSetsComparison(ca[0],ca[1]);
			ChiSquaredDistribution d=new ChiSquaredDistribution(ca[0].length-1);
			g=1-d.cumulativeProbability(p);
			if(Double.isNaN(g))
				g=1;
		}
		return new double[]{mdr,g};
	}
	public static void main(String[] args){
		data.GenotypeFolds geno=new data.GenotypeFolds("D:/workspace/data/WTCCC/sly_in_20171010/simulated_data/2order/DME -5/74.1600.3.antesnp100.txt", 1, 5);
		geno.load();
		for(int i=0;i<geno.getNumOfSnps();i++){
			for(int j=i+1;j<geno.getNumOfSnps();j++){
				double[] f=Computer.MDR_CE_Gini_K2_G(geno, new int[]{i,j});
				System.out.println(String.format("(%d,%d)\tmdr : %f\tce : %f\tgini : %f\tk2 : %f\tg : %f", i,j,f[0],f[1],f[2],f[3],f[4]));
			}
		}
	}
}
