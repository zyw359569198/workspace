@echo off
rem 不显示执行过程
C:
rem 切换至指定盘符
cd C:\MRGJZServer\wwwroot\gcld.3.1\gcld
rem 进入指定文件夹
for /f "tokens=* delims=" %%i in ('dir /b/s *.class') do (
rem 查找目标文件夹及子文件夹内的所有zip后缀的文件
rename "%%i" "*.java"
rem 将所有zip后缀的文件更改为exe后缀
)
exit
rem 退出