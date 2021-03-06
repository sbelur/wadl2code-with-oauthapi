package wadl.conversion.util

import wadl.conversion.util.builders._
import wadl.conversion.core.models._
import wadl.conversion.classgen._



//import wadl.generated.api.facebook.methods._


object Generator {

	def main(args:Array[String]){
		
		var provider = args(0)
		if(null == provider) throw new IllegalArgumentException("Please provider provider name as first argument")
		var wadlpath = args(1)
		if(null == wadlpath) throw new IllegalArgumentException("Please provider provider wadlpath as second argument")
		GenClass.provider  = provider
		val content = WADLInput.readSource(new java.io.File(args(1)))
		val asNode = scala.xml.XML.loadString(content)
		val appBuilder =  AppConfiguration.appBuilder
		val wadlApp = appBuilder.buildApp(asNode)
		
		
		wadlApp.resourcesCollection.foreach({
			
			resourcesEl => generateClassFiles(resourcesEl) 	
			
		});
		
		println("done generating")
		JarCreator.createJar(provider + "-api-from-wadl.jar")
	
		
	}
	
	private def generateClassFiles(el:Resources){
		val resourceScanner = new ResourceClassGenerator(el.basePath)
		resourceScanner.scanResourceList(el.resourceCollection)
	}
	
}