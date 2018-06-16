package ru.kulikovman.flickrviewer;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import ru.kulikovman.flickrviewer.models.PhotoPreview;

public class RealmHelper {
    private static RealmHelper sRealmHelper;
    private Realm mRealm;

    public static RealmHelper get() {
        if (sRealmHelper == null) {
            sRealmHelper = new RealmHelper();
        }
        return sRealmHelper;
    }

    private RealmHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    public boolean baseIsEmpty() {
        return mRealm.isEmpty();
    }

    OrderedRealmCollection<PhotoPreview> getPhotoPreviewList() {
        return mRealm.where(PhotoPreview.class)
                .findAll();
    }

    PhotoPreview getPhotoPreviewById(long previewId) {
        return mRealm.where(PhotoPreview.class)
                .equalTo(PhotoPreview.ID, previewId)
                .findFirst();
    }



    /*OrderedRealmCollection<Task> getUnfinishedTasks() {
        return mRealm.where(Task.class)
                .equalTo(Task.DONE, false)
                .findAll()
                .sort(new String[]{Task.TARGET_DATE, Task.PRIORITY, Task.TITLE},
                        new Sort[]{Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING});
    }

    OrderedRealmCollection<Task> getTasksByGroup(long groupId) {
        return mRealm.where(Task.class)
                .equalTo(Task.DONE, false)
                .equalTo(Task.GROUP_ID, groupId)
                .findAll()
                .sort(new String[]{Task.TARGET_DATE, Task.PRIORITY, Task.TITLE},
                        new Sort[]{Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING});
    }

    OrderedRealmCollection<Task> getIncomeTasks() {
        return mRealm.where(Task.class)
                .equalTo(Task.DONE, false)
                .equalTo(Task.GROUP_ID, 0)
                .findAll()
                .sort(new String[]{Task.TARGET_DATE, Task.PRIORITY, Task.TITLE},
                        new Sort[]{Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING});
    }

    OrderedRealmCollection<Task> getTodayTasks() {
        long todayDate = DateHelper.getTodayCalendarWithoutTime().getTimeInMillis();
        return mRealm.where(Task.class)
                .equalTo(Task.DONE, false)
                .lessThanOrEqualTo(Task.TARGET_DATE, todayDate)
                .findAll()
                .sort(new String[]{Task.TARGET_DATE, Task.PRIORITY, Task.TITLE},
                        new Sort[]{Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING});
    }

    OrderedRealmCollection<Task> getWeekTasks() {
        long weekDate = DateHelper.getAfterWeekCalendarWithoutTime().getTimeInMillis();
        return mRealm.where(Task.class)
                .equalTo(Task.DONE, false)
                .lessThanOrEqualTo(Task.TARGET_DATE, weekDate)
                .or()
                .equalTo(Task.DONE, false)
                .equalTo(Task.TARGET_DATE, Long.MAX_VALUE)
                .findAll()
                .sort(new String[]{Task.TARGET_DATE, Task.PRIORITY, Task.TITLE},
                        new Sort[]{Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING});
    }

    OrderedRealmCollection<Task> getMonthTasks() {
        long monthDate = DateHelper.getAfterMonthCalendarWithoutTime().getTimeInMillis();
        return mRealm.where(Task.class)
                .equalTo(Task.DONE, false)
                .lessThanOrEqualTo(Task.TARGET_DATE, monthDate)
                .or()
                .equalTo(Task.DONE, false)
                .equalTo(Task.TARGET_DATE, Long.MAX_VALUE)
                .findAll()
                .sort(new String[]{Task.TARGET_DATE, Task.PRIORITY, Task.TITLE},
                        new Sort[]{Sort.ASCENDING, Sort.DESCENDING, Sort.ASCENDING});
    }

    OrderedRealmCollection<Group> getNotEmptyGroups() {
        return mRealm.where(Group.class)
                .notEqualTo(Group.TASK_COUNTER, 0)
                .findAll()
                .sort(new String[]{Group.TASK_COUNTER, Group.NAME},
                        new Sort[]{Sort.DESCENDING, Sort.ASCENDING});
    }

    Map<String, Long> getTaskCounters() {
        Map<String, Long> counters = new HashMap<>();

        // Получаем даты на сегодня и плюс месяц
        long todayDate = DateHelper.getTodayCalendarWithoutTime().getTimeInMillis();
        long weekDate = DateHelper.getAfterWeekCalendarWithoutTime().getTimeInMillis();
        long monthDate = DateHelper.getAfterMonthCalendarWithoutTime().getTimeInMillis();

        // Получаем количество незавершенных задач
        long unfinishedTasks = mRealm.where(Task.class).equalTo(Task.DONE, false).count();
        long incomeTasks = mRealm.where(Task.class).equalTo(Task.DONE, false).equalTo(Task.GROUP_ID, 0).count();
        long todayTasks = mRealm.where(Task.class).equalTo(Task.DONE, false).lessThanOrEqualTo(Task.TARGET_DATE, todayDate).count();
        long weekTasks = mRealm.where(Task.class).equalTo(Task.DONE, false).lessThanOrEqualTo(Task.TARGET_DATE, weekDate).count();
        long monthTasks = mRealm.where(Task.class).equalTo(Task.DONE, false).lessThanOrEqualTo(Task.TARGET_DATE, monthDate).count();

        // Сохраняем количество
        counters.put("unfinishedTasks", unfinishedTasks);
        counters.put("incomeTasks", incomeTasks);
        counters.put("todayTasks", todayTasks);
        counters.put("weekTasks", weekTasks);
        counters.put("monthTasks", monthTasks);

        return counters;
    }*/
}
