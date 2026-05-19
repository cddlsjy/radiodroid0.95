package net.programmierecke.radiodroid2.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import net.programmierecke.radiodroid2.history.TrackHistoryDao;
import net.programmierecke.radiodroid2.history.TrackHistoryEntry;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                TrackHistoryEntry.class,
                RadioStation.class,
                RadioStationFts.class,
                UpdateTimestamp.class
        },
        version = 5,
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class RadioDroidDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final Executor databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile RadioDroidDatabase INSTANCE;

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        }
    };

    public static RadioDroidDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RadioDroidDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RadioDroidDatabase.class, "radiodroid_db")
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void closeInstance() {
        INSTANCE = null;
    }

    public static RadioDroidDatabase forceRecreateDatabase(Context context) {
        context.deleteDatabase("radiodroid_db");
        INSTANCE = null;
        return getDatabase(context);
    }

    public Executor getQueryExecutor() {
        return databaseWriteExecutor;
    }

    public abstract TrackHistoryDao songHistoryDao();
    public abstract RadioStationDao radioStationDao();
    public abstract UpdateTimestampDao updateTimestampDao();
}