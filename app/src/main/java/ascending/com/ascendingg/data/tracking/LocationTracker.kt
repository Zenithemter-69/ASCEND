package ascending.com.ascendingg.data.tracking

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener

class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

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

    // State flows
    private val _currentLocation = androidx.lifecycle.MutableStateFlow<android.location.Location?>(null)
    val currentLocation: Flow<android.location.Location?> = _currentLocation
        .distinctUntilChanged()

    private var _totalDistanceMeters = 0f
    private var _stepCount = 0

    val totalDistanceMeters: Float
        get() = _totalDistanceMeters

    val stepCount: Int
        get() = _stepCount

    // Distance per step estimation (average human step ~0.75m)
    private val DISTANCE_PER_STEP = 0.75f

    /**
     * Start tracking location and calculate steps/distance.
     * Returns a Flow that emits the current location (one-shot).
     */
    fun startTracking(): Flow<android.location.Location?> {
        if (!hasLocationPermission()) {
            return flow { }
        }

        // Get last known location once
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                _currentLocation.value = location
            }
        }

        // Emit current location and complete
        return flow {
            // Wait briefly then complete
            Thread.sleep(100)
            _currentLocation.value = null
        }
    }

    fun resetStats() {
        _totalDistanceMeters = 0f
        _stepCount = 0
    }
}