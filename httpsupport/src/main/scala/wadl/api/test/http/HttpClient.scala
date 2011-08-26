package wadl.api.test.http


import java.lang.reflect.Method
import org.apache.http._
import org.apache.http.util.EntityUtils
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CookieStore
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods._
import org.apache.http.client.params.HttpClientParams
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.conn.params.ConnManagerParams
import org.apache.http.conn.scheme.PlainSocketFactory
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.scheme.SchemeRegistry
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.conn.ssl.X509HostnameVerifier
import org.apache.http.cookie.Cookie
import org.apache.http.entity.StringEntity
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.HttpResponse
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager
import org.apache.http.message.BasicHeader
import org.apache.http.message.BasicNameValuePair
import org.apache.http.message.BufferedHeader
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpParams
import org.apache.http.protocol.HTTP
import org.apache.http.protocol.HttpContext
import org.apache.http.util.CharArrayBuffer
import java.security.cert.X509Certificate
import javax.net.ssl._
import java.util.Properties
import java.io._
import org.scribe.model.Token

import TestConstants._
import scala.collection.mutable.ListBuffer

import scala.collection.JavaConversions._

class HttpClient(val m:AnyRef,provider:String) {
	
	var originalTargetURL:java.net.URI = _
  
	def invoke():String = {
	    val httpClient = initialize()
		makeRequest(m,httpClient)
	}
	
	
	
	def makeRequest(apiMethod:AnyRef,httpClient:HttpClient#TestHttpClient):String = {
	  	val usedMethod:Method  = apiMethod.getClass().getMethod("getUsedMethods")
		var queryParamList = List(usedMethod.invoke(apiMethod).asInstanceOf[java.util.List[String]].toArray :_*)
	    val httpVerbMethod:Method  = apiMethod.getClass().getMethod("getHttpVerb")
	  	val httpVerb = httpVerbMethod.invoke(apiMethod).asInstanceOf[java.lang.String]
	  	val httpmethod = getHttpMethod(httpVerb,apiMethod)
	  	val contentTypeValue = getHeaderValue(CONTENT_TYPE_HEADER_NAME,apiMethod)
	  	if(contentTypeValue != APPLICATION_X_WWW_FORM_URLENCODED){
	  		addQueryParamsForMethod(httpmethod,queryParamList,apiMethod)
	  	}
	  	formRequestPath(httpmethod,apiMethod)
	  	val httpCtx:HttpContext  = httpClient.createHttpContext()
	  	addHeaderParamsForMethod(httpmethod,queryParamList,apiMethod)
	  	httpmethod.getMethod match {
	  	  case "POST"|"PUT" => applyBody(httpmethod,apiMethod,queryParamList)
	  	  case _ => 
	  	}
	  	println("sending request to target "+httpmethod.getURI)
	  	val httpResp:HttpResponse  = httpClient.execute(httpmethod,httpCtx)
	    onResponseComplete(httpResp,httpClient)
	}
	
	
	private def applyBody(httpMethod:HttpRequestBase,apiMethod:AnyRef,queryParamList:List[AnyRef]){
		
	    val value = getHeaderValue(CONTENT_TYPE_HEADER_NAME,apiMethod)
	    var handled = false
		value match {
		  case APPLICATION_X_WWW_FORM_URLENCODED => addNameValuePairsToBody()
		  case x => println("No special logic for "+x +" as of now")
		}
	    
	    if(!handled){
		    val getBodyContentToBeSentAsIs:Method  = apiMethod.getClass().getMethod("getBodyContentToBeSentAsIs")
			val bodyValue = getBodyContentToBeSentAsIs.invoke(apiMethod)
			if(bodyValue != null){
				bodyValue match {
				    case b  if b.toString().length > 0 => applyBodyAsIs()
				}
			}
		    def applyBodyAsIs() {
		       val se = new StringEntity(bodyValue.toString)
	           httpMethod.asInstanceOf[HttpEntityEnclosingRequestBase].setEntity(se)
		    }
	    }
	    
		
	    	    
		
		def addNameValuePairsToBody(){
		  val formparams = List[NameValuePair]()
		  queryParamList.filter ({
			   p => isQueryType(p.toString,apiMethod)
		  }).foreach ({
		   paramName => {
		     val paramValue = getParamValue(paramName.toString,apiMethod)
		     new BasicNameValuePair(paramName.toString, paramValue) :: formparams
		   }
		 })
		  val entity = new UrlEncodedFormEntity(ListBuffer(formparams: _*), UTF_8)
		  httpMethod.asInstanceOf[HttpEntityEnclosingRequestBase].setEntity(entity)
		  handled = true
		}
	}
	
	def formRequestPath(httpmethod:HttpRequestBase,apiMethod:AnyRef){
	   var uri = httpmethod.getURI
	   val host = uri.getHost
	   var start = uri.toString.indexOf(host)
	   start = start + host.length
	   var reqPath = uri.toString.substring(start)
	   var scheme = uri.getScheme
	   var port=uri.getPort.toString
	   println(port)
	   var portPart = ""
	   if (port != "-1" && port != "80" && port != "443") {
	      println("appending port "+port)
          portPart = ":" + port
       }
	   val getAppName:Method  = apiMethod.getClass().getMethod("getApplicationName")
	   val appNameValue = getAppName.invoke(apiMethod)
	   val getSmartKey:Method  = apiMethod.getClass().getMethod("getSmartKey")
	   val smartKeyValue = getSmartKey.invoke(apiMethod)
	   val getProvider:Method  = apiMethod.getClass().getMethod("getProviderName")
	   val providerValue = getProvider.invoke(apiMethod)
       var hostStr = scheme+"://"+appNameValue+"-api.apigee.com"+portPart
       reqPath = hostStr + "/v1/"+providerValue+ reqPath
       reqPath.contains("?") match {
	     case true => reqPath = reqPath + "&smartkey="+java.net.URLEncoder.encode(smartKeyValue.toString,"UTF-8") 
	     case false => reqPath = reqPath + "?smartkey="+java.net.URLEncoder.encode(smartKeyValue.toString,"UTF-8")
	   }
	   httpmethod.setURI(new java.net.URI(reqPath))
	}
	
	
    def addHeaderParamsForMethod(httpmethod:HttpRequestBase,queryParamList:List[Object],apiMethod:AnyRef){
		var uri= httpmethod.getURI.toString
		
		queryParamList.filter ({
			   p => isHeaderType(p.toString,apiMethod)
		 }).foreach ({
		   p => {
		     val value = getHeaderValue(p.toString,apiMethod)
		     val testheader = new BasicHeader(p.toString, value)
             httpmethod.addHeader(testheader)
		   }
		 })
  }
	
	
	
	
	def addQueryParamsForMethod(httpmethod:HttpRequestBase,queryParamList:List[Object],apiMethod:AnyRef){
		var uri= httpmethod.getURI.toString
		
		queryParamList.filter ({
			   p => isQueryType(p.toString,apiMethod)
		 }).foreach ({
		   p => {
		     val value = getParamValue(p.toString,apiMethod)
		   
		     if(uri.contains("?")){
		    	 
		         uri = uri + "&" + p +"=" + java.net.URLEncoder.encode(value,"UTF-8")
		        
		     }
		     else{
		         uri = uri + "?" + p +"=" + java.net.URLEncoder.encode(value,"UTF-8")
		        
		     }
		     
		   }
		})
		
		httpmethod.setURI(new java.net.URI(uri))
	}
	
	val isQueryType = (p:String,apiMethod:AnyRef) => "query" == getParamStyleValue(p,apiMethod)
	
	val isHeaderType = (p:String,apiMethod:AnyRef) => "header" == getParamStyleValue(p,apiMethod)
	
	
	def getParamStyleValue(p:String,apiMethod:AnyRef) = {
	  var getParamStyleMethod = apiMethod.getClass().getDeclaredMethod("getAllParamStyles")
	  val styles = getParamStyleMethod.invoke(apiMethod.getClass).asInstanceOf[java.util.Map[AnyRef,AnyRef]]
	  val target:Method  = apiMethod.getClass().getDeclaredMethod("getParamStyle",Class.forName("java.lang.String"))
	  val value = target.invoke(apiMethod,p).asInstanceOf[java.lang.String]
	  value
	}
		
	
	private def getParamValue(p:String,apiMethod:AnyRef):String = {
	  val target:Method  = apiMethod.getClass().getMethod("get"+p.capitalize)
	  val value = target.invoke(apiMethod).asInstanceOf[java.lang.String]
	  value
	}
	
	private def getHeaderValue(p:String,apiMethod:AnyRef):String = {
	  
	  val target:Method  = apiMethod.getClass().getMethod("getTestHeader",classOf[String])
	  val value = target.invoke(apiMethod,p).asInstanceOf[java.lang.String]
	  value
	}
	
	
	
	
	def onResponseComplete(httpResp:HttpResponse,httpClient:DefaultHttpClient):String = {
	  val resEntity:HttpEntity = httpResp.getEntity()
      val resp = EntityUtils.toString(resEntity)
      if (resEntity != null) {
          resEntity.consumeContent()
      }
      httpClient.getConnectionManager().shutdown()
      resp
	}
	
	def getHttpMethod(verb:String,apiMethod:AnyRef) = {
	    var  httpMethod:HttpRequestBase = null
	    val target:Method  = apiMethod.getClass().getMethod("getTarget")
	  	val targetURL = target.invoke(apiMethod).asInstanceOf[java.lang.String]
	    
		verb.toUpperCase match {
	  	  
	  	  case "GET" => httpMethod = new HttpGet(targetURL)
	  	    
	  	  case "POST" => httpMethod = new HttpPost(targetURL)
	  	    
	  	  case "DELETE" =>  httpMethod = new HttpPut(targetURL) 
	  	    
	  	  case "PUT" =>httpMethod = new HttpDelete(targetURL)
	  	}
	    httpMethod
	}
	
	
	def initialize() = {
	  var _httpClient = null
	  val params:HttpParams = new BasicHttpParams()
        ConnManagerParams.setMaxTotalConnections(params, 100)
        System.setProperty("http.conn-manager.max-per-route", String.valueOf(20))
        val schemeRegistry = new SchemeRegistry()
        schemeRegistry.register(new Scheme("http", PlainSocketFactory
                .getSocketFactory(), 80))
        val easyTrustManager:TrustManager = new X509TrustManager() {
            
            
            override  def checkClientTrusted(chain:Array[X509Certificate],
                                            authType:String)   {
                // I accept everything
            }

            
            override  def checkServerTrusted(chain:Array[X509Certificate],
                                            authType:String) {
                // I accept everything
            }

            
            override def getAcceptedIssuers():Array[X509Certificate] = {
                null
            }
        }

        val hostnameVerifier = new X509HostnameVerifier() {

            
            override def verify(s:String, sslSession:SSLSession) = {
                true
            }

            
            override def verify(s:String, sslSocket:SSLSocket)  {

            }

            
            override def verify(s:String, x509Certificate:X509Certificate) {

            }

            
            override def verify(s:String, strings:Array[String], strings1:Array[String])  {

            }
        }
        val sslcontext:SSLContext = SSLContext.getInstance("TLS")
        val ar = Array[TrustManager](easyTrustManager)
        sslcontext.init(null, ar, null)
        val socketFactory = new SSLSocketFactory(sslcontext)
        socketFactory
                .setHostnameVerifier(hostnameVerifier)
        schemeRegistry.register(new Scheme("https", socketFactory, 443))

        HttpClientParams.setRedirecting(params, false)
        HttpConnectionParams.setSoTimeout(params, 60000)
        HttpConnectionParams.setConnectionTimeout(params, 60000)
        val cm:ClientConnectionManager = new ThreadSafeClientConnManager(params,
                schemeRegistry)
        
        var ref:HttpClient#TestHttpClient = new TestHttpClient(cm, params)
        ref
	}
	
	
	
	
     class TestHttpClient(val cm:ClientConnectionManager, val params:HttpParams ) extends DefaultHttpClient(cm,params) {

                
        override def createHttpContext() =  {
            super.createHttpContext()
        }

    }
	
	

}