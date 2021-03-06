package bootstrap.liftweb

import net.liftweb.common._
import net.liftweb.util._
  import Helpers._
import net.liftweb.http._
import net.liftweb.sitemap._
  import Loc._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    println("In boot")
    getClass.getClassLoader match {
      case rcl: java.net.URLClassLoader =>
        println("Classpath:" + rcl.getURLs.mkString("\n  ", "\n  ",""))
    }
    
    def shouldRedirect(r: Req) = !r.request.serverName.endsWith(".tk") &&
      r.request.serverName != "localhost"
    LiftRules.statelessDispatchTable.append {
      case r if shouldRedirect(r) => () => Full(
        PermRedirectResponse("http://reactive-web.tk"+r.uri, r, r.cookies: _*) 
      )        
    }
    
    // where to search snippet
    LiftRules.addToPackages("reactive.web.demo")

    reactive.web.Reactions.initComet
    
    // Build SiteMap
    def sitemap = () => SiteMap(
      Menu("About")  /"index",
      Menu("Core")  /"0"  >>PlaceHolder  submenus(
        Menu("EventStream")  /"demos"/"core"/"EventStream",
        Menu("Signal")  /"demos"/"core"/"Signal",
        Menu("SeqSignal")  /"demos"/"core"/"SeqSignal",
        Menu("Forwardable")  /"demos"/"core"/"Forwardable"
      ),
      Menu("Web")  /"1"  >>PlaceHolder  submenus(
        Menu("Low Level API")  /"demos"/"web"/"LowLevel",
        Menu("Events")  /"demos"/"web"/"DOMEventSource",
        Menu("Properties")  /"demos"/"web"/"DOMProperty",
        Menu("Elements")  /"demos"/"web"/"Elements",
        Menu("Getting Started")  /"demos"/"web"/"GettingStarted",
        Menu("Simple demo")  /"demos"/"demos"/"SimpleDemo"
      ),
      Menu("HTML")  /"2"  >>PlaceHolder  submenus(
        Menu("Span")  /"demos"/"html"/"Span",
        Menu("Div")  /"demos"/"html"/"Div",
        Menu("Button")  /"demos"/"html"/"Button",
        Menu("TextInput")  /"demos"/"html"/"TextInput",
        Menu("CheckboxInput")  /"demos"/"html"/"CheckboxInput",
        Menu("Select")  /"demos"/"html"/"Select"
      ),
      Menu("Scaladocs")  /"3"  >>PlaceHolder  submenus(
        Menu("reactive-core")  /"reactive-core-api"/ **,
        Menu("reactive-web")  /"reactive-web-api"/ **
      ),
      reactive.web.demo.snippet.DemoPane.menu
    )
    LiftRules.setSiteMapFunc(sitemap)
    LiftRules.liftRequest.append {
      case Req("reactive-core-api"::_, _, _) => false
      case Req("reactive-web-api"::_, _, _) => false
    }
    LiftRules.useXhtmlMimeType = false
  }
}

