package Øving2;

import java.util.Date;

public class Oving2AlgDat {
    public static void main(String[] args) {
        int n = 5000;
        double x = 3;
        int runningTime = 10_000;

        //Time measurement of method one
        Date startOne = new Date();
        double resultOne;
        double timeOne;
        int rounds = 0;
        Date stopOne;
        do {
            resultOne = calculateExponentOne(x, n);
            stopOne = new Date();
            ++rounds;
        } while (stopOne.getTime()-startOne.getTime() < runningTime);
        timeOne = (double)(stopOne.getTime()-startOne.getTime()) / rounds;
        System.out.println("Finished running method one");

        //Time measurement of method two
        Date startTwo = new Date();
        double resultTwo;
        double timeTwo;
        Date stopTwo;
        rounds = 0;
        do {
            resultTwo = calculateExponentTwo(x, n);
            stopTwo = new Date();
            ++rounds;
        } while (stopTwo.getTime()-startTwo.getTime() < runningTime);
        timeTwo = (double)(stopTwo.getTime()-startTwo.getTime()) / rounds;
        System.out.println("Finished running method two");

        //Time measurement of Math.pow
        Date startThree = new Date();
        double resultThree;
        double timeThree;
        Date stopThree;
        rounds = 0;
        do {
            resultThree = Math.pow(x, n);
            stopThree = new Date();
            ++rounds;
        } while (stopThree.getTime()-startThree.getTime() < runningTime);
        timeThree = (double)(stopThree.getTime()-startThree.getTime()) / rounds;
        System.out.println("Finished running Math.pow");

        System.out.println();
        System.out.println("Result method one: " + resultOne + "\nTotal milliseconds per round: " + timeOne);
        System.out.println();
        System.out.println("Result method two: " + resultTwo + "\nTotal milliseconds per round: " + timeTwo);
        System.out.println();
        System.out.println("Result Math.pow: " + resultThree + "\nTotal milliseconds per round: " + timeThree);
        System.out.println();

        if (timeOne > timeTwo && timeOne > timeThree) {
            System.out.println("Method one is the slowest method");
            if (timeTwo > timeThree) System.out.println("Math.pow is the fastest method");
            else System.out.println("Method two is the fastest method");
        }
        else if (timeTwo > timeOne && timeTwo > timeThree) {
            System.out.println("Method two is the slowest method");
            if (timeOne > timeThree) System.out.println("Math.pow is the fastest method");
            else System.out.println("Method one is the fastest method");
        }
        else{
            System.out.println("Math.pow is the slowest method");
            if (timeOne > timeTwo) System.out.println("Method two is the fastest");
            else System.out.println("Method one is the fastest");
        }
    }

    private static double calculateExponentOne(double x, int n) {
        if (n == 0)
            return 1;
        else
            return x * calculateExponentOne(x,n - 1);
    }

    private static double calculateExponentTwo(double x, int n) {
        if (n == 0)
            return 1;
        if (n % 2 != 0)
            return x * calculateExponentTwo(x * x, (n -1)/2);
        else
            return calculateExponentTwo(x * x, n/2);
    }
}
