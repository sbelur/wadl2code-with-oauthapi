package wadl.conversion.util.builders

import scala.xml._
import wadl.conversion.core.models._
import wadl.conversion.util._

trait ParameterBuilderComponent {
	
	val parameterBuilder:ParameterBuilder
	
	class ParameterBuilder {
	
		def buildParams(node:Node):Option[Parameter] = {
		
			val attrMap = node.attributes.asAttrMap
			val name = attrMap("name")
			val attrtype = attrMap("type")
			val style = attrMap("style")
			val default = attrMap.getOrElse("default",ParameterConstants.NO_DEFAULT_VALUE)
			val reqd = attrMap("required")
			val par = Parameter(name,attrtype,style)
			par.required = reqd.toBoolean
			par.defaultResponseType = default
			val optionNodes = NodeExtractor.getNamedNodes(node,"option")
			if(optionNodes.length != 0){
			   optionNodes.foreach({
			  	 n => addOptionalValue(n,par)
			   })
				
			}
			Some(par)
		}
		
		private def addOptionalValue(n:Node,p:Parameter){
			 p.paramOptions = (n.attributes.asAttrMap("value")) :: p.paramOptions
		}
	} 	
}