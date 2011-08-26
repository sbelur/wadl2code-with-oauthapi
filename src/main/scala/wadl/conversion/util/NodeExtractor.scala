package wadl.conversion.util


import wadl.conversion.core._

object NodeExtractor {

	def getNamedNodes(container:scala.xml.Node,child:String)={
	   container \ child
	}
}