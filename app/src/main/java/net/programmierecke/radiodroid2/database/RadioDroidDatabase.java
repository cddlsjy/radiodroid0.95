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
        version = 14,
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class RadioDroidDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final Executor databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile RadioDroidDatabase INSTANCE;
    private static volatile boolean isClosing = false;

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

    public static final Migration MIGRATION_5_14 = new Migration(5, 14) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Empty migration for version jumps from 5 to 14
            // This allows importing databases from version 14 to version 5 apps
        }
    };

    public static RadioDroidDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RadioDroidDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RadioDroidDatabase.class, "radio_droid_database")
                            .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_14)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void closeInstance() {
        INSTANCE = null;
    }

    public static RadioDroidDatabase forceRecreateDatabase(final Context context) {
        synchronized (RadioDroidDatabase.class) {
            // 设置关闭标志，阻止 onOpen 中的任务执行
            isClosing = true;
            
            // 关闭现有实例
            if (INSTANCE != null) {
                INSTANCE.close();
                INSTANCE = null;
                
                // 添加短暂延迟，确保数据库完全关闭并释放文件句柄
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // 创建新实例 - 使用正确的数据库名称 "radio_droid_database"
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    RadioDroidDatabase.class, "radio_droid_database")
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_14)
                    .fallbackToDestructiveMigration()
                    .build();
            
            // 重置关闭标志
            isClosing = false;
            
            return INSTANCE;
        }
    }

    public Executor getQueryExecutor() {
        return databaseWriteExecutor;
    }

    public abstract TrackHistoryDao songHistoryDao();
    public abstract RadioStationDao radioStationDao();
    public abstract UpdateTimestampDao updateTimestampDao();
}