package controllers

import akka.util.ByteString
import play.api.http.HttpEntity
import play.api.mvc._
import com.monsanto.arch.kamon.prometheus.Prometheus
import com.monsanto.arch.kamon.prometheus.PrometheusExtension._
import akka.pattern.ask
import com.monsanto.arch.kamon.prometheus.metric.TextFormat

import scala.concurrent.duration._


class MetricsController extends Controller {

  def index = Action async {

    import scala.concurrent._
    import ExecutionContext.Implicits.global
    import scala.language.postfixOps

   for {
      extension    ← Prometheus.kamonInstance
      currentState ← extension.ref.ask(GetCurrentSnapshot)(1 second)
    } yield {
      currentState match {
        case NoCurrentSnapshot => {
          Result(
            header = ResponseHeader(200, Map.empty),
            body = HttpEntity.NoEntity
          )
        }
        case Snapshot(s) ⇒ {
          Result(
            header = ResponseHeader(200, Map.empty),
            body = HttpEntity.Strict(ByteString(TextFormat.format(s)), Some(""))
          )
        }
      }
    }
  }
}