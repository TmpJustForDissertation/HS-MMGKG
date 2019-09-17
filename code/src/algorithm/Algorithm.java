package algorithm;
import java.io.Serializable;
public interface Algorithm extends Serializable{
	/*
	 * -1.不知道
	 * 0.创建了，宣告启动
	 * 1.运行中
	 * 2.宣告停止
	 * 3.停止了
	 * 4.结果集已经更新了
	 * 5.算法运行完毕，但未停止
	 */
	public static final int STATUS_CREATED=0;
	public static final int STATUS_RUNNING=1;
	public static final int STATUS_STOP_CREATED=2;
	public static final int STATUS_STOPPED=3;
	public static final int STATUS_RESULT_UPDATED=4;
	public static final int STATUS_OVER_NOT_STOP=5;
	/*
	 * 这个接口用于封装一个方法
	 * ，这个方法可以查找在这个数据库中所有和疾病相关的snp组合
	 * ，可能是1阶，2阶，3阶的方法
	 * ，也可能是穷举或者AI的方法。
	 */
	//public String giveMeMessage();
	public void fit();
	public String getAlgorithmDescription();
	/*
	public void fillDialog(DialogAlgorithmDetail dialog);
	public void releaseDialog();
	public ReentrantLock getLockOfDialog();
	*/
	public int getStatus();
	public void setStatus(int status);
	public void fillObject(Object o,int type);
	public data.Solution[] getSolutions(int n);
	public data.Criteria run();
}
