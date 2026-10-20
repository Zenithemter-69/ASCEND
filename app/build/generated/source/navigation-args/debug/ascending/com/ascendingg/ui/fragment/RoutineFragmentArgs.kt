package ascending.com.ascendingg.ui.fragment

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.Long
import kotlin.jvm.JvmStatic

public data class RoutineFragmentArgs(
  public val userId: Long = 1L,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putLong("userId", this.userId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("userId", this.userId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): RoutineFragmentArgs {
      bundle.setClassLoader(RoutineFragmentArgs::class.java.classLoader)
      val __userId : Long
      if (bundle.containsKey("userId")) {
        __userId = bundle.getLong("userId")
      } else {
        __userId = 1L
      }
      return RoutineFragmentArgs(__userId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): RoutineFragmentArgs {
      val __userId : Long?
      if (savedStateHandle.contains("userId")) {
        __userId = savedStateHandle["userId"]
        if (__userId == null) {
          throw IllegalArgumentException("Argument \"userId\" of type long does not support null values")
        }
      } else {
        __userId = 1L
      }
      return RoutineFragmentArgs(__userId)
    }
  }
}
