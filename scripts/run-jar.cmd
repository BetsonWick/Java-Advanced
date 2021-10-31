call build-with-module-jar.bat
cd ..\
SET root=%CD%
SET scripts=%root%\scripts
SET module=info.kgeorgiy.ja.strelnikov.implementor
SET output=%root%\out
cd ..\
SET sources=%CD%\java-advanced-2021
SET impl_module=info.kgeorgiy.java.advanced.implementor
cd %root%
java -p "%sources%\artifacts;%sources%\lib" --add-modules %impl_module% -jar result.jar -jar "%impl_module%.Impler" "%output%\ImplerImpl.jar"
cd "%scripts%"