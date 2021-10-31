cd ../../
root=${PWD}
impl=${PWD}/java-advanced/java-solutions/info/kgeorgiy/ja/strelnikov/implementor/
link=https://docs.oracle.com/en/java/javase/11/docs/api/
cd ${impl}
javadoc.exe -private -link ${link} -splitindex -author -d ./javadoc -p "${root}/java-advanced-2021/artifacts" --add-modules "info.kgeorgiy.java.advanced.implementor" "${root}/java-advanced/java-solutions/info/kgeorgiy/ja/strelnikov/implementor/Implementor.java"
cd ${root}/java-advanced/scripts