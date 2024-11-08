import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    private static int genRandomRange(int range){
        return (int)(Math.random()*range);
    }
    public static int randomInRangeInclusive(int min, int max){
        return genRandomRange(max - min + 1) + min;
    }
    public static ArrayList<Integer> genRandomArrayList(int size, int min, int max){
        ArrayList<Integer> list = new ArrayList<>(size);
        for(int i = 0; i < size; ++i){
            list.add(randomInRangeInclusive(min, max));
        }
        return list;
    }
    public static CopyOnWriteArrayList<Integer> genRandomSafeList(int size, int min, int max){
        return new CopyOnWriteArrayList<>(
                genRandomArrayList(size, min, max)
        );
    }
    public static void main(String[] args) {
        long programStartTime = System.nanoTime();
        CopyOnWriteArrayList<Integer> list = null;
        Scanner in = new Scanner(System.in);
        System.out.print("Введіть розмір масива: ");
        int size;
        double multiplier = 0.0;
        try {
            size = in.nextInt();
            if(size <= 0){
                System.out.println("Розмір не може бути менше 1");
                return;
            }
            list = genRandomSafeList(size, -100, 100);
            System.out.println("Початковий масив                     : " + list);
            System.out.print("Введіть множник до масива: ");
            multiplier = in.nextDouble();
        }
        catch (InputMismatchException e){
            System.out.println("Введено некоректне число");
        }
        try {
            long startTime = System.nanoTime();
            list = Multiply.getMultiplied(list, multiplier);
            long endTime = System.nanoTime();
            System.out.printf("Початковий масив домножений на %5.2f : " + list + '\n', multiplier);
            System.out.printf("Виконання операції множення масиву зайняло: %.5f секунд\n", (endTime - startTime) / 1_000_000_000.0);
        } catch (Exception e) {
            System.err.println("Виконання операції закінчилося з помилкою:");
            e.printStackTrace();
        }
        long programEndTime = System.nanoTime();
        System.out.printf("Виконання всієї програми зайняло: %.5f секунд\n", (programEndTime - programStartTime) / 1_000_000_000.0);
    }
}