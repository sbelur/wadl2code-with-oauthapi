package wadl.conversion.classgen

import wadl.conversion.core.models._
import javassist._

class ResourceClassGenerator(val base:String) {

	val methodClassGenerator = new MethodClassGenerator
	
	def scanResourceList(resourceList:List[Resource]){
		
		createMethodBaseClass()
		createConstantsClass()
		resourceList.foreach({
			r => generateClassesForResourceMethods(r)
		})
		
	}
	private def generateClassesForResourceMethods(r:Resource) = {
		
		methodClassGenerator.setContext(r,base)
		methodClassGenerator .scanMethodList()
		
		
	}
	
	private def createConstantsClass()
	{
	  var pkg = GenClass.prefix + "methods"
	  var mclass:CtClass = GenClass.createClass("MethodConstants" , pkg,null)
	  
	  val oAUTH1Field:CtField  = CtField.make("""public static final java.lang.String OAUTH1 = "oauth1";""",mclass)
	  mclass.addField(oAUTH1Field)
	  
	  val basicAUTHField:CtField  = CtField.make("""public static final java.lang.String BASIC_AUTH = "basicauth";""",mclass)
	  mclass.addField(basicAUTHField)
	  
	  val xmlFormat:CtField  = CtField.make("""public static final java.lang.String XML_FORMAT = "xml";""",mclass)
	  mclass.addField(xmlFormat)
	  
	  val jsonFormat:CtField  = CtField.make("""public static final java.lang.String JSON_FORMAT = "json";""",mclass)
	  mclass.addField(jsonFormat)
	  
	  GenClass.writeClass(mclass)
		
	}
	
	private def createMethodBaseClass():CtClass = {
		
		var pkg = GenClass.prefix + "methods"
		var mclass:CtClass = GenClass.createClass("ResourceMethodBase" , pkg,null)
		
		val appNameField:CtField  = CtField.make("""private java.lang.String applicationName = null;""",mclass)
		mclass.addField(appNameField)
		
		val getAppNameMethod:CtMethod = CtNewMethod.make("public java.lang.String getApplicationName(){return applicationName;}",mclass)
		mclass.addMethod(getAppNameMethod)
		val setAppNameMethod:CtMethod = CtNewMethod.make("public void setApplicationName(String name){applicationName = name;}",mclass)
		mclass.addMethod(setAppNameMethod)
		
		
		val skeyField:CtField  = CtField.make("""private java.lang.String smartKey = null;""",mclass)
		mclass.addField(skeyField)
		
		val getSmartKeyMethod:CtMethod = CtNewMethod.make("public java.lang.String getSmartKey(){return smartKey;}",mclass)
		mclass.addMethod(getSmartKeyMethod)
		val setSmartKeyMethod:CtMethod = CtNewMethod.make("public void setSmartKey(String key){smartKey = key;}",mclass)
		mclass.addMethod(setSmartKeyMethod)
		
		
		val usedMethodsField:CtField  = CtField.make("""private java.util.List usedMethods = new java.util.ArrayList();""",mclass)
		mclass.addField(usedMethodsField)
		
		val testHeadersField:CtField  = CtField.make("""private java.util.Map testHeaders = new java.util.HashMap();""",mclass)
		mclass.addField(testHeadersField)
		
		val bodyField:CtField  = CtField.make("""private java.lang.String testBody = null;""",mclass)
		mclass.addField(bodyField)
		
		val getbodyContentToBeSentAsIs:CtMethod = CtNewMethod.make("public java.lang.String getBodyContentToBeSentAsIs(){return testBody;}",mclass)
		mclass.addMethod(getbodyContentToBeSentAsIs)
		val setbodyContentToBeSentAsIs:CtMethod = CtNewMethod.make("public void setBodyContentToBeSentAsIs(String content){testBody = content;}",mclass)
		mclass.addMethod(setbodyContentToBeSentAsIs)
		
		
		val getUsedMethods:CtMethod = CtNewMethod.make("public java.util.List getUsedMethods(){return usedMethods;}",mclass)
		mclass.addMethod(getUsedMethods)
		val addTestHeader:CtMethod = CtNewMethod.make("public void addTestHeader(String name,String value){testHeaders.put(name.toLowerCase(),value);}",mclass)
		mclass.addMethod(addTestHeader)
		val getAllTestHeaders:CtMethod = CtNewMethod.make("public java.util.Map getAllTestHeaders(){return testHeaders;}",mclass)
		mclass.addMethod(getAllTestHeaders)
		val getTestHeader:CtMethod = CtNewMethod.make("public String getTestHeader(String name){return (String)testHeaders.get(name);}",mclass)
		mclass.addMethod(getTestHeader)
		
		GenClass.writeClass(mclass)
		
		mclass
	}
	
	
	
	
	
}