package wadl.conversion.core.models

import scala.xml._

case class Application {

	var resourcesCollection:List[Resources] = List()
	
	override def toString = {
		
		val xml = <application>
					{
					 for(ctr <- 0 until resourcesCollection.size) yield
					 	Unparsed(resourcesCollection(ctr).toString)
					}			 
							 
				  </application>
		
		val asString = xml.toString
		val asNode = XML.loadString(asString)
		val printer = new PrettyPrinter(10,20)
		val sb = new StringBuilder
		printer.format(asNode,sb)
		sb.toString
		
	}
		
	
}