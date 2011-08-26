package wadl.conversion.util.builders

import scala.xml._
import wadl.conversion.core.models._
import wadl.conversion.util._


trait MethodBuilderComponent {

	self: ParameterBuilderComponent =>
	
	val methodBuilder: MethodBuilder
	
	
	
	class  MethodBuilder {
		def buildMethods(node:Node):Option[Method] = {
			val attrMap = node.attributes.asAttrMap
			val metName = attrMap("name")
			val metId = attrMap("id")
			val met = Method(metId,HttpMethod.values.find(_.toString == metName).getOrElse(HttpMethod.GET))
			val reqNodes = NodeExtractor.getNamedNodes(node,"request")
			if(reqNodes.length != 0){
			   reqNodes.foreach({
			  	 n => addRequestNode(n,met)
			   })
			}
			Some(met)
		}
		
		private def addRequestNode(n:Node,m:Method){
			val paramNodeSeq = NodeExtractor.getNamedNodes(n,"param")
			m.request .setParams((for {
				aParamNode <- paramNodeSeq
				par <- parameterBuilder.buildParams(aParamNode)
			} yield par).toList)
		}
	}
	
}