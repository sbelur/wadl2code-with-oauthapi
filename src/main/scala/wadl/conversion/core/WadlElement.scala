package wadl.conversion.core

sealed trait WadlElement {
   def name
   
   override def toString = "<Element>"+ name + "</Element>"
}


case class Application extends WadlElement {

  def name(): Unit = { "Application" }

}


case class Resources extends WadlElement {

  def name(): Unit = { "Resources" }

}

case class Resource extends WadlElement {

  def name(): Unit = { "Resource" }

}

case class Parameter extends WadlElement {

  def name(): Unit = { "Parameter" }

}

case class Method extends WadlElement {

  def name(): Unit = { "Method" }

}