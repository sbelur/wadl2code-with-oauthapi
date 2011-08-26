package wadl.conversion.util

import java.io._

object WADLInput {

	def readSource(in:File):String = {
	  val reader = new BufferedReader(new FileReader(in))
	  var buffer = new StringBuilder
	  var line:String = reader.readLine
	  while(line != null){
	 	  buffer.append(line)
	 	  line = reader.readLine
	  }
	  buffer.toString
	}
	
	
	def main(args:Array[String]){
		val input = readSource(
				new File("input\\twitter-wadl.xml"))
				
	}
}