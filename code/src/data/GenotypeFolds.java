package data;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/*
 * 根据boost的设计方式来设计我自己底层的genotype存储方式
 * 这个对象代表的是整个一个数据，包含所有的样本和SNP
 * 为了可以快速的计算MDR，在此基础上，使样本分成平均的fold份
 */
public class GenotypeFolds implements Serializable{
	public static final int TYPE_NONE=1;
	public static final int TYPE_TPLINK=0;
	private static final long serialVersionUID = 1L;
	private static int SIZE_OF_LONG=64;
	private static long MASK_ONE=0x8000000000000000l;
	private static int[] BITS=_INITIAL_BITS();
	//fold number
	private int fold=5;
	//患病样本数
	private int mCase;
	//对照组样本数
	private int mControl;
	/*
	 * 内存中存储的真正的数据，此处存储的是患病组
	 * 第一维为snp的数目
	 * 第二维是fold编号
	 * 第三维长度为3，因为每个snp有3个基因型，两种纯合子，一种杂合子
	 * 第四维为ceil(mCaseOfFoldLast/sizeof(long))
	 */
	private long[][][][] memCase;
	private int sizeOfMemCase;
	/*
	 * 同上，存储的是对照组数据
	 */
	private long[][][][] memControl;
	private int sizeOfMemControl;
	/*
	 * 文件名，gwas数据文件
	 */
	private String filename=null;
	/*
	 * snp的名字信息，大概是snps的rs id
	 */
	private String[] names=null;
	/*
	 * gwas中snp的数目
	 */
	private int n=0;
	/*
	 * 用于记录每个snp的等位基因信息，第一个位置是频数大的，第二个位置是频数小的
	 */
	public char[][] snps=null;
	/*
	 * format用于记录输入文件的格式，不同的格式有不同的处理方法
	 * format 0为plink的tped格式
	 * 此时，filename存储的是文件名，例如d:/example
	 * tped文件的地址是d:/example.tped
	 * tfam文件的地址是d:/example.tfam
	 */
	private int format=0;
	private static int[] _INITIAL_BITS(){
		int[] r=new int[0x10000];
		for(int i=0;i<r.length;i++){
			r[i]=bitCount(i);
		}
		return r;
	}
	public GenotypeFolds(String filename,int format,int fold){
		this.filename=filename;
		this.format=format;
		this.fold=fold;
		names=null;
		mCase=0;
		mControl=0;
		n=0;
	}
	public final boolean load(){
		boolean r=false;
		switch(format){
		case 0:
			r=load0();
			break;
		case 1:
			r=load1();
			break;
		default:
			r=false;
			break;
		}
		return r;
	}
	private final boolean load0(){
		boolean b=true;
		String filenamePed=null;
		String filenameFam=null;
		filenamePed=filename+".tped";
		filenameFam=filename+".tfam";
		HashSet<Integer> hCase=new HashSet<Integer>();
		HashSet<Integer> hControl=new HashSet<Integer>();
		LineIterator it=null;
		try{
			it= FileUtils.lineIterator(new File(filenameFam), "UTF-8");
			while (it.hasNext()) {
				String line=it.next();
				char c=line.charAt(line.length()-1);
				if(c=='2'){
					hCase.add(mCase+mControl);
					mCase++;
				}
				else if(c=='1'){
					hControl.add(mCase+mControl);
					mControl++;
				}
				else{
					System.out.println(line);
					throw new Exception("the sample's phenotype of line "+(mCase+mControl+1)+" is missing , the application don't support the sample has missing value of phenotype .");
				}
			}
			//System.out.println(String.format("mCase : %d , mControl : %d , n : %d",mCase,mControl,n));			
		}
		catch (Exception e) {
			e.printStackTrace();
			b=false;
		}
		finally{
			LineIterator.closeQuietly(it);
		}
		if(!b)
			return false;
		n=0;
		try{
			it= FileUtils.lineIterator(new File(filenamePed), "UTF-8");
			while (it.hasNext()) {
				it.next();
				n++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			b=false;
		}
		finally{
			LineIterator.closeQuietly(it);
		}
		if(!b)
			return false;
		names=new String[n];
		Integer[] aCase=new Integer[mCase];
		Integer[] aControl=new Integer[mControl];
		snps=new char[n][2];
		hCase.toArray(aCase);
		hControl.toArray(aControl);
		/*
		 * 根据fold将样本分成fold份
		 */
		Random ran=new Random();
		int[] rCase=sample(ran,mCase,mCase);
		int[] rControl=sample(ran,mControl,mControl);
		int[][] indexOfCaseInEachFold=new int[fold][];
		int[][] indexOfControlInEachFold=new int[fold][];
		for(int i=0;i<fold-1;i++){
			indexOfCaseInEachFold[i]=new int[mCase/fold];
			indexOfControlInEachFold[i]=new int[mControl/fold];
		}
		indexOfCaseInEachFold[fold-1]=new int[mCase-mCase/fold*(fold-1)];
		indexOfControlInEachFold[fold-1]=new int[mControl-mControl/fold*(fold-1)];
		for(int i=0,j=0,k=0;i<mCase&&j<fold;){
			if(k<indexOfCaseInEachFold[j].length){
				indexOfCaseInEachFold[j][k]=aCase[rCase[i]];
				i++;
				k++;
			}
			else{
				j++;
				k=0;
				//indexOfCaseInEachFold[j][k]=aCase[rCase[i]];
			}
		}
		for(int i=0,j=0,k=0;i<mControl&&j<fold;){
			if(k<indexOfControlInEachFold[j].length){
				indexOfControlInEachFold[j][k]=aControl[rControl[i]];
				i++;
				k++;
			}
			else{
				j++;
				k=0;
				//indexOfControlInEachFold[j][k]=aControl[rControl[i]];
			}
		}
		try{
			if(indexOfCaseInEachFold[fold-1].length%GenotypeFolds.SIZE_OF_LONG==0){
				sizeOfMemCase=indexOfCaseInEachFold[fold-1].length/GenotypeFolds.SIZE_OF_LONG;
				memCase=new long[n][fold][3][sizeOfMemCase];
			}
			else{
				sizeOfMemCase=indexOfCaseInEachFold[fold-1].length/GenotypeFolds.SIZE_OF_LONG+1;
				memCase=new long[n][fold][3][sizeOfMemCase];
			}
			if(indexOfControlInEachFold[fold-1].length%GenotypeFolds.SIZE_OF_LONG==0){
				sizeOfMemControl=indexOfControlInEachFold[fold-1].length/GenotypeFolds.SIZE_OF_LONG;
				memControl=new long[n][fold][3][sizeOfMemControl];
			}
			else{
				sizeOfMemControl=indexOfControlInEachFold[fold-1].length/GenotypeFolds.SIZE_OF_LONG+1;
				memControl=new long[n][fold][3][sizeOfMemControl];
			}
			it= FileUtils.lineIterator(new File(filenamePed), "UTF-8");
			for(int i=0;i<n;i++){
				String line=it.next();
				/*
				 * 获得每个基因的数目
				 */
				String[] strs=line.split("\t",4);
				names[i]=strs[1];
				String str=line.split("\t",5)[4];
				int c0=0;
				int c1=0;
				for(int j=0;j<str.length();j+=2){
					char c=str.charAt(j);
					if(c0==0){
						c0++;
						snps[i][0]=c;
					}
					else if(c==snps[i][0]){
						c0++;
					}
					else if(c1==0){
						c1++;
						snps[i][1]=c;
					}
					else if(c==snps[i][1]){
						c1++;
					}
					else{
						throw new Exception("the line "+(i+1)+" has more than 2 Allele");
					}
				}
				if(c0<c1){
					char tc=snps[i][0];
					snps[i][0]=snps[i][1];
					snps[i][1]=tc;
				}
				for(int j=0;j<fold;j++){
					for(int k=0;k<indexOfCaseInEachFold[j].length;k++){
						byte tb=0;
						if(str.charAt(indexOfCaseInEachFold[j][k]*4)==snps[i][1]){
							tb++;
						}
						if(str.charAt(indexOfCaseInEachFold[j][k]*4+2)==snps[i][1]){
							tb++;
						}
						GenotypeFolds.setData(memCase[i][j],k,tb);
					}
				}
				for(int j=0;j<fold;j++){
					for(int k=0;k<indexOfControlInEachFold[j].length;k++){
						byte tb=0;
						if(str.charAt(indexOfControlInEachFold[j][k]*4)==snps[i][1]){
							tb++;
						}
						if(str.charAt(indexOfControlInEachFold[j][k]*4+2)==snps[i][1]){
							tb++;
						}
						GenotypeFolds.setData(memControl[i][j],k,tb);
					}
				}
			}	
		}
		catch (Exception e) {
			e.printStackTrace();
			b=false;
		}
		finally{
		   LineIterator.closeQuietly(it);
		}
		return b;
	}
	private static final int bitCount(long i)
	{
		i = i - ((i >> 1) & 0x5555555555555555l);
		i = (i & 0x3333333333333333l) + ((i >> 2) & 0x3333333333333333l);
		i = (i + (i >> 4)) & 0x0f0f0f0f0f0f0f0fl;
		i = i + (i >> 8);
		i = i + (i >> 16);
		i = i + (i >> 32);
		return (int)i & 0x7f;
	}
	/*
	 * 将memCase的第i个snp，第j个样本的值设为tb
	 * 这是为了模仿数组的建立过程
	 * 内部的实现要将j映射到一个bit上，tb用于指定memCase的第二个维度
	 */
	private static final void setData(long[][] mem,int indexSample, byte tb) {
		//计算出j所对应的样本的位置
		int indexOfVector=indexSample/SIZE_OF_LONG;
		int indexInVector=indexSample-indexOfVector*SIZE_OF_LONG;
		mem[tb][indexOfVector]|=MASK_ONE>>>indexInVector;
		
	}
	private static final int popcount(long i ){
	    return BITS[(int)(i&0xFFFF)] + BITS[(int)((i>>>16)&0xFFFF)] + BITS[(int)((i>>>32)&0xFFFF)] + BITS[(int)((i>>>48)&0xFFFF)];
	}
	private static final int popcount(long[] a,int l){
		int r=0;
		for(int i=0;i<l;i++){
			r=r+popcount(a[i]);
		}
		return r;
	}
	
	private final static void AND_VEC(long[] a,long[] b,int l){
		for(int i=0;i<l;i++){
			a[i]=a[i]&b[i];
		}
	}
	/*
	 * 输出指定snp的列联表
	 * 第一维是flod
	 * 第二维是y，是否患病
	 * 第三维是snp的组合
	 */
	public final int[][][] getTable(int[] snps){
		int kk=(int)Math.pow(3,snps.length);
		int[][][] c=new int[fold][2][kk];
		long[][][] a=new long[fold][kk][];
		for(int iF=0;iF<fold;iF++){
			for(int i=0;i<kk;){
				a[iF][i++]=Arrays.copyOf(memControl[snps[0]][iF][0],sizeOfMemControl);
				a[iF][i++]=Arrays.copyOf(memControl[snps[0]][iF][1],sizeOfMemControl);
				a[iF][i++]=Arrays.copyOf(memControl[snps[0]][iF][2],sizeOfMemControl);
			}
		}
		for(int iF=0;iF<fold;iF++){
			for(int j=1,step=3;j<snps.length;j++,step*=3){
				int i=0;
				while(i<kk){
					long[] b=memControl[snps[j]][iF][0];
					for(int k=0;k<step;k++){
						AND_VEC(a[iF][i++],b,sizeOfMemControl);
					}
					b=memControl[snps[j]][iF][1];
					for(int k=0;k<step;k++){
						AND_VEC(a[iF][i++],b,sizeOfMemControl);
					}
					b=memControl[snps[j]][iF][2];
					for(int k=0;k<step;k++){
						AND_VEC(a[iF][i++],b,sizeOfMemControl);
					}
				}
			}
		}
		for(int iF=0;iF<fold;iF++){
			for(int i=0;i<kk;i++){
				c[iF][0][i]=popcount(a[iF][i],sizeOfMemControl);
			}
		}
		for(int iF=0;iF<fold;iF++){
			for(int i=0;i<kk;){
				a[iF][i++]=Arrays.copyOf(memCase[snps[0]][iF][0],sizeOfMemCase);
				a[iF][i++]=Arrays.copyOf(memCase[snps[0]][iF][1],sizeOfMemCase);
				a[iF][i++]=Arrays.copyOf(memCase[snps[0]][iF][2],sizeOfMemCase);
			}
		}
		for(int iF=0;iF<fold;iF++){
			for(int j=1,step=3;j<snps.length;j++,step*=3){
				int i=0;
				while(i<kk){
					long[] b=memCase[snps[j]][iF][0];
					for(int k=0;k<step;k++){
						AND_VEC(a[iF][i++],b,sizeOfMemCase);
					}
					b=memCase[snps[j]][iF][1];
					for(int k=0;k<step;k++){
						AND_VEC(a[iF][i++],b,sizeOfMemCase);
					}
					b=memCase[snps[j]][iF][2];
					for(int k=0;k<step;k++){
						AND_VEC(a[iF][i++],b,sizeOfMemCase);
					}
				}
			}
		}
		for(int iF=0;iF<fold;iF++){
			for(int i=0;i<kk;i++){
				c[iF][1][i]=popcount(a[iF][i],sizeOfMemCase);
			}
		}
		return c;
	}
	private final boolean load1(){
		LineIterator it=null;
		boolean b=true;
		HashSet<Integer> hCase=new HashSet<Integer>();
		HashSet<Integer> hControl=new HashSet<Integer>();
		try{
			it= FileUtils.lineIterator(new File(filename), "UTF-8");
			while (it.hasNext()) {
				String line = it.nextLine().replace(",","\t");
				if(names==null){
					//the first line is names of snps
					names=line.split("\t");
					n=names.length-1;
				}
				else{
					char c=line.charAt(line.length()-1);
					if(c=='1'){
						hCase.add(mCase+mControl);
						mCase++;
					}
					else if(c=='0'){
						hControl.add(mCase+mControl);
						mControl++;
					}
					else{
						throw new Exception("the last column in the file must be 0(control sample) or 1(case sample)");
					}
				}
			}
			//System.out.println(String.format("mCase : %d , mControl : %d , n : %d",mCase,mControl,n));			
		}
		catch (Exception e) {
			e.printStackTrace();
			b=false;
		}
		finally{
			LineIterator.closeQuietly(it);
		}
		if(!b)
			return false;
		Integer[] aCase=new Integer[mCase];
		Integer[] aControl=new Integer[mControl];
		hCase.toArray(aCase);
		hControl.toArray(aControl);
		Random ran=new Random();
		int[] rCase=sample(ran,mCase,mCase);
		int[] rControl=sample(ran,mControl,mControl);
		int[][] indexOfCaseInEachFold=new int[fold][];
		int[][] indexOfControlInEachFold=new int[fold][];
		for(int i=0;i<fold-1;i++){
			indexOfCaseInEachFold[i]=new int[mCase/fold];
			indexOfControlInEachFold[i]=new int[mControl/fold];
		}
		indexOfCaseInEachFold[fold-1]=new int[mCase-mCase/fold*(fold-1)];
		indexOfControlInEachFold[fold-1]=new int[mControl-mControl/fold*(fold-1)];
		for(int i=0,j=0,k=0;i<mCase&&j<fold;){
			if(k<indexOfCaseInEachFold[j].length){
				indexOfCaseInEachFold[j][k]=aCase[rCase[i]];
				i++;
				k++;
			}
			else{
				j++;
				k=0;
				//indexOfCaseInEachFold[j][k]=aCase[rCase[i]];
			}
		}
		for(int i=0,j=0,k=0;i<mControl&&j<fold;){
			if(k<indexOfControlInEachFold[j].length){
				indexOfControlInEachFold[j][k]=aControl[rControl[i]];
				i++;
				k++;
			}
			else{
				j++;
				k=0;
				//indexOfControlInEachFold[j][k]=aControl[rControl[i]];
			}
		}
		HashMap<Integer,int[]> hCa=new HashMap<Integer,int[]>();
		HashMap<Integer,int[]> hCo=new HashMap<Integer,int[]>();
		for(int iF=0;iF<fold;iF++){
			for(int j=0;j<indexOfCaseInEachFold[iF].length;j++){
				hCa.put(indexOfCaseInEachFold[iF][j],new int[]{iF,j});
			}
		}
		for(int iF=0;iF<fold;iF++){
			for(int j=0;j<indexOfControlInEachFold[iF].length;j++){
				hCo.put(indexOfControlInEachFold[iF][j],new int[]{iF,j});
			}
		}
		try{
			if(indexOfCaseInEachFold[fold-1].length%GenotypeFolds.SIZE_OF_LONG==0){
				sizeOfMemCase=indexOfCaseInEachFold[fold-1].length/GenotypeFolds.SIZE_OF_LONG;
				memCase=new long[n][fold][3][sizeOfMemCase];
			}
			else{
				sizeOfMemCase=indexOfCaseInEachFold[fold-1].length/GenotypeFolds.SIZE_OF_LONG+1;
				memCase=new long[n][fold][3][sizeOfMemCase];
			}
			if(indexOfControlInEachFold[fold-1].length%GenotypeFolds.SIZE_OF_LONG==0){
				sizeOfMemControl=indexOfControlInEachFold[fold-1].length/GenotypeFolds.SIZE_OF_LONG;
				memControl=new long[n][fold][3][sizeOfMemControl];
			}
			else{
				sizeOfMemControl=indexOfControlInEachFold[fold-1].length/GenotypeFolds.SIZE_OF_LONG+1;
				memControl=new long[n][fold][3][sizeOfMemControl];
			}
			it= FileUtils.lineIterator(new File(filename), "UTF-8");
			it.next();
			int i0=0;
			while(it.hasNext()){
				String line=it.next();
				if(line.charAt(line.length()-1)=='0'){
					for(int j=0;j<n;j++){
						byte tb=(byte)(line.charAt(j*2)-'0');
						int[] t=hCo.get(i0);
						GenotypeFolds.setData(memControl[j][t[0]],t[1], tb);
						if(tb>2){
							throw new Exception("the element in the file must be 0,1 or 2");
						}
					}
					i0++;
				}
				else{
					for(int j=0;j<n;j++){
						//System.out.println(i1+"\t"+j);
						byte tb=(byte)(line.charAt(j*2)-'0');
						int[] t=hCa.get(i0);
						GenotypeFolds.setData(memCase[j][t[0]],t[1],tb);
						if(tb>2){
							throw new Exception("the element in the file must be 0,1 or 2");
						}
					}
					i0++;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			b=false;
		}
		finally{
		   LineIterator.closeQuietly(it);
		}
		return b;
	}
	public final int getNumOfSnps() {
		// TODO Auto-generated method stub
		return n;
	}
	public final int getNumOfSamples() {
		// TODO Auto-generated method stub
		return mCase+mControl;
	}
	public final String[] getNames() {
		// TODO Auto-generated method stub
		return names;
	}
	public final int getNumOfCases() {
		// TODO Auto-generated method stub
		return mCase;
	}
	public final int getNumOfControls(){
		return mControl;
	}
	public final void exchange(int[] indexes, int[] ranPosi) {
		// TODO Auto-generated method stub
		for(int i=0;i<indexes.length;i++){
			for(int iF=0;iF<fold;iF++){
				long[][][] t=memCase[indexes[i]];
				memCase[indexes[i]]=memCase[ranPosi[i]];
				memCase[ranPosi[i]]=t;
				t=memControl[indexes[i]];
				memControl[indexes[i]]=memControl[ranPosi[i]];
				memControl[ranPosi[i]]=t;
			}
		}
	}
	public final String getFilename() {
		// TODO Auto-generated method stub
		return filename;
	}
	/*
	 * 非重复从n里抽出k个值
	 */
	private final int[] sample(Random ran,int n,int k){
		int[] r=new int[k];
		boolean[] flag=new boolean[n];
		//Arrays.fill(flag, false);
		for(int i=0;i<k;i++){
			int ir=ran.nextInt(n-i);
			for(int j=0;j<=ir;j++){
				if(flag[j]){
					ir++;
				}
			}
			r[i]=ir;
			flag[ir]=true;
		}
		return r;
	}
}
