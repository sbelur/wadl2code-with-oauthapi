package wadl.conversion.util

import wadl.conversion.util.builders._
import wadl.conversion.core.models._
import wadl.conversion.classgen._



//import wadl.generated.api.facebook.methods._


object Generator {

	def main(args:Array[String]){
		
		var provider = args(0)
		if(null == provider) throw new IllegalArgumentException("Please provider provider name as first argument")
		GenClass.provider  = provider
		val content = WADLInput.readSource(new java.io.File("input/twitter-wadl.xml"))
		val asNode = scala.xml.XML.loadString(content)
		val appBuilder =  AppConfiguration.appBuilder
		val wadlApp = appBuilder.buildApp(asNode)
		
		
		wadlApp.resourcesCollection.foreach({
			
			resourcesEl => generateClassFiles(resourcesEl) 	
			
		});
		
		println("done generating")
		JarCreator.createJar(provider + "-api-from-wadl.jar")
		/*val getuser = new Getuser
		getuser.setUser("4000")
		println(getuser.invoke)*/
		
		
	}
	
	private def generateClassFiles(el:Resources){
		val resourceScanner = new ResourceClassGenerator(el.basePath)
		resourceScanner.scanResourceList(el.resourceCollection)
	}
	
}