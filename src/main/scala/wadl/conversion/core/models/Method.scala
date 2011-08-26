package wadl.conversion.core.models

import wadl.conversion.util.HttpMethod._
import scala.xml._

case class Method(id:String,httpMethod:HttpMethodType) {
	
	val request = new Request();
	
	
	override def toString() = {
		
		val xml = <method id={id} nm={httpMethod.toString}>
					{
						for(val ctr <- 0 until request.parameters.size) yield
							 Unparsed(request.parameters(ctr).toString)
	
					}
				  </method>
		xml.toString
						
	}
	
}