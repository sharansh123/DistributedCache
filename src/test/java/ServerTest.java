import my.cache.logic.ConcurrentCache;
import my.cache.model.StatData;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class ServerTest {

    ArrayList<Long> set;
    ArrayList<Long> get;
    ArrayList<Long> remove;

    public void performLoadTest(long load, int ttl) throws InterruptedException {
        //CacherImpl cacher = new CacherImpl();
        //ByteCache cacher = new ByteCache();
        ConcurrentCache cacher = new ConcurrentCache();
        for(int i = 0; i < load; i++){
            Thread.ofVirtual().start(
                    () -> {
                        String id = UUID.randomUUID().toString();
                        //System.out.println("Running id: " +id);
                        Random random = new Random();
                        cacher.set(id.getBytes(StandardCharsets.ISO_8859_1), id.getBytes(StandardCharsets.ISO_8859_1), random.nextInt(ttl));
                        cacher.get(id.getBytes(StandardCharsets.ISO_8859_1));
                        cacher.get(id.getBytes(StandardCharsets.ISO_8859_1));
                        cacher.get(id.getBytes(StandardCharsets.ISO_8859_1));
                        cacher.get(id.getBytes(StandardCharsets.ISO_8859_1));
                    }
            );
        }
        set = new ArrayList<>();
        get = new ArrayList<>();
        remove = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);
        Thread.ofVirtual().start(() -> {
            while(true){
                try {
                    //System.out.println("Reading set times");
                    Long time = cacher.getSetTimes().take();
                    set.add(time);
                    if(set.size() == load) break;
                } catch (InterruptedException e) {
                    System.out.println("Unable to read set times");
                }
            }
            latch.countDown();
        });
        Thread.ofVirtual().start(() -> {
            while(true){
                try {
                    //System.out.println("Reading get times");
                    Long time = cacher.getGetTimes().take();
                    get.add(time);
                    if(get.size() == 4*load) break;
                } catch (InterruptedException e) {
                    System.out.println("Unable to read get times");
                }
            }
            latch.countDown();
        });
        Thread.ofVirtual().start(() -> {
            while(true){
                try {
                    //System.out.println("Reading remove times");
                    Long time = cacher.getRemoveTimes().take();
                    remove.add(time);
                    if(remove.size() == load) break;
                } catch (InterruptedException e) {
                    System.out.println("Unable to read remove times");
                }
            }
            latch.countDown();
        });
        latch.await();
    }

    public int calculate(ArrayList<Long> x){
        long s = 0;
        for(Long time: x){
            s += time;
        }
        int avg =  (int) (s / x.size());
        return avg;
    }

    public void getHistogram(List<StatData> x){
        int[] set = new int[25];
        int[] get = new int[25];
        int[] remove = new int[25];
        for(StatData statData: x){
            set[statData.getSetVal()/100] += 1;
            get[statData.getGetVal()/100] += 1;
            remove[statData.getRemoveVal()/100] += 1;
        }
        System.out.println(Arrays.toString(set));
        System.out.println(Arrays.toString(get));
        System.out.println(Arrays.toString(remove));
    }

    @Test
    public void loadTest() throws InterruptedException {
        int load = 1000000;
        Random random = new Random();
        ArrayList<StatData> data = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Thread.sleep(1000);
            System.out.println("Loading " + i + " of " + 10);
            performLoadTest(load, random.nextInt(10000));
            data.add(new StatData(calculate(set), calculate(get), calculate(remove)));
        }

        for(StatData statData: data){
            System.out.println(statData);
        }
        //getHistogram(data);
//        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
//        for (GarbageCollectorMXBean gcBean : gcBeans) {
//            System.out.println("Name: " + gcBean.getName());
//            System.out.println("Collection Count: " + gcBean.getCollectionCount());
//            System.out.println("Collection Time (ms): " + gcBean.getCollectionTime());
//        }
    }
}
