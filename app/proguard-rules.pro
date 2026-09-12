# Room Database keep rules
-keep class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase {
    *;
}
-keep class * extends androidx.room.EntityDeletionOrUpdateAdapter {
    <init>(...);
}
-keep class * extends androidx.room.EntityInsertionAdapter {
    <init>(...);
}
-keep class * extends androidx.room.SharedSQLiteStatement {
    <init>(...);
}
-keep class com.example.kasirjose.data.** { *; }

# Keep entities and DAO implementation generated classes
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }

# ViewModels and ViewModelFactory
-keep class com.example.kasirjose.viewmodel.** { *; }

# Navigation & UI models
-keep class com.example.kasirjose.ui.** { *; }
