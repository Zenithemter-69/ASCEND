package ascending.com.ascendingg.data.repository;

@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0016\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0019\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000bJ\u0012\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\rJ!\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0015J\u0019\u0010\u0016\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\nH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0017"}, d2 = {"Lascending/com/ascendingg/data/repository/HabitRepository;", "", "database", "Lascending/com/ascendingg/data/database/AppDatabase;", "(Lascending/com/ascendingg/data/database/AppDatabase;)V", "habitDao", "Lascending/com/ascendingg/data/database/HabitDao;", "addHabit", "", "habit", "Lascending/com/ascendingg/data/database/HabitEntity;", "(Lascending/com/ascendingg/data/database/HabitEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllHabits", "Landroidx/lifecycle/LiveData;", "", "Lascending/com/ascendingg/data/model/Habit;", "incrementStreak", "", "id", "", "timestamp", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateHabit", "app_debug"})
public final class HabitRepository {
    @org.jetbrains.annotations.NotNull
    private final ascending.com.ascendingg.data.database.AppDatabase database = null;
    @org.jetbrains.annotations.NotNull
    private final ascending.com.ascendingg.data.database.HabitDao habitDao = null;
    
    @javax.inject.Inject
    public HabitRepository(@org.jetbrains.annotations.NotNull
    ascending.com.ascendingg.data.database.AppDatabase database) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<ascending.com.ascendingg.data.model.Habit>> getAllHabits() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object addHabit(@org.jetbrains.annotations.NotNull
    ascending.com.ascendingg.data.database.HabitEntity habit, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super long[]> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object updateHabit(@org.jetbrains.annotations.NotNull
    ascending.com.ascendingg.data.database.HabitEntity habit, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object incrementStreak(long id, long timestamp, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
}