package com.reyhaneh.ahani.iotclient.pages

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chargemap.compose.numberpicker.ListItemPicker
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import com.reyhaneh.ahani.iotclient.server.Row
import com.reyhaneh.ahani.iotclient.server.ServerInterface
import com.reyhaneh.ahani.iotclient.server.resposneFromErrorBody
import kotlinx.coroutines.launch

class SummaryPageViewModel(application: Application) : AndroidViewModel(application) {
    var isLoading by mutableStateOf(false)

    var lightValues: List<DataPoint> by mutableStateOf(listOf())
    var temperatureValues: List<DataPoint> by mutableStateOf(listOf())
    var moistureValues: List<DataPoint> by mutableStateOf(listOf())
    var plotLabels: List<String> by mutableStateOf(listOf())

    var chartLimit by mutableStateOf("10")
    var selection: List<DataPoint>? by mutableStateOf(null)

    init {
        loadData()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            getApplication(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    fun loadData() {
        viewModelScope.launch {
            isLoading = true

            try {
                val serverInterface = ServerInterface.getInstance(getApplication<Application>().applicationContext,
                    "",
                    "")
                setupChartDatapoints(serverInterface.fetchRecentRecords(chartLimit).data)
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()
                showToast("Could not collect data: ${errorBody?.let { resposneFromErrorBody(it).reason }}")
            } catch (e: java.net.SocketTimeoutException) {
                showToast("Could not collect data, server timed out, check your internet connection")
            } catch (e: Exception) {
                showToast("Could not collect data: ${e}")
            } finally {
                isLoading = false
            }
        }
    }

    private fun setupChartDatapoints(chartData: List<Row>) {
        val light: MutableList<DataPoint> = mutableListOf()
        val temperature: MutableList<DataPoint> = mutableListOf()
        val moisture: MutableList<DataPoint> = mutableListOf()
        val labels: MutableList<String> = mutableListOf()

        chartData.forEachIndexed { index, row ->
            light.add(DataPoint(index.toFloat(), row.light.toFloat()))
            temperature.add(DataPoint(index.toFloat(), row.temperature.toFloat()))
            moisture.add(DataPoint(index.toFloat(), row.moisture.toFloat()))
            labels.add(row.created)
        }

        lightValues = light
        temperatureValues = temperature
        moistureValues = moisture
        plotLabels = labels
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SummaryPage(viewModel: SummaryPageViewModel = viewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(viewModel.plotLabels.isNotEmpty()) {
            LineGraph(
                plot = LinePlot(
                    listOf(
                        LinePlot.Line(
                            viewModel.lightValues,
                            LinePlot.Connection(color = Color(0xFFB0B97A)),
                            LinePlot.Intersection(color = Color(0xFFB0B97A)),
                            LinePlot.Highlight(color = Color(0xFFB0B97A)),
                        ),
                        LinePlot.Line(
                            viewModel.temperatureValues,
                            LinePlot.Connection(color = Color(0xFFF49586)),
                            LinePlot.Intersection(color = Color(0xFFF49586)),
                            LinePlot.Highlight(color = Color(0xFFF49586)),
                        ),
                        LinePlot.Line(
                            viewModel.moistureValues,
                            LinePlot.Connection(color = Color(0xFFF7E188)),
                            LinePlot.Intersection(color = Color(0xFFF7E188)),
                            LinePlot.Highlight(color = Color(0xFFF7E188)),
                        )
                    ),
                    grid = LinePlot.Grid(Color.LightGray, steps = 10),
                    xAxis = LinePlot.XAxis(steps = viewModel.plotLabels.size),
                ),
                modifier = Modifier.height(300.dp),
                onSelection = { offset, points ->
                    viewModel.selection = points
                },
                onSelectionEnd = {
                    viewModel.selection = null
                }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        ListItemPicker(
            value = viewModel.chartLimit,
            list = listOf("10", "20", "all"),
            onValueChange = {
                viewModel.chartLimit = it
                viewModel.loadData()
            },
            label = { "Show ${it} records" }
        )

        AnimatedVisibility(viewModel.selection != null) {
            if(viewModel.selection != null) {
                Column {
                    Text(
                        text = "Created :${viewModel.plotLabels[viewModel.selection!![0].x.toInt()]}"
                    )
                    Text(
                        text = "Light: ${viewModel.selection!![0].y}"
                    )
                    Text(
                        text = "Temperature: ${viewModel.selection!![1].y}"
                    )
                    Text(
                        text = "Moisture: ${viewModel.selection!![2].y}"
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryPageDrawer() {
    val activity = (LocalContext.current as? Activity)
    Button(onClick = {
        activity?.finish()
    }) {
        Text("Logout")
    }
}