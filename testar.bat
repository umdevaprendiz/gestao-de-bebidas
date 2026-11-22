@echo off
rem Compila e executa os testes automaticos.
cd /d "%~dp0"
chcp 65001 >nul

if not exist out mkdir out
javac -encoding UTF-8 -d out -sourcepath src src\teste\TestesBasicos.java
if errorlevel 1 (
    echo.
    echo Erro na compilacao. Corrija os erros acima e tente de novo.
    exit /b 1
)

java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out teste.TestesBasicos
