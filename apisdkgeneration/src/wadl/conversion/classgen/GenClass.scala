package wadl.conversion.classgen

import javassist._

object GenClass {

	import ClassType._
	
	val prefix = "wadl.generated.api."
		
    var opDir:String = "generatedclasses" 		
    	
    var provider:String = _	
	
	def  createClass(className:String) = {
		val pool:ClassPool = ClassPool.getDefault();
		val cc:CtClass = pool.makeClass(prefix+provider+"."+className.toLowerCase+"."+className.capitalize);
		cc
	}
	
	def  createClass(className:String,pkg:String,superclz:CtClass=null) = {
		
		
		val pool:ClassPool = ClassPool.getDefault()
		var cc:CtClass = null
		var packageName:String = null
		if(pkg.endsWith(".")) packageName = pkg
		else packageName = pkg+"."
		if(superclz != null){
			cc = pool.makeClass(packageName+className.capitalize,superclz)
		}
		else
			cc = pool.makeClass(packageName+className.capitalize)
		cc
	}
	
	
	def writeClass(clazz:CtClass){
		clazz.writeFile(new java.io.File(".").getAbsolutePath);
	}
	
	
	
	
	
}