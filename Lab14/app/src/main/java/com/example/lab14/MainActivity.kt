package com.example.lab14

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab14.ui.theme.Lab14Theme
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class MyViewModel : ViewModel() {
    companion object GattAttributes {
        const val SCAN_PERIOD: Long = 3000
        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2
        val UUID_HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
        val UUID_HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        val UUID_CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    val scanResults = MutableLiveData<List<ScanResult>>(null)
    val fScanning = MutableLiveData<Boolean>(false)
    private val mResults = java.util.HashMap<String, ScanResult>()
    val mConnectionState = MutableLiveData<Int>(-1)
    val mBPM = MutableLiveData<Int>(0)
    val mBPMEntryList = MutableLiveData<List<Entry>>(listOf())
    private var mBluetoothGatt: BluetoothGatt? = null

    var xValue: Int = 0
    fun convertIntToEntry(yValue: Int) = Entry((xValue++).toFloat(), yValue.toFloat())

    fun addEntry(newEntry: Entry) {
        val currentList = mBPMEntryList.value ?: listOf()
        val updatedList = currentList + newEntry

        if (updatedList.size > 25) {
            val trimmedList = updatedList.drop(1)
            mBPMEntryList.postValue(trimmedList)
        }
        else {
            mBPMEntryList.postValue(updatedList)
        }
    }

    fun resetGraph() {
        mBPM.postValue(0)
        mBPMEntryList.postValue(listOf())
        xValue = 0
    }

    fun scanDevices(scanner: BluetoothLeScanner) {
        viewModelScope.launch(Dispatchers.IO) {
            fScanning.postValue(true)
            mResults.clear()
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build()
            scanner.startScan(null, settings, leScanCallback)
            delay(SCAN_PERIOD)
            scanner.stopScan(leScanCallback)
            scanResults.postValue(mResults.values.toList())
            fScanning.postValue(false)
        }
    }

    fun connectDevice(context: Context, device: BluetoothDevice) {
        Log.d("DBG", "Connect attempt to ${device.name} ${device.address}")
        mConnectionState.postValue(STATE_CONNECTING)
        mBluetoothGatt = device.connectGatt(context, false, mGattCallback)
    }

    fun disconnectDevice() {
        Log.d("DBG", "Disconnect device")
        mBluetoothGatt?.disconnect()
        resetGraph()
    }

    val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val deviceAddress = device.address
            mResults[deviceAddress] = result
            Log.d("DBG", "Device address: $deviceAddress (${result.isConnectable})")
        }
    }

    private val mGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectionState.postValue(STATE_CONNECTED)
                Log.i("DBG", "Connected to GATT server")
                Log.i("DBG", "Attempting to start service discovery")
                gatt.discoverServices()
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mConnectionState.postValue(STATE_DISCONNECTED)
                gatt.disconnect()
                gatt.close()
                Log.i("DBG", "Disconnected from GATT server")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("DBG", "GATT FAILED")
                return
            }

            Log.d("DBG", "onServicesDiscovered()")

            for (gattService in gatt.services) {
                Log.d("DBG", "Service ${gattService.uuid}")
                if (gattService.uuid == UUID_HEART_RATE_SERVICE) {
                    Log.d("DBG", "FOUND Heart Rate Service")

                    for (gattCharacteristic in gattService.characteristics)
                        Log.d("DBG", "Characteristic ${gattCharacteristic.uuid}")

                    /* setup the system for the notification messages */
                    val characteristic = gatt.getService(UUID_HEART_RATE_SERVICE).getCharacteristic(UUID_HEART_RATE_MEASUREMENT)
                    if (gatt.setCharacteristicNotification(characteristic, true)) {
                        // then enable them on the server
                        val descriptor = characteristic.getDescriptor(UUID_CLIENT_CHARACTERISTIC_CONFIG)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        val writing = gatt.writeDescriptor(descriptor)
                    }
                }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == UUID_HEART_RATE_MEASUREMENT) {
                mBPM.postValue(extractHeartRateValue(characteristic)
                    .also { addEntry(convertIntToEntry(it)) })
            }
        }

        fun extractHeartRateValue(characteristic: BluetoothGattCharacteristic): Int {
            val flag = characteristic.properties
            val format = if (flag and 0x01 != 0) {
                BluetoothGattCharacteristic.FORMAT_UINT16
            }
            else { BluetoothGattCharacteristic.FORMAT_UINT8 }
            return characteristic.getIntValue(format, 1) ?: -1
        }
    }
}

@Composable
fun LineChartView(
    entries: List<Entry>,
    label: String
) {
    AndroidView(
        modifier = Modifier.fillMaxSize().background(Color.White),
        factory = { context ->
            LineChart(context).apply {
                axisLeft.axisMinimum = 65f
                axisLeft.axisMaximum = 115f
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, label).apply {
                color = Color.Blue.toArgb()
                valueTextColor = Color.Black.toArgb()
                setCircleColor(Color.Red.toArgb())
                circleRadius = 3f
                setDrawValues(false)
                setDrawCircles(true)
            }

            val lineData = LineData(dataSet)
            chart.data = lineData
            chart.invalidate()
        }
    )
}

@Composable
fun ShowGraph(model: MyViewModel, onNavigateBack: () -> Unit) {
    val bpmEntryList: List<Entry> by model.mBPMEntryList.observeAsState(listOf())

    Column() {
        Button({ onNavigateBack() }) { Text("Back") }
        LineChartView(bpmEntryList, "")
    }
}

@Composable
fun ShowDevices(mBluetoothAdapter: BluetoothAdapter, model: MyViewModel, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val value: List<ScanResult>? by model.scanResults.observeAsState(null)
    val fScanning: Boolean by model.fScanning.observeAsState(false)
    val connectionState: Int by model.mConnectionState.observeAsState(-1)
    val bpm: Int by model.mBPM.observeAsState(0)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { model.scanDevices(mBluetoothAdapter.bluetoothLeScanner) },
            enabled = !fScanning,
            modifier = Modifier.padding(8.dp).height(64.dp).width(144.dp)
        ) {
            Text(if (fScanning) "Scanning" else "Scan Now")
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = when (connectionState) {
                MyViewModel.STATE_CONNECTED     -> "Connected"
                MyViewModel.STATE_CONNECTING    -> "Connecting..."
                MyViewModel.STATE_DISCONNECTED  -> "Disconnected"
                else -> ""
            })

            if (connectionState == MyViewModel.STATE_CONNECTED) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = if (bpm != 0) "$bpm" else "--", fontSize = 36.sp)
                    Text(text = "BPM", fontSize = 12.sp)
                }

                Button({ onNavigateBack() }) { Text("Graph") }
                Button(
                    onClick = { model.disconnectDevice() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text("Disconnect")
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = Color.Gray
        )

        if (value.isNullOrEmpty()) {
            Text(text = "No devices found", modifier = Modifier.padding(8.dp))
        }
        else {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(2.dp)) {
                    items(value ?: emptyList()) { result ->
                        val deviceName = result.device.name ?: "UNKNOWN"
                        val deviceAddress = result.device.address
                        val deviceStrength = result.rssi

                        Row(
                            modifier = Modifier.padding(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${deviceStrength}dBm",
                                modifier = Modifier.padding(end = 10.dp).align(Alignment.CenterVertically)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = deviceName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(text = deviceAddress)
                            }

                            Button(
                                enabled = result.isConnectable(),
                                onClick = { model.connectDevice(context, result.device) },
                                modifier = Modifier.padding(8.dp).align(Alignment.CenterVertically)
                            ) {
                                Text("Connect")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun App(mBluetoothAdapter: BluetoothAdapter) {
    val viewModel: MyViewModel = viewModel()
    var currentScreen by remember { mutableStateOf("main") }

    when (currentScreen) {
        "main" -> ShowDevices(mBluetoothAdapter, viewModel(), onNavigateBack = { currentScreen = "graph"})
        "graph" -> ShowGraph(viewModel, onNavigateBack = { currentScreen = "main"})
    }
}

class MainActivity : ComponentActivity() {
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private fun hasPermissions(): Boolean {
        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {
            Log.e("DBG", "No Bluetooth LE capability")
            return false
        }
        else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADMIN), 1);
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        setContent {
            Lab14Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        Log.i("DBG", "Device has Bluetooth support: ${hasPermissions()}")
                        when {
                            mBluetoothAdapter == null       -> Text("Bluetooth is not supported on this device")
                            !mBluetoothAdapter!!.isEnabled  -> Text("Bluetooth is turned off")
                            else                            -> App(mBluetoothAdapter!!)
                        }
                    }
                }
            }
        }
    }
}