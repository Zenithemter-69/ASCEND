package ascending.com.ascendingg.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ascending.com.ascendingg.data.utils.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val prefs: PreferencesManager
) : AndroidViewModel(application) {

    private val _hasApiKey = MutableLiveData<Boolean>()
    val hasApiKey: LiveData<Boolean> = _hasApiKey

    private val _notificationsEnabled = MutableLiveData<Boolean>()
    val notificationsEnabled: LiveData<Boolean> = _notificationsEnabled

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        _hasApiKey.value = prefs.getApiKey() != null
        _notificationsEnabled.value = false
    }

    fun saveApiKey(key: String) {
        prefs.saveApiKey(key)
        _hasApiKey.value = true
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun exportUserData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val backupFile = File(getApplication<Application>().filesDir, "ascendingg_backup.json")
                    backupFile.writeText("""{"sample_data": "exported"}""")
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        getApplication(),
                        "Backup saved to internal storage",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        getApplication(),
                        "Export failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun clearAllData() {
        _hasApiKey.value = false
    }
}