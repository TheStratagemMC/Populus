import java.util.ArrayList;

/**
 * Created by Axel on 2/23/2016.
 */
public class Test {
    public static void main(String[] args){
        ArrayList<Dog> dogs = new ArrayList<>();

        dogs.add(new Dog());
        dogs.add(new Dog());

        Dog dog = dogs.get(0);
        dog.count();

        for (Dog d : dogs){
            d.print();
        }
    }

    public static class Dog{
        int foo = 3;

        public void count(){
            foo++;
        }

        public void print(){
            System.out.println(foo+"");
        }
    }
}
