package utils
import play.api.ApplicationLoader
import play.api.inject.guice.{GuiceApplicationLoader, GuiceApplicationBuilder}

/**
  * Created by hariprasadk on 08/12/16.
  */
class CustomApplicationLoader extends GuiceApplicationLoader {
  override protected def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {
    super.builder(context).disableCircularProxies(false)
  }
}