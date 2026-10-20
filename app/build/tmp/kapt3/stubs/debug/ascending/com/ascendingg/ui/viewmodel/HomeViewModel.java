package ascending.com.ascendingg.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0013"}, d2 = {"Lascending/com/ascendingg/ui/viewmodel/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "habitRepository", "Lascending/com/ascendingg/data/repository/HabitRepository;", "locationTracker", "Lascending/com/ascendingg/data/tracking/LocationTracker;", "(Lascending/com/ascendingg/data/repository/HabitRepository;Lascending/com/ascendingg/data/tracking/LocationTracker;)V", "getLocationTracker", "()Lascending/com/ascendingg/data/tracking/LocationTracker;", "todaysHabits", "Lkotlinx/coroutines/flow/Flow;", "", "Lascending/com/ascendingg/data/model/Habit;", "getTodaysHabits", "()Lkotlinx/coroutines/flow/Flow;", "incrementStreak", "", "habitId", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final ascending.com.ascendingg.data.repository.HabitRepository habitRepository = null;
    @org.jetbrains.annotations.NotNull
    private final ascending.com.ascendingg.data.tracking.LocationTracker locationTracker = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.Flow<java.util.List<ascending.com.ascendingg.data.model.Habit>> todaysHabits = null;
    
    @javax.inject.Inject
    public HomeViewModel(@org.jetbrains.annotations.NotNull
    ascending.com.ascendingg.data.repository.HabitRepository habitRepository, @org.jetbrains.annotations.NotNull
    ascending.com.ascendingg.data.tracking.LocationTracker locationTracker) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final ascending.com.ascendingg.data.tracking.LocationTracker getLocationTracker() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.Flow<java.util.List<ascending.com.ascendingg.data.model.Habit>> getTodaysHabits() {
        return null;
    }
    
    public final void incrementStreak(long habitId) {
    }
}