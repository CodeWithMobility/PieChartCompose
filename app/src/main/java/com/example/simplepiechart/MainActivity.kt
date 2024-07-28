package com.example.simplepiechart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.nativeCanvas
import com.example.simplepiechart.ui.theme.SimplePieChartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimplePieChartTheme {
                PieChartContainer()
            }
        }
    }
}
@Composable
fun PieChartContainer() {
    val pieData = listOf(
        PieChartData(value = 25f, color = Color.Red, label = "Red"),
        PieChartData(value = 15f, color = Color.Green, label = "Green"),
        PieChartData(value = 35f, color = Color.Blue, label = "Blue"),
        PieChartData(value = 20f, color = Color.Magenta, label = "Magenta"),
        PieChartData(value = 25f, color = Color.Yellow, label = "Yellow")
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PieChart(data = pieData, modifier = Modifier.size(300.dp))
    }
}


@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    val totalValue = data.sumOf { it.value.toInt() }
    val angles = data.map { it.value / totalValue * 360f }
    val animatedAngles = angles.map { angle ->
        remember { Animatable(0f) }.apply {
            LaunchedEffect(key1 = angle) {
                animateTo(angle, animationSpec = tween(durationMillis = 1000))
            }
        }
    }
    val sweepAngles = animatedAngles.map { it.value }

    Canvas(modifier = modifier) {
        var startAngle = -90f
        data.zip(sweepAngles).forEach { (slice, sweepAngle) ->
            drawArc(
                color = slice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )

            // Calculate the angle for the label
            val angleInDegrees = startAngle + sweepAngle / 2
            val angleInRadians = Math.toRadians(angleInDegrees.toDouble())

            // Calculate the position for the label
            val radius = size.minDimension / 2
            val labelRadius = radius * 0.7 // Position the label at 70% of the radius
            val x = (size.center.x + labelRadius * Math.cos(angleInRadians)).toFloat()
            val y = (size.center.y + labelRadius * Math.sin(angleInRadians)).toFloat()

            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText(
                    slice.label,
                    x,
                    y,
                    paint
                )
            }

            startAngle += sweepAngle
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPieChartContainer() {
    PieChartContainer()
}


data class PieChartData(
    val value: Float,
    val color: Color,
    val label: String
)