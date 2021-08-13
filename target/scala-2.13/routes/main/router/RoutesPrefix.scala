// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/15092/Desktop/tactical/Tactical-Card-Game/conf/routes
// @DATE:Fri Aug 13 18:02:33 CST 2021


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
