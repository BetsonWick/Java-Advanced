@ECHO off
SET ROOT=%cd%
cd %~dp0
cd ..\..\..\..\..\..\
SET LIB=../lib
javac -cp .;%LIB%/* -encoding UTF-8 info\kgeorgiy\ja\strelnikov\i18n\utils\*.java
javac -cp .;%LIB%/* -encoding UTF-8 info\kgeorgiy\ja\strelnikov\i18n\output\*.java
javac -cp .;%LIB%/* -encoding UTF-8 info\kgeorgiy\ja\strelnikov\i18n\TextStatistics.java
java info.kgeorgiy.ja.strelnikov.i18n.TextStatistics ru ru-RU info\kgeorgiy\ja\strelnikov\i18n\in.txt info\kgeorgiy\ja\strelnikov\i18n\out.txt
cd %ROOT%
@ECHO on