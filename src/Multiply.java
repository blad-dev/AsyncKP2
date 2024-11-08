import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Multiply {
    private static class MultiplyTask implements Callable<CopyOnWriteArrayList<Integer>>{
        List<Integer> list;
        double multiplier;
        MultiplyTask(List<Integer> list, double multiplier){
            this.list = list;
            this.multiplier = multiplier;
        }
        @Override
        public CopyOnWriteArrayList<Integer> call()  {
            ArrayList<Integer> returnList = new ArrayList<>(list.size());
            for(int element : this.list){
                returnList.addLast((int)(element * multiplier));
            }
            return new CopyOnWriteArrayList<>(returnList);
        }
    }
    static MultiplyTask createTask(CopyOnWriteArrayList<Integer> list,
                                   double multiplier, int indexFrom, int indexTo){
        return new MultiplyTask(list.subList(indexFrom, indexTo), multiplier);
    }
    public static CopyOnWriteArrayList<Integer> getMultiplied(
            CopyOnWriteArrayList<Integer> list,
            double multiplier
    ) throws InterruptedException, ExecutionException {
        return getMultiplied(list, multiplier, Runtime.getRuntime().availableProcessors());
    }
    public static CopyOnWriteArrayList<Integer> getMultiplied(
            CopyOnWriteArrayList<Integer> list,
            double multiplier,
            int threadsCount
    ) throws InterruptedException, ExecutionException {
        final int amountOfElementsForMostThreads = list.size() / threadsCount;
        final int amountOfElementsForFirstThread = list.size() - (amountOfElementsForMostThreads * (threadsCount-1));
        int currentIndex = amountOfElementsForFirstThread;
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);

        ArrayList<Future<CopyOnWriteArrayList<Integer>>> futures = new ArrayList<>(threadsCount);
        futures.addLast(executor.submit(createTask(list, multiplier, 0, amountOfElementsForFirstThread)));
        for(int i = 1; i < threadsCount; ++i){
            futures.addLast(executor.submit(
                    createTask(list, multiplier, currentIndex, currentIndex + amountOfElementsForMostThreads)
            ));
            currentIndex += amountOfElementsForMostThreads;
        }
        CopyOnWriteArrayList<Integer> returnList = new CopyOnWriteArrayList<>();
        for(Future<CopyOnWriteArrayList<Integer>> future : futures){
            if(future.isCancelled()){
                throw new InterruptedException("Task was cancelled");
            }
            while (!future.isDone()){
                if(future.isCancelled()){
                    throw new InterruptedException("Task was cancelled");
                }
                Thread.sleep(1);
            }
            returnList.addAll(future.get());
        }
        executor.close();
        return returnList;
    }
}

