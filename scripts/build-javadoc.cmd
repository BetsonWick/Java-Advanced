SET modules=..\..\java-advanced-2021\modules\info.kgeorgiy.java.advanced.implementor\info\kgeorgiy\java\advanced\implementor
SET pathI=..\java-solutions\info\kgeorgiy\ja\strelnikov\implementor\Implementor.java
SET doc=https://docs.oracle.com/en/java/javase/11/docs/api/
javadoc -private -author -version -link %doc% -d ..\javadoc %pathI% %modules%\Impler.java %modules%\JarImpler.java %modules%\ImplerException.java