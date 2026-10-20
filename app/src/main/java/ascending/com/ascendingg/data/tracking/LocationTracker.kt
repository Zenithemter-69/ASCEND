package ascending.com.ascendingg.data.tracking

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Tracks location-based metrics like distance traveled, steps, and current location.
 * Uses FusedLocationProviderClient from Google Play Services.
 */
class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Permission check helpers
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private fun hasLocationPermission(): Boolean {
        return permissions.all { ContextCompat.checkSelfPermission(
            context,
            it
        ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // State
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: Flow<Location?> = _currentLocation
        .distinctUntilChanged()

    private var _totalDistanceMeters = 0f
    private var _stepCount = 0
    private var lastLocation: Location? = null

    val totalDistanceMeters: Float
        get() = _totalDistanceMeters

    val stepCount: Int
        get() = _stepCount

    // Distance per step estimation (average human step ~0.75m)
    private val DISTANCE_PER_STEP = 0.75f

    /**
     * Start tracking location and calculating steps/distance.
     * Returns a Flow that emits the current location.
     */
    fun startTracking(): Flow<Location?> {
        if (!hasLocationPermission()) {
            return flow { }
        }

        // Get last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _currentLocation.value = location
                lastLocation = location
            }
        }

        return generate {
            while (isActive) {
                try {
                    val locationRequest = android.location.LocationRequest.Builder(
                        priority = android.location.LocationRequest.PRIORITY_HIGH_ACCURACY,
                        intervalMillis = 5000
                    ).build()

                    val callback = object : android.location.LocationCallback() {
                        override fun onLocationResult(locationResult: android.location.LocationResult) {
                            locationResult.locations.forEach { location ->
                                if (location != null && isActive) {
                                    // Update distance and steps
                                    if (lastLocation != null) {
                                        val distance = lastLocation!!.distanceTo(location)
                                        if (distance > 0) {
                                            _totalDistanceMeters += distance
                                            // Estimate steps from distance
                                            val newSteps = (distance / DISTANCE_PER_STEP).toInt()
                                            _stepCount += newSteps
                                        }
                                    }
                                    lastLocation = location
                                    _currentLocation.value = location
                                }
                            }
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(locationRequest, callback, null)

                    // Wait and then clean up
                    Thread.sleep(60000)
                    fusedLocationClient.removeLocationUpdates(callback)
                    break
                } catch (e: SecurityException) {
                    break
                }
            }
        }
    }

    fun resetStats() {
        _totalDistanceMeters = 0f
        _stepCount = 0
        lastLocation = null
    }
}