package wadl.conversion.core.models

case class Request {

	private var params: List[Parameter] = Nil
	
	def addParam(p:Parameter){
		p :: params
	}
	
	def setParams(pList:List[Parameter]){
		params ++= (pList) 
	}
	
	def parameters = params
	
}