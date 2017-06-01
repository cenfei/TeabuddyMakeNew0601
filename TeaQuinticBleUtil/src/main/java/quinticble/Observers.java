package quinticble;

import java.util.ArrayList;
import java.util.List;

/**
 * 观察者
 */
public class Observers {

    /**
     * 蓝牙数据观察者
     */
    private static List<BleDataObserver> bleDataObservers = new ArrayList<>();

    /**
     * 注册蓝牙数据观察者
     * @param bleDataObserver 蓝牙数据观察者实例
     */
    public static void registerBleDataObserver(BleDataObserver bleDataObserver) {
        bleDataObservers.clear();
        bleDataObservers.add(bleDataObserver);
    }

    /**
     * 获取蓝牙数据观察者列表
     * @return 蓝牙数据观察者列表
     */
    static List<BleDataObserver> getBleDataObservers() {
        return bleDataObservers;
    }
}
