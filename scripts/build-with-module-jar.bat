cd ..\
SET root=%CD%
SET scripts=%root%\scripts
SET module=info.kgeorgiy.ja.strelnikov.implementor
SET output=%root%\out\production
cd ..\
SET sources=%CD%\java-advanced-2021
javac -p "%sources%\artifacts;%sources%\lib" --module "%module%" --module-source-path "%root%\modules" -d "%output%"
cd "%output%"
jar -c -f "%root%\result.jar" -m "%scripts%\MANIFEST.MF" -p "%sources%\artifacts" -C "%output%\%module%" .
cd "%scripts%"