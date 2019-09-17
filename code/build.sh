#! /bin/bash
# 此处应该是项目文件夹所在目录
cur_dir=$(pwd)
echo $cur_dir


function compile(){
        # 记录项目的根目录所在路径
        project_name=hs-mmgkg
        project_dir=/code/HS-MMGKG
        project_src=$project_dir/src # 源代码所在根目录
        project_lib=$project_dir/lib #依赖jar所在目录
        project_class=$project_dir/target # 编译后class文件存放根目录
        echo "begin compile"
        echo $project_dir
        echo $project_src
        echo $project_lib
        echo $project_class

        # src目录下的所有java文件的名称存入到 项目根目录/src/sources.list文件中 先检查是否存在，如存在先删除 
        rm -rf $project_src/sources.list
        # $project_src -name  '*.java'表示在 $project_src目录下以及子目录下寻找以.java目录结尾的 文件 并存放到source.list临时文件
        find $project_src  -name '*.java' > $project_src/sources.list
        echo "java source file >>>"
        cat $project_src/sources.list

        # 构建存放编译好的class文件的基目录,先删除目录

        rm -rf $project_class
        mkdir $project_class

        # 组装cp参数
        # 将所有的jar文件绝对路径记录下来到lib.list文件中
        rm -rf $project_lib/lib.list
        find $project_lib  -name '*.jar' > $project_lib/lib.list
        # 将当前目录.添加进去
        cpvar=.:
        # 一行一行读取lib.list文件并去每行文件路径最终的文件名 ${line##*/}
        while read line
        do
            echo $line
            cpvar=${cpvar}${project_lib}"/"${line##*/}":"
            echo $cpvar
        done < $project_lib/lib.list

        echo "print cpvar "
        echo $cpvar
        # 删除这个中间文件
        rm -rf $project_lib/lib.list
        # 截取cpvar最后一个字符：
        # 获取cpvar字符串长度
        length=${#cpvar}-1
        # 取 0 - length 长度的字符串
        cpvar=${cpvar:0:length}
        echo $cpvar
         # 批量编译java文件 
        # 编码：-encoding utf-8
        # 依赖库以冒号:隔开 
        # -sourcepath 参数指定源码目录跟目录， @$project_src/sources.list 指定源码文件名
        javac -d $project_class -encoding UTF-8 -cp $cpvar  -g -sourcepath $project_src @$project_src/sources.list

        # 删除 sources.list零时文件
        rm -rf $project_src/sources.list

        #删除存在的jar 若编译过的话
        # rm $qddemo/qddemo.jar   
        cd $project_class
        jar -cvfm $project_class/${project_name}.jar $project_dir/MANIFEST.MF *
        chmod a+x $project_class/${project_name}.jar

        echo "将依赖包从"${project_lib}"复制到"${project_class}/lib"目录下. "
        # 将依赖jar包从$project_lib 目录 复制到 $project_target/lib目录下
        cp -r  $project_lib $project_class/lib
}
compile
exit 0
