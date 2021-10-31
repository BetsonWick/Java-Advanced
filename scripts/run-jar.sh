./build-with-module-jar.sh
cd ../
root=${PWD}
scripts=${root}/scripts
module=info.kgeorgiy.ja.strelnikov.implementor
output=${root}/out
cd ../
sources=${PWD}/java-advanced-2021
impl_module="info.kgeorgiy.java.advanced.implementor"
cd ${root}
java -p "${sources}/artifacts;${sources}/lib" --add-modules ${impl_module} -jar result.jar -jar "${impl_module}.Impler" "${output}/ImplerImpl.jar"
cd "${scripts}"