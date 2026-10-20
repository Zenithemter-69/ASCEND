package ascending.com.ascendingg.data.database;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0016\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H\'J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\'J\u001b\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u000eH\'\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0005H\'\u00a8\u0006\u0012"}, d2 = {"Lascending/com/ascendingg/data/database/HabitDao;", "", "getAllHabits", "Lkotlinx/coroutines/flow/Flow;", "", "Lascending/com/ascendingg/data/database/HabitEntity;", "incrementStreak", "", "id", "", "timestamp", "insertAll", "", "habits", "", "([Lascending/com/ascendingg/data/database/HabitEntity;)[J", "update", "habit", "app_debug"})
@androidx.room.Dao
public abstract interface HabitDao {
    
    @androidx.room.Query(value = "SELECT * FROM habits WHERE isActive = 1 ORDER BY id")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<ascending.com.ascendingg.data.database.HabitEntity>> getAllHabits();
    
    @androidx.room.Insert
    @org.jetbrains.annotations.NotNull
    public abstract long[] insertAll(@org.jetbrains.annotations.NotNull
    ascending.com.ascendingg.data.database.HabitEntity[] habits);
    
    @androidx.room.Update
    public abstract int update(@org.jetbrains.annotations.NotNull
    ascending.com.ascendingg.data.database.HabitEntity habit);
    
    @androidx.room.Query(value = "UPDATE habits SET completionStreak = completionStreak + 1, lastCompleted = :timestamp WHERE id = :id")
    public abstract int incrementStreak(long id, long timestamp);
}