package wadl.conversion.core.models

import scala.xml._

case class Resource(path:String) {

	var params:List[Parameter] = List()
	var methods:List[Method] = List()
	
	override def toString = {
		val xml = <resource path={path}>
					{
						for(ctr <- 0  until params.size) yield
							Unparsed(params(ctr).toString)
						
					}
					{
						for(ctr <- 0  until methods.size) yield
							Unparsed(methods(ctr).toString)
						
					}
			      </resource>
		xml.toString
	}
	
}