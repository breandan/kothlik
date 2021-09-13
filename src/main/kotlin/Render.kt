import ai.hypergraph.kaliningraph.browserCmd
import edu.mcgill.markovian.pmap
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.PlotSvgExportPortable
import jetbrains.letsPlot.*
import jetbrains.letsPlot.geom.geomDensity
import jetbrains.letsPlot.intern.*
import java.io.File
import kotlin.random.Random

fun Plot.display() =
  File.createTempFile("test", ".svg").also {
    val plotSize = DoubleVector(1000.0, 500.0)
    val plot = PlotSvgExportPortable.buildSvgImageFromRawSpecs( this@display.toSpec(), plotSize)
    it.writeText(plot)
  }.also {
    ProcessBuilder(browserCmd, it.path).start()
  }

const val POPCOUNT = 10000

fun compare(vararg samplers: (Double) -> Double): Plot =
  compare(
    *samplers.map { f ->
      (1..POPCOUNT).pmap { f(Random.Default.nextDouble()) }
    }.toTypedArray()
  )

fun compare(vararg samples: List<Double>): Plot =
  letsPlot(
    mapOf<String, Any>(
      "x" to samples.fold(listOf<Double>()) { acc, function ->
        acc + function
      },
      "" to samples.mapIndexed { i, s -> List(s.size) { "PDF$i" } }.flatten()
    )
  ).let {
    it + geomDensity(alpha = .3) { x = "x"; fill = "" } + ggsize(500, 250)
  }
