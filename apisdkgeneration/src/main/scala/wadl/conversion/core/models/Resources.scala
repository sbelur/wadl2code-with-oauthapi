package wadl.conversion.core.models

import scala.xml._

case class Resources {
 
	var basePath:String = ""
	var resourceCollection:List[Resource] = Nil	
	
	def addResource(r:Resource){
		r :: resourceCollection
	}
	
	def getResourceElements = resourceCollection

	override def toString = {
		val xml = <resources base={basePath}>
					{
					 for(ctr <- 0 until resourceCollection.size) yield
					 	Unparsed(resourceCollection(ctr).toString)
					} 	
				  </resources>
		xml.toString	
	}
		
	
}