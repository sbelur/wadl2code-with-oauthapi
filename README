Project DESCRIPTION
[Briefly - API Code generator with WADL as Input]

(NOTE: status -> WIP)

This project aims to generate APIs for methods provided by a API provider like twitter 
allowing the user of the API to :
a> make request to the target through Apigee Proxy thus gaining the same benefits which the APIGEE
CONSOLE (apigee.com/console) would provide.
b> Handle Authentication protocols like OAuth behind the scene, this hiding the complexity of oauth 
from the end user.


Example : [For Twitter - would be similar to other providers as well based on WADL inputs]

<code snippet>

   Twitterstatusupdate twitterstatusupdate = new Twitterstatusupdate();
   twitterstatusupdate.setAuthenticationMethod(MethodConstants.OAUTH1);
   twitterstatusupdate.setFormat(MethodConstants.XML_FORMAT);
   twitterstatusupdate.setStatus("hello world"+new Random().nextInt());
   System.out.println(twitterstatusupdate.invoke());	

</code snippet>

