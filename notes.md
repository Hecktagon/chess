# **Hello world:**

## ***How to do Hello World in Java:***

In Java, all code is defined in a class that contains properties and methods.   
Each class is stored in a file with a filename that is the same as the class, and has a .java extension.
If your class has a method name main, then it can act as the starting point for your program.  

```
public class HelloWorld   
    public static void main(String[] args) {  
        System.out.println("Hello World!");  
    }  
}
```

Then, to run java code you first compile it into *Java ByteCode* using:  

`➜ javac HelloWorld.java`  

then run the compiled code using:  

`➜ java HelloWorld`

## ***More Compiling Info***

You can also compile java files into a separate folder, and compile multiple files at once using the syntax:  

`➜ javac -d bin File1.java File2.java File3.java`  

This will compile all 3 files into a "bin" folder in your current working directory.  

Then, to run your compiled files, you would use the -cp "Classpath" tag to tell java where to look:  

`➜ java -cp bin File2`  

This tells java to look for a "bin" directory in our current directory, and then run the "main" function
within the File2.class compiled file.





