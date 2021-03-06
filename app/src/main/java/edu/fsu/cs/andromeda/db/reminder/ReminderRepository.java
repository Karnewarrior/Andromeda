package edu.fsu.cs.andromeda.db.reminder;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.fsu.cs.andromeda.db.AndromedaDB;

public class ReminderRepository {

    private Application application;
    private ReminderDao reminderDao;

    private LiveData<List<Reminder>> allReminders;
    private LiveData<List<Reminder>> allRemindersByToDoFk;

    public ReminderRepository(Application application) {
        this.application = application;

        AndromedaDB andromedaDB = AndromedaDB.getInstance(application);
        reminderDao = andromedaDB.reminderDao();
        allReminders = reminderDao.getAllReminders();
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return allReminders;
    }

    public LiveData<List<Reminder>> getAllRemindersByToDoFk(int toDoFk) {
        allRemindersByToDoFk = reminderDao.getAllRemindersByToDoFk(toDoFk);
        return allRemindersByToDoFk;
    }

    public long upsertReminder(Reminder reminder){
        try{
            return new UpsertReminderAsync(reminderDao).execute(reminder).get();
        } catch (ExecutionException | InterruptedException e){
            e.printStackTrace();
        }
        return 0;
    }

    public void deleteReminder(Reminder reminder){
        new DeleteReminderAsync(reminderDao).execute(reminder);
    }

    // ASYNC TASKS
    public class UpsertReminderAsync extends AsyncTask<Reminder, Void, Long> {

        private ReminderDao reminderDao;

        public UpsertReminderAsync(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }

        @Override
        protected Long doInBackground(Reminder... reminders) {
            long id;
            id = reminderDao.upsertReminder(reminders[0]);
            return id;
        }
    }

    public class DeleteReminderAsync extends AsyncTask<Reminder, Void, Void>{

        private ReminderDao reminderDao;

        public DeleteReminderAsync(ReminderDao reminderDao) {
            this.reminderDao = reminderDao;
        }

        @Override
        protected Void doInBackground(Reminder... reminders) {
            reminderDao.deleteReminder(reminders[0]);
            return null;
        }
    }
}
