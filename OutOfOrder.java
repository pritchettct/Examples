import java.util.Random;
import java.util.Scanner;
 
/**
 * Counting out-of-order pairs in an array.
 * 
 * This program compares the times for two different algorithms for
 * computing the number of out-of-order pairs in an array.
 *
 * @author Coley Pritchett 
 * @version 9/5/14
 */
public class OutOfOrder
{
    public static final int DOUBLE_NESTED_LOOP = 1;
    public static final int COUNT_AS_YOU_SORT = 2;
    public static final int CHANGE_VALUES = 3;
    public static final int CHANGE_SIZE_AND_VALUES = 4;
    public static final int PRINT_ARRAY = 5;
    public static final int QUIT = 6;
    public static final int NUM_PER_LINE = 5;
    public static final int MAX = 500000;
    public static int count;
    public static int center;
    
    public OutOfOrder()
    {
    	count = 0;
    	center = 0;
    }

    
    /**
     * Computes number out-of-order by checking every pair.
     * 
     * @param a    A list of integers
     * @return     The number of pairs out of order
     *             in the list 
     */
    public static int countOutOfOrder1(int [] a)
    {
        int counting = 0;
        
        for (int i = 0; i < a.length; i++)
        {
        	for (int j = i + 1; j < a.length; j++)
        	{
        		if (a[i] > a[j])
        		{
        			counting++;
        		}
        	}
        }
        
        return counting;
    }

    /**
     * Computes number out-of-order while doing a mergesort, using
     * a modified algorithm obtained from the text book.
     * 
     * @param a    A list of integers
     * @return     The number of pairs out of order
     *             in the list 
     */
    public static int countOutOfOrder2(int [] a)
    {
        count = 0;
        center = 0;
    	mergeSort(a);
        return count;
    }
    
    
    public static void mergeSort(int [] a, int[] tmpArray, int left, int right)
    {    	
    	if (right == center)
    	{
    		if (a[left] > a[right])
    		{
    			count++;
    		}
    	}
    	else
    	{
    		if (a[right] < a[left])
    		{
    			count++;
    		}
    	}
    	if (left < right)
    	{
    		center = (left + right) / 2;
    		mergeSort(a, tmpArray, left, center);
    		mergeSort(a, tmpArray, center + 1, right);
    		merge(a, tmpArray, left, center + 1, right);
    	}
    }
    
    public static void mergeSort(int[] a)
    {
    	int[] tmpArray = new int[a.length];
    	
    	mergeSort(a, tmpArray, 0, a.length - 1);
    }
    
	/**
	* Helper method for mergeSort
	*/
    public static void merge(int [] a, int[] tmpArray, int leftPos,
    							int rightPos, int rightEnd)
    {
    	int leftEnd = rightPos - 1;
    	int tmpPos = leftPos;
    	int numElements = rightEnd - leftPos + 1;
    	
    	while (leftPos <= leftEnd && rightPos <= rightEnd)
    	{
    		if (Integer.compare(a[leftPos], a[rightPos]) <= 0)
    		{
    			tmpArray[tmpPos++] = a[leftPos++];
    		}
    		else
    		{
    			tmpArray[tmpPos++] = a[rightPos++];
    		}
    	}
    	
    	while (leftPos <= leftEnd)
    	{
    		tmpArray[tmpPos++] = a[leftPos++];
    	}
    	
    	while (rightPos <= rightEnd)
    	{
    		tmpArray[tmpPos++] = a[rightPos++];
    	}
    	
    	for (int i = 0; i < numElements; i++, rightEnd--)
    	{
    		a[rightEnd] = tmpArray[rightEnd];
    	}
    }

    /**
     * Fills an int array with random integers.
     * 
     * @param a    the array to hold the integers
     * @param ran  to get a random integer
     */
    public static void fill(int [] a, Random ran)
    {
        for (int i = 0; i < a.length; i++)
        {
            a[i] = ran.nextInt(MAX);
        }
    }
    
    /**
     * Ask user for array size and allocates.
     * 
     * @param cin   for keyboard input 
     * @return   	the array
     */
    public static int [] fixArraySize(Scanner cin)
    {
        int [] a;
        int numElements;
        System.out.print("How many elements do you want: ");
        numElements = cin.nextInt();
        a = new int[numElements];
        return a;
    }
    
    
    /**
     * Prints the contents of an integer array.
     * 
     * @param a    A list of integers
     */
    public static void printArray(int [] a)
    {
        System.out.println();
        for (int i = 0; i < a.length; i++)
        {
            System.out.printf("%12d\t", a[i]);
            if (i % NUM_PER_LINE == NUM_PER_LINE - 1)
            {
                System.out.println();
            }
        }
        if (a.length % NUM_PER_LINE != 0)
        {
            System.out.println();
        }
    }
    
    
    /**
     * Asks the user what to do next.
     * 
     * @param cin   for keyboard input
     * @return      the menu item chosen by the user
     */
    
    public static int menu(Scanner cin)
    {
        int choice;

        System.out.println();
        System.out.println("***********************************************");
        System.out.println("           ARRAY FILL ALGORITHMS  ");
        System.out.println("***********************************************");
        System.out.println("       1.  Algorithm #1 ");
        System.out.println("       2.  Algorithm #2 ");
        System.out.println("       3.  Change the array values");
        System.out.println("       4.  Change size and values of the array");
        System.out.println("       5.  Print the array");
        System.out.println("       6.  Quit  ");
        System.out.println("***********************************************");
        System.out.print("\n\nEnter your choice: ");
        choice = cin.nextInt();
        return choice;
    }
   
    /**
     * Asks the user what to do next.
     * 
     * @param args   unused
     */
    public static void main(String args[])
    { 

        int choice;
        int [] a = null;
        long starttime = 0;
        long finishtime;
        Scanner cin = new Scanner(System.in);
        Random randomGenerator = new Random();
        int answer = 0;

        a = fixArraySize(cin);
        fill(a, randomGenerator);

        choice = menu(cin);
        while (choice != QUIT)
        {
            if (choice ==  DOUBLE_NESTED_LOOP 
                || choice ==  COUNT_AS_YOU_SORT)
            {
                starttime = System.currentTimeMillis();
            }
            switch (choice)
            {
                case DOUBLE_NESTED_LOOP: 
                    answer = countOutOfOrder1(a);
                    break;
                case COUNT_AS_YOU_SORT:  
                    answer = countOutOfOrder2(a);
                    break;
                case CHANGE_SIZE_AND_VALUES:
                    a = fixArraySize(cin);
                    fill(a, randomGenerator);
                    break;
                case CHANGE_VALUES:
                    fill(a, randomGenerator);
                    break;
                case PRINT_ARRAY:
                    printArray(a);
                    break;
                default:
                    System.out.println("Invalid choice: Select again\n");
            }
            if (choice ==  DOUBLE_NESTED_LOOP 
                || choice ==  COUNT_AS_YOU_SORT)
            {
                finishtime = System.currentTimeMillis();
                System.out.println("There were " + answer + " out-of-order.");
                System.out.println("The time to complete is " 
                     + (finishtime - starttime) + " time units.\n\n\n\n");
            }
            choice = menu(cin);
        }
    }

}
