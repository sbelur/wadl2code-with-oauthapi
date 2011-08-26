package wadl.conversion.classgen

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip._
import java.util.jar._
import java.io._
import org.apache.commons.io._


object JarCreator {
  
  
		
		def createJar(name:String){
		   
		    Runtime.getRuntime().exec("jar -cvf "+name+" wadl/*")
		}
		
  
  
		
		
		
		
		

}