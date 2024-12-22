package com.example.kotlin_lab6
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PowerCalculatorApp()
        }
    }
}

@Composable
fun PowerCalculatorApp() {
    var nominalPower by remember { mutableStateOf("26") }
    var usageCoefficient by remember { mutableStateOf("0.27") }
    var tangentValue by remember { mutableStateOf("1.62") }
    var calculationResults by remember { mutableStateOf<CalculationResults?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Power Calculator", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields
        InputField(
            value = nominalPower,
            label = "Nominal Power (kW)",
            onValueChange = { nominalPower = it }
        )

        InputField(
            value = usageCoefficient,
            label = "Usage Coefficient",
            onValueChange = { usageCoefficient = it }
        )

        InputField(
            value = tangentValue,
            label = "Tangent (tg)",
            onValueChange = { tangentValue = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            calculationResults = performCalculations(
                nominalPower.toDoubleOrNull() ?: 0.0,
                usageCoefficient.toDoubleOrNull() ?: 0.0,
                tangentValue.toDoubleOrNull() ?: 0.0
            )
        }) {
            Text("Calculate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results Display
        calculationResults?.let { ResultsCard(it) }
    }
}

@Composable
fun InputField(value: String, label: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun ResultsCard(results: CalculationResults) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Calculation Results", style = MaterialTheme.typography.titleMedium)

            ResultRow("Group Coefficient", results.groupCoefficient.format(4))
            ResultRow("Effective Equipment Number", results.effectiveEquipmentNumber.toString())
            ResultRow("Estimated Active Load (kW)", results.estimatedActiveLoad.format(2))
            ResultRow("Estimated Reactive Load (kVAR)", results.estimatedReactiveLoad.format(2))
            ResultRow("Full Power (kVA)", results.fullPower.format(3))
            ResultRow("Group Electricity (A)", results.groupElectricity.format(2))
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)

data class CalculationResults(
    val groupCoefficient: Double,
    val effectiveEquipmentNumber: Int,
    val estimatedActiveLoad: Double,
    val estimatedReactiveLoad: Double,
    val fullPower: Double,
    val groupElectricity: Double
)

fun performCalculations(
    nominalPower: Double,
    usageCoefficient: Double,
    tangent: Double
): CalculationResults {
    val adjustedNominalPower = 4 * nominalPower
    val totalActiveLoad = (adjustedNominalPower * usageCoefficient) + 50
    val totalReactiveLoad = totalActiveLoad * tangent

    val groupCoefficient = totalActiveLoad / (adjustedNominalPower + 100)
    val effectiveEquipmentNumber = ((adjustedNominalPower + 200).pow(2) / 5000).toInt()
    val estimatedActiveLoad = totalActiveLoad * 1.25
    val estimatedReactiveLoad = totalReactiveLoad * 1.1
    val fullPower = sqrt(estimatedActiveLoad.pow(2) + estimatedReactiveLoad.pow(2))
    val groupElectricity = estimatedActiveLoad / 0.4

    return CalculationResults(
        groupCoefficient = groupCoefficient,
        effectiveEquipmentNumber = effectiveEquipmentNumber,
        estimatedActiveLoad = estimatedActiveLoad,
        estimatedReactiveLoad = estimatedReactiveLoad,
        fullPower = fullPower,
        groupElectricity = groupElectricity
    )
}
