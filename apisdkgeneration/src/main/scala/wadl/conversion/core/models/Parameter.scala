package wadl.conversion.core.models

import scala.xml._

case class Parameter(name:String,paramType:String,style:String) {

	var required = true
	var defaultResponseType = "json"
	
	
	
	var paramOptions:List[String] = List[String]()
	
	override def toString = {
		
		val xml =  <param name={name}  style={style}  required={required.toString}  default={defaultResponseType}/>
		xml.toString
			
		
	}
		
		
		
	
	
	
}

object ParameterConstants {	
		val NO_DEFAULT_VALUE="noDefaultValue"
}