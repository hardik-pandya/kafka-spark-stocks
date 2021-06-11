import scala.util.Random

case class TickerList(tickers:List[Ticker]) {

  def generatedTickers(tickerList: TickerList): List[Ticker]= {

    val r = new scala.util.Random
    val generatedTickers : List[Ticker] = tickerList.tickers.map(t => (Ticker(t.name, t.price + r.nextInt((t.price * 10/ 100)))))
    generatedTickers
  }
}
case class Ticker(name:String, price: Int)





