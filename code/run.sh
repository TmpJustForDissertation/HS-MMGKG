##HS-MMGKG的运行示例
echo "以下开始HS-MMGKG的运行示例"
cd /code/HS-MMGKG
bash build.sh
java -cp target/hs-mmgkg.jar:target/lib/commons-io-2.5.jar:target/lib/commons-math3-3.6.1.jar main.Command -file /data/example.txt -order 2 -hms 5 -hmcr 0.8 -par 0.4 -tmax 400000 -fold 5 -ishow 4000 -nshow 4 -pvalue 0.05 -nsolution 40
java -cp target/hs-mmgkg.jar:target/lib/commons-io-2.5.jar:target/lib/commons-math3-3.6.1.jar main.Command -file /data/example.tped -order 2 -hms 5 -hmcr 0.8 -par 0.4 -tmax 400000 -fold 5 -ishow 4000 -nshow 4 -pvalue 0.05 -nsolution 40

