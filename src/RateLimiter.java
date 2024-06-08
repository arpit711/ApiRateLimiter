
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RateLimiter {
    private static long now = System.currentTimeMillis();
//    private int size =  100;
//    private int rate = 100;
//    private int curretSize = 0;
//    private AtomicInteger count;
//    private int maxPreSecondVisit;
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final ConcurrentHashMap<String, Queue<Long>> requestTimestamps;

    public RateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
        this.requestTimestamps =  new ConcurrentHashMap<>();
    }

    public boolean isAllowed(String cliendId) {
        long currentTime = System.currentTimeMillis();
        requestTimestamps.putIfAbsent(cliendId, new ConcurrentLinkedQueue<>());

        Queue<Long> timeStamps = requestTimestamps.get(cliendId);
        synchronized (timeStamps) {
            while(!timeStamps.isEmpty() && (currentTime - timeStamps.peek() > windowSizeInMillis)) {
                timeStamps.poll();
            }

        }
        if (timeStamps.size() < maxRequests) {
            timeStamps.add(currentTime);
            return true;
        } else
            return false;
    }

    public static void main(String[] args) throws InterruptedException {
        int maxRequests = 5;
        long windowSizeInMillis = 10000; //10 seconds

        RateLimiter rateLimiter = new RateLimiter(maxRequests, windowSizeInMillis);
        String clientId = "client1";
        for(int i = 0; i < 10; i++) {
            System.out.println("Request #" + i + " from " + clientId + ": " + (rateLimiter.isAllowed(clientId) ? "Allowed" : "Denied"));
            Thread.sleep(1000); // Simulate 1 second interval between requests
        }



    }

}
