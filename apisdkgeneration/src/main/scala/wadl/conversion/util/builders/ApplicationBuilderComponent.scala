package wadl.conversion.util.builders

import scala.xml._
import wadl.conversion.core.models._
import wadl.conversion.util._
import wadl.conversion.util.builders._
import wadl.conversion.util.NodeExtractor._

trait ApplicationBuilderComponent{

	self : ResourcesBuilderComponent =>
	
	val appBuilder:ApplicationBuilder
	
	class ApplicationBuilder {
		
	    def buildApp(node:Elem):Application = {
	      val wadlApp = Application() 
	      val resourcesCollection:NodeSeq = getNamedNodes(node,"resources")
	      wadlApp.resourcesCollection  = (for {
	    	 aNode <- resourcesCollection
	    	 someResources <- processResourcesNode(aNode)
	      } yield someResources).toList
	      wadlApp
	    }
	    
	    def processResourcesNode(node:Node):Option[Resources] = {
	    	val basePath = node.attribute("base")
	    	val resourcesElement:Resources = Resources()
	    	resourcesElement.basePath  = basePath.get.toList.head.toString
	    	var resourceNodes:NodeSeq = getNamedNodes(node, "resource")
	    	resourcesElement.resourceCollection  = (for {
	    		n <- resourceNodes
	    		resEl <- AppConfiguration.resourceBuilder.buildResource(n)
	    	} yield resEl).toList
	    	Some(resourcesElement)
	    }
	 	
	}

}