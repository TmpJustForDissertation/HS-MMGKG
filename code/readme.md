# 简介
HS-MMGKG是一个在GWAS数据上检索上位性的算法，该软件是使用java来实现的，该软件是吉林大学计算机学院2016级博士孙立岩毕业设计的一部分，该网页上的所有文件和信息是孙立岩毕业论文的补充内容。
# 文件介绍
/code/HS-MMGKG/lib下有两个jar包，是HS-MMGKG编译和运行时的依赖jar包；<br>
/code/HS-MMGKG/src下是HS-MMGKG的全部源码；<br>
/code/HS-MMGKG/build.sh是编译HS-MMGKG的全部源码并构建jar包的脚本；<br>
/code/HS-MMGKG/MANIFEST.MF是构建HS-MMGKG的jar包时需要的配置文件；<br>
/code/HS-MMGKG/readme.md是说明文件；<br>
/code/run.sh是编译和执行HS-MMGKG的示例程序的脚本；<br>
# 编译方法
可以通过执行build.sh进行编译。<br>
# 运行方法
可以直接运行hs-mmgkg.jar来执行，它是一个图形交互界面，支持多线程。<br>
也可以通过命令行来执行，无图形界面：<br>
\#\#利用HS-MMGKG算法在模拟的GWAS数据上检索上位性。<br>
java -cp target/hs-mmgkg.jar:target/lib/commons-io-2.5.jar:target/lib/commons-math3-3.6.1.jar main.Command -file /data/example.txt -order 2 -hms 5 -hmcr 0.8 -par 0.4 -tmax 400000 -fold 5 -ishow 4000 -nshow 4 -pvalue 0.05 -nsolution 40<br>
\#\#利用HS-MMGKG算法在真实的GWAS数据上检索上位性。<br>
java -cp target/hs-mmgkg.jar:target/lib/commons-io-2.5.jar:target/lib/commons-math3-3.6.1.jar main.Command -file /data/example.tped -order 2 -hms 5 -hmcr 0.8 -par 0.4 -tmax 400000 -fold 5 -ishow 4000 -nshow 4 -pvalue 0.05 -nsolution 40<br>


# 参数列表

参数|默认值|描述
----|----|----
file|null|输入文件的路径，当其文件名的结尾为.tped或.tfam时，HS-MMGKG认为这是一个真实的GWAS数据，否则认为是一个模拟的GWAS数据，两种数据格式不同，可参照/data部分。
order|2|欲检索的上位性的阶数。
hms|5|对于每一个目标函数和声库的大小，因为HS-MMGKG使用了5个目标函数，所以和声库的总大小是5*5=25。
hmcr|0.8|和声算法参数。
par|0.4|和声算法参数。
tmax|-1|和声算法在运行多少代后，停止，-1表示，永不停止。
fold|5|MDR中所使用的交叉验证的折数。
ishow|4000|和声算法每运行多少次迭代，显示回显。
nshow|4|回显时，显示多少个和声。
pvalue|0.05|产生结果时，p-value的阈值。
nsolution|40|返回结果的数量，只返回最优的nsolution个和声作为结果。