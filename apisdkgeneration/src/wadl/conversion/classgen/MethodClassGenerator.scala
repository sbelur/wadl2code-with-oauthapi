package wadl.conversion.classgen

import javassist._
import wadl.conversion.core.models._


class MethodClassGenerator {
	
	private var parent:Resource = _
	private var basePath:String = _
		
	
	def setContext(res:Resource, basePath:String){
	 	parent = res
	 	this.basePath = basePath 
	 	
	}
	
	def unSetContext(){
	 	parent = null
	 	this.basePath = null 
	 	
	}

	def scanMethodList(){
		val methods:List[Method] = parent.methods
		methods.foreach ({
			m => generateMethodClass(m) 
		})
	}
	
	private def generateMethodClass(m:Method){
		var pkg = GenClass.prefix + GenClass.provider + "."+ "methods"
		val pool:ClassPool = ClassPool.getDefault();
		var mclass:CtClass = GenClass.createClass(m.id , pkg,pool.get(GenClass.prefix +"methods."+"ResourceMethodBase"))
		try{
			mclass.defrost();
			buildClassBody(mclass,m)
		}
		finally{
			
			
			GenClass.writeClass(mclass)
		
				
			unSetContext()
		}
	}
	
	def setParamStyles(name:String,params:List[Parameter]){
	  
	   var clazz = Class.forName(name)
	 
	   var setParamStyleMethod = clazz.getDeclaredMethod("setParamStyle",Class.forName("java.lang.String"),Class.forName("java.lang.String"))
	   params.foreach({
		   p => setParamStyleMethod.invoke(clazz,p.name,p.style)
	   })
	   
	   
/*	   var getParamStyleMethod = clazz.getDeclaredMethod("getAllParamStyles")
	 
	   val styles = getParamStyleMethod.invoke(clazz).asInstanceOf[java.util.Map[AnyRef,AnyRef]]*/
	 
	}
	
	private def buildClassBody(mclass:CtClass,m:Method){
		
		
		
		/*val initializeMethod = new CtMethod(CtClass.voidType,"initialize",null,mclass)
		initializeMethod.setBody("this.usedMethods = new java.util.ArrayList();")
		mclass.addMethod(initializeMethod)*/
		var requesturi = basePath 
		if(!basePath.endsWith("/")) requesturi = requesturi + "/"
		requesturi = requesturi+ parent.path
		
		var paramstylefield = """private static java.util.Map paramstyles = new java.util.HashMap();"""
		var styleFieldInitializer ="{" 
		parent.params.foreach({
		   p => styleFieldInitializer = styleFieldInitializer+ """paramstyles.put(""""+p.name+"""",""""+p.style+"""");"""
	    }) 
	    m.request .parameters .foreach({
		   p => styleFieldInitializer = styleFieldInitializer+ """paramstyles.put(""""+p.name+"""",""""+p.style+"""");"""
	    }) 
	    
	     styleFieldInitializer =  styleFieldInitializer + "}" 
	   
		val paramStyleField:CtField  = CtField.make(paramstylefield,mclass)
		mclass.addField(paramStyleField)
		
		var staticconstructor = mclass.makeClassInitializer
		staticconstructor.setBody(styleFieldInitializer)
		//mclass.addConstructor(staticconstructor)
		
		
		val targetField:CtField  = CtField.make("""private java.lang.String target = """"+requesturi +"""";""",mclass)
		mclass.addField(targetField)
		
		val authenticationMethod:CtField  = CtField.make("""private String authenticationMethod = null;""",mclass)
		mclass.addField(authenticationMethod)
	  
	    val getAuthenticationMethod:CtMethod = CtNewMethod.make("public  String getAuthenticationMethod(){return authenticationMethod;}",mclass)
		mclass.addMethod(getAuthenticationMethod)
		
		val setAuthenticationMethod:CtMethod = CtNewMethod.make("public  void setAuthenticationMethod(String authType){authenticationMethod = authType;}",mclass)
		mclass.addMethod(setAuthenticationMethod)
		
		
	  	val getAllParamStyles:CtMethod = CtNewMethod.make("public static java.util.Map getAllParamStyles(){return paramstyles;}",mclass)
		mclass.addMethod(getAllParamStyles)
	  
		val getParamStyleMethod:CtMethod = CtNewMethod.make("public static String getParamStyle(String param){return (String)paramstyles.get(param);}",mclass)
		mclass.addMethod(getParamStyleMethod)
		
		val setParamStyleMethod:CtMethod = CtNewMethod.make("public static void setParamStyle(String param,String value){paramstyles.put(param,value);}",mclass)
		mclass.addMethod(setParamStyleMethod)
	    
	    
		val targetMethod:CtMethod = CtNewMethod.make("public String getTarget(){return this.target;}",mclass)
		mclass.addMethod(targetMethod)
		
		val settargetMethod:CtMethod = CtNewMethod.make("public void setTarget(String value){this.target = value;}",mclass)
		mclass.addMethod(settargetMethod)
	  
	  
		parent.params .foreach( {
			p => attachParam(p,mclass)
		})
		m.request .parameters .foreach ({
			p => attachParam(p,mclass)
		})
		
		
		
		/*val nm = "targ";
		
		val setTargetMethod:CtMethod = CtNewMethod.make("public void set"+nm+"(String target){getUsedMethods().add(target);}",mclass)
		mclass.addMethod(setTargetMethod)
		*/
		
		val httpMethodField:CtField  = CtField.make("""private java.lang.String httpVerb = """"+m.httpMethod.toString() +"""";""",mclass)
		mclass.addField(httpMethodField)
		
		val gethttpMethod:CtMethod = CtNewMethod.make("public String getHttpVerb(){return httpVerb ;}",mclass)
		mclass.addMethod(gethttpMethod)
		
		val sethttpMethod:CtMethod = CtNewMethod.make("public void setHttpVerb(String verb){httpVerb = verb;}",mclass)
		mclass.addMethod(sethttpMethod)
		
		var arg = GenClass.prefix +  "methods"+".ResourceMethodBase"
		
		
		//"""+arg+""" methodArg
		val invokeMethod:CtMethod = CtNewMethod.make("""public String invoke(){return new wadl.api.test.http.HttpClient(this,""""+GenClass.provider+"""").invoke();}""",mclass)
		
	
		mclass.addMethod(invokeMethod)
		
		
	}
	
	private def attachParam(p:Parameter,mclass:CtClass){
	   val name = p.name 
	   val aField:CtField  = new CtField(ClassPool.getDefault().get("java.lang.String"), name,mclass)
	   mclass.addField(aField)
	 
	   var getBody = """public String get"""+name.capitalize+"""(){return this."""+name + """;}"""
	   
	    
	   try{
		   val getMethod:CtMethod = CtNewMethod.make(getBody,mclass)
		   mclass.addMethod(getMethod)
	   }
	   catch {
	     case  ex: Exception => 
	   }
	   
	   /*val setMethod = CtNewMethod.setter(name, aField)
	   mclass.addMethod(setMethod)*/
	   var setBody = """public void set"""+name.capitalize+"""(String value){this."""+name+"""=value;getUsedMethods().add(""""+name + """");"""  +
	   		""" String tar = getTarget(); if(tar.contains("{"""+name+"""}")) { tar = tar.replace("\{"""+name+"""\}",value); setTarget(tar); } }"""
	   
	   
	   try{
		   val setMethod:CtMethod = CtNewMethod.make(setBody,mclass)
		   mclass.addMethod(setMethod)
	   }
	   catch {
	     case  ex: Exception =>  
	   }
	}
	
}