package wadl.conversion.util.builders

import scala.xml._
import wadl.conversion.core.models._
import wadl.conversion.util._

trait ResourceBuilderComponent {
 
	self: ParameterBuilderComponent with MethodBuilderComponent =>
	
	val resourceBuilder:ResourceBuilder
	
	class ResourceBuilder {
	
		def buildResource(node:Node):Option[Resource] = {
			
			val res = Resource(node.attribute("path").get.toList.head.toString)
			val paramNodeSeq = NodeExtractor.getNamedNodes(node,"param")
			res.params ++= ((for {
				aParamNode <- paramNodeSeq
				par <- parameterBuilder.buildParams(aParamNode)
			} yield par).toList)
			
			val methodNodeSeq = NodeExtractor.getNamedNodes(node,"method")
			res.methods ++= ((for {
				aMethodNode <- methodNodeSeq
				met <- methodBuilder.buildMethods(aMethodNode)
			} yield met).toList)
			
			Some(res)
		}
		
	}	
	
}