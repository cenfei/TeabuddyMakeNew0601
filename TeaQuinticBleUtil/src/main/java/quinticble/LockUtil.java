package quinticble;

import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * 锁工具
 */
class LockUtil {

    private Map<String, Semaphore> semaphoreMap;

    private Map<String, List<Date>> locks;

    private static LockUtil instance;

    private Timeout deadLockResolver;

    private LockUtil() {
        Log.e("Fox ","Fox LockUtil");

//        locks = new ConcurrentHashMap<>();
//        semaphoreMap = new ConcurrentHashMap<>();
//        deadLockResolver = new Timeout(5000, new Timeout.TimerEvent() {
//            @Override
//            public void onTimeout() {
//                for (String key : locks.keySet()) {
//                    while (locks.get(key).size() > 0 && (new Date().getTime() - locks.get(key).get(0).getTime() > 30000)) {
//                        Log.i("-- lock --", "dead lock " + key);
//                        releaseLock(key);
//                    }
//                }
//                deadLockResolver.restart();
//            }
//        });
        //deadLockResolver.start();
    }

    public static LockUtil getInstance() {
        if (instance == null) {
            instance = new LockUtil();
        }
        return instance;
    }

    /**
     * 获取锁
     * @param lockId 锁id
     */
    public void aquireLock(String lockId) {
//        Log.e("Fox ","Fox aquireLock");

//        if (!semaphoreMap.containsKey(lockId)) {
//            semaphoreMap.put(lockId, new Semaphore(1));
//            locks.put(lockId, Collections.synchronizedList(new ArrayList<Date>()));
//        }
//        try {
//            Log.i("-- lock --", "acquire lock " + lockId);
//            locks.get(lockId).add(new Date());
//            semaphoreMap.get(lockId).acquire();
//        } catch (InterruptedException e) {
//
//        }
    }

    /**
     * 释放锁
     * @param lockId 锁id
     */
    public void releaseLock(String lockId) {
//        Log.e("Fox ","Fox releaseLock");
//        if (!semaphoreMap.containsKey(lockId)) {
//            semaphoreMap.put(lockId, new Semaphore(1));
//            locks.put(lockId, Collections.synchronizedList(new ArrayList<Date>()));
//        }
//        if (semaphoreMap.get(lockId).availablePermits() == 0) {
//            Log.i("-- lock --", "release lock " + lockId);
//            if (locks.get(lockId).size() > 0) {
//                locks.get(lockId).remove(0);
//            }
//            semaphoreMap.get(lockId).release();
//        }
    }
}
