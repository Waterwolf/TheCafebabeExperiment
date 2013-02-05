TheCafebabeExperiment
=====================

Bunch of Java bytecode analyzing and processing tools written in mostly Scala.

#Module division
Each module is divided into their own IDEA module.

##CommandTools
CommandTools hosts a command line argument parser that allows quickly searching for bytecode patterns in specified files.

##Utilities
Utilities consists of commonly used classes such as CafeGlob (custom glob syntax especially for bytecode processing) and ClassPackage.


#Compiling the project

To compile the project, you will need scala and ASM-all libraries.

#Using CommandTools

Example usage:

    java -jar commandtools.jar findpattern "GETSTATIC{?, out, ?} LDC INVOKEVIRTUAL{?, println, ?}"

This would search current folder (non-recursively unless -r flag is specified) for class files containing given bytecode pattern. Bytecode pattern uses custom CafeGlob syntax, which you can find from https://github.com/Waterwolf/TheCafebabeExperiment/blob/master/Utilities/src/st/icemi/cbe/util/bytecode/matchers/cafeglob/CafeGlob.scala at the moment.