package wadl.conversion.util.builders

import scala.xml._
import wadl.conversion.core.models._

trait ResourcesBuilderComponent {
 
	self: ResourceBuilderComponent =>
	
	val resourcesBuilder:ResourcesBuilder
	
	class ResourcesBuilder {
	
		def buildResources(node:Elem) = {
			println("building resources from input"+node)
		}
		
	}	
	
}