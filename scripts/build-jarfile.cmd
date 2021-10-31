cd ..\
SET root=%CD%
cd ..\
set pc=%CD%\java-advanced-2021
SET impl=%root%\java-solutions\info\kgeorgiy\ja\strelnikov\implementor\
SET package=%pc%\modules\info.kgeorgiy.java.advanced.implementor\info\kgeorgiy\java\advanced\implementor
javac -d %impl% %impl%\Implementor.java %package%\Impler.java %package%\JarImpler.java %package%\ImplerException.java
cd %impl%
jar -cvfe implementor.jar info.kgeorgiy.ja.strelnikov.implementor.Implementor info\kgeorgiy\ja\strelnikov\implementor\*  info\kgeorgiy\java\advanced\implementor\*
cd %root%\scripts
